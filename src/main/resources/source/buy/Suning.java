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
        this.getWebDriver().get();
        this.getWebDriver().findElement(By.id("buyNowAddCart")).click();
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
