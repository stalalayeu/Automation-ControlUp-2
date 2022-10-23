package com.controlup.automation.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.StringTokenizer;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

public class CookieManager {
	private static final String COOKIE_FILE_PATH = "./src/test/resources/Cookies.data";
	private static final String DELIMITER = "|";
	
    public static void storeCookies(WebDriver driver) {
        File file = new File(COOKIE_FILE_PATH);
        try (FileWriter fileWrite = new FileWriter(file);							
             BufferedWriter bufWrite = new BufferedWriter(fileWrite);)
        {
            for(Cookie c: driver.manage().getCookies()) {
            	LocalDateTime localExpiry = null;
            	if (c.getExpiry() != null) {
            		localExpiry = new java.sql.Timestamp(c.getExpiry().getTime()).toLocalDateTime();
            	}
            	
            	bufWrite.write(
            			c.getName()+DELIMITER+
            			c.getDomain()+DELIMITER+
            			c.getPath()+DELIMITER+
            			localExpiry+DELIMITER+
            			c.isSecure()+DELIMITER+
            			c.getValue());
            	bufWrite.newLine();
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
    }    
    
	public static void loadCookies(WebDriver driver) {
		File file = new File(COOKIE_FILE_PATH);
		if (!file.exists())
			return;
		
    	try(FileReader fileReader = new FileReader(file);							
        	BufferedReader bufReader = new BufferedReader(fileReader);)
    	{			
    		String strline;			
     		while((strline=bufReader.readLine())!=null){									
    			StringTokenizer token = new StringTokenizer(strline,DELIMITER);									
    			if (token.hasMoreTokens()) {					
    				String name = token.nextToken();
    				String domain = token.nextToken();
    				String path = token.nextToken();
    				Date expiry = null;					

    				String val;			
    				if(!(val=token.nextToken()).equals("null"))
    				{	
   						expiry = java.sql.Timestamp.valueOf(LocalDateTime.parse(val));
    				}		
    				Boolean isSecure = new Boolean(token.nextToken()).booleanValue();
    				String value = token.nextToken();
    				while (token.hasMoreTokens()) {
    					value += token.nextToken(); 
    				}
    				
    				Cookie ck = new Cookie(name,value,domain,path,expiry,isSecure);			
    				driver.manage().addCookie(ck); 					
    			}		
    		}		
    	}catch(Exception ex){					
    		ex.printStackTrace();			
    	}
    }
}
