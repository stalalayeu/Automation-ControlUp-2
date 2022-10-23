package com.controlup.automation.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ResultPage extends BasePage {
    @FindBy(css = "section#result p#answer")
    private WebElement conversionResult;

    public ResultPage(WebDriver driver) {
        super(driver);
    }

    @Step("Get conversion operation result")
    public String getConversionResult() {
        return  conversionResult.getText();
    }
}
