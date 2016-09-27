package com.sooncode.jdbc.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * java 基本数据类型
 * 
 * @author pc
 *
 */
public class JavaBaseType {

	public static final String Integer = "Integer";
	public static final String Long = "Long";
	public static final String Short = "Short";
	public static final String Byte = "Byte";
	public static final String Float = "Float";
	
	public static final String Double = "Double";
	public static final String Character = "Character";
	public static final String Boolean = "Boolean";
	public static final String Date = "Date";
	public static final String String = "String";
	
	public static final String List = "List";
	
	public static final Map<String,String> map = new HashMap<>();
	 static{
		 map.put("Integer", Integer);
		 map.put("Long", Long);
		 map.put("Short", Short);
		 map.put("Byte", Byte);
		 map.put("Float", Float);
		 
		 map.put("Double", Double);
		 map.put("Character", Character);
		 map.put("Boolean", Boolean);
		 map.put("Date", Date);
		 map.put("String", String);
		 
		 map.put("List", List);
	 }
}
