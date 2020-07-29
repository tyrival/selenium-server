package cn.o.build.omapinsight;

import com.tyrival.selenium.model.BaseScript;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Date;

/**
 * @Description: BiMap模块
 * @Author: Zhou Chenyu
 * @Date: 2018/3/1
 * @Version: V1.0
 * @Modified By:
 * @Modified Date:
 * @Why:
 */
public class BiMap extends BaseScript {

    /**
     * 创建地图BI
     */
    public Boolean create() {

        WebDriverWait wait = new WebDriverWait(this.getWebDriver(), 5);
        WebElement element;
        By by;

        try {
            by = By.xpath("//*[contains(@class, 'main-tab')][4]")
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            this.addLog("进入导航页面");
            this.getWebDriver().findElement(by).click();
            this.addLog("选择数据可视化");
            this.waitSeconds(1);
            by = By.xpath("//*[@class='home-switch'][4]/div[@class='home-content']/a[2]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            this.getWebDriver().findElement(by).click();
            this.addLog("选择地图生成");
        } catch (Exception e) {
            this.addErrorLog("导航页加载失败");
            return FAIL;
        }

        try {
            this.waitSeconds(5);
            wait.until(ExpectedConditions.urlContains("#maphome"));
            this.addLog("进入地图生成页面");
        } catch (Exception e) {
            this.addErrorLog("未能进入地图生成页面");
            return FAIL;
        }

        try {
            by = By.xpath("//*[@id='map-type-list']/div[1]/button");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            this.getWebDriver().findElement(by).click();
            this.addLog("选中流向图");
        } catch (Exception e) {
            this.addErrorLog("未能选中流向图");
            return FAIL;
        }

        try {
            by = By.id("show-data-import-modal");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            this.getWebDriver().findElement(by).click();
            this.addLog("选择数据");
            this.waitSeconds(1);
            by = By.id("server-data");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            this.getWebDriver().findElement(by).click();
            this.addLog("选择在线数据");
            this.waitSeconds(1);
        } catch (Exception e) {
            this.addErrorLog("未能打开数据选择面板");
            return FAIL;
        }

        try {
            by = By.id("user-data-select");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            by = By.xpath("//*[@id='tab-content']/div[1]/div[1]/div[@class='data-card-button']/button[contains(@class, 'data-select')]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            By byDataName = By.xpath("//*[@id='tab-content']/div[1]/div[1]/h4");
            String dataName = this.getWebDriver().findElement(byDataName).getText();
            this.getWebDriver().findElement(by).click();
            this.addLog("选择数据：" + dataName);
            this.waitSeconds(1);
        } catch (Exception e) {
            this.addErrorLog("没有可选择的数据");
            return FAIL;
        }

        try {
            by = By.id("reload-preview");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            this.getWebDriver().findElement(by).click();
            this.addLog("点击运行按钮");
            this.waitSeconds(1);
        } catch (Exception e) {
            this.addErrorLog("未加载出运行按钮");
            return FAIL;
        }

        try {
            by = By.id("save-resource-name");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            element = this.getWebDriver().findElement(by);
            element.click();
            element.clear();
            String mapName = "测试" + new Date().getTime();
            element.sendKeys(mapName);
            this.addLog("输入地图名称：" + mapName);
            by = By.id("map-publish");
            this.getWebDriver().findElement(by).click();
            this.addLog("点击发布按钮");
            this.waitSeconds(1);
        } catch (Exception e) {
            this.addErrorLog("未加载出发布按钮");
            return FAIL;
        }

        try {
            by = By.id("LAY_preview");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            by = By.xpath("//*[@id='LAY_preview']/div[1]/div[1]/button");
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            this.addErrorLog("发布地图成功");
        } catch (Exception e) {
            this.addErrorLog("发布地图失败");
            return FAIL;
        }
        return SUCCESS;
    }
}
