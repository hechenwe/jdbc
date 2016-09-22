package com.sooncode.jdbc.service;

import java.util.Hashtable;


public class JdbcServiceFactory {
	private static Hashtable<String, JdbcService> services = new Hashtable<>();
	
	private JdbcServiceFactory(){
		
	}
	
	public static JdbcService getJdbcService(){
		JdbcService js = services.get("default");
		if(js==null){
			js= new JdbcService();
			services.put("default", js);
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
