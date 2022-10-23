package com.controlup.automation.uitest;

import com.controlup.automation.components.AnnoyingPopup;
import com.controlup.automation.listeners.AllureUiListener;
import com.controlup.automation.utils.ElementsUtil;
import com.controlup.automation.BaseTest;
import com.controlup.automation.config.SuiteConfiguration;
import com.controlup.automation.utils.CookieManager;
import com.controlup.automation.utils.PropertiesLoader;
import com.controlup.automation.utils.WebDriverManager;

import io.qameta.allure.Step;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Base class for TestNG-based UI test classes
 */
@Listeners({AllureUiListener.class})
public class BaseUiTest  extends BaseTest{

    protected static URL gridHubUrl = null;
    protected static String baseUrl;
    protected static Capabilities capabilities;
    protected static PropertiesLoader propertiesLoader;
    protected static String testCaseId;
    protected static int testRunId;
    protected static String msg;
    protected Properties env;
    protected WebDriver driver;

    @BeforeSuite(alwaysRun = true)
    public void initTestSuite() throws IOException {
        SuiteConfiguration config = new SuiteConfiguration();
        WebDriverManager.setupWebDriver(config);
        
        baseUrl = config.getProperty("site.url");
        capabilities = config.getCapabilities();
        propertiesLoader = new PropertiesLoader();

        env = new Properties();
        env.setProperty("Base URL", baseUrl);
    }

    @BeforeMethod
    @Step("Browser initialization")
    public void initWebDriver(Method method) {
        driver = WebDriverManager.getDriver(capabilities);
        driver.manage().window().maximize();
        driver.get(baseUrl);
        
        CookieManager.loadCookies(driver);
        
        By annoyingBy = By.cssSelector(AnnoyingPopup.DEFAULT_SELECTOR);
        try { 
        	ElementsUtil.waitForVisible(driver, annoyingBy, 1);
        }catch (com.jayway.awaitility.core.ConditionTimeoutException e) {}
        
        if (driver.findElements(annoyingBy).size() > 0) {
            new AnnoyingPopup(driver).close();
            ElementsUtil.waitForNotVisible(driver, annoyingBy, 2);        	
        }
    }

    @AfterSuite(alwaysRun = true)
    @Step("Browser cleanup")
    public void tearDown() {
        WebDriverManager.dismissAll();

        File file = Paths.get(System.getProperty("user.dir"), "/target/allure-results").toAbsolutePath().toFile();
        if (!file.exists()) {
            logger.info("Created dirs: " + file.mkdirs());
        }
        try (FileWriter out = new FileWriter("./target/allure-results/environment.properties")) {
            env.store(out, "Environment variables for report");
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

}
