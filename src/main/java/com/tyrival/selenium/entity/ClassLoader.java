package com.tyrival.selenium.entity;

import com.tyrival.selenium.entity.task.Config;
import com.tyrival.selenium.entity.task.System;
import com.tyrival.selenium.utils.FileUtil;
import com.tyrival.selenium.utils.PathUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

/**
 * @Description: 动态编译脚本
 * @Author: Zhou Chenyu
 * @Date: 2018/3/5
 * @Version: V1.0
 * @Modified By:
 * @Modified Date:
 * @Why:
 */
public class ClassLoader {

    /**
     * 源码文件后缀
     */
    private static final String EXT_NAME = ".java";
    /**
     * 默认依赖包
     */
    private static final String[] DEFAULT_DEPENDENCY_JAR = new String[]{};
//    private static final String[] DEFAULT_DEPENDENCY_JAR = new String[]{
//            "selenium-api-3.9.1.jar",
//            "selenium-support-3.9.1.jar",
//            "selenium-java-3.9.1.jar",
//            "guava-24.0-jre.jar"
//    };
    /**
     * 编译器
     */
    private JavaCompiler compiler;
    /**
     * 文件管理器
     */
    private StandardJavaFileManager fileManager;
    /**
     * 日志记录
     */
    private DiagnosticCollector<JavaFileObject> diagnostics;
    /**
     * ABS_CLASSPATH绝对路径
     */
    private String ABS_CLASSPATH;
    /**
     * 源码所在绝对路径
     */
    private String ABS_RESOURCE_PATH;
    /**
     * 依赖包路径
     */
    private String DEPENDENCY_JAR_PATH;
    /**
     * 获取包名的匹配
     */
    private Pattern packagePattern;
    /**
     * 匹配包名的正则
     */
    private static final String REG_EX = "(?<=package\\s).*(?=;)";
    /**
     * 配置文件
     */
    private Config config;
    /**
     * 测试系统
     */
    private System system;
    /**
     * 依赖jar包列表
     */
    private String[] DEPENDENCY_JARS;
    /**
     * javac -classpath参数分隔符，Linux为冒号，Windows为分号
     */
    private String CLASSPATH_SEPARATOR;
    /**
     * 缓存已编译的脚本的名称和Hash值，Map<类名, Hash值>
     */
    private static Map<String, String> classCache = new HashMap<>();

    public ClassLoader(System system, Config config) {
        this.config = config;
        this.system = system;
        this.compiler = ToolProvider.getSystemJavaCompiler();
        this.fileManager = compiler.getStandardFileManager(null, null, null);
        this.diagnostics = new DiagnosticCollector<JavaFileObject>();
        this.packagePattern = Pattern.compile(REG_EX);
        this.ABS_CLASSPATH = PathUtil.getClasspath();
        this.ABS_RESOURCE_PATH = PathUtil.getSourcePath() + system.getName() + "/";
        this.DEPENDENCY_JARS = concatArrayWithoutBlank(DEFAULT_DEPENDENCY_JAR, this.system.getJars());
        String libPath = PathUtil.getLibPath();
        StringBuilder jarPath = new StringBuilder();
        this.CLASSPATH_SEPARATOR
                = java.lang.System.getProperty("os.name").indexOf("Windows") >= 0 ? ";" : ":";
        for (int i = 0; i < this.DEPENDENCY_JARS.length; i++) {
            String jarName = this.DEPENDENCY_JARS[i];
            jarPath.append(this.CLASSPATH_SEPARATOR).append(libPath).append(jarName);
        }
        this.DEPENDENCY_JAR_PATH = jarPath.toString();
    }

    /**
     * 编译java文件
     *
     * @param moduleName 源码文件名
     * @return 类 Class
     */
    public Class compiler(String moduleName) throws Exception {
        // 获取源码路径
        String sourceFilePath = new StringBuilder(this.ABS_RESOURCE_PATH)
                .append(moduleName).append(EXT_NAME).toString();
        File sourceFile = new File(sourceFilePath);
        // 获取源码的hash值
        String hash = String.valueOf(FileUtil.read(sourceFilePath).hashCode());

        // 解析包名
        String packageName = this.getPackageName(sourceFilePath);
        // 类全名
        String fullClassName = packageName + "." + moduleName;

        // 判断缓存中是否已存在编译过的类
        String originalHash = ClassLoader.classCache.get(fullClassName);
        // 如果模块名存在，且hash值相等，则无需重复编译
        if (originalHash != null && hash.equals(originalHash)) {
            return Class.forName(fullClassName);
        }

        List<File> sourceFileList = new ArrayList<File>();
        sourceFileList.add(sourceFile);
        // 获取要编译的编译单元
        Iterable<? extends JavaFileObject> compilationUnits
                = fileManager.getJavaFileObjectsFromFiles(sourceFileList);
        /*
         * 编译参数，
         * -encoding：编码方式为utf-8
         * -classpath：依赖包和依赖的classpath绝对路径，用半角冒号:隔开
         * -d：编译的目标路径
         */
        Iterable<String> options = Arrays.asList(
                "-encoding", "utf-8",
                "-classpath", this.ABS_CLASSPATH + this.DEPENDENCY_JAR_PATH,
                "-d", this.ABS_CLASSPATH);
        // 获取编译任务
        CompilationTask compilationTask = compiler.getTask(
                null, this.fileManager, this.diagnostics, options, null, compilationUnits);
        // 运行编译任务
        Boolean result = compilationTask.call();
        if (!result) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.diagnostics.getDiagnostics().size(); i++) {
                sb.append(diagnostics.getDiagnostics().get(i).toString()).append("\n");
            }
            throw new Exception(moduleName + "编译失败。\n" + sb.toString());
        }
        fileManager.close();
        // 储存类名和Hash值
        ClassLoader.classCache.put(fullClassName, hash);
        return Class.forName(fullClassName);
    }

    /**
     * 从源文件中获得包名
     *
     * @param srcPath
     */
    private String getPackageName(String srcPath) throws Exception {
        String result = null;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(srcPath));
            String data = br.readLine();
            while (data != null) {
                if (data.indexOf("package") != -1) {
                    Matcher m = this.packagePattern.matcher(data);
                    if (m.find()) {
                        result = m.group();
                    }
                    break;
                }
                data = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            throw new Exception("动态获取包名失败");
        }
        return result;
    }

    private static String[] concatArrayWithoutBlank(String[] arr1, String[] arr2) {
        List<String> list = new ArrayList();
        list = addArrayToListWithoutBlank(list, arr1);
        list = addArrayToListWithoutBlank(list, arr2);
        String[] array = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
    private static List addArrayToListWithoutBlank(List list, String[] arr) {
        if (arr == null || arr.length <= 0) {
            return list;
        }
        for (int i = 0; i < arr.length; i++) {
            if (StringUtils.isBlank(arr[i])) {
                continue;
            }
            list.add(arr[i]);
        }
        return list;
    }
}