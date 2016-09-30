package com.sooncode.jdbc.db;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 配置文件 读取工具类
 * 
 * @author pc
 *
 */
class PropertiesUtil {
	/**
	 * 配置文件所在路径
	 */
	private String filePath;

	PropertiesUtil(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * 根据Key读取Value
	 * 
	 * @param filePath
	 *            配置文件 (默认在src下)
	 * @param key
	 *            关键字
	 * @return 字符串;异常时返回null.
	 */
	String getString(String key) {
		key = key.toUpperCase();
		Properties p = new Properties();
		try {
			FileInputStream fis = new FileInputStream(this.filePath);
			InputStreamReader in = new InputStreamReader(fis, "utf-8");
			p.load(in);
			String value = p.getProperty(key);
			if (value != null) {
				return value.trim();
			} else {
				return null;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据Key读取Value
	 * 
	 * @param filePath
	 *            配置文件 (默认在src下)
	 * @param key
	 *            关键字
	 * @return Integer 整数 ;异常时返回null.
	 */
	Integer getInt(String key) {
		key = key.toUpperCase();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(this.filePath));
			properties.load(in);
			String value = properties.getProperty(key);
			if (value != null) {
				Integer val = Integer.parseInt(value.trim());
				return val;
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}