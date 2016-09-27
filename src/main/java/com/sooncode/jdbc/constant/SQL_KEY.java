package com.sooncode.jdbc.constant;
/**
 * SQL 语句的关键字 
 * @author he chen 
 *
 */
public class SQL_KEY {
	 
public static final String SELECT  = "SELECT"+STRING.SPACING;

 
	public static final String UPDATE = "UPDATE"+STRING.SPACING;

 
public static final String  DELETE = "DELETE FROM"+STRING.SPACING;
public static final String  INSERT = "INSERT INTO"+STRING.SPACING;
public static final String  FROM = STRING.SPACING+"FROM"+STRING.SPACING;
public static final String  AS = STRING.SPACING+"AS"+STRING.SPACING;
public static final String  WHERE = STRING.SPACING+"WHERE"+STRING.SPACING;
public static final String  AND = STRING.SPACING+"AND"+STRING.SPACING;
public static final String  OR  = STRING.SPACING+"OR"+STRING.SPACING;
public static final String  LIMIT = STRING.SPACING+"LIMIT"+STRING.SPACING;
public static final String  VALUES = STRING.SPACING+"VALUES"+STRING.SPACING;
public static final String  SET = STRING.SPACING+"SET"+STRING.SPACING;
public static final String  COUNT = "COUNT(1)";
public static final String  SIZE = "SIZE";
public static final String  LIKE = "LIKE";
public static final String  DATE_FORMAT = "DATE_FORMAT";
public static final String  IN = STRING.SPACING+"IN"+STRING.SPACING;
public static final String  BETWEEN = STRING.SPACING+"BETWEEN"+STRING.SPACING;
public static final String  ORDER_BY  = STRING.SPACING+"ORDER BY"+STRING.SPACING;

public static final String IS  = STRING.SPACING+"IS";     
public static final String NOT  = STRING.SPACING+"NOT";      
      
/**
 * "1=1"
 */
public static final String  ONE_EQ_ONE = "1=1";
/**
 * 等号 " = "  (等号两边有一个空格)
 */
public static final String  EQ = STRING.SPACING+"="+STRING.SPACING;

/**
 * 逗号 " , "(逗号两边有一个空格)
 */
public static final String COMMA = STRING.SPACING+","+STRING.SPACING;

/**
 * "( "
 */
public static final String L_BRACKET = "("+STRING.SPACING;
/**
 * " )"
 */
public static final String R_BRACKET = STRING.SPACING+")";
/**
 * " NULL"
 */
public static final String NULL = STRING.SPACING+"NULL";
/**
 * 问号 " ? " 
 */
public static final String QUESTION = STRING.SPACING+"?"+STRING.SPACING;




}
