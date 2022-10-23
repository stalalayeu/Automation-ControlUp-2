package com.controlup.automation.utils;

import com.jayway.awaitility.core.ConditionTimeoutException;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.ui.Select;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ElementsUtil {

    private static final int DEFAULT_EXPLICIT_WAIT = 10;

    private static final Function<Supplier<WebElement>, Boolean> tryElement = r -> Try.of(() -> r.get().isDisplayed()).getOrElse(() -> false);

    public static boolean isClassPresent(String className, WebElement element) {
        return Arrays.asList(element.getAttribute("class").split(" ")).contains(className);
    }

    public static boolean waitForClassPresent(String regexp, WebElement element, int seconds) {
        try {
            waitFor(seconds).until(() -> isClassPresent(regexp, element));
        } catch (TimeoutException | ConditionTimeoutException ex) {
            return false;
        }
        return true;
    }

    public static boolean waitForClassNotPresent(String regexp, WebElement element, int seconds) {
        try {
            waitFor(seconds).until(() -> !isClassPresent(regexp, element));
        } catch (TimeoutException | ConditionTimeoutException ex) {
            return false;
        }
        return true;
    }

    public static boolean isAlertPresent(WebDriver driver) {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    public static boolean isTextPresent(WebElement element, String text) {
        try {
            return element.getText().contains(text);
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isDisplayed(Supplier<WebElement> supplier) {
        return Try.of(() -> supplier.get().isDisplayed()).getOrElse(false);
    }

    public static boolean isDisplayed(WebDriver driver, By by) {
        boolean isElementDisplayed;
        try {
            isElementDisplayed = driver.findElement(by).isDisplayed();
        } catch (Exception e) {
            isElementDisplayed = false;
        }
        //  logger.debug("Is element " + by + " displayed - " + isElementDisplayed);
        return isElementDisplayed;
    }

    public static Option<WebElement> getIfDisplayed(WebElement parentElem, By by) {
        try {
            WebElement elem = parentElem.findElement(by);
            if (elem.isDisplayed()) {
                return Option.of(elem);
            }
        } catch (Exception e) {
            // logger.debug("Element '" + by + "' is NOT displayed");
        }

        return Option.none();
    }

    public static Option<WebElement> getIfDisplayed(WebDriver driver, By by) {
        try {
            WebElement elem = driver.findElement(by);
            if (elem.isDisplayed()) {
                return Option.of(elem);
            }
        } catch (Exception e) {
            //  logger.debug("Element '" + by + "' is NOT displayed");
        }
        return Option.none();
    }

    public static boolean isDisplayed(WebElement parentElement, By by, int seconds) {
        return Try.of(() -> {
            waitFor(seconds).until(() -> isDisplayed(parentElement.findElement(by)));
            return true;
        }).getOrElse(() -> false);
    }

    public static boolean isDisplayed(WebElement parentElement, By by) {
        return tryElement.apply(() -> parentElement.findElement(by));
    }

    public static boolean isAllDisplayed(List<WebElement> elements) {
        return isAllDisplayed(elements.toArray(new WebElement[0]));
    }

    public static boolean isAllDisplayed(WebElement... elements) {
        return Stream.of(elements)
                .allMatch(b -> tryElement.apply(() -> b));
    }

    public static boolean isAllDisplayed(WebDriver driver, By... selectors) {
        return Stream.of(selectors)
                .allMatch(b -> tryElement.apply(() -> driver.findElement(b)));
    }

    public static boolean isDisplayed(WebElement element) {
        return tryElement.apply(() -> element);
    }

    public static boolean isDisplayed(WebElement element, int seconds) {
        return Try.of(() -> {
            waitFor(seconds).until(() -> isDisplayed(element));
            return true;
        }).getOrElse(() -> false);
    }

    public static boolean isAnyDisplayed(List<WebElement> elements) {
        return isAnyDisplayed(elements.toArray(new WebElement[0]));
    }

    public static boolean isAnyDisplayed(WebElement... elements) {
        return Stream.of(elements)
                .anyMatch(e -> tryElement.apply(() -> e));
    }

    public static boolean isAnyDisplayed(WebDriver driver, By... selectors) {
        return Stream.of(selectors)
                .anyMatch(b -> tryElement.apply(() -> driver.findElement(b)));
    }

    public static boolean isElementPresented(WebDriver driver, By by) {
        boolean isElementPresented = !driver.findElements(by).isEmpty();
        return isElementPresented;
    }

    /**
     * Wait until an element is no longer attached to the DOM.
     * This modified version from Selenium ExpectedConditions, but without stupid ExpectedCondition<Boolean> as return
     *
     * @param element The element to wait for.
     * @return false is the element is still attached to the DOM, true otherwise.
     */
    public static boolean isStale(WebElement element) {
        try {
            // Calling any method forces a staleness check
            element.isEnabled();
            return false;
        } catch (StaleElementReferenceException expected) {
            return true;
        }
    }

    public static boolean isClickable(WebElement element) {
        try {
            return element.isDisplayed() && element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public static void waitForVisible(WebElement element) {
        waitForVisible(element, DEFAULT_EXPLICIT_WAIT);
    }

    public static void waitForVisible(WebElement element, By childElem, int seconds) {
        waitFor(seconds).until(() -> isDisplayed(element.findElement(childElem)));
    }

    public static void waitForVisible(WebDriver driver, By by) {
        waitForVisible(driver, by, DEFAULT_EXPLICIT_WAIT);
    }

    public static WebElement waitForVisible(WebElement element, int seconds) {
        waitFor(seconds).until(() -> isDisplayed(element));
        return element;
    }

    public static void waitForVisible(WebDriver driver, By by, int seconds) {
        waitFor(seconds).until(() -> isDisplayed(driver, by));
    }

    public static WebElement waitFor(WebElement parentElem, By by, int seconds) {
        return waitFor(seconds)
                .withException("WebElement with selector [" + by + "] did not appear in " + seconds + "s")
                .untilGot(() -> parentElem.findElement(by));
    }

    public static WebElement waitFor(Supplier<WebElement> supplier, int seconds) {
        return waitFor(seconds)
                .withException("WebElement with did not appear in " + seconds + "s")
                .untilGot(supplier::get);
    }

    public static WebElement waitFor(WebDriver driver, By by, int seconds) {
        return waitFor(seconds)
                .withException("WebElement with selector [" + by + "] did not appear in " + seconds + "s")
                .untilGot(() -> driver.findElement(by));
    }

    public static String getDataValue(WebElement tokenElement) {
        String attribute = tokenElement.getAttribute("data-value");
        return (attribute != null) ? attribute : "";
    }


    public static void waitForNotVisible(WebDriver driver, By selector, int seconds) {
        waitFor(seconds).until(() -> !isDisplayed(driver, selector));
    }

    public static void waitForNotVisible(WebElement element) {
        waitForNotVisible(element, DEFAULT_EXPLICIT_WAIT);
    }

    public static void waitForNotVisible(WebElement element, int seconds) {
        waitFor(seconds).until(() -> !isDisplayed(element));
    }

    public static boolean elementHasStoppedMoving(WebElement element) {
        Point initialLocation = ((Locatable) element).getCoordinates().inViewPort();
        sleepUninterruptibly(250, MILLISECONDS);
        Point finalLocation = ((Locatable) element).getCoordinates().inViewPort();
        return initialLocation.equals(finalLocation);
    }

    public static boolean noNewElements(WebDriver driver, By selector) {
        int initialSize = driver.findElements(selector).size();
        sleepUninterruptibly(1000, MILLISECONDS);
        int newSize = driver.findElements(selector).size();
        return initialSize == newSize;
    }

    public static boolean isClickable(WebDriver driver, By by) {
        try {
            WebElement webElement = driver.findElement(by);
            return webElement.isDisplayed() && webElement.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public static Document getMarkup(String rawText) {
        return Jsoup.parse(rawText);
    }

    public static String getTextUsingJquery(WebDriver driver, String cssSelector) {
        return ((JavascriptExecutor) driver).executeScript("return $(\"" + cssSelector + "\").text()").toString();
    }

    public static void waitForAjaxCompleted(WebDriver driver, int seconds) {
        waitFor(seconds).until(() -> (Boolean) ((JavascriptExecutor) driver).executeScript("return jQuery.active == 0"));
    }

    private static Await waitFor(int seconds) {
        return Await.waitFor(seconds).ignoreException(NoSuchElementException.class);
    }

    public static String serialize(WebDriver driver, WebElement element) {
        return ((JavascriptExecutor) driver).executeScript("return arguments[0].outerHTML;", element).toString();
    }

    public static Color parse(String input) {
        Pattern c = Pattern.compile("rgba*\\( *([0-9]+), *([0-9]+), *([0-9]+),.*\\)");
        Matcher m = c.matcher(input);
        if (m.matches()) {
            return new Color(Integer.valueOf(m.group(1)),  // r
                    Integer.valueOf(m.group(2)),  // g
                    Integer.valueOf(m.group(3))); // b
        }
        return null;
    }

    public static void assertNot(String errorMessage, @SuppressWarnings("rawtypes") Callable callable) {
        try {
            Object value = callable.call().toString();
            throw new Exception(errorMessage + ": Got value [" + value + "]");
        } catch (AssertionError | TimeoutException | NoSuchElementException | InvalidElementStateException e) {
            //    logger.info("Conditions were not met: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties setProperty(String key, Object val) {
        Properties tableProperties = new Properties();
        tableProperties.put(key, val.toString());
        return tableProperties;
    }

    /**
     * 3 seconds Waiter for animation to stop
     *
     * @param element - who's moving
     */
    public static void waitUntilElementStoppedMoving(WebElement element) {
        Await.waitFor(3)
                .until(() -> elementHasStoppedMoving(element));
    }

    public static void dragAndDropElements(WebDriver driver, WebElement from, WebElement to) {
        new Actions(driver).clickAndHold(from)
                .moveToElement(to)
                .release(to)
                .build().perform();
    }

    public static void dragAndDropElement(WebDriver driver, WebElement element, int offsetX, int offsetY) {
        new Actions(driver).clickAndHold(element)
                .moveByOffset(offsetX, offsetY)
                .release(element)
                .build().perform();
    }

    public static void dragAndDropElementMobile(WebDriver driver, WebElement element, int offsetX, int offsetY) {
        new Actions(driver).moveToElement(element)
                .dragAndDropBy(element, offsetX, offsetY)
                .build().perform();
    }

    public static boolean selectTextIgnoreCase(WebElement selectElement, String textToSelect) {
        WebElement option = new Select(selectElement).getOptions().stream().filter(element -> element.getText().trim().toLowerCase().equals(textToSelect.toLowerCase())).findFirst().orElse(null);
        if (option != null) {
            option.click();
            return true;
        }
        return false;
    }

    public static boolean selectByValue(WebElement selectElement, String value) {
        try {
            new Select(selectElement).selectByValue(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void mouseOverElement(WebDriver driver, WebElement element) {//Mouseover the top left corner
        Actions actions = new Actions(driver);
        actions.moveToElement(element);
        actions.perform();
    }

    public static void mouseOverElementCenter(WebDriver driver, WebElement element) {//Mouseover from the center of the element
        Actions actions = new Actions(driver);
        actions.moveToElement(element, 0, 0);
        actions.perform();
    }
}
