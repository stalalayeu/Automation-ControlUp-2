package com.controlup.automation.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class WeatherWidget extends BaseComponent {
    public static final String DEFAULT_SELECTOR = "#ctl00_MainContentHolder_PanelWeather2";

    private static String WIND      = ".weatherapi-weather-todays-stats-location div.weatherapi_descr";
    private static String PRECIP    = ".weatherapi-weather-todays-stats-location span.weatherapi_descr";
    private static String PRESSURE  = ".weatherapi-weather-todays-stats-location div.awe_desc";
    private static String TEMP      = "div.weatherapi-weather-current-temp";

    private WebElement wind;
    private WebElement precip;
    private WebElement pressure;
    private WebElement temp;

    public WeatherWidget(WebElement holder) {
    	super(holder);
    	this.wind = holder.findElement(By.cssSelector(WIND));
    	this.precip = holder.findElement(By.cssSelector(PRECIP));
        this.pressure = holder.findElement(By.cssSelector(PRESSURE));
        this.temp = holder.findElement(By.cssSelector(TEMP));
    }

    public String getTemperature(){
    	return temp.getText();
    }

    public float getTemperatureFloat(){
        return Float.parseFloat(getTemperature().split(" ")[0]);
    }
}
