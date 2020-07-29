package cn.o.build.scm;

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
        this.getWebDriver().findElement(By.id("usernameid")).click();
        this.getWebDriver().findElement(By.id("usernameid")).clear();
        this.getWebDriver().findElement(By.id("usernameid")).sendKeys(account);
        this.addLog("输入密码");
        this.getWebDriver().findElement(By.id("passwordid")).clear();
        this.getWebDriver().findElement(By.id("passwordid")).sendKeys(password);
        this.addLog("输入验证码");
        this.getWebDriver().findElement(By.id("randid")).clear();
        this.getWebDriver().findElement(By.id("randid")).sendKeys("1234");
        this.addLog("点击登陆按钮");
        this.getWebDriver().findElement(By.id("btnsubmit")).click();
        try {
            WebDriverWait wait = new WebDriverWait(this.getWebDriver(), 5);
            wait.until(ExpectedConditions.titleContains("社会管理综合治理信息平台"));
            WebElement ele = this.getWebDriver().findElement(By.id("btn-logout"));
            this.addLog("登陆成功");
            return SUCCESS;
        } catch (Exception e) {
            WebElement ele = this.getWebDriver().findElement(By.id("info_prompt"));
            this.addErrorLog("登陆失败" + ele.getText());
            return FAIL;
        }

    }

    /**
     * 注销
     */
    public void logout() {
        this.addLog("点击注销按钮");
        waitSeconds(2);
        this.getWebDriver().findElement(By.id("btn-logout")).click();
        this.addLog("注销成功");
    }
}
