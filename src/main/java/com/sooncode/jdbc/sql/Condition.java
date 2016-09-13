package com.sooncode.jdbc.sql;
/**
 * 查询条件 最小单位
 * @author pc
 *
 */
public class Condition {
    /**
     * 字段(属性) 
     */
	private String key ;
	
	/**
	 * 条件对应的值
	 */
	private Object val;
	
	/**
	 * 条件使用的符号
	 */
	private String conditionSign;

	
	/**
	 *类型  
	 */
	private String type ="1";  //"0" 表示自定义 
	
	/**
	 * 条件字符串(SQL片段)
	 */
	private String condition ;
	
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getVal() {
		return val;
	}

	public void setVal(Object val) {
		this.val = val;
	}

	public String getConditionSign() {
		return conditionSign;
	}

	public void setConditionSign(String conditionSign) {
		this.conditionSign = conditionSign;
	}

	public Condition() {
		 
	}
	public Condition(String key, Object val, String conditionSign) {
		super();
		this.key = key;
		this.val = val;
		this.conditionSign = conditionSign;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	
	
}
