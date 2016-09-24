package com.sooncode.jdbc.result;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sooncode.jdbc.util.T2E;

/**
 * 查询返回结果集
 * @author pc
 *
 */
public class ResultMap {

	private Map<String,Object> resultMap;
	
	public ResultMap(Map<String,Object> map){
		
		Map<String,Object> newMap = new HashMap<>();
		for(Entry<String, Object> en : map.entrySet()){
			String key = en.getKey().toUpperCase();
			Object val = en.getValue();
			newMap.put(key, val);
		}
		this.resultMap = newMap;
	}
	/**
	 * 获取 字段对应的值
	 * @param key  字段对应的关键字，或者实体对应属性名称，或者 类名（表名）+属性名（字段名）（user.name 或 user_name）; 当key中含有类名和属性名时区分大小写 ，其他不区分大小写。
	 * @return 字段对应的值 
	 */
	public Object get(String key){
		   if(key==null || key.trim().equals("")){
			   return null;
		   }
		   StringBuffer str = new StringBuffer(key);
		   int n = str.indexOf(".");
		   if(n!=-1){
			   String[] strs = str.toString().split("\\.");
			   if(strs.length==2){
			   String tableName = T2E.toColumn(strs[0]);
			   String conName = T2E.toColumn(strs[1]);
			   key = tableName+"_"+conName;
			   }else{
				   return null;
			   }
		   }
		
		   String newKey = key.toUpperCase();
		   Object obj = resultMap.get(newKey);
		   if(obj==null){
			   String key1= T2E.toColumn(key).toUpperCase();
			   obj = resultMap.get(key1);
			   if(obj == null){
				   
			   }
		   }
		   
		   
		   return obj;
	}
}
