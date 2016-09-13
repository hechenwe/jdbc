package com.sooncode.jdbc.util.create_entity;

import java.util.HashMap;
import java.util.Map;
import java.sql.Types;

public class Jdbc2Java {
	private static Map<String, String> jdbcData;
	private static Map<String, String> javaData;
	private static Map<String, String> javaDataClassName;

	public static Map<String, String> getJdbcData() {
		Map<String, String> map = new HashMap<String, String>();

		map.put("" + Types.BIT, "BIT");
		map.put("" + Types.TINYINT, "TINYINT");
		map.put("" + Types.SMALLINT, "SMALLINT");

		map.put("" + Types.INTEGER, "INTEGER");
		map.put("" + Types.BIGINT, "BIGINT");
		map.put("" + Types.FLOAT, "FLOAT");

		map.put("" + Types.REAL, "REAL");
		map.put("" + Types.DOUBLE, "DOUBLE");
		map.put("" + Types.NUMERIC, "NUMERIC");

		map.put("" + Types.DECIMAL, "DECIMAL");
		map.put("" + Types.CHAR, "CHAR");
		map.put("" + Types.VARCHAR, "VARCHAR");

		map.put("" + Types.LONGVARCHAR, "LONGVARCHAR");
		map.put("" + Types.DATE, "DATE");
		map.put("" + Types.TIME, "TIME");

		map.put("" + Types.TIMESTAMP, "TIMESTAMP");
		map.put("" + Types.BINARY, "BINARY");
		map.put("" + Types.VARBINARY, "VARBINARY");

		map.put("" + Types.LONGVARBINARY, "LONGVARBINARY");
		map.put("" + Types.NULL, "NULL");
		map.put("" + Types.OTHER, "OTHER");

		map.put("" + Types.JAVA_OBJECT, "JAVA_OBJECT");
		map.put("" + Types.DISTINCT, "DISTINCT");
		map.put("" + Types.STRUCT, "STRUCT");

		map.put("" + Types.ARRAY, "ARRAY");
		map.put("" + Types.BLOB, "BLOB");
		map.put("" + Types.CLOB, "CLOB");

		map.put("" + Types.REF, "REF");
		map.put("" + Types.DATALINK, "DATALINK");
		map.put("" + Types.BOOLEAN, "BOOLEAN");

		map.put("" + Types.ROWID, "ROWID");
		map.put("" + Types.NCHAR, "NCHAR");
		map.put("" + Types.NVARCHAR, "NVARCHAR");

		map.put("" + Types.LONGNVARCHAR, "LONGNVARCHAR");
		map.put("" + Types.NCLOB, "NCLOB");
		map.put("" + Types.SQLXML, "SQLXML");
		jdbcData = map;
		return jdbcData;
	}

	public static Map<String, String> getJavaData() {
		Map<String, String> map = new HashMap<String, String>();

		map.put("BIT", "Boolean");
		map.put("TINYINT", "Integer");
		map.put("SMALLINT", "Integer");

		map.put("INTEGER", "Integer");
		map.put("BIGINT", "Long");
		map.put("FLOAT", "Double");

		map.put("REAL", "Float");
		map.put("DOUBLE", "Double");
		map.put("NUMERIC", "java.math.BigDecimal");

		map.put("DECIMAL", "java.math.BigDecimal");
		map.put("CHAR", "String");
		map.put("VARCHAR", "String");

		map.put("LONGVARCHAR", "String");
		map.put("DATE", "java.util.Date");
		map.put("TIME", "java.sql.Time");

		map.put("TIMESTAMP", "java.util.Date");
		map.put("BINARY", "byte[]");
		map.put("VARBINARY", "byte[]");

		map.put("LONGVARBINARY", "byte[]");
		map.put("NULL", "Object");
		map.put("OTHER", "Object");

		map.put("JAVA_OBJECT", "Object");
		map.put("DISTINCT", "Object");
		map.put("STRUCT", "Object");

		map.put("ARRAY", "Object");
		map.put("BLOB", "Object");
		map.put("CLOB", "Object");

		map.put("REF", "Object");
		map.put("DATALINK", "Object");
		map.put("BOOLEAN", "Object");

		map.put("ROWID", "Object");
		map.put("NCHAR", "String");
		map.put("NVARCHAR", "String");

		map.put("LONGNVARCHAR", "String");
		map.put("NCLOB", "Object");
		map.put("SQLXML", "String");

		javaData = map;
		return javaData;
	}

	public static Map<String, String> getJavaDataClassName() {
		Map<String, String> map = new HashMap<String, String>();

		map.put("BigDecimal", "java.math.BigDecimal");
		map.put("Date", "java.util.Date");
		map.put("Time", "java.sql.Time");
		map.put("Timestamp", "java.util.Date");

		javaDataClassName = map;
		return javaDataClassName;
	}

}
