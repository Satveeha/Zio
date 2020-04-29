package com.zuci.zio.commonFuntions;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ConfigRead {
	
	public String readProperty(String key) {
		String returnText = "";
		try {
			File file = new File("config.properties");
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			returnText = properties.getProperty(key);
			fileInput.close();
		} catch (Exception e) {
		}
		return returnText;
	}
	
}
