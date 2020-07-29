package cn.o.build.scm;

import com.tyrival.selenium.model.BaseScript;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @Description: 工作流模块
 * @Author: Zhou Chenyu
 * @Date: 2018/3/1
 * @Version: V1.0
 * @Modified By:
 * @Modified Date:
 * @Why:
 */
public class Workflow extends BaseScript {

    /**
     * 创建工作流
     */
    public Boolean create() {

        WebDriverWait wait = new WebDriverWait(this.getWebDriver(), 30);
        WebElement element;
        By by;
        String flowName = "Selenium脚本测试" + java.lang.System.currentTimeMillis();

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("main-content-menu")));
            element = this.getWebDriver().findElement(By.xpath("//ul[@id='main-content-menu']/li[2]/div[2]"));
            if (!"业务协同".equals(element.getText())) {
                this.addErrorLog("未找到业务协同模块入口");
                return FAIL;
            }
            this.addLog("进入模块索引页面");
            element.click();
        } catch (Exception e) {
            this.addErrorLog("模块索引页加载失败");
            return FAIL;
        }

        try {
            by = By.id("submitCreate");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            this.addLog("进入业务协同模块");
            this.getWebDriver().findElement(by).click();
        } catch (Exception e) {
            this.addErrorLog("未能进入事件功能界面");
            return FAIL;
        }

        try {
            this.addLog("新建工作流...");
            Integer tryTime = 1;
            Integer maxTime = 5;
            popCreate(tryTime, maxTime);
            this.addLog("打开新建工作流弹窗");
        } catch (Exception e) {
            this.addErrorLog("新建工作流弹窗打开失败");
            return FAIL;
        }

        try {
            by = By.id("classify_new");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            by = By.xpath("//tbody[@id='affair_table_body']/tr[2]/td[2]/div/div[@class='typeDetail'][1]");
            element = this.getWebDriver().findElement(by);
            if (!"非法场所".equals(element.getText())) {
                this.addErrorLog("第一个流程类别不是非法场所");
                return FAIL;
            }
            element.click();
            this.addLog("选择事项类别为非法场所");
        } catch (Exception e) {
            this.addErrorLog("事项类别列表为空");
            return FAIL;
        }

        try {
            this.addLog("打开事件内容表单...");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addNewTable")));
            waitSeconds(2);
            element = this.getWebDriver().findElement(By.id("flowTitle"));
            this.addLog("输入流程主题：" + flowName);
            element.click();
            element.clear();
            element.sendKeys(flowName);
            this.addLog("事件内容表单加载完成");
        } catch (Exception e) {
            this.addErrorLog("未能打开事件内容表单");
            return FAIL;
        }

        try {
            by = By.id("flowTitle");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            element = this.getWebDriver().findElement(By.xpath("//tbody[@id='tbobyFlow']/tr[3]/td[2]/select"));
            this.addLog("选择事件来源");
            element.click();
            new Select(element).selectByVisibleText("电脑端");
        } catch (Exception e) {
            this.addErrorLog("事件来源Select未能加载成功");
            return FAIL;
        }

        try {
            element = this.getWebDriver().findElement(By.xpath("//tbody[@id='tbobyFlow']/tr[3]/td[4]/select"));
            element.click();
            this.addLog("选择紧急程度");
            element.click();
            new Select(element).selectByVisibleText("一般");
        } catch (Exception e) {
            this.addErrorLog("紧急程度Select未能加载成功");
            return FAIL;
        }

        // 选择坐标
        try {
            this.getWebDriver().findElement(By.id("coordinateId")).click();
            by = By.id("lnglatBox");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            this.addLog("地图标注弹窗加载完成");
        } catch (Exception e) {
            this.addErrorLog("地图标注弹窗加载失败");
            return FAIL;
        }

        try {
            String x = "8860158.4271508";
            String y = "4610195.9434148";
            this.addLog("选择坐标: " + x + "," + y);
            ((JavascriptExecutor) this.getWebDriver()).executeScript("var ele=arguments[0];ele.innerHTML='x：" + x + "'", this.getWebDriver().findElement(By.id("xBox")));
            ((JavascriptExecutor) this.getWebDriver()).executeScript("var ele=arguments[0];ele.innerHTML='y：" + y + "'", this.getWebDriver().findElement(By.id("yBox")));
            by = By.id("mapCheck");
            wait.until(ExpectedConditions.elementToBeClickable(by));
            this.getWebDriver().findElement(by).click();
            this.addLog("确定坐标");
            waitSeconds(1);
        } catch (Exception e) {
            this.addErrorLog("地图坐标选择功能出错");
            return FAIL;
        }

        try {
            this.addLog("填写位置描述");
            by = By.xpath("//tbody[@id='tbobyFlow']/tr[5]/td[2]/input");
            wait.until(ExpectedConditions.elementToBeClickable(by));
            element = this.getWebDriver().findElement(by);
            element.click();
            element.clear();
            element.sendKeys("selenium位置描述");
        } catch (Exception e) {
            this.addErrorLog("位置描述组件错误");
            return FAIL;
        }

        try {
            this.addLog("填写事件详情");
            element = this.getWebDriver().findElement(By.xpath("//tbody[@id='tbobyFlow']/tr[6]/td[2]/textarea"));
            element.click();
            element.clear();
            element.sendKeys("selenium事件详情");
        } catch (Exception e) {
            this.addErrorLog("事件详情组件错误");
            return FAIL;
        }

        try {
            this.addLog("提交");
            this.getWebDriver().findElement(By.id("submitEvent")).click();
            this.addLog("提交");
        } catch (Exception e) {
            this.addErrorLog("提交按钮加载错误");
            return FAIL;
        }

        try {
            by = By.xpath("//select[@id='selectId']/option[1]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            element = this.getWebDriver().findElement(By.id("selectId"));
            new Select(element).selectByIndex(1);
            this.addLog("选择路由");
            waitSeconds(2);
        } catch (Exception e) {
            this.addErrorLog("路由列表加载失败");
            return FAIL;
        }

        try {
            by = By.xpath("//select[@id='rangeSelect']/option[1]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            element = this.getWebDriver().findElement(By.id("rangeSelect"));
            new Select(element).selectByIndex(0);
            this.getWebDriver().findElement(By.id("personAdd")).click();
            this.addLog("选择人员");
            waitSeconds(2);
        } catch (Exception e) {
            this.addErrorLog("人员列表加载失败");
            return FAIL;
        }

        try {
            this.getWebDriver().findElement(By.id("selectUp")).click();
            by = By.xpath("//select[@id='selectPerson']/option[1]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            this.addLog("提交路由信息");
            waitSeconds(3);
        } catch (Exception e) {
            this.addErrorLog("提交路由功能错误");
            return FAIL;
        }

        try {
            by = By.xpath("//tbody[@id='table-event-submit']/tr[1]/td[3]/a");
            element = this.getWebDriver().findElement(by);
            String name = element.getText();
            name = name.substring(0, name.length() - 3);
            if (flowName.indexOf(name) != 0) {
                this.addErrorLog("创建工作流失败");
                return FAIL;
            }
            this.addLog("创建工作流成功");
        } catch (Exception e) {
            this.addErrorLog("创建工作流失败");
            return FAIL;
        }
        return SUCCESS;
    }

    /**
     * 审批工作流
     */
    public Boolean approve() {
        WebDriverWait wait = new WebDriverWait(this.getWebDriver(), 10);
        WebElement element;
        By by;
        String flowId;

        try {
            by = By.id("navbar-user");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            this.getWebDriver().findElement(by).click();
            this.addLog("点击待办事项按钮");
        } catch (Exception e) {
            this.addErrorLog("待办事项按钮未加载成功");
            return FAIL;
        }

        try {
            by = By.id("table-event-todo");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            this.getWebDriver().findElement(by);
            this.addLog("进入待办事件页面");
        } catch (Exception e) {
            this.addErrorLog("待办事件页面未加载成功");
            return FAIL;
        }

        try {
            by = By.xpath("//tbody[@id='table-event-todo']/tr[1]/td[2]/a");
            flowId = this.getWebDriver().findElement(by).getText();
            by = By.xpath("//tbody[@id='table-event-todo']/tr[1]/td[9]/a");
            wait.until(ExpectedConditions.elementToBeClickable(by));
            this.getWebDriver().findElement(by).click();
            this.addLog("点击流程ID为" + flowId + "的[处理]按钮");
        } catch (Exception e) {
            this.addErrorLog("待办事件页面中没有事件实例");
            return FAIL;
        }

        try {
            by = By.id("event_info");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            waitSeconds(1);
            this.addLog("打开事件详情弹窗");
        } catch (Exception e) {
            this.addErrorLog("事件详情弹窗打开失败");
            return FAIL;
        }

        try {
            by = By.id("infoDeal");
            wait.until(ExpectedConditions.elementToBeClickable(by));
            element = this.getWebDriver().findElement(By.id("infoDeal"));
            this.addLog("点击[提交]按钮");
            element.click();
            waitSeconds(3);
        } catch (Exception e) {
            this.addErrorLog("【提交】按钮未加载成功");
            return FAIL;
        }

        try {
            by = By.id("event_upload");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            waitSeconds(1);
            this.addLog("处理意见窗口出现");
        } catch (Exception e) {
            this.addErrorLog("处理意见窗口加载失败");
            return FAIL;
        }

        try {
            this.getWebDriver().findElement(By.id("comment")).click();
            this.getWebDriver().findElement(By.id("comment")).clear();
            this.getWebDriver().findElement(By.id("comment")).sendKeys("同意");
            this.addLog("输入处理意见");
        } catch (Exception e) {
            this.addErrorLog("处理意见窗口加载失败");
            return FAIL;
        }

        try {
            this.addLog("点击[下一步]按钮");
            this.getWebDriver().findElement(By.id("uploadNext")).click();
        } catch (Exception e) {
            this.addErrorLog("【下一步】按钮加载失败");
            return FAIL;
        }

        try {
            this.addLog("路由选择窗口弹出");
            by = By.id("select_new");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            waitSeconds(1);
            this.addLog("选择路由[办结]");
        } catch (Exception e) {
            this.addErrorLog("路由场口加载失败");
            return FAIL;
        }

        try {
            this.addLog("路由选择窗口弹出");
            by = By.id("select_new");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            waitSeconds(1);
            this.addLog("选择路由[办结]");
        } catch (Exception e) {
            this.addErrorLog("路由场口加载失败");
            return FAIL;
        }

        try {
            this.addLog("路由选择窗口弹出");
            by = By.id("select_new");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            waitSeconds(1);
            this.addLog("选择路由[办结]");
        } catch (Exception e) {
            this.addErrorLog("路由弹窗加载失败");
            return FAIL;
        }

        try {
            by = By.xpath("//select[@id='selectId']/option[text()='办结']");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            element = this.getWebDriver().findElement(By.id("selectId"));
            new Select(element).selectByVisibleText("办结");
            this.addLog("选择路由[办结]");
        } catch (Exception e) {
            this.addErrorLog("路由节点列表加载失败");
            return FAIL;
        }

        try {
            this.getWebDriver().findElement(By.id("selectUp")).click();
            this.waitSeconds(3);
            this.addLog("提交路由");
        } catch (Exception e) {
            this.addErrorLog("[提交]按钮加载失败");
            return FAIL;
        }

        try {
            by = By.xpath("//tbody[@id='table-event-todo']/tr[1]/td[2]/a");
            String currentFlowId = this.getWebDriver().findElement(by).getText();
            if (currentFlowId.equals(flowId)) {
                this.addErrorLog("流程审批失败");
            }
            this.addLog("流程审批成功");
        } catch (Exception e) {
            this.addLog("流程审批成功");
            return FAIL;
        }
        return SUCCESS;
    }

    // 尝试打开创建工作流窗口，超过maxTime次后停止
    private void popCreate(Integer tryTime, Integer maxTime) throws Exception {
        if (tryTime == maxTime) {
            throw new Exception("新建流程窗口打开失败");
        }
        waitSeconds(2);
        if (!this.getWebDriver().findElement(By.id("classify_new")).isDisplayed()) {
            tryTime++;
            this.addLog("尝试打开流程窗口");
            this.getWebDriver().findElement(By.id("submitCreate")).click();
            waitSeconds(2);
            popCreate(tryTime, maxTime);
        }
        return;
    }
}
