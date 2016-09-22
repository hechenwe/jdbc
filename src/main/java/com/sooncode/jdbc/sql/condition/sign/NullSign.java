package com.sooncode.jdbc.sql.condition.sign;
/**
 * 模糊匹配
 * @author pc
 *
 */
public class NullSign extends Sign  {

	/**
	 * IS NULL 
	 */
	public static final Sign IS_NULL = new Sign("IS NULL");
	/**
	 * IS NOT NULL
	 */
	public static final Sign IS_NOT_NULL = new Sign("IS NOT NULL");
	 
}
