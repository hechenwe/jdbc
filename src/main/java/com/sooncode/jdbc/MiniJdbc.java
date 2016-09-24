package com.sooncode.jdbc;

import java.beans.PropertyDescriptor;
 
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.mysql.jdbc.Statement;
 

/**
 * JDBC 执行SQL语句
 * 
 * @author pc
 *
 */
public class MiniJdbc {

	private final static Logger logger = Logger.getLogger("MiniJdbc.class");

	private Connection connection;

	/**
	 * 初始化MiniJdbc
	 * 
	 * @param dbType
	 *            数据库类型 ：MYSQL,ORCAL,DB2等
	 * @param ip
	 * @param port
	 * @param dataName
	 * @param encodeing UTF-8
	 * @param userName
	 * @param password
	 */
	public MiniJdbc(String dbType, String ip, String port, String dataName, String encodeing, String userName, String password) {

		String mysqlUrl = "";
		String driver = "";
		if (dbType.equals("MYSQL")) {
			mysqlUrl = "jdbc:mysql://" + ip + ":" + port + "/" + dataName + "?useUnicode=true&characterEncoding=" + encodeing;
			driver = "com.mysql.jdbc.Driver";
		} else {
			this.connection = null;
		}

		try {
			Class.forName(driver);
			this.connection = DriverManager.getConnection(mysqlUrl, userName, password);
		} catch (Exception e) {
			this.connection = null;
			logger.error("【MiniJdbc】数据库连接错误：" + e.getMessage());
		}

	}

	/**
	 * 执行非查询语句(更新语句)
	 * 
	 * @param connection
	 *            数据库连接
	 * @param readySql
	 *            可执行的非查询语句
	 * @param params 
	 * @return 一般情况是返回受影响的行数,当有主键为自增字段,在添加数据时返回 自增值
	 */
	public Long executeUpdate(String readySql ,Map<Integer,Object> params) {
		if (connection == null) {
			return null;
		}
		  
		String sql = readySql;
		logger.debug("【MiniJdbc】 预编译SQL: " + sql);
		logger.debug("【MiniJdbc】预编译SQL对应的参数: " + params);
		 
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Long n = 0L;
		try {
			preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			 if(params!=null && params.size()>0){
			for (Entry<Integer, Object> en : params.entrySet()) {
				Integer index = en.getKey();
				Object obj = en.getValue();
				preparedStatementSet(preparedStatement, index, obj);
			}
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
			logger.debug("【MiniJdbc】 SQL语句执行异常 : " + sql);
			return null;
		} finally {
			 close(resultSet, preparedStatement, connection);
		}
		  
	}

	/**
	 * 执行查询语句
	 * 
	 * @param sql可执行SQL
	 * @return
	 * @throws SQLException
	 */
	/**
	 * 执行查询语句(可能有多条记录)。
	 * 可防止SQL注入，推荐使用。
	 * @parameter 参数模型
	 * @return List
	 */
	public List<Map<String, Object>> executeQueryL(String readySql ,Map<Integer,Object> params) {
		if (connection == null) {
			return null;
		}
		
		logger.debug("【MiniJdbc】 预编译SQL: \r\t" + readySql);
		logger.debug("【MiniJdbc】 预编译SQL对应的参数: " + params);
		 
		 
		List<Map<String, Object>> resultList = new LinkedList<>();

		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(readySql);
            if(params!=null && params.size()>0){
			for (Entry<Integer, Object> en : params.entrySet()) {
				Integer index = en.getKey();
				Object obj = en.getValue();
				preparedStatementSet(preparedStatement, index, obj);
			}
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
			logger.debug("【MiniJdbc】: SQL语句执行异常  \r\t " + readySql );
			//e.printStackTrace();
			return null;
		} finally {
			 close(resultSet, preparedStatement, connection);
		}
	}

	/**
	 * 抓取对象
	 * 
	 * @param list
	 * @param clas
	 * @return List对象 ,或简单对象
	 */
	public List<?> getEntityObject(List<Map<String, Object>> list, Class<?> entityClass) {

		List<Object> objects = new ArrayList<>();
		for (Map<String, Object> map : list) {
			try {
				Object object = entityClass.newInstance();
				List<Field> fields = getFields(object);// 获取声明的对象。
				for (Field field : fields) {
					String key = toColumn(entityClass.getSimpleName()) + "_" + toColumn(field.getName());
					Object value = map.get(key);
					if (value == null) {
						value = map.get(field.getName());
						if (value == null) {
							continue;
						}
					}
					PropertyDescriptor pd = new PropertyDescriptor(field.getName(), entityClass);
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

	/**
	 * 释放数据库连接资源
	 * 
	 * @param objs
	 */
	private void close(Object... objs) {
		for (Object obj : objs) {
			try {
				Method method = obj.getClass().getMethod("close");
				if (method != null) {
					method.invoke(obj);
				}
			} catch (Exception e) {
				logger.error ("【MiniJdbc】数据库释放连接资源错误：" + e.getMessage());
			}
		}

	}

	private String toColumn(String field) {

		String string = new String();
		char[] c = field.toCharArray();
		int i = 0;
		StringBuilder sb = new StringBuilder();
		while (i < field.length()) {

			while (i < field.length() && (isLower(c[i]) || isNumber(c[i]) || is$(c[i]))) {
				sb.append(c[i]);
				i++;
			}
			if (i != 0) {
				string = string + sb.toString().toUpperCase() + "_";
			}
			if (i < field.length()) {
				sb = new StringBuilder();
				sb.append(c[i++]);
			}

		}

		return sb.toString().toUpperCase();
	}

	/**
	 * 字符是否是‘$’
	 * 
	 * @param c
	 * @return
	 */
	private boolean is$(char c) {
		if (c == '$') {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 字符是否是小写字母
	 * 
	 * @param c
	 * @return
	 */
	private boolean isLower(char c) {
		if (c >= 'a' && c <= 'z') {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 字符是否是数字
	 * 
	 * @param c
	 * @return
	 */
	private boolean isNumber(char c) {
		if (c >= '0' && c <= '9') {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取被反射代理对象的属性集(除serialVersionUID属性外)
	 * 
	 * @return
	 */
	private List<Field> getFields(Object object) {
		List<Field> list = new ArrayList<>();

		Class<?> thisClass = object.getClass();

		int n = 0;
		for (; thisClass != Object.class; thisClass = thisClass.getSuperclass()) {

			Field[] fields = thisClass.getDeclaredFields();
			if (n == 0) {
				for (Field f : fields) {
					if (!f.getName().equals("serialVersionUID")) {
						list.add(f);
					}
				}

			} else {
				for (Field f : fields) {
					int i = f.getModifiers();
					boolean isPrivate = Modifier.isPrivate(i);
					if (!f.getName().equals("serialVersionUID") && isPrivate == false) {
						list.add(f);
					}
				}
			}

			n++;
		}

		return list;
	}
	/**
     * 设置参数
     * @param preparedStatement
     * @param index
     * @param obj
     */
	private void preparedStatementSet(PreparedStatement preparedStatement, Integer index, Object obj) {

		String className = obj.getClass().getName();

		try {

			if (className.equals("java.lang.String")) {
				preparedStatement.setString(index, obj.toString());
			}

			if (className.equals("java.lang.Integer")) {
				preparedStatement.setInt(index, (Integer) obj);
			}

			if (className.equals("java.lang.Long")) {
				preparedStatement.setLong(index, (Long) obj);
			}

			if (className.equals("java.lang.Short")) {
				preparedStatement.setShort(index, (Short) obj);
			}

			if (className.equals("java.lang.Boolean")) {
				preparedStatement.setBoolean(index, (Boolean) obj);
			}

			if (className.equals("java.lang.Byte")) {
				preparedStatement.setByte(index, (Byte) obj);
			}

			if (className.equals("java.util.Date")) {
				String d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(obj);
				preparedStatement.setString(index, d);
			}

			if (className.equals("java.lang.Float")) {
				preparedStatement.setFloat(index, (Float) obj);
			}
			if (className.equals("java.math.BigDecimal")) {
				preparedStatement.setBigDecimal(index, (BigDecimal) obj);
			}
			if (className.equals("java.lang.Double")) {
				preparedStatement.setDouble(index, (Double) obj);
			}

		} catch (SQLException e) {

			//e.printStackTrace();
			logger.error("【MiniJdbc】:  预编译SQL设置参数失败 !");
		}

	}

	 

	
	

}
