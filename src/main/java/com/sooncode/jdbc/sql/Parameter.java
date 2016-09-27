package com.sooncode.jdbc.sql;

import java.util.HashMap;
import java.util.Map;

import com.sooncode.jdbc.constant.SQL_KEY;
import com.sooncode.jdbc.constant.STRING;

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
	 * 获取 格式之后的SQL语句
	 * @return
	 */
	public String getFormatSql() {
		String sql = new String ( this.getReadySql());
		return getFormatSql(sql);
	}
	
	
	/**
	 * 获取 格式之后的SQL语句
	 * @return
	 */
	public static String getFormatSql(String readySql) {
		String sql = new String ( readySql);
		sql = sql .replace(SQL_KEY.SELECT, STRING.R_T+SQL_KEY.SELECT+STRING.R_T);
		sql = sql .replace(SQL_KEY.INSERT, STRING.R_T+SQL_KEY.INSERT);
		sql = sql .replace(SQL_KEY.UPDATE, STRING.R_T+SQL_KEY.UPDATE+STRING.R_T);
		sql = sql .replace(SQL_KEY.DELETE, STRING.R_T+SQL_KEY.DELETE+STRING.R_T);
		sql = sql .replace(SQL_KEY.COMMA, SQL_KEY.COMMA+STRING.R_T);
		sql = sql .replace(SQL_KEY.FROM,STRING.R_T+ "FROM ");
		sql = sql .replace(SQL_KEY.WHERE, STRING.R_T+"WHERE ");
		sql = sql .replace(SQL_KEY.AND, STRING.R_T+"AND ");
		sql = sql .replace(SQL_KEY.L_BRACKET,STRING.R_T+ SQL_KEY.L_BRACKET);
		sql = sql .replace(SQL_KEY.VALUES,STRING.R_T+ "VALUES");
		sql = sql .replace(STRING.SPACING+STRING.SPACING,STRING.SPACING);
		return sql ;
	}
	 
	/**
	 * 获取参数注入后的SQL语句。</br>
	 * 注意：该SQL不一定能直接远行！
	 * @return 参数注入后的SQL语句
	 */
	public String getSql() {
		StringBuilder sql = new StringBuilder();
		sql.append(this.readySql);
		if (this.params.size() > 0) {

			for (int i = 1; i <= this.params.size(); i++) {
				Object value = this.params.get(i);
				int n = sql.indexOf(STRING.QUESTION);
				sql.replace(n, n + 1, STRING.S_QUOTES  + value.toString() +  STRING.S_QUOTES );
			}
		} else {
			return readySql;
		}
		return sql.toString();
	}
	/**
	 * 参数模型是否没有异常</br>
	 * 当预编译SQL为空（null），或 为空字符串时，为存在异常；当预编译中的参数大于参数Map中的个数时，存在异常。
	 * @return 存在异常时返回false；没有异常时返回true.
	 */
	public boolean isNotException(){
		
		if(this.readySql == null || this.readySql.trim().equals("")){
			return false;
		}else{
			StringBuilder sql = new StringBuilder(this.readySql);
			int size = 0;
			while(sql.indexOf(STRING.QUESTION)!=-1){
				int n = sql.indexOf(STRING.QUESTION);
				sql.replace(n, n + 1, STRING.AT); 
				size ++;
			}
			
			if(this.params.size()>=size){
				return true;
			}else{
				return false;
			}
			 
		}
		
		
	}

}
