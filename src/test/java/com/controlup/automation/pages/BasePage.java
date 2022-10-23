package com.controlup.automation.pages;

import com.controlup.automation.config.SuiteConfiguration;
import com.controlup.automation.utils.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Abstract class representation of a Page in the UI. Page object pattern
 */
public abstract class BasePage {
	protected Logger logger;
	protected WebDriver driver;
	protected static final String BASE_URL;

	static {
		try {
			SuiteConfiguration config = new SuiteConfiguration();
			BASE_URL = config.getProperty("site.url");
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load suite configuration", e);
		}
	}

	/**
	 * Conversions flows with related convertor's page Urls
	 */
	public enum ConversionFlow {
		CELSIUS_FAHRENHEIT("temperature/celsius-to-fahrenheit.htm"),
		METERS_FEET("length/meters-to-feet.htm"),
		OUNCES_GRAMS("weight/ounces-to-grams.htm");

		private String url;
		private ConversionFlow(String converterUrl){
			this.url = converterUrl;
		}

		public String getUrl() {
			return this.url;
		}
	}

	/**
	 * Constructor injecting the WebDriver interface
	 * @param driver
	 */
	public BasePage(WebDriver driver) {
		this.driver = driver;
		this.logger = LoggerFactory.getLogger(this.getClass());

		PageFactory.initElements(driver, this);
	}

	/**
	 * Navigate to the Home page via direct link
	 * @return home page instance
	 */
	public HomePage navigateToHomePage(){
		driver.navigate().to(BASE_URL);
	    return new HomePage(driver);
	}

}
