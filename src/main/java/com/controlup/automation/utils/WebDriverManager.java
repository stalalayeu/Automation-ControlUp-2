package com.controlup.automation.utils;

import io.github.bonigarcia.wdm.config.DriverManagerType;
import io.github.bonigarcia.wdm.managers.ChromeDriverManager;
import io.github.bonigarcia.wdm.managers.EdgeDriverManager;
import io.github.bonigarcia.wdm.managers.FirefoxDriverManager;
import io.github.bonigarcia.wdm.managers.InternetExplorerDriverManager;
import org.openqa.selenium.SessionNotCreatedException;
import ru.stqa.selenium.factory.WebDriverPool;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.UnreachableBrowserException;

import com.controlup.automation.config.SuiteConfiguration;

public class WebDriverManager {

	private static Optional<String> gridURL;
	
    public static void setupWebDriver(SuiteConfiguration suiteConfiguration) throws IOException {
    	WebDriverManager.gridURL = Optional.of(suiteConfiguration.getProperty("grid.url"));
 
    	switch (suiteConfiguration.getCapabilities().getBrowserName()) {
            case "firefox":
                FirefoxDriverManager.getInstance(DriverManagerType.FIREFOX)
                    .driverVersion(suiteConfiguration.getProperty("firefox-driver.version")).setup();
                break;
            case "MicrosoftEdge":
                EdgeDriverManager.getInstance(DriverManagerType.EDGE)
                    .driverVersion(suiteConfiguration.getProperty("edge-driver.version")).setup();
                break;
            case "internet explorer":
                InternetExplorerDriverManager.getInstance(DriverManagerType.IEXPLORER)
                    .driverVersion(suiteConfiguration.getProperty("ie-driver.version")).arch32().setup();
                break;
            case "chrome":
            default:
                ChromeDriverManager.getInstance(DriverManagerType.CHROME)
                    .driverVersion(suiteConfiguration.getProperty("chrome-driver.version")).setup();
        }
    }
    
    public static WebDriver getDriver(Capabilities capabilities) {
    	capabilities = checkHeadless(capabilities);
    	try {
			return WebDriverPool.DEFAULT.getDriver(new URL(gridURL.orElse("")), capabilities);
		} catch (MalformedURLException | UnreachableBrowserException | SessionNotCreatedException e) {
			return WebDriverPool.DEFAULT.getDriver(capabilities);
		}
    }

    public static void dismissDriver(WebDriver driver) {
    	WebDriverPool.DEFAULT.dismissDriver(driver);
    }    
    
    public static void dismissAll() {
    	WebDriverPool.DEFAULT.dismissAll();
    }
    
    private static Capabilities checkHeadless(Capabilities caps) {
    	if ("chrome".equalsIgnoreCase(caps.getBrowserName()) && Boolean.TRUE.equals(caps.getCapability("headless"))) {
            Map<String, Object> chromeArgs= new HashMap<>();
            chromeArgs.put("args", new String[] {"--headless" ,"--disable-gpu" ,"--window-size=1920,1200"});
            DesiredCapabilities capabilitiesExtra = new DesiredCapabilities();
            capabilitiesExtra.setCapability(ChromeOptions.CAPABILITY, chromeArgs);
            caps = caps.merge(capabilitiesExtra);
    	}
    	return caps;
    }
}
