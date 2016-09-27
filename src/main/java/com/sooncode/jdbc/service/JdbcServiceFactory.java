package com.sooncode.jdbc.service;

import java.util.Hashtable;

import com.sooncode.jdbc.constant.DATA;


public class JdbcServiceFactory {
	private static Hashtable<String, JdbcService> services = new Hashtable<>();
	
	private JdbcServiceFactory(){
		
	}
	
	public static JdbcService getJdbcService(){
		JdbcService js = services.get(DATA.DEFAULT_KEY);
		if(js==null){
			js= new JdbcService();
			services.put(DATA.DEFAULT_KEY, js);
		}
		return js;
	}
	public static  JdbcService getJdbcService(String dbKey){
		JdbcService js = services.get(dbKey);
		if(js==null){
			js= new JdbcService(dbKey);
			services.put(dbKey, js);
		}
		return js;
	}
	
}
