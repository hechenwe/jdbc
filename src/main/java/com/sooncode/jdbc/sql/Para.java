package com.sooncode.jdbc.sql;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
 
/**
 * SQL中的参数注入
 * 
 * @author pc
 *
 */
public class Para {

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
		if (args.length == 1 && args[0].getClass().getSuperclass().getName().equals("java.util.AbstractMap")) {
			map = (Map<String, Object>) args[0];
			map = getMap(templateSql,map);
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = "${" + entry.getKey() + "}";
				Object value = entry.getValue();
			 
				String newTemp = templateSql.replace(key, "?");
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
							map.put("${" + f.getName() + "}", f.get(obj).toString());
						} else {
							map.put("${" + f.getName() + "}", "null");
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
			map = getMap(templateSql,map);
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				String newTemp = templateSql.replace(key, "?");
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

	private static Map<String,Object> getMap(String templateSql,Map<String,Object> map ){
		
		StringBuffer sb = new StringBuffer();
		sb.append(templateSql);
		Map<Integer,String> tempMap = new HashMap<>();
		for(Entry<String, Object>en:map.entrySet()){
			String key = en.getKey();
			int index = sb.indexOf(key);
			if(index!=-1){
				tempMap.put(index,key);
			}
			 
		}
		 Map<String,Object> newMap = new HashMap<>();
		
		for(Entry<Integer, String>en:tempMap.entrySet()){
			String key = en.getValue();
					
			newMap.put(key,map.get(key));
		}
		return newMap;
	}
	
}
