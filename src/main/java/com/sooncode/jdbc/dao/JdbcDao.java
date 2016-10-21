package com.sooncode.jdbc.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.Main;

import com.sooncode.jdbc.Jdbc;
import com.sooncode.jdbc.JdbcFactory;
import com.sooncode.jdbc.ToEntity;
import com.sooncode.jdbc.cglib.Cglib;
import com.sooncode.jdbc.constant.JavaBaseType;
import com.sooncode.jdbc.constant.SQL_KEY;
import com.sooncode.jdbc.constant.STRING;
import com.sooncode.jdbc.json.SoonJson;
import com.sooncode.jdbc.reflect.Genericity;
import com.sooncode.jdbc.reflect.RObject;
import com.sooncode.jdbc.sql.ComSQL;
import com.sooncode.jdbc.sql.Parameter;
import com.sooncode.jdbc.sql.condition.Cond;
import com.sooncode.jdbc.sql.condition.Conditions;
import com.sooncode.jdbc.util.Pager;
import com.sooncode.jdbc.util.T2E;
import com.sooncode.jdbc.util.create_entity.Column;
import com.sooncode.jdbc.util.create_entity.TableBuilder;

/**
 * Jdbc Dao 服务
 * 
 * @author pc
 * 
 */
public class JdbcDao implements JdbcDaoInterface {

	public final static Logger logger = Logger.getLogger("JdbcDao.class");

	/**
	 * 数据处理对象JDBC
	 */
	private Jdbc jdbc;

	JdbcDao() {
		jdbc = JdbcFactory.getJdbc();
	}

	JdbcDao(String dbKey) {
		jdbc = JdbcFactory.getJdbc(dbKey);
	}

	public Object get(Object obj) {
		Parameter p = ComSQL.select(obj);
		List<Map<String, Object>> list = jdbc.executeQueryL(p);
		if (list.size() != 1) {
			return null;
		} else {
			Map<String, Object> map = list.get(0);
			return ToEntity.toEntityObject(map, obj.getClass());
		}
	}

	public List<?> gets(Object obj) {
		Parameter p = ComSQL.select(obj);
		List<Map<String, Object>> list = jdbc.executeQueryL(p);
		List<?> objects = ToEntity.findEntityObject(list, obj.getClass());
		return objects;
	}

	public List<?> gets(Conditions con) {
		Object obj = con.getObj();
		String tableName = T2E.toColumn(obj.getClass().getSimpleName());
		String columns = ComSQL.columns(obj);
		Parameter p = con.getWhereSql();
		String sql = SQL_KEY.SELECT + columns + SQL_KEY.FROM + tableName + SQL_KEY.WHERE + SQL_KEY.ONE_EQ_ONE
				+ p.getReadySql();
		p.setReadySql(sql);
		List<Map<String, Object>> list = jdbc.executeQueryL(p);
		List<?> objects = ToEntity.findEntityObject(list, obj.getClass());
		return objects;
	}

	public List<?> gets(Class<?> entityClass, Cond cond) {
		RObject rObj = new RObject(entityClass);
		Object obj = rObj.getObject();
		String tableName = T2E.toColumn(obj.getClass().getSimpleName());
		String columns = ComSQL.columns(obj);
		Parameter p = cond.getParameter();
		String sql = SQL_KEY.SELECT + columns + SQL_KEY.FROM + tableName + SQL_KEY.WHERE + p.getReadySql();
		p.setReadySql(sql);
		List<Map<String, Object>> list = jdbc.executeQueryL(p);
		List<?> objects = ToEntity.findEntityObject(list, obj.getClass());
		return objects;
	}

	public Pager<?> getPager(long pageNum, long pageSize, Object left, Object... others) {
		RObject leftRO = new RObject(left);
		// 1.单表
		if (others.length == 0) {
			Parameter p = ComSQL.select(left, pageNum, pageSize);
			List<Map<String, Object>> list = jdbc.executeQueryL(p);
			Long size = getSize(left, others);

			List<?> lists = ToEntity.findEntityObject(list, left.getClass());
			Pager<?> pager = new Pager<>(pageNum, pageSize, size, lists);
			return pager;

		} else if (others.length == 1) {// 3.一对多

			RObject rightRO = new RObject(others[0]);
			String leftPk = leftRO.getPk();
			String rightPk = rightRO.getPk();

			if (rightRO.hasField(leftPk) && !leftRO.hasField(rightPk)) {
				Parameter p = ComSQL.getO2M(left, others[0], pageNum, pageSize);
				List<Map<String, Object>> list = jdbc.executeQueryL(p);
				Object l = ToEntity.findEntityObject(list, left.getClass());
				List<?> res = ToEntity.findEntityObject(list, others[0].getClass());
				leftRO.invokeSetMethod(leftRO.getListFieldName(others[0].getClass()), res);
				long size = getSize(left, others);
				Pager<?> pager = new Pager<>(pageNum, pageSize, size, l);
				return pager;
			} else {
				return o2o(pageNum, pageSize, left, others);
			}
		} else if (others.length == 2) {// 4.多对多
			String leftPk = new RObject(left).getPk();
			String rightPk = new RObject(others[1]).getPk();
			RObject middle = new RObject(others[0]);
			if (middle.hasField(leftPk) && middle.hasField(rightPk)) {
				Parameter p = ComSQL.getM2M(left, others[0], others[1], pageNum, pageSize);
				List<Map<String, Object>> list = jdbc.executeQueryL(p);
				Object l = ToEntity.findEntityObject(list, left.getClass()).get(0);
				leftRO = new RObject(l);

				Object res = ToEntity.findEntityObject(list, others[1].getClass());
				String listFieldName = leftRO.getListFieldName(others[1].getClass());
				leftRO.invokeSetMethod(listFieldName, res);
				long size = getSize(left, others);
				Pager<?> pager = new Pager<Object>(pageNum, pageSize, size, leftRO.getObject());
				return pager;
			} else { // 一对一
				return o2o(pageNum, pageSize, left, others);
			}

		} else {// 一对一
			return o2o(pageNum, pageSize, left, others);
		}
	}

	public Pager<?> getPager(long pageNum, long pageSize, Conditions conditions) {
		String columns = ComSQL.columns(conditions.getObj());
		Parameter where = conditions.getWhereSql();
		String tableName = T2E.toColumn(conditions.getObj().getClass().getSimpleName());
		Long index = (pageNum - 1) * pageSize;
		String sql = SQL_KEY.SELECT + columns + SQL_KEY.FROM + tableName + SQL_KEY.WHERE + SQL_KEY.ONE_EQ_ONE
				+ where.getReadySql() + SQL_KEY.LIMIT + index + STRING.COMMA + pageSize;
		String sql4size = SQL_KEY.SELECT + SQL_KEY.COUNT + SQL_KEY.AS + SQL_KEY.SIZE + SQL_KEY.FROM + tableName
				+ SQL_KEY.WHERE + SQL_KEY.ONE_EQ_ONE + where.getReadySql();

		Parameter sqlP = new Parameter();
		sqlP.setReadySql(sql);
		sqlP.setParams(where.getParams());

		Parameter sizeP = new Parameter();
		sizeP.setReadySql(sql4size);
		sizeP.setParams(where.getParams());

		List<Map<String, Object>> list = jdbc.executeQueryL(sqlP);
		Long size = (Long) jdbc.executeQueryM(sizeP).get(SQL_KEY.SIZE);
		List<?> lists = ToEntity.findEntityObject(list, conditions.getObj().getClass());
		Pager<?> pager = new Pager<>(pageNum, pageSize, size, lists);
		return pager;
	}

	public Pager<?> getPager(long pageNum, long pageSize, Class<?> entityClass, Cond cond) {

		if (entityClass == null || cond == null || cond.isHaveCond() == false) {
			return null;
		}

		RObject rObj = new RObject(entityClass);
		String columns = ComSQL.columns(rObj.getObject());
		Parameter where = cond.getParameter();
		String tableName = T2E.toColumn(entityClass.getSimpleName());
		Long index = (pageNum - 1) * pageSize;
		String sql = SQL_KEY.SELECT + columns + SQL_KEY.FROM + tableName + SQL_KEY.WHERE + where.getReadySql()
				+ SQL_KEY.LIMIT + index + STRING.COMMA + pageSize;
		String sql4size = SQL_KEY.SELECT + SQL_KEY.COUNT + SQL_KEY.AS + SQL_KEY.SIZE + SQL_KEY.FROM + tableName
				+ SQL_KEY.WHERE + where.getReadySql();

		Parameter sqlP = new Parameter();
		sqlP.setReadySql(sql);
		sqlP.setParams(where.getParams());

		Parameter sizeP = new Parameter();
		sizeP.setReadySql(sql4size);
		sizeP.setParams(where.getParams());

		List<Map<String, Object>> list = jdbc.executeQueryL(sqlP);
		Long size = (Long) jdbc.executeQueryM(sizeP).get(SQL_KEY.SIZE);
		List<?> lists = ToEntity.findEntityObject(list, entityClass);
		Pager<?> pager = new Pager<>(pageNum, pageSize, size, lists);
		return pager;
	}

	public long save(Object object) {
		// 验证obj
		if (ToEntity.isNull(object) == false) {
			return 0L;
		}
		Parameter p = ComSQL.insert(object);
		long n = jdbc.executeUpdate(p);
		return n;

	}

	public boolean saves(List<?> objs) {
		// 验证obj
		if (objs == null) {
			return false;
		}

		for (Object obj : objs) {
			if (ToEntity.isNull(obj) == false) {
				return false;
			}
		}

		List<String> sqls = new LinkedList<>();
		for (Object obj : objs) {
			Parameter p = ComSQL.insert(obj);
			sqls.add(p.getReadySql());
		}
		return jdbc.executeUpdates(sqls);

	}

	public boolean saveOrUpdates(List<?> objs) {
		List<String> sqls = new LinkedList<>();
		String readySql = new String();
		List<Map<Integer, Object>> parameters = new ArrayList<>();
		for (Object object : objs) {
			RObject rObj = new RObject(object);
			Object id = rObj.getPkValue();
			if (id != null) {// obj 有id update();
				Parameter p = ComSQL.update(object);
				readySql = p.getReadySql();
				parameters.add(p.getParams());
			} else {// obj 没有id
				Object oldObj = get(object);
				if (oldObj == null) {
					Parameter p = ComSQL.insert(object);
					sqls.add(p.getReadySql());
				}
			}
		}

		if (readySql.equals(STRING.NULL_STR) && sqls.size() > 0) {
			return jdbc.executeUpdates(sqls);
		} else if (parameters.size() > 0) {
			return jdbc.executeUpdate(readySql, parameters);
		} else {
			return false;
		}

	}

	public long saveOrUpdate(Object obj) {

		RObject rObj = new RObject(obj);
		Object id = rObj.getPkValue();
		if (id != null) {// obj 有id update();
			RObject r = new RObject(obj.getClass());
			r.setPk(id);
			Object newObj = get(r.getObject());
			if (newObj == null) {
				return save(obj);
			} else {
				return update(obj);
			}

		} else {// obj 没有id
			Object oldObj = get(obj);
			if (oldObj == null) {
				return save(obj);// 用obj 去数据库查询 如果不存在 则保存
			} else {
				return -1L;// 用obj 去数据库查询 如何存在 不更新 不保存
			}
		}

	}

	public long update(Object object) {
		if (ToEntity.isNull(object) == false) {
			return 0L;
		}
		Object pkValue = new RObject(object).getPkValue();
		if (pkValue == null) {
			return 0L;
		}
		Parameter p = ComSQL.update(object);
		long n = jdbc.executeUpdate(p);
		return n;

	}

	public long delete(Object object) {
		if (ToEntity.isNull(object) == false) {
			return 0L;
		}
		Parameter p = ComSQL.delete(object);
		long n = jdbc.executeUpdate(p);
		return n;

	}

	/**
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param left
	 * @param others
	 * @return
	 */
	private Pager<?> o2o(Long pageNum, Long pageSize, Object left, Object... others) {
		Parameter p = ComSQL.getO2O(left, others);
		// logger.debug(sql);
		List<Map<String, Object>> list = jdbc.executeQueryL(p);

		String TClassName = left.getClass().getName();// Genericity.getGenericity(this.getClass(),

		List<Object> resultList = new LinkedList<>();

		for (Map<String, Object> m : list) { // 遍历数据库返回的数据集合

			RObject rObject = new RObject(TClassName);
			List<Field> fields = rObject.getFields();// T 对应的属性

			for (Field f : fields) {
				Object obj = m.get(T2E.toField(Genericity.getSimpleName(TClassName).toUpperCase() + STRING.UNDERLINE
						+ T2E.toColumn(f.getName())));
				if (obj != null) {
					rObject.invokeSetMethod(f.getName(), obj);
				}

				String typeSimpleName = f.getType().getSimpleName();
				String typeName = f.getType().getName();
				String baseType = JavaBaseType.map.get(typeSimpleName);
				if (baseType == null) {// 不包含 Integer Long Short Byte Float
										// Double Character Boolean Date String
										// List

					RObject rO = new RObject(typeName);

					List<Field> fs = rO.getFields();

					for (Field ff : fs) {

						Object o = m.get(T2E
								.toField(typeSimpleName.toUpperCase() + STRING.UNDERLINE + T2E.toColumn(ff.getName())));
						if (o != null) {
							rO.invokeSetMethod(ff.getName(), o);
						}

					}

					rObject.invokeSetMethod(f.getName(), rO.getObject());
				}
			}

			resultList.add(rObject.getObject());

		}

		long size = getSize(left, others);
		Pager<?> pager = new Pager<>(pageNum, pageSize, size, resultList);
		return pager;
	}

	public long count(String key, Object obj) {
		String sql = "SELECT COUNT(" + key + ") AS SIZE" + " FROM " + T2E.toTableName(obj.getClass().getSimpleName())
				+ " WHERE 1=1 " + ComSQL.where(obj).getReadySql();
		Parameter p = new Parameter();
		p.setReadySql(sql);
		p.setParams(ComSQL.where(obj).getParams());
		Map<String, Object> map = jdbc.executeQueryM(p);
		Long n = (Long) map.get("SIZE");
		if (n != null && n > 0) {
			return n;
		} else {
			return 0L;
		}
	}

	/**
	 * 获取查询长度
	 * 
	 * @param left
	 * @param others
	 * @return
	 */
	private long getSize(Object left, Object... others) {
		Parameter p = new Parameter();

		if (others.length == 0) { // 单表

			p = ComSQL.selectSize(left);
		} else

		if (others.length == 1) { // 一对多
			RObject leftRO = new RObject(left);
			RObject rightRO = new RObject(others[0]);
			String leftPk = leftRO.getPk();
			String rightPk = rightRO.getPk();

			if (rightRO.hasField(leftPk) && !leftRO.hasField(rightPk)) {
				p = ComSQL.getO2Msize(left, others[0]);
			} else {
				p = ComSQL.O2OSize(left, others);
			}

		} else if (others.length == 2) {// 多对多
			String leftPk = new RObject(left).getPk();
			String rightPk = new RObject(others[1]).getPk();
			RObject middle = new RObject(others[0]);
			if (middle.hasField(leftPk) && middle.hasField(rightPk)) {
				p = ComSQL.selectSize(others[1]);
			} else {
				p = ComSQL.O2OSize(left, others);
			}
		} else {// 一对一
			p = ComSQL.O2OSize(left, others);
		}

		Map<String, Object> map = jdbc.executeQueryM(p);
		Object obj = map.get(SQL_KEY.SIZE);
		if (obj == null) {
			return 0L;
		} else {
			return (long) obj;
		}
	}

	@Override
	public long update(Object oldEntityObject, Object newEnityObject) {
		Object old = this.get(oldEntityObject);
		if (old != null) {
			Object key = new RObject(old).getPkValue();
			RObject rObj = new RObject(newEnityObject);
			rObj.setPk(key);
			long n = this.update(rObj.getObject());
			if (n == 1) {
				return 1L;
			} else {
				return 0L;
			}
		} else {
			return 0L;
		}

	}

	@SuppressWarnings("unchecked")
	public String get4json(String json) {
		Map<String, Object> map = SoonJson.getMap(json);
		String key = new String();
		Object val = new String();
		for (Entry<String, Object> en : map.entrySet()) {
			key = en.getKey();
			val = en.getValue();
			break;
		}

		Map<String, Object> proMap = SoonJson.getMap(val.toString());
		
		String tableName = T2E.toTableName(key);

		TableBuilder tb = new TableBuilder("127.0.0.1", "3306", "root", "hechenwe@gmail.com", "jdbc");
		Map<String, Column> columns = tb.getColumns(tableName);
		HashMap<String, Class<?>> propertyMap = new HashMap<>();
		for (Entry<String, Column> en : columns.entrySet()) {
			Column c = en.getValue();
			String propertyName =T2E.toField(  en.getKey());
			try {
				propertyMap.put(propertyName, Class.forName("java.lang."+c.getJavaDataType()));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		// 生成动态 Bean
		Cglib bean = new Cglib(propertyMap);
		for (Entry<String, Object> en : proMap.entrySet()) {
		bean.setValue(en.getKey(), en.getValue());
		}
		// 获得bean的实体
		Object object = bean.getObject();

		 

	    Parameter  p = 	ComSQL.select(tableName, object);
		 
		List<Map<String,Object>> list =   jdbc.executeQueryL(p) ;
		 
		String str = 	 SoonJson.getJsonArray(list);
		 
		
		return str;
	}
	
	 

}