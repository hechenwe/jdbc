package com.sooncode.jdbc.sql.condition.sign;

public class EqualSign extends Sign {
	/**
	 * 大于（>）
	 */
	public static final Sign GT = new Sign(">");
	/**
	 * 大于等于 （>=）
	 */
	public static final Sign GTEQ = new Sign(">=");
	/**
	 * 小于（<）
	 */
	public static final Sign LT = new Sign("<");

	/**
	 * 小于等于（<=）
	 */
	public static final Sign LTEQ = new Sign("<=");

	/**
	 * 不等于 （<>）
	 */
	public static final Sign NOT_EQ = new Sign("<>");

}
