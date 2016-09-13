package com.sooncode.jdbc.sql;

import java.util.HashMap;

import java.util.Map;
import java.util.Map.Entry;

import com.sooncode.jdbc.reflect.RObject;
import com.sooncode.jdbc.util.T2E;

/**
 * 查询条件构造器
 * 
 * @author pc
 *
 */
public class Conditions {

	private Object obj;
	private Map<String, Condition> ces;
	private String oderByes = "";

	public Conditions(Object obj) {
		this.obj = obj;
		RObject rObj = new RObject(obj);
		Map<String, Object> map = rObj.getFiledAndValue();
		Map<String, Condition> list = new HashMap<>();
		for (Entry<String, Object> en : map.entrySet()) {
			String key = en.getKey();
			Object val = en.getValue();
			Condition c = new Condition(key, val, null);
			list.put(key, c);
		}

		this.ces = list;
	}

	/**
	 * 设置条件
	 * 
	 * @param key
	 * @param sign
	 * @return
	 */
	public Conditions setCondition(String key, Sign sign) {

		Condition c = ces.get(key);
		if (c != null) {
			c.setConditionSign(sign.name());
			ces.put(c.getKey(), c);
		}
		return this;
	}
	/**
	 * 设置 条件
	 * 
	 * @param condition 条件
	 * @return
	 */
	public Conditions setBetweenCondition(String key,Object start,Object end) {
		
		Condition c = ces.get(key);
		c.setType("0");
		if (c != null) {
			String sql = " " +T2E.toColumn(key)+" BETWEEN " + start + " AND " + end + " ";
			c.setCondition(sql);
			ces.put(c.getKey(), c);
		}
		return this;
	}

	/**
	 * 设置排序
	 * 
	 * @param key
	 * @param sign
	 * @return
	 */
	public Conditions setOderBy(String key, Sort sort) {

		key = T2E.toColumn(key);
		if (key == null || key.equals("")) {
			return this;
		} else {
			if (this.oderByes.equals("")) {
				this.oderByes = this.oderByes + " " + key.toUpperCase() + " " + sort.name();

			} else {
				this.oderByes = this.oderByes + " , " + key.toUpperCase() + " " + sort.name();
			}
		}

		return this;

	}

	 
	
	
	public Parameter getWhereSql() {
		
		Parameter p = new Parameter();
		Map<Integer,Object> para = new HashMap<>();
		String sql = new String();
		int index =1;
		for (Entry<String, Condition> en : this.ces.entrySet()) {
			Condition c = en.getValue();
			String con = T2E.toColumn(c.getKey());
			if(c.getType().equals("1")){
				if (c.getVal() != null) {
					String sign = c.getConditionSign();
					String newSign = Sign.Signmap.get(sign);
					newSign = newSign == null ? " = " : newSign;
					if (newSign.equals("LIKE")) {
						sql = sql + " AND " + con + " LIKE '%?%'";// + c.getVal() + "%'";
					} else {
						sql = sql + " AND " + con + " " + newSign + "?";
					}
					para.put(index,c.getVal());
					index++;
				}
			}else{//自定义 
				sql = sql +" AND " + c.getCondition();
			}
			
			
		}
		if (!this.oderByes.equals("")) {
			sql = sql + " ORDER BY " + this.oderByes;
		}
		p.setReadySql(sql);
		p.setParams(para);
		return p;
	}

	public Object getObj() {
		return obj;
	}

}
