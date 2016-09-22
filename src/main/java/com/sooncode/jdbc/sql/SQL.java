package com.sooncode.jdbc.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sooncode.jdbc.reflect.RObject;
import com.sooncode.jdbc.sql.condition.Cond;
import com.sooncode.jdbc.util.T2E;

/**
 * SQL工具类 注意: 对象的类名 与 数据库表名"一致"
 * 
 * @author pc
 *
 */
public class SQL {

	/** 预编译SQL */
	private String readySql = "";

	/** 参数 ，从1开始 */
	private Map<Integer, Object> params = new HashMap<>();

	public String toString() {
		return this.readySql;
	}

	public Parameter getParameter() {
		Parameter p = new Parameter();
		p.setReadySql(this.readySql);
		p.setParams(params);
		return p;
	}

	public SQL SELECT() {
		this.readySql = "SELECT * ";
		return this;
	}

	public SQL SELECT(String... keys) {
		String k = "";
		for (int i = 0; i < keys.length; i++) {
			String key = T2E.toColumn(keys[i]);
			if (i == 0) {
				k = k + " " + key;
			} else {
				k = k + " , " + key;
			}
		}

		this.readySql = "SELECT " + k;
		return this;
	}

	public SQL SELECT(Class<?> clas) {

		RObject rObject = new RObject(clas);
		Map<String, Object> columns = rObject.getFiledAndValue();
		int m = 0;
		String c = "";
		for (Entry<String, Object> entry : columns.entrySet()) {

			if (m != 0) {
				c = c + " , ";
			}
			c = c + T2E.toColumn(entry.getKey());
			m++;
		}
		this.readySql = "SELECT " + c;
		return this;
	}

	public SQL FROM(Class<?> clas) {

		this.readySql = this.readySql + " FROM " + T2E.toColumn(clas.getSimpleName());
		return this;
	}

	public SQL FROM(String entityName) {

		this.readySql = this.readySql + " FROM " + T2E.toColumn(entityName);
		return this;
	}

	public SQL WHERE() {

		this.readySql = this.readySql + " WHERE ";
		return this;
	}

	public SQL WHERE(Cond cond) {

		this.readySql = this.readySql + " WHERE " + cond.getParameter().getReadySql();
		this.params = cond.getParameter().getParams();
		return this;
	}

	/**
	 * 添加关键字到SQL中
	 * 
	 * @param key
	 *            关键字 集
	 * @return SelSql 对象
	 */
	public SQL PUT_KEY(String key) {

		this.readySql = this.readySql + " " + key;
		return this;
	}

	public SQL AND() {

		this.readySql = this.readySql + " AND ";
		return this;
	}

	public SQL OR() {

		this.readySql = this.readySql + " OR ";
		return this;
	}

	/**
	 * 求和
	 * 
	 * @param fieldName
	 * @return
	 */
	public SQL SUM(String fieldName) {
		String column = T2E.toColumn(fieldName);
		String str = "SUM(" + column + ") ";
		this.readySql = this.readySql + str;
		return this;
	}

	/**
	 * 求平均
	 * 
	 * @param fieldName
	 * @return
	 */
	public SQL AVG(String fieldName) {
		String column = T2E.toColumn(fieldName);
		String str = "AVG(" + column + ") AS AVG" + column + " ";
		this.readySql = this.readySql + str;
		return null;
	}

	/**
	 * 求最大值
	 * 
	 * @param fieldName
	 * @return
	 */
	public SQL MAX(String fieldName) {
		String column = T2E.toColumn(fieldName);
		String str = "MAX(" + column + ") AS MAX" + column + " ";
		this.readySql = this.readySql + str;
		return null;
	}

	/**
	 * 求最小值
	 * 
	 * @param fieldName
	 * @return
	 */
	public SQL MIN(String fieldName) {
		String column = T2E.toColumn(fieldName);
		String str = "MIN(" + column + ") AS MIN" + column + " ";
		this.readySql = this.readySql + str;
		return null;
	}

	/**
	 * 等于
	 * 
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public SQL EQ(String fieldName, Object value) {
		String sql = "";
		if (value != null) {
			sql = " " + T2E.toColumn(fieldName) + "='" + value + "'";
		}
		this.readySql = this.readySql + sql;
		return this;
	}

	/**
	 * 大于
	 * 
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public SQL GT(String fieldName, Object value) {
		String sql = "";
		if (value != null) {
			sql = " " + T2E.toColumn(fieldName) + ">'" + value + "'";
		}
		this.readySql = this.readySql + sql;
		return this;
	}

	/**
	 * 小于
	 * 
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public SQL LT(String fieldName, Object value) {
		String sql = "";
		if (value != null) {
			sql = " " + T2E.toColumn(fieldName) + "<'" + value + "'";
		}
		this.readySql = this.readySql + sql;
		return this;
	}

	/**
	 * 大于等于
	 * 
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public SQL GTEQ(String fieldName, Object value) {
		String sql = "";
		if (value != null) {
			sql = " " + T2E.toColumn(fieldName) + ">='" + value + "'";
		}
		this.readySql = this.readySql + sql;
		return this;
	}

	/**
	 * 小于等于
	 * 
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public SQL LTEQ(String fieldName, Object value) {
		String sql = "";
		if (value != null) {
			sql = " " + T2E.toColumn(fieldName) + "<='" + value + "'";
		}
		this.readySql = this.readySql + sql;
		return this;
	}

	/**
	 * 排序语句片段
	 * 
	 * @param fields
	 *            需要排序的属性集
	 * @return SelSql 对象
	 */
	public SQL ORDER_BY(String... fields) {
		String str = "";
		int n = 0;
		for (String f : fields) {
			if (n != 0) {
				str = str + ",";
			}
			str = str + T2E.toColumn(f);
			n++;
		}
		this.readySql = this.readySql + " ORDER BY " + str;
		return this;
	}

	/**
	 * 降序
	 * 
	 * @return
	 */
	public SQL DESC() {
		String str = " DESC ";
		this.readySql = this.readySql + str;
		return this;
	}

	/**
	 * 匹配前部分
	 * 
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public SQL LIKE_THIS(String fieldName, Object value) {
		String sql = "";
		if (value != null) {
			sql = " AND " + T2E.toColumn(fieldName) + " LIKE '%" + value + "'";
		}
		this.readySql = this.readySql + sql;
		return this;
	}

	/**
	 * 匹配后部分
	 * 
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public SQL THIS_LIKE(String fieldName, Object value) {
		String sql = "";
		if (value != null) {
			sql = " AND " + T2E.toColumn(fieldName) + " LIKE '" + value + "%'";
		}
		this.readySql = this.readySql + sql;
		return this;
	}

	public SQL LIKE(String fieldName, Object value) {
		String sql = "";
		if (value != null) {
			sql = " " + T2E.toColumn(fieldName) + " LIKE '%" + value + "%'";
		}
		this.readySql = this.readySql + sql;
		return this;
	}

	public SQL BETWEEN(String fieldName, Object value1, Object value2) {
		String sql = "";
		if (value1 != null && value2 != null) {
			sql = " AND " + T2E.toColumn(fieldName) + " BETWEEN '" + value1 + "' AND '" + value2 + "'";
		}

		this.readySql = this.readySql + sql;
		return this;
	}

	public SQL COUNT(String key) {
		this.readySql = this.readySql + "COUNT(" + key + ") ";
		return this;
	}

	public SQL AS(String key) {
		this.readySql = this.readySql + "AS " + T2E.toColumn(key) + " ";
		return this;
	}

	public SQL GROUP_BY(String key) {
		this.readySql = this.readySql + " GROUP BY " + T2E.toColumn(key) + " ";
		return this;
	}

	public SQL DISTINCT(String key) {
		this.readySql = this.readySql + " DISTINCT " + T2E.toColumn(key) + " ";
		return this;
	}

	/**
	 * mysql 时间函数
	 * 
	 * @param key
	 *            属性
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @return
	 */
	public SQL DATE(String key, String startDate, String endDate) {
		this.readySql = this.readySql + " DATE(" + T2E.toColumn(key) + ")BETWEEN '" + startDate + "' AND '" + endDate
				+ "'";
		return this;
	}

	/**
	 * 分页功能
	 * 
	 * @param pageNumber
	 *            第几页
	 * @param pageSize
	 *            每页长度
	 * @return Sql 对象
	 */
	public SQL LIMIT(Long pageNumber, Long pageSize) {
		Long index = (pageNumber - 1) * pageSize;
		String sql = " LIMIT " + index + "," + pageSize;
		this.readySql = this.readySql + sql;
		return this;

	}

}
