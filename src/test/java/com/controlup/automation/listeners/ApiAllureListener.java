package com.controlup.automation.listeners;

import io.qameta.allure.Attachment;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class ApiAllureListener extends TestListenerAdapter {

    @Override
    public void onTestFailure(ITestResult result) {
        if (result.getMethod().getGroups().length != 0) {
            try {
                jsonResponseToCheck(result.getTestClass().getName()+ "." +result.getMethod().getMethodName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Attachment(value = "JSON response", type = "text/plain")
    public byte[] jsonResponseToCheck(String dumpName) throws IOException {
        File myJsonFile = new File(Paths.get("test-output", dumpName + ".json").toAbsolutePath().toString());
        byte[] bytesArray;
        if (myJsonFile.exists()) {
	        bytesArray = new byte[(int) myJsonFile.length()];
	        FileInputStream fis = new FileInputStream(myJsonFile);
	        fis.read(bytesArray);
	        fis.close();
        }else {
        	bytesArray = null;
        }
        
        return bytesArray;
    }  
    
}
