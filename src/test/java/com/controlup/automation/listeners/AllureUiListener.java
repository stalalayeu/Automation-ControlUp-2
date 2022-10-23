package com.controlup.automation.listeners;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.controlup.automation.uitest.BaseUiTest;

public class AllureUiListener extends TestListenerAdapter {

    @Override
    public void onTestFailure(ITestResult result) {
        if (result.getMethod().getGroups().length == 0) {
            Object currentClass = result.getInstance();
            WebDriver driver = ((BaseUiTest) currentClass).getDriver();
            byte[] srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            saveScreenshot(srcFile);
        }
    }

    @Attachment(value = "Failed test screenshot", type = "image/png")
    private byte[] saveScreenshot(byte[] screenshot) {
        return screenshot;
    }
}
