package com.sooncode.jdbc.sql;

import java.util.HashMap;
import java.util.Map;

/**
 * 符号
 * 
 * @author pc
 *
 */
public enum Sign {

	/**大于 (>) */
	GT,
	
	/**大于等于 (>=)*/
	GTEQ,

	/** 小于(<) */
	LT,

	/** 小于等于(<=) */
	LTEQ,
	
	/**不等于*/
    NOT_EQ,
	/**模糊匹配('%xxx%')*/
	LIKE;

	public static Map<String, String> Signmap;
	static {
		Signmap = new HashMap<>();
		Signmap.put("GT", ">");
		Signmap.put("GTEQ", ">=");
		Signmap.put("LIKE", "LIKE");
		Signmap.put("LT", "<");
		Signmap.put("LTEQ", "<=");
		Signmap.put("NOT_EQ", "<>");
	}

}
