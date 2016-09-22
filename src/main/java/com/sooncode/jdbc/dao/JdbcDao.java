package com.sooncode.jdbc.dao;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sooncode.jdbc.Jdbc;
import com.sooncode.jdbc.JdbcFactory;
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
public class JdbcDao {

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

	/**
	 * 获取一个实体(逻辑上只有一个匹配的实体存在)
	 * 
	 * @param obj 封装的查询条件
	 * @return
	 * @return 实体
	 */
	public Object get(Object obj) {

		Parameter  p = ComSQL.select(obj) ;
		List<Map<String, Object>> list = jdbc.executeQueryL(p);
		@SuppressWarnings("unchecked")
		List<Object> rList = (List<Object>) findObject(list, obj.getClass());
		if (rList.size() == 1) {
			return rList.get(0);
		}
		return null;
	}

	/**
	 * 获取多个实体
	 * 
	 * @param obj
	 *            封装的查询条件
	 * @return 实体集
	 */
	public List<?> gets(Object obj) {
		Parameter p = ComSQL.select(obj);
		List<Map<String, Object>> list = jdbc.executeQueryL(p);
		List<?> objects = findObject(list, obj.getClass());
		return objects;
	}

	public List<?> gets(Conditions con ) {
		Object obj = con.getObj();
		String tableName = T2E.toColumn(obj.getClass().getSimpleName());
		String columns = ComSQL.columns(obj);
		Parameter p = con.getWhereSql();
		String sql = "SELECT " + columns + " FROM " + tableName + " WHERE 1=1 " + p.getReadySql();
		p.setReadySql(sql);
		List<Map<String, Object>> list = jdbc.executeQueryL(p);
		List<?> objects = findObject(list, obj.getClass());
		return objects;
	}
	
	
	public List<?> gets(Class<?> entityClass,Cond cond) {
		RObject rObj = new RObject(entityClass);
		Object obj = rObj.getObject();
		String tableName = T2E.toColumn(obj.getClass().getSimpleName());
		String columns = ComSQL.columns(obj);
		Parameter p = cond.getParameter();
		String sql = "SELECT " + columns + " FROM " + tableName + " WHERE " + p.getReadySql();
		p.setReadySql(sql);
		List<Map<String, Object>> list = jdbc.executeQueryL(p);
		List<?> objects = findObject(list, obj.getClass());
		return objects;
	}

	/**
	 * 分页查询
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param left
	 * @param others
	 * @return
	 */
	public Pager<?> getPager(Long pageNum, Long pageSize, Object left, Object... others) {
		RObject leftRO = new RObject(left);
		// 1.单表
		if (others.length == 0) {
			Parameter  p = ComSQL.select(left, pageNum, pageSize);
			List<Map<String, Object>> list = jdbc.executeQueryL(p);
			Long size = getSize(left, others);

			List<?> lists = findObject(list, left.getClass());
			Pager<?> pager = new Pager<>(pageNum, pageSize, size, lists);
			return pager;

		} else if (others.length == 1) {// 3.一对多

			RObject rightRO = new RObject(others[0]);
			String leftPk = leftRO.getPk();
			String rightPk = rightRO.getPk();

			if (rightRO.hasField(leftPk) && !leftRO.hasField(rightPk)) {
				Parameter p = ComSQL.getO2M(left, others[0], pageNum, pageSize);
				List<Map<String, Object>> list = jdbc.executeQueryL(p);
				Object l = findObject(list, left.getClass());
				@SuppressWarnings("unchecked")
				List<Object> res = (List<Object>) findObject(list, others[0].getClass());
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
				Object l = findObject(list, left.getClass()).get(0);
				leftRO = new RObject(l);

				Object res = findObject(list, others[1].getClass());
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
    
	/**
	 * 分页查询 （单表查询）
	 * @param pageNum
	 * @param pageSize
	 * @param conditions
	 * @return
	 */
	public Pager<?> getPager(Long pageNum, Long pageSize, Conditions conditions) {
		String columns = ComSQL.columns(conditions.getObj());
		Parameter where = conditions.getWhereSql();
		String tableName = T2E.toColumn(conditions.getObj().getClass().getSimpleName());
		Long index = (pageNum - 1) * pageSize;
		String sql = "SELECT " + columns + " FROM " + tableName + " WHERE 1=1 " + where.getReadySql() + " LIMIT " + index + "," + pageSize;
		String sql4size = "SELECT COUNT(1) AS SIZE  FROM " + tableName + " WHERE 1=1 " + where.getReadySql();
		logger.debug("[可执行SQL] " + sql);
		logger.debug("[可执行SQL] " + sql4size);
		Parameter sqlP = new Parameter();
		sqlP.setReadySql(sql);
		sqlP.setParams(where.getParams());
		
		Parameter sizeP = new Parameter();
		sizeP.setReadySql(sql4size);
		sizeP.setParams(where.getParams());
		 
		List<Map<String, Object>> list = jdbc.executeQueryL(sqlP);
		Long size = (Long) jdbc.executeQueryM(sizeP).get("size");
		List<?> lists = findObject(list, conditions.getObj().getClass());
		Pager<?> pager = new Pager<>(pageNum, pageSize, size, lists);
		return pager;
	}
	/**
	 * 分页查询 （单表查询）
	 * @param pageNum 当前页
	 * @param pageSize 每页数量
	 * @param entityClass 实体类
	 * @param cond 查询条件模型
	 * @return 分页模型 ;参数异常时放回空模型
	 */
	public Pager<?> getPager(Long pageNum, Long pageSize,Class<?> entityClass, Cond cond) {
		
		if(pageNum ==null || pageSize == null || entityClass == null || cond == null || cond.isHaveCond()== false){
			return null;
		}
		
		RObject rObj = new RObject(entityClass);
		String columns = ComSQL.columns(rObj.getObject());
		Parameter where = cond.getParameter();
		String tableName = T2E.toColumn(entityClass.getSimpleName());
		Long index = (pageNum - 1) * pageSize;
		String sql = "SELECT " + columns + " FROM " + tableName + " WHERE " + where.getReadySql() + " LIMIT " + index + "," + pageSize;
		String sql4size = "SELECT COUNT(1) AS SIZE  FROM " + tableName + " WHERE " + where.getReadySql();
		logger.debug("[可执行SQL] " + sql);
		logger.debug("[可执行SQL] " + sql4size);
		Parameter sqlP = new Parameter();
		sqlP.setReadySql(sql);
		sqlP.setParams(where.getParams());
		
		Parameter sizeP = new Parameter();
		sizeP.setReadySql(sql4size);
		sizeP.setParams(where.getParams());
		
		List<Map<String, Object>> list = jdbc.executeQueryL(sqlP);
		Long size = (Long) jdbc.executeQueryM(sizeP).get("size");
		List<?> lists = findObject(list, entityClass);
		Pager<?> pager = new Pager<>(pageNum, pageSize, size, lists);
		return pager;
	}

	/**
	 * 保存一个实体对象
	 * 
	 * @param object
	 * @return 一般情况是返回保存的数量（1）,但是，当主键为自增字段时,则返回主键对应的值，当执行出现异常时放回null.
	 */
	public Long save(Object object) {
		// 验证obj
		if (isNull(object) == false) {
			return null;
		}
		Parameter p  = ComSQL.insert(object);
		Long n = jdbc.executeUpdate(p);
		return n;

	}

	/**
	 * 保存多个实体对象
	 * 
	 * @param object
	 * @return 保存数量
	 */
	public Boolean saves(List<?> objs) {
		// 验证obj
		if (objs == null) {
			return false;
		}

		for (Object obj : objs) {
			if (isNull(obj) == false) {
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

	/**
	 * 保存和更新智能匹配 多个实体
	 * 
	 * @param objs
	 */
	public Boolean saveOrUpdates(List<?> objs) {
		List<String> sqls = new LinkedList<>();
		String readySql = "";
		List<Map<Integer,Object>> parameters = new ArrayList<>();
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

		if(readySql.equals("")&&sqls.size()>0){
			return jdbc.executeUpdates(sqls);
		}else if(parameters.size()>0){
			return jdbc.executeUpdates(readySql,parameters);
		}else{
			return false;
		}
		
	}

	/**
	 * 保存和更新智能匹配
	 * 
	 * @param obj
	 *            要保存或者更新的对象
	 * @return 一般情况是返回保存的数量（1）,但是，当主键为自增字段时,则返回主键对应的值，当执行出现异常时放回null；没有更新 也没有保存时返回 -1
	 */
	public Long saveOrUpdate(Object obj) {

		RObject rObj = new RObject(obj);
		Object id = rObj.getPkValue();
		if (id != null) {// obj 有id update();
			RObject r = new RObject(obj.getClass());
			r.setPk(id);
			Object newObj = get(r.getObject());
			if(newObj == null){
				return save(obj);
			}else {
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

	/**
	 * 修改一个实体对象
	 * 
	 * @param object
	 * @return 更新数量
	 */
	public Long update(Object object) {
		if (isNull(object) == false) {
			return 0L;
		}
		Parameter p = ComSQL.update(object);
		Long n = jdbc.executeUpdate(p);
		return n;

	}

	/**
	 * 删除一个实体对象
	 * 
	 * @param object
	 * @return 删除数量
	 */
	public int delete(Object object) {
		if (isNull(object) == false) {
			return 0;
		}
		Parameter p = ComSQL.delete(object);
		int n = new Long(jdbc.executeUpdate(p)).intValue();
		return n;

	}

	/**
	 * 验证 object是否为空 或 其属性是否全为空
	 * 
	 * @param object
	 *            被验证的实体
	 * @return
	 */
	private boolean isNull(Object object) {
		if (object == null) {
			return false;
		}
		// obj的属性值不全为null
		RObject rObj = new RObject(object);
		Map<String, Object> files = rObj.getFiledAndValue();
		boolean b = false;
		for (Map.Entry<String, Object> en : files.entrySet()) {
			if (en.getValue() == null) {
				b = b || false;
			} else {
				b = b || true;
			}
		}

		if (b == false) {
			return false;
		} else {
			return true;
		}
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
		Parameter p = ComSQL.getO2O (left, others);
		// logger.debug(sql);
		List<Map<String, Object>> list = jdbc.executeQueryL(p);

		String str = "Integer Long Short Byte Float Double Character Boolean Date String List";

		String TClassName = left.getClass().getName();// Genericity.getGenericity(this.getClass(),
														// 0);// 泛型T实际运行时的全类名

		List<Object> resultList = new LinkedList<>();

		for (Map<String, Object> m : list) { // 遍历数据库返回的数据集合

			RObject rObject = new RObject(TClassName);
			List<Field> fields = rObject.getFields();// T 对应的属性

			for (Field f : fields) {
				Object obj = m.get(T2E.toField(Genericity.getSimpleName(TClassName).toUpperCase() + "_" + T2E.toColumn(f.getName())));
				if (obj != null) {
					rObject.invokeSetMethod(f.getName(), obj);
				}

				String typeSimpleName = f.getType().getSimpleName();
				String typeName = f.getType().getName();
				if (!str.contains(typeSimpleName)) {

					RObject rO = new RObject(typeName);

					List<Field> fs = rO.getFields();

					for (Field ff : fs) {

						Object o = m.get(T2E.toField(typeSimpleName.toUpperCase() + "_" + T2E.toColumn(ff.getName())));
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
		//String sql = "";
		if (others.length == 0) { // 单表
			//sql = ComSQL.selectSize(left);
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

		
		
		//Map<String, Object> map = jdbc.executeQueryM(sql);
		Map<String, Object> map = jdbc.executeQueryM(p);
		Object obj = map.get("size");
		return (Long) obj;

	}

	/**
	 * 抓取对象
	 * 
	 * @param list
	 * @param clas
	 * @return List对象 ,或简单对象
	 */
	private List<?> findObject(List<Map<String, Object>> list, Class<?> clas) {
		List<Object> objects = new LinkedList<>();
		for (Map<String, Object> map : list) {
			try {
				Object object = clas.newInstance();
				Field[] fields = clas.getDeclaredFields();
				for (Field field : fields) {
					String fieldName = field.getName();
					String key = T2E.toField(T2E.toColumn(clas.getSimpleName()) + "_" + T2E.toColumn(fieldName));
					Object value = map.get(key);
					if (value == null) {
						value = map.get(fieldName);
						if (value == null) {
							continue;
						}
					}
					PropertyDescriptor pd = new PropertyDescriptor(fieldName, clas);
					Method method = pd.getWriteMethod();
					method.invoke(object, value);
				}
				if (objects.size() >= 1 && object.toString().equals(objects.get(objects.size() - 1).toString())) {
					continue;
				}
				objects.add(object);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return objects;
	}
}