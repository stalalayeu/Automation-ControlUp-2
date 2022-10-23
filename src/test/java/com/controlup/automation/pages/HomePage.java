package com.controlup.automation.pages;

import com.controlup.automation.components.WeatherWidget;
import com.controlup.automation.utils.ElementsUtil;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

    public HomePage(WebDriver driver) {
        super(driver);
    }
    
    @Step("Navigate to Result page with conversion")
    public ResultPage navigateToResultPage(ConversionFlow flow, String amount) {
        driver.navigate().to(BASE_URL + flow.getUrl() + "?val=" + amount);
        return new ResultPage(driver);
    }

}
