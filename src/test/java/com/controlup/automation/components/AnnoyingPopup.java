package com.controlup.automation.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AnnoyingPopup extends BaseComponent {
    public static final String DEFAULT_SELECTOR = "div.ui-newuser-layer-dialog";
    private static final String CLOSE_ICON = "a.close-layer";
    
    public AnnoyingPopup(WebElement holder) {
    	super(holder);
    }

    public AnnoyingPopup(WebDriver driver) {
    	super(driver.findElement(By.cssSelector(DEFAULT_SELECTOR)));
    }    
    
    public void close() {
    	holder.findElement(By.cssSelector(CLOSE_ICON)).click();
    }
}
