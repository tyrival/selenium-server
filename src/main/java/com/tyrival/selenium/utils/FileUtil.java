package com.tyrival.selenium.utils;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @Description:
 * @Author: Zhou Chenyu
 * @Date: 2018/3/7
 * @Version: V1.0
 * @Modified By:
 * @Modified Date:
 * @Why:
 */
public class FileUtil {

    public static String write(String filePath, String content) throws Exception {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File doc = file.getParentFile();
                if (!doc.exists()) {
                    doc.mkdirs();
                }
                file.createNewFile();
            }
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            out.write(content);
            out.flush();
            out.close();
        } catch (Exception e) {
            throw new Exception("写入文件失败");
        }
        return filePath;
    }

    public static String read(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        BufferedReader br = null;
        String line = null;
        StringBuffer buf = new StringBuffer();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
            while ((line = br.readLine()) != null) {
                buf.append(line).append("\n");
            }
        } catch (Exception e) {

        } finally {
            // 关闭流
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    br = null;
                }
            }
        }
        return buf.toString();
    }

    public static List<String> listFilePath(String docPath) {
        File file = new File(docPath);
        File[] array = file.listFiles();
        array = sortFileByModifyDate(array);

        List<String> pathList = new ArrayList<>();
        String rootPath = PathUtil.getRootPath();
        for (int i = 0; i < array.length; i++) {
            File f = array[i];
            if (!f.isFile() || f.getName().indexOf(".") == 0) {
                continue;
            }
            pathList.add(f.getPath().replace(rootPath, ""));
        }
        return pathList;
    }

    public static List<String> listFileName(String docPath) {
        File file = new File(docPath);
        File[] array = file.listFiles();
        if (array == null || array.length <= 0) {
            return new ArrayList<>();
        }
        array = sortFileByModifyDate(array);

        List<String> nameList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            File f = array[i];
            if (!f.isFile() || f.getName().indexOf(".") == 0) {
                continue;
            }
            nameList.add(f.getName());
        }
        return nameList;
    }

    public static File[] sortFileByModifyDate(File[] array) {
        Arrays.sort(array, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0) {
                    return 1;
                } else if (diff == 0) {
                    return 0;
                } else {
                    return -1;
                }
            }

            @Override
            public boolean equals(Object obj) {
                return true;
            }
        });
        return array;
    }

    public static File rename(File file, String name) {
        if (file == null) {
            return null;
        }
        String docPath = file.getParent();
        String ext = getExtName(file);
        StringBuilder newPath = new StringBuilder(docPath).append(File.pathSeparator)
                .append(name).append(File.separator).append(ext);
        File newFile = new File(newPath.toString());
        file.renameTo(newFile);
        return newFile;
    }

    public static String getExtName(File file) {
        if (file == null) {
            return null;
        }
        String name = file.getName();
        if (StringUtils.isBlank(name)) {
            return null;
        }
        Integer index = name.lastIndexOf(".");
        if (index >= 0) {
            return name.substring(index, name.length());
        }
        return null;
    }

    public static Boolean delete(String filePath) {
        File file = new File(filePath);
        Boolean success = false;
        if (file.exists()) {
            success = file.delete();
        }
        return success;
    }
}
