package com.controlup.automation.uitest;

import com.controlup.automation.pages.HomePage;
import com.controlup.automation.pages.BasePage.ConversionFlow;
import io.qameta.allure.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpressTest extends BaseUiTest {

    @DataProvider(name = "ParameterizedConversion")
    public Object[][] data() {
        return new Object[][] {
                { ConversionFlow.CELSIUS_FAHRENHEIT, "100", "100°C= 212.0000°F"},
                { ConversionFlow.METERS_FEET, "50", "50m= 164ft 0.5039400in"},
                { ConversionFlow.OUNCES_GRAMS, "80", "80oz= 2267.962g"}
        };
    }

    @Feature("Metric-conversions")
    @Story("Temperature conversion")
    @Severity(SeverityLevel.NORMAL)
    @Test(description = "Celsius To Fahrenheit conversion")
    public void testSimpleConversion() {
     	HomePage homePage = new HomePage(driver);

     	String result = homePage
                .navigateToResultPage(ConversionFlow.CELSIUS_FAHRENHEIT, "100")
                .getConversionResult();

     	System.out.println(String.format("Result for conversion '%1s' : %2s",
                ConversionFlow.CELSIUS_FAHRENHEIT, result));
    }

    @Feature("Metric-conversions")
    @Story("Various conversion")
    @Severity(SeverityLevel.NORMAL)
    @Parameters({"flow", "amount"})
    @Test(description = "Various parameterized conversion", dataProvider = "ParameterizedConversion")
    public void testParameterizedConversion(ConversionFlow flow, String amount, String expected) {
        HomePage homePage = new HomePage(driver);

        String result = homePage
                .navigateToResultPage(flow, amount)
                .getConversionResult();

        System.out.println(String.format("Result for conversion '%1s' : %2s", flow, result));
        assertThat(result).isEqualTo(expected);
    }

}
