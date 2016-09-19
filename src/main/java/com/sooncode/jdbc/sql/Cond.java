package com.sooncode.jdbc.sql;

import com.sooncode.jdbc.util.T2E;

public class Cond {
	/***
	 * 表达式
	 */
	protected String expression;

	protected Parameter parameter;

	/** 字段名称 */
	protected String key;

	/** 条件使用的值 */
	protected Object value;

	/** 条件使用的值数组 */
	protected Object[] values;

	/** 条件使用的符号 */
	protected String sign;

	public Cond(String expression) {

		this.expression = expression;
	}
	
	
	public Cond(String key,Sign sign,Object value) {
		String sql = T2E.toColumn(key) +" "+ sign.name() + " ? ";
		
	}

}
