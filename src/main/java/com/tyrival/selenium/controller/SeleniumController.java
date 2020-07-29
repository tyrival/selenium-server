package com.tyrival.selenium.controller;

import com.tyrival.selenium.entity.BaseResponse;
import com.tyrival.selenium.entity.TestFactory;
import com.tyrival.selenium.entity.task.Log;
import com.tyrival.selenium.entity.task.Source;
import com.tyrival.selenium.utils.FileUtil;
import com.tyrival.selenium.utils.PathUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author tyrival
 */
@Controller
@RequestMapping(value = "/selenium")
public class SeleniumController {

    @PostMapping("/test")
    public BaseResponse<Object> test(@RequestBody JSONObject param) throws Exception {
        return null;
    }

    /**
     * 顺序执行脚本
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/run")
    public BaseResponse<Object> run(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam("param") String param) throws Exception {

        // 解析参数
        BaseResponse<Object> res = new BaseResponse<Object>();
        JSONObject json = (JSONObject) JSONObject.parse(param);
        String system = (String) json.get("system");
        String config = (String) json.get("config");
        JSONArray tasks = (JSONArray) json.get("tasks");
        if (tasks == null || tasks.size() <= 0) {
            return res;
        }
        List<String> taskNames = new ArrayList<>();
        List<List<String[]>> taskParams = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            JSONObject task = tasks.getJSONObject(i);
            taskNames.add(task.getString("name"));
            List<String[]> list = new ArrayList<>();
            JSONArray paramArray = task.getJSONArray("params");
            if (paramArray != null && paramArray.size() > 0) {
                for (int j = 0; j < paramArray.size(); j++) {
                    JSONArray jArr = paramArray.getJSONArray(j);
                    String[] array = formatJSONArrayToArray(jArr);
                    list.add(array);
                }
            }
            taskParams.add(list);
        }
        // 执行测试
        try {
            TestFactory factory = new TestFactory(system, config);
            List<String> logFileList = factory.executeTasks(taskNames, taskParams);
            res.setData(logFileList);
            if (factory.getLog().getError()) {
                res.setSuccess(false);
                res.setMessage("任务运行失败。");
            }
            factory.destroy();
        } catch (Exception e) {
            res.setMessage(e.getMessage());
            res.setSuccess(false);
        }
        // 输出结果
        return res;
    }

    /**
     * 获取System列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/system/list", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public BaseResponse<Object> listSystem(HttpServletRequest request, HttpServletResponse response) {
        BaseResponse<Object> res = new BaseResponse<Object>();
        res.setData(TestFactory.listSystem());
        return res;
    }

    /**
     * 保存System列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/system/save", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public BaseResponse<Object> saveSystem(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @RequestBody Map map) {
        BaseResponse<Object> res = new BaseResponse<Object>();
        try {
            String content = (String) map.get("system");
            if (StringUtils.isEmpty(content)) {
                res.setMessage("传入参数错误，无法解析。");
                res.setSuccess(false);
                return res;
            }
            TestFactory.saveSystem(content);
        } catch (Exception e) {
            res.setMessage("保存系统设置失败，异常信息：" + e.getMessage());
        }
        return res;
    }

    /**
     * 获取Config列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/config/list", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public BaseResponse<Object> listConfig(HttpServletRequest request, HttpServletResponse response) {
        BaseResponse<Object> res = new BaseResponse<Object>();
        try {
            res.setData(TestFactory.listConfig());
        } catch (Exception e) {
            res.setSuccess(false);
            res.setMessage(e.getMessage());
        }
        return res;
    }

    /**
     * 获取模板清单
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/template/list", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public BaseResponse<Object> listTemplate(HttpServletRequest request, HttpServletResponse response) {
        BaseResponse<Object> res = new BaseResponse<Object>();
        try {
            String templatePath = PathUtil.getTemplatePath();
            List<String> templateNameList = FileUtil.listFileName(templatePath);
            res.setData(templateNameList);
        } catch (Exception e) {
            res.setSuccess(false);
            res.setMessage("查询模板列表失败，错误信息：" + e.getMessage());
        }
        return res;
    }

    /**
     * 保存模板清单
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/template/save", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public BaseResponse<Object> saveTemplate(HttpServletRequest request,
                                             HttpServletResponse response,
                                             @RequestParam("name") String name,
                                             @RequestParam("content") String content) {
        BaseResponse<Object> res = new BaseResponse<Object>();
        try {
            String templatePath = PathUtil.getTemplatePath();
            String filePath = templatePath + name;
            FileUtil.write(filePath, content);
            res.setData(name);
        } catch (Exception e) {
            res.setSuccess(false);
            res.setMessage("保存模板失败，错误信息：" + e.getMessage());
        }
        return res;
    }

    /**
     * 获取模板内容
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/template/get", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public BaseResponse<Object> getTemplate(HttpServletRequest request,
                                            HttpServletResponse response,
                                            @RequestParam("name") String name) {
        BaseResponse<Object> res = new BaseResponse<Object>();
        try {
            String templatePath = PathUtil.getTemplatePath();
            String filePath = templatePath + name;
            String content = FileUtil.read(filePath);
            res.setData(content);
        } catch (Exception e) {
            res.setSuccess(false);
            res.setMessage("获取模板失败，错误信息：" + e.getMessage());
        }
        return res;
    }

    /**
     * 重命名模板
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/template/rename", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public BaseResponse<Object> renameTemplate(HttpServletRequest request,
                                               HttpServletResponse response,
                                               @RequestParam("oldName") String oldName,
                                               @RequestParam("newName") String newName) {
        BaseResponse<Object> res = new BaseResponse<Object>();
        try {
            String templatePath = PathUtil.getTemplatePath();
            String oldPath = templatePath + oldName;
            File file = new File(oldPath);
            File newFile = FileUtil.rename(file, newName);
            res.setData(newFile.getName());
        } catch (Exception e) {
            res.setSuccess(false);
            res.setMessage("重命名模板失败，错误信息：" + e.getMessage());
        }
        return res;
    }

    /**
     * 删除模板
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/template/delete", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public BaseResponse<Object> deleteTemplate(HttpServletRequest request,
                                               HttpServletResponse response,
                                               @RequestParam("name") String name) {
        BaseResponse<Object> res = new BaseResponse<Object>();
        try {
            String templatePath = PathUtil.getTemplatePath();
            String path = templatePath + name;
            Boolean success = FileUtil.delete(path);
            if (success) {
                res.setData(name);
            } else {
                res.setSuccess(false);
                res.setMessage("删除模板失败");
            }
        } catch (Exception e) {
            res.setSuccess(false);
            res.setMessage("删除模板失败，错误信息：" + e.getMessage());
        }
        return res;
    }

    /**
     * 获取脚本列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/source/list", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public BaseResponse<Object> getSource(HttpServletRequest request,
                                          HttpServletResponse response,
                                          @RequestParam("system") String system) {
        BaseResponse<Object> res = new BaseResponse<Object>();
        try {
            res.setData(Source.list(system));
        } catch (Exception e) {
            res.setSuccess(false);
            res.setMessage("获取模板失败，错误信息：" + e.getMessage());
        }
        return res;
    }

    /**
     * 获取脚本内容
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/source/get", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public BaseResponse<Object> getSource(HttpServletRequest request,
                                          HttpServletResponse response,
                                          @RequestParam("system") String system,
                                          @RequestParam("name") String name) {
        BaseResponse<Object> res = new BaseResponse<Object>();
        try {
            Source source = new Source(system, name);
            String content = source.parseCode();
            res.setData(content);
        } catch (Exception e) {
            res.setSuccess(false);
            res.setMessage("获取模板失败，错误信息：" + e.getMessage());
        }
        return res;
    }

    /**
     * 保存脚本
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/source/save", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public BaseResponse<Object> saveSource(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @RequestParam("system") String system,
                                           @RequestParam("name") String name,
                                           @RequestParam("oldName") String oldName,
                                           @RequestParam("code") String code) {
        BaseResponse<Object> res = new BaseResponse<Object>();
        try {
            if (!name.equals(oldName)) {
                FileUtil.delete(PathUtil.getSourcePath() + system + "/" + oldName + Source.EXT_NAME);
            }
            Source source = new Source(system.trim(), name.trim(), code);
            Boolean flag = source.saveCode();
            res.setSuccess(flag);
        } catch (Exception e) {
            res.setSuccess(false);
            res.setMessage("获取模板失败，错误信息：" + e.getMessage());
        }
        return res;
    }

    /**
     * 获取日志清单
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/log/list", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public BaseResponse<Object> listLog(HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam("date") String date) {
        BaseResponse<Object> res = new BaseResponse<Object>();
        try {
            List<String> logList = Log.listLogPath(date);
            res.setData(logList);
        } catch (Exception e) {
            res.setSuccess(false);
            res.setMessage("未查询到日志文件。");
        }
        return res;
    }

    /**
     * 保存汇总日志
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/log/save/summary", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public BaseResponse<Object> saveSummaryLog(HttpServletRequest request, HttpServletResponse response,
                                               @RequestParam("startTime") String startTime,
                                               @RequestParam("endTime") String endTime,
                                               @RequestParam("content") String content) {
        BaseResponse<Object> res = new BaseResponse<Object>();
        try {
            startTime = startTime.replaceAll(":", "-")
                    .replaceAll(" ", "-");
            endTime = endTime.replaceAll(":", "-")
                    .replaceAll(" ", "-");
            content = content.replaceAll("@", "\n");
            String[] dateArray = startTime.split("-");
            String docPath = PathUtil.getLogPath() + dateArray[0] + "/summary";
            File root = new File(docPath);
            if (!root.exists()) {
                root.mkdirs();
            }
            String path = docPath + "/";
            String filePath = new StringBuilder(path)
                    .append(startTime)
                    .append("_")
                    .append(endTime)
                    .append(".txt").toString();

            filePath = FileUtil.write(filePath, content);
            res.setData(filePath.replace(PathUtil.getRootPath(), ""));
        } catch (Exception e) {
            res.setSuccess(false);
            res.setMessage("保存日志文件失败。异常信息：" + e.getMessage());
        }
        return res;
    }

    /**
     * 获取日志清单
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/log/download", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public BaseResponse<Object> downloadLog(HttpServletRequest request, HttpServletResponse response,
                                            @RequestParam("path") String path) {
        BaseResponse<Object> res = new BaseResponse<Object>();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            //获取输入流
            bis = new BufferedInputStream(new FileInputStream(new File(PathUtil.getRootPath() + path)));
            //获取输出流
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment; filename=" + new String(path.getBytes("utf-8"), "ISO8859-1"));
            bos = new BufferedOutputStream(response.getOutputStream());

            //定义缓冲池大小，开始读写
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
            //刷新缓冲，写出
            bos.flush();

        } catch (Exception e) {
            res.setMessage("文件下载失败。异常消息：" + e.getMessage());
        } finally {
            //关闭流
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    res.setMessage("读取文件流关闭时发生异常。异常消息：" + e.getMessage());
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    res.setMessage("输出文件流关闭时发生异常。异常消息：" + e.getMessage());
                }
            }
        }
        return res;
    }

    private String[] formatJSONArrayToArray(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        String[] array = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            array[i] = jsonArray.getString(i);
        }
        return array;
    }
}
