package com.sooncode.jdbc.sql.condition;

import com.sooncode.jdbc.util.T2E;

/**
 * 排序模型
 * @author pc
 *
 */
public class OrderBy {

	private String orderBy;
	/**
	 * 创建排序模型
	 * @param key 属性名称 
	 * @param sort  DESC：降序 ；ASC：升序。
	 */
	public OrderBy(String key,Sort sort){
		
		this.orderBy= T2E.toColumn(key)+" " + sort.name();
	}	
	
	public String toString (){
		return orderBy;
	}
	
}
