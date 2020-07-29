package cn.o.build.omapinsight;

import com.tyrival.selenium.model.BaseScript;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Common extends BaseScript {

    /**
     * 系统登陆
     */
    public Boolean login(String account, String password) {
        this.addLog("输入用户名");
        this.getWebDriver().findElement(By.id("account")).click();
        this.getWebDriver().findElement(By.id("account")).clear();
        this.getWebDriver().findElement(By.id("account")).sendKeys(account);
        this.addLog("输入密码");
        this.getWebDriver().findElement(By.id("password")).clear();
        this.getWebDriver().findElement(By.id("password")).sendKeys(password);
        this.addLog("点击登陆按钮");
        this.getWebDriver().findElement(By.id("submit")).click();
        try {
            WebDriverWait wait = new WebDriverWait(this.getWebDriver(), 5);
            wait.until(ExpectedConditions.urlContains("index.html"));
            this.addLog("登陆成功");
            return SUCCESS;
        } catch (Exception e) {
            this.addLog("登陆失败");
            return FAIL;
        }
    }

    /**
     * 注销
     */
    public void logout() {
        this.addLog("点击注销按钮");
        waitSeconds(2);
        this.getWebDriver().findElement(By.id("login-out")).click();
        this.addLog("注销成功");
    }
}
