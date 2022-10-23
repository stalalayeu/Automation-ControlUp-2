package com.controlup.automation.config;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Loads test suite configuration from resource files.
 */
public class SuiteConfiguration {

    private static final String APPLICATION_PROPERTIES = "/application.properties";

    private Properties properties;

    public SuiteConfiguration() throws IOException {
        this(System.getProperty("application.properties", APPLICATION_PROPERTIES));
    }

    public SuiteConfiguration(String fromResource) throws IOException {
        properties = new Properties();
        properties.load(SuiteConfiguration.class.getResourceAsStream(fromResource));
    }

    public Capabilities getCapabilities() throws IOException {
        String capabilitiesFile = properties.getProperty("capabilities");

        Properties capsProps = new Properties();
        capsProps.load(SuiteConfiguration.class.getResourceAsStream(capabilitiesFile));

        DesiredCapabilities capabilities = new DesiredCapabilities();
        for (String name : capsProps.stringPropertyNames()) {
            String value = capsProps.getProperty(name);
            if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
                capabilities.setCapability(name, Boolean.valueOf(value));
            } else if (value.startsWith("file:")) {
                capabilities.setCapability(name, new File(".", value.substring(5)).getCanonicalFile().getAbsolutePath());
            } else {
                capabilities.setCapability(name, value);
            }

            String deviceName = properties.getProperty("device.name");
            if (!deviceName.isEmpty()) {
                Map<String, Object> deviceMetrics = new HashMap<>();
                deviceMetrics.put("width", Integer.valueOf(properties.getProperty("device.width")));
                deviceMetrics.put("height", Integer.valueOf(properties.getProperty("device.height")));
                deviceMetrics.put("pixelRatio", Integer.valueOf(properties.getProperty("pixel.ratio")));

                Map<String, Object> mobileEmulation = new HashMap<>();
                mobileEmulation.put("deviceMetrics", deviceMetrics);
                mobileEmulation.put("userAgent", properties.getProperty("user.agent"));
                Map<String, Object> chromeOptions = new HashMap<>();
                chromeOptions.put("mobileEmulation", mobileEmulation);
                capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
            }
        }
       
        return capabilities;
    }

    public Capabilities getCapabilities_() throws IOException {
        String capabilitiesFile = properties.getProperty("capabilities")
        		.replace(".capabilities", ".json");

        StringBuilder sBuilder = new StringBuilder();
        new BufferedReader(new InputStreamReader(SuiteConfiguration.class
        	.getResourceAsStream(capabilitiesFile)))
        	.lines().forEachOrdered(sBuilder::append);
       
        @SuppressWarnings("unchecked")
		Map<String, ?> map = new Gson().fromJson(sBuilder.toString(), Map.class);
        
        DesiredCapabilities capabilities = new DesiredCapabilities();
        map.entrySet().stream().forEachOrdered(
        		(entry)->{capabilities.setCapability(entry.getKey(), entry.getValue());}
        );
        
        return capabilities;
    }    
    
    public boolean hasProperty(String name) {
        return properties.contains(name);
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }
}