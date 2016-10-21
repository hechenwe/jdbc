package com.sooncode.jdbc.sql;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sooncode.jdbc.constant.CLASS_NAME;
import com.sooncode.jdbc.constant.DATE_FORMAT;
import com.sooncode.jdbc.constant.SQL_KEY;
import com.sooncode.jdbc.constant.STRING;
import com.sooncode.jdbc.reflect.RObject;
import com.sooncode.jdbc.util.T2E;

/**
 * 通用SQL语句构造
 * 
 * @author pc
 *
 */
public class ComSQL {
	
    private ComSQL(){}
 
	/**
	 * 构造插入数据的可执行的SQL
	 * </br>1.根据object对象的类名映射成数据库表名.
	 * </br>2.根据object对象的属性,映射成字段,根据其属性值插入相应数据.
	 * 
	 * @param object
	 *            数据对象
	 * @return 可执行SQL
	 */
	public static Parameter insert(Object object) {
		 
		String tableName = T2E.toColumn(object.getClass().getSimpleName());
		Map<String, Object> map =  new RObject(object).getFiledAndValue();
		String columnString = SQL_KEY.L_BRACKET;
		String filedString = SQL_KEY.L_BRACKET;
		int n = 0;
		for (Map.Entry<String, Object> entry : map.entrySet()) {

			columnString = columnString + T2E.toColumn(entry.getKey());
			if (entry.getValue() == null) {
				filedString = filedString + SQL_KEY.NULL;
			} else {

				if (entry.getValue().getClass().getName().equals(CLASS_NAME.DATE)) {
					filedString = filedString + STRING.S_QUOTES+ new SimpleDateFormat(DATE_FORMAT.ALL_DATE).format(entry.getValue()) + STRING.S_QUOTES;
				} else {
					filedString = filedString + STRING.S_QUOTES + entry.getValue() + STRING.S_QUOTES;
				}
			}
			if (n != map.size() - 1) {
				columnString += SQL_KEY.COMMA;
				filedString += SQL_KEY.COMMA;
			} else {
				columnString += SQL_KEY.R_BRACKET ;
				filedString += SQL_KEY.R_BRACKET;
			}
			n++;

		}
		String sqlString = SQL_KEY.INSERT + tableName + columnString + SQL_KEY.VALUES + filedString;
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
		String sql = SQL_KEY.DELETE  + tableName + SQL_KEY.WHERE;
		String s = new String ();
		int n = 0;
		Map<Integer,Object> par = new HashMap<>();
		int index=1;
		for (Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != null) {
				if (n != 0) {
					s = s + SQL_KEY.AND;
				}
				s = s + T2E.toColumn(entry.getKey()) +   SQL_KEY.EQ  + STRING.QUESTION; 
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
		 
		String s = new String();
		String pk = T2E.toColumn(rObject.getPk());
		
		String pkString = pk + SQL_KEY.EQ + STRING.QUESTION ; 
		s = s+ pkString;
		Map<Integer,Object> param = new HashMap<>();
		param.put(1, rObject.getPkValue());
		int index=2;
		for (Entry<String, Object> entry : map.entrySet()) {
			
			if (entry.getValue() != null && !entry.getKey().trim().equals(rObject.getPk().trim())) {
				 
				s = s +SQL_KEY.COMMA + T2E.toColumn(entry.getKey())  + SQL_KEY.EQ + STRING.QUESTION ;
				param.put(index,entry.getValue());
				index++;
			}
		}
		param.put(param.size()+1, rObject.getPkValue());
		String sql = SQL_KEY.UPDATE + tableName + SQL_KEY.SET  + s + SQL_KEY.WHERE + pkString;
		p.setReadySql(sql);
		p.setParams(param);
		return p;
	}
 
	
	
	/**
	 * 获取查询语句的可执行SQL (单表)
	 * 
	 * @param object
	 * @return 可执行SQL
	 */
	public static Parameter select(Object object) {
		String tableName = T2E.toColumn(object.getClass().getSimpleName());
		Map<String, Object> map = new RObject(object).getFiledAndValue();
		int m = 0;
		String s = SQL_KEY.ONE_EQ_ONE;
		String c = new String();
		Map<Integer,Object> paramet = new HashMap<>();
		Integer index = 1;
		for (Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != null) {
				s = s +SQL_KEY.AND;
				s = s + tableName+STRING.POINT+ T2E.toColumn(entry.getKey()) +SQL_KEY.EQ +STRING.QUESTION;
				paramet.put(index,entry.getValue());
				index++;
			}
			if (m != 0) {
				c = c + SQL_KEY.COMMA;
			}
			c = c + tableName+STRING.POINT+T2E.toColumn(entry.getKey()) + SQL_KEY.AS +   tableName+STRING.UNDERLINE+T2E.toColumn(entry.getKey());
			m++;
		}
		String sql = SQL_KEY.SELECT  + c  + SQL_KEY.FROM + tableName + SQL_KEY.WHERE + s;
		Parameter p = new Parameter();
		p.setReadySql(sql);
		p.setParams(paramet);
		return p;
	}
	/**
	 * 获取查询语句的可执行SQL (单表)
	 * 
	 * @param object
	 * @return 可执行SQL
	 */
	public static Parameter select(String tableName, Object object) {
		//String tableName = T2E.toColumn(object.getClass().getSimpleName());
		Map<String, Object> map = new RObject(object).getFiledAndValue();
		int m = 0;
		String s = SQL_KEY.ONE_EQ_ONE;
		String c = new String();
		Map<Integer,Object> paramet = new HashMap<>();
		Integer index = 1;
		for (Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey().replace("$cglib_prop_", "");
			if (entry.getValue() != null) {
				s = s +SQL_KEY.AND;
				s = s + tableName+STRING.POINT+ T2E.toColumn(key) +SQL_KEY.EQ +STRING.QUESTION;
				paramet.put(index,entry.getValue());
				index++;
			}
			if (m != 0) {
				c = c + SQL_KEY.COMMA;
			}
			c = c + tableName+STRING.POINT+T2E.toColumn(key) + SQL_KEY.AS +   tableName+STRING.UNDERLINE+T2E.toColumn(key);
			m++;
		}
		String sql = SQL_KEY.SELECT  + c  + SQL_KEY.FROM + tableName + SQL_KEY.WHERE + s;
		Parameter p = new Parameter();
		p.setReadySql(sql);
		p.setParams(paramet);
		return p;
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
		String c = new String();
		for (Entry<String, Object> entry : columns.entrySet()) {
			if (m != 0) {
				c = c + SQL_KEY.COMMA;
			}
			c = c +tableName + STRING.POINT+ T2E.toColumn(entry.getKey()) + SQL_KEY.AS+tableName + STRING.UNDERLINE+ T2E.toColumn(entry.getKey());
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
		 String sql = new String ();
		 int i = 0;
		 for (Object o : objs) {
			 if(i != 0){
				 sql = sql + SQL_KEY.COMMA;
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
		String s = new String ();
		Map<Integer,Object> paramets = new HashMap<>();
		Integer index = 1;
		for (Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != null) {
				s = s + SQL_KEY.AND;
				s = s + tableName +  STRING.POINT + T2E.toColumn(entry.getKey())+SQL_KEY.EQ +STRING.QUESTION ; 
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
		String sql = p.getReadySql() + SQL_KEY.LIMIT + index + STRING.COMMA + pageSize;
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
		String s = SQL_KEY.ONE_EQ_ONE ;
		Map<Integer,Object> paramet = new HashMap<>();
		int index =1;
		for (Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != null) {
				s = s +SQL_KEY.AND;
				s = s + T2E.toColumn(entry.getKey()) + SQL_KEY.EQ + STRING.QUESTION ; 
				paramet.put(index,entry.getValue());
				index ++;
			}
		}
		String sql =  SQL_KEY.SELECT + SQL_KEY.COUNT + SQL_KEY.AS + SQL_KEY.SIZE + SQL_KEY.FROM + tableName +SQL_KEY.WHERE+ s;
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
		
		String where = new String ();
		String from = STRING.SPACING + leftTable;
		int m = 0;
		
		 
		for (Map.Entry<String, String> en : map.entrySet()) {
			if (m != 0) {
				where = where + SQL_KEY.AND;
			}
			where = where + leftTable + STRING.POINT  + en.getValue() + SQL_KEY.EQ  + en.getKey() + STRING.POINT + en.getValue();
			from = from + STRING.COMMA + en.getKey();
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
		
		
		String sql = SQL_KEY.SELECT + SQL_KEY.COUNT + SQL_KEY.AS + SQL_KEY.SIZE + SQL_KEY.FROM  + from + SQL_KEY.WHERE + where + leftWhere;
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

		String col = new String();
		int n = 0;
		for (Map.Entry<String, Object> en : leftFileds.entrySet()) {
			if (n != 0) {
				col = col + SQL_KEY.COMMA;
			}
			col = col + leftTable + STRING.POINT  + T2E.toColumn(en.getKey()) + SQL_KEY.AS+ leftTable + STRING.UNDERLINE + T2E.toColumn(en.getKey());
			n++;
		}
		for (Map.Entry<String, Object> en : rightFileds.entrySet()) {

			col = col + SQL_KEY.COMMA + rightTable + STRING.POINT + T2E.toColumn(en.getKey()) + SQL_KEY.AS + rightTable + STRING.UNDERLINE + T2E.toColumn(en.getKey());

		}
		String sql = SQL_KEY.SELECT  + col + SQL_KEY.FROM + leftTable + SQL_KEY.COMMA + middleTable + SQL_KEY.COMMA + rightTable;

		sql = sql + SQL_KEY.WHERE + leftTable + STRING.POINT + leftPk + SQL_KEY.EQ+ middleTable + STRING.POINT + leftPk + SQL_KEY.AND + rightTable + STRING.POINT + rightPk + SQL_KEY.EQ + middleTable +  STRING.POINT + rightPk + SQL_KEY.AND  + leftTable + STRING.POINT  + leftPk + STRING.EQ + STRING.S_QUOTES   + leftFileds.get(T2E.toField(leftPk)) + STRING.S_QUOTES;
		Long index = (pageNumber - 1) * pageSize;
		sql = sql + SQL_KEY.LIMIT   + index +  STRING.COMMA  + pageSize;
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
		String col = new String();
		int n = 0;
		for (Map.Entry<String, Object> en : leftFileds.entrySet()) {
			if (n != 0) {
				col = col +STRING.COMMA ;
			}
			col = col + STRING.SPACING + leftTable + STRING.POINT + T2E.toColumn(en.getKey()) + SQL_KEY.AS  + leftTable + STRING.UNDERLINE + T2E.toColumn(en.getKey());
			n++;
		}
		 
		Map<String, Object> field = new RObject(right).getFiledAndValue();//EntityCache.getKeyAndValue(right);

		for (Map.Entry<String, Object> en : field.entrySet()) {
			col = col + STRING.COMMA  + rightTable + STRING.POINT + T2E.toColumn(en.getKey()) + SQL_KEY.AS +  rightTable + STRING.UNDERLINE + T2E.toColumn(en.getKey());
		}

		String where = new String();
		String from = STRING.SPACING  + leftTable;

		where = where + leftTable + STRING.POINT + leftPk + SQL_KEY.EQ + rightTable + STRING.POINT+ leftPk;
		where = where +SQL_KEY.AND +leftTable + STRING.POINT + leftPk + SQL_KEY.EQ + STRING.S_QUOTES  + leftValue + STRING.S_QUOTES ;
		from = from + STRING.COMMA  + rightTable;

		String sql = SQL_KEY.SELECT  + col + SQL_KEY.FROM  + from +  SQL_KEY.WHERE  + where;
		Long index = (pageNumber - 1) * pageSize;
		sql = sql +  SQL_KEY. LIMIT  + index + STRING.COMMA + pageSize;
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
		
		String where = new String();
		String from = STRING.SPACING + leftTable;
		
		where = where + leftTable + STRING.POINT + leftPk + SQL_KEY.EQ + rightTable + STRING.POINT + leftPk;
		where = where +SQL_KEY.AND+leftTable + STRING.POINT + leftPk + SQL_KEY.EQ + STRING.S_QUOTES  + leftValue +  STRING.S_QUOTES ;
		from = from + STRING.COMMA + rightTable;
		
		String sql = SQL_KEY.SELECT + SQL_KEY.COUNT + SQL_KEY.AS + SQL_KEY.SIZE + SQL_KEY.FROM + from +  SQL_KEY.WHERE   + where;
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
		String col = new String();
		int n = 0;
		for (Map.Entry<String, Object> en : leftFileds.entrySet()) {
			if (n != 0) {
				col = col +STRING.COMMA ;
			}
			col = col + STRING.SPACING  + leftTable +STRING.POINT  + T2E.toColumn(en.getKey()) +SQL_KEY.AS  + leftTable + STRING.UNDERLINE + T2E.toColumn(en.getKey());
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
				col = col +STRING.COMMA  + table + STRING.POINT  + T2E.toColumn(en.getKey()) + SQL_KEY.AS  + table + STRING.UNDERLINE+ T2E.toColumn(en.getKey());
			}
		}
		
		String where = new String();
		String from = STRING.SPACING + leftTable;
		int m = 0;
		 
		for (Map.Entry<String, String> en : map.entrySet()) {
			if (m != 0) {
				where = where + SQL_KEY.AND ; 
			}
			where = where + leftTable + STRING.POINT  + en.getValue() + SQL_KEY.EQ  + en.getKey() + STRING.POINT+ en.getValue();
			from = from + STRING.COMMA + en.getKey();
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
		
		String sql =SQL_KEY.SELECT  + col + SQL_KEY.FROM + from + SQL_KEY.WHERE  + where + leftWhere;
		
		leftP.setReadySql(sql);
		leftP.setParams(paramets);
		return leftP;
	}

	 
}
