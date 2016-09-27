package com.sooncode.jdbc.service;
import java.util.List;
import org.apache.log4j.Logger;

import com.sooncode.jdbc.dao.JdbcDao;
import com.sooncode.jdbc.dao.JdbcDaoFactory;
import com.sooncode.jdbc.util.Pager;

/**
 * JDBC 服务
 * 
 * @author pc
 * 
 */
public final class JdbcService {

	public final static Logger logger = Logger.getLogger("JdbcService.class");

	/**
	 * 数据处理对象JDBC
	 */
	private JdbcDao jdbcDao;

	JdbcService() {//默认包类可访问
		jdbcDao = JdbcDaoFactory.getJdbcDao();
	}

	JdbcService(String dbKey) { //默认包类可访问
		jdbcDao = JdbcDaoFactory.getJdbcDao(dbKey);
	}

	/**
	 * 获取一个实体(逻辑上只有一个匹配的实体存在)
	 * 
	 * @param obj
	 *            封装的查询条件
	 * @return
	 * @return 实体
	 */
	public Object get(Object obj) {
		return jdbcDao.get(obj);
	}

	/**
	 * 获取多个实体
	 * 
	 * @param obj
	 *            封装的查询条件
	 * @return 实体集
	 */
	public List<?> gets(Object obj) {

		return jdbcDao.gets(obj);
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
		return jdbcDao.getPager(pageNum, pageSize, left, others);
	}

	/**
	 * 保存一个实体对象
	 * 
	 * @param object
	 * @return 保存数量
	 */
	public Long save(Object object) {
		return jdbcDao.save(object);

	}

	/**
	 * 保存多个实体对象
	 * 
	 * @param object
	 * @return 保存数量
	 */
	public void saves(List<Object> objs) {
		// 验证obj
		jdbcDao.saves(objs);

	}

	/**
	 * 保存和更新智能匹配 多个实体
	 * 
	 * @param objs
	 */
	public void saveOrUpdates(List<Object> objs) {
		jdbcDao.saveOrUpdates(objs);
	}

	/**
	 * 保存和更新智能匹配
	 * 
	 * @param obj
	 *            要保存或者更新的对象
	 * @return -1 ：没有更新 也没有保存
	 */
	public Long saveOrUpdate(Object obj) {

		return jdbcDao.saveOrUpdate(obj);

	}

	/**
	 * 修改一个实体对象
	 * 
	 * @param object
	 * @return 更新数量
	 */
	public Long update(Object object) {
		return jdbcDao.update(object);

	}
	
	/**
	 * 更新一个实体
	 * @param oldEntityObject 已存在的实体 （以这个实体为条件查询）
	 * @param newEnityObject 以这个实体的条件更新
	 * @return 成功返回1;失败返回 0.
	 */
	public Long update(Object oldEntityObject,Object newEnityObject) {
		return jdbcDao.update(oldEntityObject, newEnityObject);
		
	}

	/**
	 * 移除一个实体对象
	 * 
	 * @param object
	 * @return 删除数量
	 */
	public Long delete(Object object) {
		return jdbcDao.delete(object);

	}

	 
}