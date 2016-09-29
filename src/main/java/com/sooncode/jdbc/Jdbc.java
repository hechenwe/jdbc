package com.sooncode.jdbc;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Date;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.sooncode.jdbc.constant.DATA;
import com.sooncode.jdbc.constant.DATE_FORMAT;
import com.sooncode.jdbc.constant.STRING;
import com.sooncode.jdbc.db.DBs;
import com.sooncode.jdbc.result.ResultMap;
import com.sooncode.jdbc.sql.Parameter;
import com.sooncode.jdbc.sql.verification.SqlVerification;

/**
 * 执行SQL语句核心类
 * 
 * @author pc
 *
 */
public class Jdbc {

	public final static Logger logger = Logger.getLogger("Jdbc.class");

	/**
	 * 数据库资源Key 默认是：default 关键字； 代表一组数据库连接参数
	 */
	private String dbKey = DATA.DEFAULT_KEY;

	/**
	 * 
	 * @param dbKey
	 *            数据源关键字
	 * 
	 */
	Jdbc(String dbKey) {
		if (dbKey != null && !dbKey.trim().equals(STRING.NULL_STR)) {
			this.dbKey = dbKey;
		}

	}

	Jdbc() {
		// 默认使用default 数据库连接参数
	}

	/**
	 * 执行更新语句：添加，删除，修改。 可防止SQL注入，推荐使用。
	 * 
	 * @param connection
	 *            数据库连接
	 * 
	 * @param sql
	 *            可执行的更新语句
	 * 
	 * @return 一般情况是返回受影响的行数,当有主键为自增字段,在添加数据时返回 自增值。当执行出现异常时放回null.
	 */
	public Long executeUpdate(Parameter p) {
		if (p == null) {
			return null;
		}

		if (p.isNotException() == false) {
			logger.debug("【JDBC】:预编译SQL和参数出现异常！");
			return null;
		}
		String sql = p.getReadySql();
		logger.debug("【JDBC】 预编译SQL: " + p.getFormatSql());
		logger.debug("【JDBC】 预编译SQL对应的参数: " + p.getParams());
		if (SqlVerification.isUpdateSql(sql) == false) {
			logger.debug("【JDBC】SQL语句不是更新语句：" + p.getFormatSql());
			return null;
		}

		Connection connection = DBs.getConnection(this.dbKey);
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Long n = 0L;
		try {
			preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			for (Entry<Integer, Object> en : p.getParams().entrySet()) {
				Integer index = en.getKey();
				Object obj = en.getValue();
				preparedStatementSet(preparedStatement, index, obj);
			}
			n = (long) preparedStatement.executeUpdate();
			resultSet = preparedStatement.getGeneratedKeys(); // 获取ID
			if (resultSet.next()) {
				Long id = resultSet.getLong(1);
				return id;
			} else {
				return n;
			}
		} catch (SQLException e) {
			logger.debug("【JDBC】 SQL语句执行异常 : " + p.getFormatSql());
			return null;
		} finally {
			DBs.close(resultSet, preparedStatement, connection);
		}
	}

	/**
	 * 批量执行更新语句(静态SQL语句)
	 * 
	 * @param sqls
	 *            可执行更新的SQL语句集合
	 * 
	 * @return 成功返回true ,失败返回 false.
	 */
	public Boolean executeUpdates(List<String> sqls) {
		for (String sql : sqls) {
			if (SqlVerification.isUpdateSql(sql) == false) {
				logger.debug("【JDBC】SQL语句不是更新语句：" + Parameter.getFormatSql(sql));
				return false;
			}

		}

		Connection connection = DBs.getConnection(this.dbKey);
		try {
			connection.setAutoCommit(false);

			Statement statement = connection.createStatement();
			for (String sql : sqls) {
				logger.debug("【JDBC】可执行SQL:" + Parameter.getFormatSql(sql));
				statement.addBatch(sql);
			}
			statement.executeBatch(); // 执行批处理
			connection.commit();
			DBs.close(statement, connection);
			return true;
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				logger.error("【JDBC】 事务回滚失败 !  " + e1.getMessage());
			}
			logger.error("【JDBC】 批量执行失败 !");
			return false;
		}

	}

	/**
	 * 执行更新语句
	 * 
	 * @param readySql
	 *            预编译SQL
	 * @param parameters
	 *            预编译SQL需要的参数
	 * @return 执行成功返回true;执行失败返回false.
	 */
	public Boolean executeUpdate(String readySql, List<Map<Integer, Object>> parameters) {
		logger.debug("【JDBC】 预编译SQL:" + Parameter.getFormatSql(readySql));
		logger.debug("【JDBC】 预编译SQL对应的参数: " + parameters);
		if (SqlVerification.isUpdateSql(readySql) == false) {
			logger.debug("【JDBC】SQL语句不是更新语句：" + Parameter.getFormatSql(readySql));
			return false;
		}
		Connection connection = DBs.getConnection(this.dbKey);
		try {
			connection.setAutoCommit(false);
			PreparedStatement ps = connection.prepareStatement(readySql);
			for (Map<Integer, Object> p : parameters) {
				for (int i = 1; i <= p.size(); i++) {
					Object value = p.get(i);
					preparedStatementSet(ps, i, value);
				}
				ps.addBatch();
			}
			ps.executeBatch(); // 执行批处理
			connection.commit();
			DBs.close(ps, connection);
			return true;
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {

			}
			logger.error("【JDBC】 批量执行失败 !");
			return false;
		}

	}

	/**
	 * 执行查询语句(可能有多条记录)。 可防止SQL注入，推荐使用。
	 * 
	 * @parameter 参数模型
	 * @return List
	 */
	public List<Map<String, Object>> executeQueryL(Parameter parameter) {
		logger.debug("【JDBC】 预编译SQL:" + parameter.getFormatSql());
		logger.debug("【JDBC】 预编译SQL对应的参数: " + parameter.getParams());
		if (SqlVerification.isSelectSql(parameter.getReadySql()) == false) {
			logger.debug("【JDBC】SQL语句不是查询语句：" + parameter.getFormatSql());
			return new LinkedList<>();
		}
		Connection connection = DBs.getConnection(this.dbKey);
		List<Map<String, Object>> resultList = new LinkedList<>();

		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(parameter.getReadySql());

			for (Entry<Integer, Object> en : parameter.getParams().entrySet()) {
				Integer index = en.getKey();
				Object obj = en.getValue();
				preparedStatementSet(preparedStatement, index, obj);
			}

			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Map<String, Object> map = new HashMap<>();

				ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
				int columnCount = resultSetMetaData.getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					String columnName = resultSetMetaData.getColumnLabel(i).toUpperCase();// 获取别名
					Object columnValue = resultSet.getObject(i);
					map.put(columnName, columnValue);
				}
				resultList.add(map);
			}
			return resultList;
		} catch (SQLException e) {
			logger.debug("【JDBC】 SQL语句执行异常 :" + parameter.getReadySql());
			// e.printStackTrace();
			return new LinkedList<>();
		} finally {
			DBs.close(resultSet, preparedStatement, connection);
		}
	}

	/**
	 * 执行查询语句 (只有一条返回记录)。 可防止SQL注入，推荐使用。
	 * 
	 * @param sql可执行SQL
	 * @return map 记录数量不为1时返回null.
	 */
	public Map<String, Object> executeQueryM(Parameter parameter) {
		logger.debug("【JDBC】 预编译SQL:" + parameter.getFormatSql());
		logger.debug("【JDBC】 预编译SQL对应的参数: " + parameter.getParams());
		if (SqlVerification.isSelectSql(parameter.getReadySql()) == false) {
			logger.debug("【JDBC】SQL语句不是查询语句：" + parameter.getFormatSql());
			return null;
		}
		List<Map<String, Object>> list = executeQueryL(parameter);
		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 执行查询语句 (只有一条返回记录)。 可防止SQL注入，推荐使用。
	 * 
	 * @param sql可执行SQL
	 * @return 返回结果集对象.
	 */
	public ResultMap executeQuery(Parameter parameter) {
		logger.debug("【JDBC】 预编译SQL:" + parameter.getFormatSql());
		logger.debug("【JDBC】 预编译SQL对应的参数: " + parameter.getParams());
		if (SqlVerification.isSelectSql(parameter.getReadySql()) == false) {
			logger.debug("【JDBC】SQL语句不是查询语句：" + parameter.getFormatSql());
			return null;
		}
		List<Map<String, Object>> list = executeQueryL(parameter);
		if (list.size() == 1) {
			ResultMap rm = new ResultMap(list.get(0));
			return rm;
		} else {
			return null;
		}
	}

	/**
	 * 执行查询语句 (只有一条返回记录) 可防止SQL注入，推荐使用。
	 * 
	 * @param sql
	 *            可执行文件
	 * @param entityClass
	 *            实体模型类型
	 * @return 实体对象
	 */
	public Object executeQuery(Parameter parameter, Class<?> entityClass) {

		logger.debug("【JDBC】 预编译SQL:" + parameter.getFormatSql());
		logger.debug("【JDBC】 预编译SQL对应的参数: " + parameter.getParams());
		if (SqlVerification.isSelectSql(parameter.getReadySql()) == false) {
			logger.debug("【JDBC】SQL语句不是查询语句：" + parameter.getFormatSql());
			return null;
		}
		List<Map<String, Object>> list = executeQueryL(parameter);
		if (list.size() == 1) {
			return ToEntity.toEntityObject(list.get(0), entityClass);
		} else {
			return null;
		}
	}

	/**
	 * 执行查询语句 注意：防止SQL注入漏洞。
	 * 
	 * @param sql
	 *            可执行文件
	 * @param entityClass
	 *            实体模型类型
	 * @return 实体对象集合
	 */
	public List<?> executeQuerys(Parameter parameter, Class<?> entityClass) {
		logger.debug("【JDBC】 预编译SQL:" + parameter.getFormatSql());
		logger.debug("【JDBC】 预编译SQL对应的参数: " + parameter.getParams());
		if (SqlVerification.isSelectSql(parameter.getReadySql()) == false) {
			logger.debug("【JDBC】SQL语句不是查询语句：" + parameter.getFormatSql());
			return new LinkedList<>();
		}
		List<Map<String, Object>> list = executeQueryL(parameter);
		return ToEntity.findEntityObject(list, entityClass);

	}

	/**
	 * 执行存储过程
	 * 
	 * @param connection
	 *            数据源
	 * 
	 * @param sql
	 *            存储过程 调用SQL语句 ,其中约定最后一个参数为输出参数， 但参数个数与 in 的参数相同时，表示没有输出参数。 如
	 *            {call proc_name2(?,?)}
	 * @param in
	 *            存储过程需要的输入参数集
	 * 
	 * @return 存储过程的 返回参数值,当没有返回参数时 返回null
	 */
	public Object executeProcedure(String sql, Object... ins) {
		logger.debug("【JDBC】:存储过程 SQL  " + sql);
		Connection connection = DBs.getConnection(this.dbKey);
		// sql 中参数的个数
		int n = countParameter(sql, STRING.QUESTION);
		// 创建调用存储过程的预定义SQL语句

		CallableStatement callableStatement = null;
		try {
			// 创建过程执行器
			callableStatement = connection.prepareCall(sql);
			// 设置入参和出参
			for (int i = 1; i <= ins.length; i++) {
				callableStatement.setObject(i, ins[i - 1]);
			}

			if (n - ins.length == 1) {
				callableStatement.registerOutParameter(n, Types.JAVA_OBJECT); // 注册出参
				callableStatement.executeUpdate();
				Object result = callableStatement.getObject(n);
				return result;
			} else if (n == ins.length) { // 没有输出参数
				callableStatement.executeUpdate();
				return null;
			} else { // 参数不匹配
				return null;
			}

		} catch (SQLException e) {
			logger.debug(" 【JDBC】 执行存储过程,出现异常：" + e.getMessage());

			return null;
		} finally {
			DBs.close(callableStatement, connection);
		}
	}

	/**
	 * 事务处理
	 * 
	 * @param connection
	 *            数据源
	 * @param sqls
	 *            可执行的更新（非查询语句）语句集 (按秩序执行)
	 * @return 成功返回true,反之返回false.
	 */
	public boolean transaction(String... sqls) {
		Connection connection = DBs.getConnection(this.dbKey);
		// ----------------验证参数-------------------

		if (connection == null) {
			return false;
		}
		// ----------------------------------------
		PreparedStatement preparedStatement = null;
		try {
			// 设置事务的提交方式为非自动提交：
			connection.setAutoCommit(false);
			// 创建执行语句
			for (String sql : sqls) {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.executeUpdate();
				logger.debug("【JDBC】 可执行SQL : " + sql);
			}
			// 在try块内添加事务的提交操作，表示操作无异常，提交事务。
			connection.commit();
			return true;
		} catch (SQLException e) {
			try {
				// .在catch块内添加回滚事务，表示操作出现异常，撤销事务：
				connection.rollback();
			} catch (SQLException e1) {
			}
			return false;

		} finally {
			try {
				// 设置事务提交方式为自动提交：
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				logger.info("【JDBC】:事务提交失败");
			}
			 
			DBs.close(preparedStatement, connection);
			 
		}
	}

	/**
	 * 设置参数
	 * 
	 * @param preparedStatement
	 * @param index
	 * @param obj
	 */
	private void preparedStatementSet(PreparedStatement preparedStatement, Integer index, Object obj) {

		String className = obj.getClass().getName();

		try {

			/*if (className.equals(String.class.getName())) {// "java.lang.String"
				preparedStatement.setString(index, obj.toString());
			} else
			if (className.equals(Integer.class.getName())) {// "java.lang.Integer"
				preparedStatement.setInt(index, (Integer) obj);
			} else
			if (className.equals(Long.class.getName())) {// "java.lang.Long"
				preparedStatement.setLong(index, (Long) obj);
			} else
			if (className.equals(Short.class.getName())) {// "java.lang.Short"
				preparedStatement.setShort(index, (Short) obj);
			} else
			if (className.equals(Boolean.class.getName())) {// "java.lang.Boolean"
				preparedStatement.setBoolean(index, (Boolean) obj);
			} else
			if (className.equals(Byte.class.getName())) {// "java.lang.Byte"
				preparedStatement.setByte(index, (Byte) obj);
			} if (className.equals(Float.class.getName())) {// "java.lang.Float"
				preparedStatement.setFloat(index, (Float) obj);
			} else if (className.equals(BigDecimal.class.getName())) {// "java.math.BigDecimal"
				preparedStatement.setBigDecimal(index, (BigDecimal) obj);
			} else if (className.equals(Double.class.getName())) {// "java.lang.Double"
				preparedStatement.setDouble(index, (Double) obj);
			} else
			
			 */
			if (className.equals(Date.class.getName())) {// "java.util.Date"
				String d = new SimpleDateFormat(DATE_FORMAT.ALL_DATE).format(obj);
				preparedStatement.setString(index, d);
			} else  {
				preparedStatement.setObject(index, obj);
			}

		} catch (SQLException e) {
			logger.error("【JDBC】:  预编译SQL设置参数失败 ");
		}

	}

	/**
	 * 计算sql中的参数个数 (执行存储过程是使用)
	 * 
	 * @param sql
	 * @param parameter
	 *            "?"
	 * @return 参数个数
	 */
	private static int countParameter(String sql, String parameter) {

		StringBuffer sb = new StringBuffer(sql);
		int n = 0;
		int index = sb.indexOf(STRING.QUESTION);
		while (index != -1) {
			sb.replace(index, index + 1, "");
			index = sb.indexOf(STRING.QUESTION);
			n++;
		}
		return n;
	}

}
