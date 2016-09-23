package com.sooncode.jdbc.dao;

 
import java.util.List;
 
import com.sooncode.jdbc.sql.condition.Cond;
import com.sooncode.jdbc.sql.condition.Conditions;
import com.sooncode.jdbc.util.Pager;
 

/**
 * Jdbc Dao 服务接口
 * 
 * @author pc
 * 
 */
public interface JdbcDaoInterface {
 

	/**
	 * 获取一个实体对象</br>
	 * 理论上只有一个匹配的实体存在。
	 * @param entityObject 查询条件封装在entityObject中。  
	 *        等值查询，多个字段之间用“与”连接（AND）。 
	 *        空值不参与匹配。
	 * @return 实体对象，当存在多个实体时返回空（null）。
	 */
	public Object get(Object entityObject) ;

		 

	/**
	 * 获取多个实体对象
	 * 
	 * @param entityObject 查询条件封装在entityObject中。  
	 *        等值查询，多个字段之间用“与”连接（AND）。 
	 *        空值不参与匹配。
	 * @return 多个实体
	 */
	public List<?> gets(Object entityObject);  

	public List<?> gets(Conditions con ) ; 
	
	
	public List<?> gets(Class<?> entityClass,Cond cond); 

	/**
	 * 分页查询
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param left
	 * @param others
	 * @return
	 */
	public Pager<?> getPager(Long pageNum, Long pageSize, Object leftEntityObject, Object... others) ; 
    
	/**
	 * 分页查询 （单表查询）
	 * @param pageNum
	 * @param pageSize
	 * @param conditions
	 * @return
	 */
	public Pager<?> getPager(Long pageNum, Long pageSize, Conditions conditions)  ;
	/**
	 * 分页查询 （单表查询）
	 * @param pageNum 当前页
	 * @param pageSize 每页数量
	 * @param entityClass 实体类
	 * @param cond 查询条件模型
	 * @return 分页模型 ;参数异常时放回空模型
	 */
	public Pager<?> getPager(Long pageNum, Long pageSize,Class<?> entityClass, Cond cond)  ;
	/**
	 * 保存一个实体对象
	 * 
	 * @param object
	 * @return 一般情况是返回保存的数量（1）,但是，当主键为自增字段时,则返回主键对应的值，当执行出现异常时放回null.
	 */
	public Long save(Object entityObject) ; 

	/**
	 * 保存多个实体对象
	 * 
	 * @param object
	 * @return 保存数量
	 */
	public Boolean saves(List<?> entityObjects) ;

	/**
	 * 保存和更新智能匹配 多个实体
	 * 
	 * @param objs
	 */
	public Boolean saveOrUpdates(List<?> entityObjects)  ;

	/**
	 * 保存和更新智能匹配
	 * 
	 * @param obj
	 *            要保存或者更新的对象
	 * @return 一般情况是返回保存的数量（1）,但是，当主键为自增字段时,则返回主键对应的值，当执行出现异常时放回null；没有更新 也没有保存时返回 -1
	 */
	public Long saveOrUpdate(Object entityObject);  
	/**
	 * 修改一个实体对象
	 * 
	 * @param object
	 * @return 更新数量
	 */
	public Long update(Object entityObject)  ;

	/**
	 * 删除一个实体对象
	 * 
	 * @param object
	 * @return 删除数量
	 */
	public int delete(Object entityObject)  ;
	 
}