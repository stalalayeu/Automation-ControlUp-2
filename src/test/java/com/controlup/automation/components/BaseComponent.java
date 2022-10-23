package com.controlup.automation.components;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.List;

public class BaseComponent implements WebElement {

	protected WebElement holder;
    
	public BaseComponent(WebElement element) {
    	this.holder = element;
    }
    
	@Override
	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		return holder.getScreenshotAs(target);
	}

	@Override
	public void click() {
		holder.click();
	}

	@Override
	public void submit() {
		holder.submit();
	}

	@Override
	public void sendKeys(CharSequence... keysToSend) {
		holder.sendKeys(keysToSend);
	}

	@Override
	public void clear() {
		holder.clear();
	}

	@Override
	public String getTagName() {
		return holder.getTagName();
	}

	@Override
	public String getAttribute(String name) {
		return holder.getAttribute(name);
	}

	@Override
	public boolean isSelected() {
		return holder.isSelected();
	}

	@Override
	public boolean isEnabled() {
		return holder.isEnabled();
	}

	@Override
	public String getText() {
		return holder.getText();
	}

	@Override
	public List<WebElement> findElements(By by) {
		return holder.findElements(by);
	}

	@Override
	public WebElement findElement(By by) {
		return holder.findElement(by);
	}

	@Override
	public boolean isDisplayed() {
		return holder.isDisplayed();
	}

	@Override
	public Point getLocation() {
		return holder.getLocation();
	}

	@Override
	public Dimension getSize() {
		return holder.getSize();
	}

	@Override
	public Rectangle getRect() {
		return holder.getRect();
	}

	@Override
	public String getCssValue(String propertyName) {
		return holder.getCssValue(propertyName);
	}

}
