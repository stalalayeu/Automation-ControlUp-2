package com.controlup.automation.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private final static Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);
    private static final String PROP_FILE_CONFIG = "config.properties";

    protected String userEmail;
    protected String userPassword;
    protected String testRailEndPoint;
    protected String testRailProjectName;
    protected String testRailUsername;
    protected String testRailPassword;
    protected String jenkinsBuildNumber;
    protected String jiraUsername;
    protected String jiraPassword;
    protected String jiraUrl;

    public PropertiesLoader() {
        try {
            dataPropertiesLoader();
        } catch (IOException e) {
            logger.info("Can not load properties: " + e.getMessage());
        }
    }

    private void dataPropertiesLoader() throws IOException {
        Properties prop = new Properties();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROP_FILE_CONFIG);
        if (inputStream == null) {
            throw new FileNotFoundException("Property file '" + PROP_FILE_CONFIG + "' not found");
        }

        prop.load(inputStream);

        userEmail = prop.getProperty("user.email");
        userPassword = prop.getProperty("user.password");
        testRailEndPoint = prop.getProperty("testrail.endpoint");
        testRailProjectName = prop.getProperty("testrail.projectname");
        testRailUsername = prop.getProperty("testrail.username");
        testRailPassword = prop.getProperty("testrail.password");
        jenkinsBuildNumber = System.getProperty("buildNumber");
        jiraUsername = prop.getProperty("jira.username");
        jiraPassword = prop.getProperty("jira.password");
        jiraUrl = prop.getProperty("jira.url");
        inputStream.close();
    }

    public String getUserEmail() { return userEmail; }

    public String getUserPassword() { return userPassword; }

    public String getTestRailEndPoint() { return testRailEndPoint; }

    public String getTestRailUsername() { return testRailUsername; }

    public String getTestRailPassword() { return testRailPassword; }

    public String getJenkinsBuildNumber() { return jenkinsBuildNumber; }

    public String getTestRailProjectName() { return testRailProjectName; }

    public String getJiraUsername() { return jiraUsername; }

    public String getJiraPassword() { return jiraPassword; }

    public String getJiraUrl() { return jiraUrl; }
}
