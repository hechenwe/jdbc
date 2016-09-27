package com.sooncode.jdbc.dao;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sooncode.jdbc.Jdbc;
import com.sooncode.jdbc.JdbcFactory;
import com.sooncode.jdbc.ToEntity;
import com.sooncode.jdbc.constant.JavaBaseType;
import com.sooncode.jdbc.constant.SQL_KEY;
import com.sooncode.jdbc.constant.STRING;
import com.sooncode.jdbc.reflect.Genericity;
import com.sooncode.jdbc.reflect.RObject;
import com.sooncode.jdbc.sql.ComSQL;
import com.sooncode.jdbc.sql.Parameter;
import com.sooncode.jdbc.sql.condition.Cond;
import com.sooncode.jdbc.sql.condition.Conditions;
import com.sooncode.jdbc.util.Pager;
import com.sooncode.jdbc.util.T2E;

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
		String sql = SQL_KEY.SELECT  + columns +  SQL_KEY.FROM  + tableName +  SQL_KEY.WHERE+ SQL_KEY.ONE_EQ_ONE   + p.getReadySql();
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
		String sql =  SQL_KEY.SELECT  + columns +  SQL_KEY.FROM  + tableName + SQL_KEY.WHERE  + p.getReadySql();
		p.setReadySql(sql);
		List<Map<String, Object>> list = jdbc.executeQueryL(p);
		List<?> objects = ToEntity.findEntityObject(list, obj.getClass());
		return objects;
	}

	public Pager<?> getPager(Long pageNum, Long pageSize, Object left, Object... others) {
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
				List<?> res =  ToEntity.findEntityObject(list, others[0].getClass());
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

	public Pager<?> getPager(Long pageNum, Long pageSize, Conditions conditions) {
		String columns = ComSQL.columns(conditions.getObj());
		Parameter where = conditions.getWhereSql();
		String tableName = T2E.toColumn(conditions.getObj().getClass().getSimpleName());
		Long index = (pageNum - 1) * pageSize;
		String sql =  SQL_KEY.SELECT  + columns + SQL_KEY.FROM  + tableName +   SQL_KEY.WHERE+SQL_KEY.ONE_EQ_ONE + where.getReadySql() +  SQL_KEY.LIMIT  
				+ index + STRING.COMMA  + pageSize;
		String sql4size =  SQL_KEY.SELECT+SQL_KEY. COUNT + SQL_KEY. AS + SQL_KEY.SIZE+SQL_KEY. FROM  + tableName +  SQL_KEY.WHERE +SQL_KEY.ONE_EQ_ONE  + where.getReadySql();

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

	public Pager<?> getPager(Long pageNum, Long pageSize, Class<?> entityClass, Cond cond) {

		if (pageNum == null || pageSize == null || entityClass == null || cond == null || cond.isHaveCond() == false) {
			return null;
		}

		RObject rObj = new RObject(entityClass);
		String columns = ComSQL.columns(rObj.getObject());
		Parameter where = cond.getParameter();
		String tableName = T2E.toColumn(entityClass.getSimpleName());
		Long index = (pageNum - 1) * pageSize;
		String sql =  SQL_KEY.SELECT  + columns +  SQL_KEY.FROM   + tableName +  SQL_KEY. WHERE  + where.getReadySql() +  SQL_KEY. LIMIT  + index
				+ STRING.COMMA   + pageSize;
		String sql4size = SQL_KEY.SELECT + SQL_KEY.COUNT + SQL_KEY.AS +SQL_KEY.SIZE + SQL_KEY.FROM  + tableName +  SQL_KEY. WHERE   + where.getReadySql();

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

	public Long save(Object object) {
		// 验证obj
		if (ToEntity.isNull(object) == false) {
			return null;
		}
		Parameter p = ComSQL.insert(object);
		Long n = jdbc.executeUpdate(p);
		return n;

	}

	public Boolean saves(List<?> objs) {
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

	public Boolean saveOrUpdates(List<?> objs) {
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

		if (readySql.equals("") && sqls.size() > 0) {
			return jdbc.executeUpdates(sqls);
		} else if (parameters.size() > 0) {
			return jdbc.executeUpdate(readySql, parameters);
		} else {
			return false;
		}

	}

	public Long saveOrUpdate(Object obj) {

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

	public Long update(Object object) {
		if (ToEntity.isNull(object) == false) {
			return 0L;
		}
		Parameter p = ComSQL.update(object);
		Long n = jdbc.executeUpdate(p);
		return n;

	}

	public Long delete(Object object) {
		if (ToEntity.isNull(object) == false) {
			return 0L;
		}
		Parameter p = ComSQL.delete(object);
		Long n = jdbc.executeUpdate(p)  ;
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
				Object obj = m.get(T2E
						.toField(Genericity.getSimpleName(TClassName).toUpperCase() + STRING.UNDERLINE + T2E.toColumn(f.getName())));
				if (obj != null) {
					rObject.invokeSetMethod(f.getName(), obj);
				}

				String typeSimpleName = f.getType().getSimpleName();
				String typeName = f.getType().getName();
				String baseType = JavaBaseType.map.get(typeSimpleName);
				if ( baseType==null  ) {//不包含 Integer Long Short Byte Float Double Character Boolean Date String List

					RObject rO = new RObject(typeName);

					List<Field> fs = rO.getFields();

					for (Field ff : fs) {

						Object o = m.get(T2E.toField(typeSimpleName.toUpperCase() + STRING.UNDERLINE + T2E.toColumn(ff.getName())));
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

	/**
	 * 获取查询长度
	 * 
	 * @param left
	 * @param others
	 * @return
	 */
	private Long getSize(Object left, Object... others) {
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
		return (Long) obj;

	}
     
	@Override
	public Long update(Object oldEntityObject, Object newEnityObject) {
		Object old = this.get(oldEntityObject);
		if(old != null){
			Object key = new RObject(old).getPkValue();
			RObject rObj = new RObject(newEnityObject);
			rObj.setPk(key);
			Long n = this.update(rObj.getObject());
			if(n!=null && n ==1){
				return 1L;
			}else{
				return 0L;
			}
		}else{
			return 0L;
		}
		 
	}

	
}