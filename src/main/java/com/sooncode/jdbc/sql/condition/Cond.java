package com.sooncode.jdbc.sql.condition;

import java.util.HashMap;
import java.util.Map;

import com.sooncode.jdbc.sql.Parameter;
import com.sooncode.jdbc.sql.condition.sign.NullSign;
import com.sooncode.jdbc.sql.condition.sign.Sign;
import com.sooncode.jdbc.util.T2E;

public class Cond {

	protected Parameter parameter;
     
	protected Cond() {

	}
	
	
    /**
     * 创建查询条件
     * @param key 属性名称（对应数据库表的字段）
     * @param sign 符号模型 ：</br> 包含：CommonSign 类 （常见符号）；LikeSign 类 （模糊查询符号）；DateFormatSign 类 （日期符号查询）。
     * @param value 条件对应的值
     */
	public Cond(String key, Sign sign, Object value) {

		String className = sign.getClass().getName();

		String sql = new String();
		if (sign.toString().contains("LIKE")) {
			sql = T2E.toColumn(key) + " LIKE ? ";
			if (sign.equals("LIKE")) {
				value = "%" + value + "%";
			} else if (sign.equals("R_LIKE")) {
				value = "%" + value;
			} else if (sign.equals("L_LIKE")) {
				value = value + "%";
			}
		} else if ("com.sooncode.jdbc.sql.condition.sign.DateFormatSign".equals(className)) {
			sql = " DATE_FORMAT(" + T2E.toColumn(key) + ",'" + sign + "') = ? ";

		} else {
			sql = T2E.toColumn(key) + " " + sign + " ? ";
		}

		Map<Integer, Object> param = new HashMap<>();
		param.put(1, value);
		this.parameter = new Parameter();
		this.parameter.setReadySql(sql);
		this.parameter.setParams(param);

	}
	 
	/**
     * 创建查询条件(IN)
     * @param key 属性名称（对应数据库表的字段）
     * @param value 条件对应的值集合
     */
	public Cond(String key, Object[] values) {
			String in = "( ";
			Map<Integer, Object> param = new HashMap<>();
			for (int i = 1; i <= values.length; i++) {
				param.put(i, values[i - 1]);
				if (i == 1) {
					in = in + " ? ";
				} else {
					in = in + " ,? ";
				}
			}
			in = in + " )";
			String sql = T2E.toColumn(key) + " IN " + in;
			this.parameter = new Parameter();
			this.parameter.setReadySql(sql);
			this.parameter.setParams(param);
	}
	/**
	 * 创建查询条件(NULL)
	 * @param key 属性名称（对应数据库表的字段）
	 * @param NullSign IS NULL 或者 IS NOT NULL
	 */
	public Cond(String key, Sign NullSign) {
		    
			String sql = T2E.toColumn(key) +" " +NullSign;
			this.parameter = new Parameter();
			this.parameter.setReadySql(sql);
	}
    /**
     * 创建 BETWEEN AND 条件
     * @param key 属性名称（对应数据库表的字段）
     * @param startObject 开始值
     * @param endObject 结束值
     */
	public Cond(String key,Object start ,Object end ){
		String sql = T2E.toColumn(key) +" BETWEEN ? AND ?";
		Map<Integer, Object> param = new HashMap<>();
		param.put(1, start );
		param.put(2, end );
		this.parameter = new Parameter();
		this.parameter.setReadySql(sql);
		this.parameter.setParams(param);
	}
	
	public And and(Cond A) {
		return new And(this, A);
	}

	public And and(Cond A, Cond B) {
		return new And(A, B);
	}

	public And and(Cond... conds) {
		return new And(conds);
	}

	public Or or(Cond A) {
		return new Or(this, A);
	}

	public Or or(Cond A, Cond B) {
		return new Or(A, B);
	}

	public Or or(Cond... conds) {
		return new Or(conds);
	}

	public Parameter getParameter() {
		return this.parameter;
	}

	/**
	 * 是否有查询条件
	 * 
	 * @return 有返回true;无返回false.
	 */
	public Boolean isHaveCond() {

		if (this.parameter != null && this.parameter.getParams() != null && this.parameter.getReadySql() != null
				&& !this.parameter.getReadySql().trim().equals("")) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 排序
	 * @param orderBies
	 * @return
	 */
	public Cond orderBy(OrderBy ... orderBies){
		
		String orderBy = " ORDER BY ";
		for (int i = 0; i < orderBies.length; i++) {
			if(i==0){
				orderBy = orderBy +" "+ orderBies[i];
			}else{
				orderBy = orderBy+" , " + orderBies[i];
			}
		}
		
		this.parameter.setReadySql(this.parameter.getReadySql()+ " " +orderBy);
		return this;
		
	}
}
