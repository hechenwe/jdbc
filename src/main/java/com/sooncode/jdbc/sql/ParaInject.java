package com.sooncode.jdbc.sql;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


import org.apache.log4j.Logger;

import com.sooncode.jdbc.constant.STRING;
 
/**
 * SQL中的参数注入
 * 
 * @author pc
 *
 */
public class ParaInject {

	private static Logger logger = Logger.getLogger("Para.class");

 
	/**
	 * 替换预SQL中的参数 获得可执行的SQL
	 * 
	 * @param templateSql SQL语句模板
	 *            
	 * @param args
	 *            注入sql中的对象集 (可选) 或者一个Map
	 * @return 参数模型 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static Parameter getParameter(String templateSql, Object... args) {
		logger.debug("【SQL语句模板】: "+templateSql);
		Map<String, Object> map = new HashMap<>();
		Parameter p = new Parameter();
		Map<Integer,Object> parames = new HashMap<>();
		Integer index =1;
		if (args.length == 1 && args[0].getClass().getSuperclass().getName().equals(AbstractMap.class.getName())) {//"java.util.AbstractMap"
			map = (Map<String, Object>) args[0];
			TemModule tm = getMap(templateSql,map);
			templateSql = tm.getTemplateSql();
			map = tm.getMap();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = STRING.OCTOTHORPE + STRING.L_CURLY_BRACKET   + entry.getKey() +  STRING.R_CURLY_BRACKET ;
				Object value = entry.getValue();
				String newTemp = templateSql.replace(key, STRING.QUESTION);
				if(!newTemp.equals(templateSql)){
					templateSql = newTemp;
					parames.put(index, value);
					index ++ ;
				}
				
			}
		} else {
			
			for (Object obj : args) {
				Class<?> cls = obj.getClass();
				Field[] field = cls.getDeclaredFields();
				for (Field f : field) {
					f.setAccessible(true);
					try {
						if (f.get(obj) != null) {
							map.put(f.getName(), f.get(obj).toString());
						} else {
							map.put(f.getName(), STRING.S_NULL);
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
			TemModule tm = getMap(templateSql,map);
			templateSql = tm.getTemplateSql();
			map = tm.getMap();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key =STRING.OCTOTHORPE + STRING.L_CURLY_BRACKET+ entry.getKey()+STRING.R_CURLY_BRACKET;
				Object value = entry.getValue();
				String newTemp = templateSql.replace(key, STRING.QUESTION);
				if(!newTemp.equals(templateSql)){
					templateSql = newTemp;
					parames.put(index, value);
					index ++ ;
				}
			}
		}
		p.setReadySql(templateSql);
		p.setParams(parames);
		return p;
	}

	private static TemModule  getMap(String templateSql,Map<String,Object> map ){
		TemModule t = new TemModule();
		StringBuffer sb = new StringBuffer();
		sb.append(templateSql);
		Map<Integer,String> tempMap = new TreeMap<>();
		for(Entry<String, Object>en : map.entrySet()){
			
			templateSql = templateSql .replace( STRING.DOLLAR + STRING.L_CURLY_BRACKET+en.getKey()+STRING.R_CURLY_BRACKET, en.getValue().toString());
			String key = STRING.OCTOTHORPE + STRING.L_CURLY_BRACKET+en.getKey()+STRING.R_CURLY_BRACKET;
			int index = sb.indexOf(key);
			if(index!=-1){
				tempMap.put(index,en.getKey());
			}
			 
		}
		
		t.setTemplateSql(templateSql);
		 Map<String,Object> newMap = new LinkedHashMap<>();
		
		for(Entry<Integer, String>en:tempMap.entrySet()){
			String key = en.getValue();
					
			newMap.put(key,map.get(key));
		}
		 t.setMap(newMap);
		return t;
	}
	
	
	
	
}


class TemModule {
	private String templateSql = new String ();
	
	private Map<String,Object> map ;

	public String getTemplateSql() {
		return templateSql;
	}

	public void setTemplateSql(String templateSql) {
		this.templateSql = templateSql;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
	
	
}
