package com.sooncode.jdbc.sql;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数模型
 * 
 * @author pc
 *
 */
public class Parameter {

	public Parameter() {

	}
    /**
     * 
     * @param readySql 预编译SQL ，或可执行SQL。
     */
	public Parameter(String readySql) {
       this.readySql = readySql;
	}

	/** 预编译SQL */
	private String readySql;
	/** 参数 ，从1开始 */
	private Map<Integer, Object> params = new HashMap<>();

	public String getReadySql() {
		return readySql;
	}

	public void setReadySql(String readySql) {
		this.readySql = readySql;
	}

	public Map<Integer, Object> getParams() {
		return params;
	}

	public void setParams(Map<Integer, Object> params) {
		this.params = params;
	}

	/**
	 * 获取参数注入后的SQL语句。
	 * 
	 * @return
	 */
	public String getSql() {
		StringBuilder sql = new StringBuilder();
		sql.append(this.readySql);
		if (this.params.size() > 0) {

			for (int i = 1; i <= this.params.size(); i++) {
				Object value = this.params.get(i);
				int n = sql.indexOf("?");
				sql.replace(n, n + 1, "'" + value.toString() + "'");
			}
		} else {
			return readySql;
		}
		return sql.toString();
	}
	/**
	 * 参数模型是否异常
	 * @return
	 */
	public boolean isException(){
		
		if(this.readySql == null || this.readySql.trim().equals("")){
			return false;
		}else{
			
			;//int parameterSize = this.readySql.
			
			return true;
		}
		
		
	}

}
