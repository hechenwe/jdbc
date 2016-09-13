package com.sooncode.jdbc.sql;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
 

import com.sooncode.jdbc.reflect.RObject;
import com.sooncode.jdbc.util.T2E;

/**
 * 常见的SQL语句构造类
 * 
 * @author pc
 *
 */
public class ComSQL {
 

	/**
	 * 构造插入数据的可执行的SQL 说明 :1.根据object对象的类名映射成数据库表名.
	 * 2.根据object对象的属性,映射成字段,根据其属性值插入相应数据.
	 * 
	 * @param object
	 *            数据对象
	 * @return 可执行SQL
	 */
	public static Parameter insert(Object object) {
		 
		String tableName = T2E.toColumn(object.getClass().getSimpleName());
		Map<String, Object> map =  new RObject(object).getFiledAndValue();
		String columnString = "(";
		String filedString = "(";
		int n = 0;
		for (Map.Entry<String, Object> entry : map.entrySet()) {

			columnString = columnString + T2E.toColumn(entry.getKey());
			if (entry.getValue() == null) {
				filedString = filedString + "NULL";
			} else {

				if (entry.getValue().getClass().getName().equals("java.util.Date")) {
					filedString = filedString + "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entry.getValue()) + "'";
				} else {
					filedString = filedString + "'" + entry.getValue() + "'";
				}
			}
			if (n != map.size() - 1) {
				columnString += ",";
				filedString += ",";
			} else {
				columnString += ")";
				filedString += ")";
			}
			n++;

		}
		String sqlString = "INSERT INTO " + tableName + columnString + " VALUES " + filedString;
		Parameter p = new Parameter();
		p.setReadySql(sqlString);
		return p;
	}

 
	
	
	/**
	 * 删除
	 * 
	 * @param object
	 * @return
	 */
	public static Parameter delete(Object object) { 
		Parameter p = new Parameter();
		String tableName = T2E.toColumn(object.getClass().getSimpleName());
		Map<String, Object> map = new RObject(object).getFiledAndValue();
		String sql = "DELETE FROM " + tableName + " WHERE ";
		String s = "";
		int n = 0;
		Map<Integer,Object> par = new HashMap<>();
		int index=1;
		for (Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != null) {
				if (n != 0) {
					s = s + " AND ";
				}
				s = s + T2E.toColumn(entry.getKey()) + "=?"; 
				par.put(index,entry.getValue());
				index++;
				n++;
			}
		}
		sql = sql + s;
		 
		p.setReadySql(sql);
		p.setParams(par);
		
		return p;
		
	}

	 
	/**
	 * 获取修改数据的SQL
	 * 
	 * @param obj
	 * @return
	 */
	public static Parameter update(Object object) {
		Parameter p = new Parameter();
		String tableName = T2E.toColumn(object.getClass().getSimpleName());
		Map<String, Object> map = new RObject(object).getFiledAndValue();
		RObject rObject = new RObject(object);
		int n = 0;
		String s = "";
		String pk = T2E.toColumn(rObject.getPk());
		
		String pkString = pk + "=?";// + rObject.getPkValue() + "'";
		Map<Integer,Object> param = new HashMap<>();
		
		int index=1;
		for (Entry<String, Object> entry : map.entrySet()) {
			
			if (entry.getValue() != null && !entry.getKey().trim().equals(rObject.getPk().trim())) {
				if (n != 0) {
					s = s + " , ";
				}
				s = s + T2E.toColumn(entry.getKey()) + "=?" ;//+ entry.getValue() + "'";
				param.put(index,entry.getValue());
				index++;
				n++;
			}
		}
		param.put(param.size()+1, rObject.getPkValue());
		
		String sql = "UPDATE " + tableName + "  SET  " + s + " WHERE " + pkString;
		p.setReadySql(sql);
		p.setParams(param);
		return p;
	}
 
	
	
	/**
	 * 获取查询语句的可执行SQL
	 * 
	 * @param object
	 * @return 可执行SQL
	 */
	public static Parameter select(Object object) {
		String tableName = T2E.toColumn(object.getClass().getSimpleName());
		Map<String, Object> map = new RObject(object).getFiledAndValue();
		int m = 0;
		String s = "1=1";
		String c = "";
		
		Map<Integer,Object> paramet = new HashMap<>();
		Integer index = 1;
		for (Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != null) {
				s = s + " AND ";
				s = s + T2E.toColumn(entry.getKey()) + " = ?";
				paramet.put(index,entry.getValue());
				index++;
			}
			if (m != 0) {
				c = c + ",";
			}
			c = c + T2E.toColumn(entry.getKey());
			m++;
		}
		String sql = "SELECT " + c + " FROM " + tableName + " WHERE " + s;
		//logger.debug("【可执行SQL】:" + sql);
		
		Parameter p = new Parameter();
		p.setReadySql(sql);
		p.setParams(paramet);
		
		return p;
	}
	
	public static String from (Object...objs){
		String from = "";
		int n = 0;
		for (Object o : objs) {
			
			if(n != 0){
				from = from + " , ";
			}
			
			from = from + T2E.toColumn(o.getClass().getSimpleName());
			n++;
		}
		return from;
	}
	
	/**
	 * 
	 * 超级查询
	 * @param object
	 * @return 可执行SQL
	 */
	public static String supSelect(Object ... objs) {
		
		String sql = "SELECT " + columns(objs) + " FROM "+ from(objs) + " WHERE 1=1 " + joint(objs) + where(objs);
		
		return sql;
	}
	
	/**
	 * 
	 * 超级查询 连接分析 
	 * @param object
	 * @return 可执行SQL
	 */
	public static String joint(Object ... objs) {
		
		Map<String,String> map = new HashMap<>();
		for (Object o : objs) {
			RObject rO = new RObject(o);
			map.put(rO.getClassName(),rO.getPk());
		}
		
		String joint = "";
		 
		for(Map.Entry<String, String> e : map.entrySet()){
			
			String fk = T2E.toColumn(e.getValue());
			String value = e.getValue();
			for (Object o : objs) {
				RObject rO = new RObject(o);
				if(rO.hasField(value)  && !e.getKey().equals(rO.getClassName())){
					joint = joint + " AND "	;
					joint = joint +T2E.toColumn(e.getKey())+"."+fk + " = " + T2E.toColumn(rO.getClassName())+"."+ fk;	
					 
				}
			}
		}
		
		return joint;
	}
	
	 
	
	
	/**
	 * 获取字段
	 * @param obj
	 * @return
	 */
	public static String columns (Object object){
		String tableName = T2E.toColumn(object.getClass().getSimpleName());
		Map<String, Object> columns = new RObject(object).getFiledAndValue();
		int m = 0;
		String c = "";
		for (Entry<String, Object> entry : columns.entrySet()) {
			if (m != 0) {
				c = c + ",";
			}
			c = c +tableName + "."+ T2E.toColumn(entry.getKey()) + " AS "+tableName + "_"+ T2E.toColumn(entry.getKey());
			m++;
		}
		return c;
	}
	
	
	/**
	 * 获取字段
	 * @param obj
	 * @return
	 */
	public static String columns (Object...objs){
		 String sql = "";
		 int i = 0;
		 for (Object o : objs) {
			 if(i != 0){
				 sql = sql + " , ";
			 }
			sql = sql + columns(o);
			i++;
		}
		 return sql;
	}
	 
	
	
	/**
	 * 查询条件sql片段
	 * 
	 * @param object
	 * @return 可执行SQL
	 */
	public static Parameter where(Object object) {
		Parameter p = new Parameter();
		String tableName = T2E.toColumn(object.getClass().getSimpleName());
		Map<String, Object> map = new RObject(object).getFiledAndValue();
		String s = "";
		Map<Integer,Object> paramets = new HashMap<>();
		Integer index = 1;
		for (Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != null) {
				s = s + " AND ";
				s = s + tableName + "."+ T2E.toColumn(entry.getKey()) + " = ?";// + entry.getValue() + "'";
				paramets.put(index,entry.getValue());
			}
		}
		p.setReadySql(s);
		p.setParams(paramets);
		return p;
	}
	
	 
	
 
	 
	/**
	 * 获取查询语句的可执行SQL(带分页)
	 * 
	 * @param object
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public static Parameter select(Object object, Long pageNumber, Long pageSize) {
		Long index = (pageNumber - 1) * pageSize;
		Parameter p = select(object);
		String sql = p.getReadySql() + " LIMIT " + index + "," + pageSize;
		p.setReadySql(sql);
		return p;
	}
 
	/**
	 * 获取记录的条数的可执行SQL
	 * 
	 * @param object
	 * @return 可执行SQL
	 */
	public static Parameter selectSize(Object object) {
		Parameter p = new Parameter();
		String tableName = T2E.toColumn(object.getClass().getSimpleName());
		Map<String, Object> map = new RObject(object).getFiledAndValue();
		String s = "1=1";
		Map<Integer,Object> paramet = new HashMap<>();
		int index =1;
		for (Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != null) {
				s = s + " AND ";
				s = s + T2E.toColumn(entry.getKey()) + "=?" ;//+ entry.getValue() + "'";
				paramet.put(index,entry.getValue());
				index ++;
			}
		}
		String sql = "SELECT COUNT(1) AS SIZE" + " FROM " + tableName + " WHERE " + s;
		p.setReadySql(sql);
		p.setParams(paramet);
		return p;
		
	}

 
	/**
	 * 获取记录的条数的可执行SQL
	 * 
	 * @param object
	 * @return 可执行SQL
	 */
	public static Parameter O2OSize(Object left, Object... others) {
		 
		String leftTable = T2E.toColumn(left.getClass().getSimpleName());
		
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < others.length; i++) {
			String simpleName = others[i].getClass().getSimpleName();
			String table = T2E.toColumn(simpleName);
			RObject rObject = new RObject(others[i]);
			String pk = T2E.toColumn(rObject.getPk());
			map.put(table, pk);
		}
		
		String where = "";
		String from = " " + leftTable;
		int m = 0;
		
		 
		for (Map.Entry<String, String> en : map.entrySet()) {
			if (m != 0) {
				where = where + " AND ";
			}
			where = where + leftTable + "." + en.getValue() + " = " + en.getKey() + "." + en.getValue();
			from = from + "," + en.getKey();
			m++;
		}
		
		Parameter leftP = where(left);
		String leftWhereSql  = leftP.getReadySql();
		Map<Integer,Object> paramets =leftP.getParams();
		String leftWhere = leftWhereSql ;
		
		for (Object obj : others) {
			Parameter thisP = where(obj);
			String thisWhereSql  = thisP.getReadySql();
			Map<Integer,Object> thisParamets =thisP.getParams();
			leftWhere = leftWhere + thisWhereSql;
			for(int i = 1;i<=thisParamets.size();i++){
				Object value = thisParamets.get(i);
				paramets.put(paramets.size()+1 ,value);
			}
		}
		
		
		String sql = "SELECT COUNT(1) AS SIZE  FROM " + from + " WHERE " + where + leftWhere;
		leftP.setReadySql(sql);
		leftP.setParams(paramets);
		return leftP;
		
	}

	/**
	 * 获取多对多映射的可执行SQL
	 * 
	 * @param left
	 *            主表对应的实体类
	 * @param middle
	 *            中间表对应的实体类
	 * @param right
	 *            N端对应的实体类
	 * @return 可执行SQL
	 */
	public static Parameter getM2M(Object left, Object middle, Object right, long pageNumber, long pageSize) {
		Parameter p = new Parameter();
		String leftTable = T2E.toColumn(left.getClass().getSimpleName());
		String middleTable = T2E.toColumn(middle.getClass().getSimpleName());
		String rightTable = T2E.toColumn(right.getClass().getSimpleName());

		RObject leftRObject = new RObject(left);
		RObject rightRObject = new RObject(right);

		String leftPk = T2E.toColumn(leftRObject.getPk());
		String rightPk = T2E.toColumn(rightRObject.getPk());
		Map<String, Object> leftFileds = new RObject(left).getFiledAndValue();//EntityCache.getKeyAndValue(left);
		Map<String, Object> rightFileds =new RObject(right).getFiledAndValue();// EntityCache.getKeyAndValue(right);

		String col = "";
		int n = 0;
		for (Map.Entry<String, Object> en : leftFileds.entrySet()) {
			if (n != 0) {
				col = col + " , ";
			}
			col = col + leftTable + "." + T2E.toColumn(en.getKey()) + " AS " + leftTable + "_" + T2E.toColumn(en.getKey());
			n++;
		}
		for (Map.Entry<String, Object> en : rightFileds.entrySet()) {

			col = col + " , " + rightTable + "." + T2E.toColumn(en.getKey()) + " AS " + rightTable + "_" + T2E.toColumn(en.getKey());

		}
		String sql = "SELECT " + col + " FROM " + leftTable + " ," + middleTable + " , " + rightTable;

		sql = sql + " WHERE " + leftTable + "." + leftPk + " = " + middleTable + "." + leftPk + " AND " + rightTable + "." + rightPk + " = " + middleTable + "." + rightPk + " AND " + leftTable + "." + leftPk + "='" + leftFileds.get(T2E.toField(leftPk)) + "'";
		Long index = (pageNumber - 1) * pageSize;
		sql = sql + " LIMIT " + index + "," + pageSize;
        p.setReadySql(sql);
		return p;
	}

	/**
	 * 获取 一对一模型的可执行SQL
	 * 
	 * @param left
	 *            被参照表对应的实体类
	 * @param other
	 *            其他参照表对应的实体类 ,至少有一个实体类
	 * @return 可执行SQL
	 */
	public static Parameter getO2M(Object left, Object right,long pageNumber,long pageSize) {
		 
		Parameter p  = new Parameter();
		String leftTable = T2E.toColumn(left.getClass().getSimpleName());
		String rightTable = T2E.toColumn(right.getClass().getSimpleName());
		RObject leftRObject = new RObject(left);
		String leftPk = T2E.toColumn(leftRObject.getPk());
		Object leftValue = leftRObject.getPkValue();
		Map<String, Object> leftFileds =new RObject(left).getFiledAndValue();// EntityCache.getKeyAndValue(left);
		String col = "";
		int n = 0;
		for (Map.Entry<String, Object> en : leftFileds.entrySet()) {
			if (n != 0) {
				col = col + ",";
			}
			col = col + " " + leftTable + "." + T2E.toColumn(en.getKey()) + " AS " + leftTable + "_" + T2E.toColumn(en.getKey());
			n++;
		}
		 
		Map<String, Object> field = new RObject(right).getFiledAndValue();//EntityCache.getKeyAndValue(right);

		for (Map.Entry<String, Object> en : field.entrySet()) {
			col = col + "," + rightTable + "." + T2E.toColumn(en.getKey()) + " AS " + rightTable + "_" + T2E.toColumn(en.getKey());
		}

		String where = "";
		String from = " " + leftTable;

		where = where + leftTable + "." + leftPk + " = " + rightTable + "." + leftPk;
		where = where +" AND "+leftTable + "." + leftPk + " = '" + leftValue + "'" ;
		from = from + "," + rightTable;

		String sql = "SELECT " + col + " FROM " + from + " WHERE " + where;
		Long index = (pageNumber - 1) * pageSize;
		sql = sql + " LIMIT " + index + "," + pageSize;
		p.setReadySql(sql);
		 
		return p;
	}
	 
	/**
	 * 获取 一对多 关联查询Siz 的SQL  
	 * @param left
	 *            被参照表对应的实体类
	 * @param other
	 *            其他参照表对应的实体类 ,至少有一个实体类
	 * @return 可执行SQL
	 */
	public static Parameter getO2Msize(Object left, Object right) {
		Parameter p = new Parameter();
		
		String leftTable = T2E.toColumn(left.getClass().getSimpleName());
		String rightTable = T2E.toColumn(right.getClass().getSimpleName());
		RObject leftRObject = new RObject(left);
		String leftPk = T2E.toColumn(leftRObject.getPk());
		Object leftValue = leftRObject.getPkValue();
		
		String where = "";
		String from = " " + leftTable;
		
		where = where + leftTable + "." + leftPk + " = " + rightTable + "." + leftPk;
		where = where +" AND "+leftTable + "." + leftPk + " = '" + leftValue + "'" ;
		from = from + "," + rightTable;
		
		String sql = "SELECT COUNT(1) AS SIZE FROM " + from + " WHERE " + where;
		p.setReadySql(sql);
		p.setParams(new HashMap<Integer,Object>());
		return p;
	}

	 
	/**
	 * 获取 一对一模型的可执行SQL
	 * 
	 * @param left
	 *            被参照表对应的实体类
	 * @param other
	 *            其他参照表对应的实体类 ,至少有一个实体类
	 * @return 可执行SQL
	 */
	public static Parameter getO2O(Object left, Object... others) {
		
		String leftTable = T2E.toColumn(left.getClass().getSimpleName());
		
		Map<String, Object> leftFileds = new RObject(left).getFiledAndValue();//EntityCache.getKeyAndValue(left);
		String col = "";
		int n = 0;
		for (Map.Entry<String, Object> en : leftFileds.entrySet()) {
			if (n != 0) {
				col = col + ",";
			}
			col = col + " " + leftTable + "." + T2E.toColumn(en.getKey()) + " AS " + leftTable + "_" + T2E.toColumn(en.getKey());
			n++;
		}
		
		Map<String, String> map = new HashMap<>();
		
		for (Object obj : others) {
			
			String table = T2E.toColumn(obj.getClass().getSimpleName());
			RObject rObject = new RObject(obj);
			String pk = T2E.toColumn(rObject.getPk());
			map.put(table, pk);
			Map<String, Object> field = new RObject(obj).getFiledAndValue();//EntityCache.getKeyAndValue(obj) ;
			
			for (Map.Entry<String, Object> en : field.entrySet()) {
				col = col + "," + table + "." + T2E.toColumn(en.getKey()) + " AS " + table + "_" + T2E.toColumn(en.getKey());
			}
		}
		
		String where = "";
		String from = " " + leftTable;
		int m = 0;
		 
		for (Map.Entry<String, String> en : map.entrySet()) {
			if (m != 0) {
				where = where + " AND ";
			}
			where = where + leftTable + "." + en.getValue() + " = " + en.getKey() + "." + en.getValue();
			from = from + "," + en.getKey();
			m++;
		}
		
		Parameter leftP = where(left);
		String leftWhereSql  = leftP.getReadySql();
		Map<Integer,Object> paramets =leftP.getParams();
		String leftWhere = leftWhereSql ;
		
		for (Object obj : others) {
			Parameter thisP = where(obj);
			String thisWhereSql  = thisP.getReadySql();
			Map<Integer,Object> thisParamets =thisP.getParams();
			leftWhere = leftWhere + thisWhereSql;
			for(int i = 1;i<=thisParamets.size();i++){
				Object value = thisParamets.get(i);
				paramets.put(paramets.size()+1 ,value);
			}
		}
		
		String sql = "SELECT " + col + " FROM " + from + " WHERE " + where + leftWhere;
		
		leftP.setReadySql(sql);
		leftP.setParams(paramets);
		return leftP;
	}

	 
}
