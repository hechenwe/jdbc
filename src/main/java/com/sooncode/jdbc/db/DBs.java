package com.sooncode.jdbc.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.sooncode.jdbc.constant.STRING;

/**
 * 数据库
 * 
 * @author pc
 *
 */
public class DBs {
	/**
	 * 数据库配置文件名称后缀"_db.properties"
	 */
	private static final String DB_PROPERTIES = "_db.properties";
	/** 数据源缓存 */
	private static Map<String, DB> dBcache = new Hashtable<>();

	/** 源码所在路径 */
	private static String classesPath = null;

	/** c3p0 配置文件 */
	public static Properties c3p0properties;

	/** 数据库 c3p0 连接池 多数据源 缓存 */
	private static Map<String, DataSource> dataSources;

	public final static Logger logger = Logger.getLogger("DBs.class");

	/**
	 * 初始化
	 */
	static {
		dBcache.clear();
		if (classesPath == null) {
			classesPath = new DBs().getClassesPath();
		}
		List<String> dbConfig = getDbConfig();
		Map<String, DataSource> dss = new HashMap<>();

		Properties c3p0 = new Properties();
		InputStreamReader in;

		try {
			in = new InputStreamReader(new FileInputStream(classesPath + "c3p0.properties"), "utf-8");
			c3p0.load(in);
			c3p0properties = c3p0;
		} catch (Exception e) {
			c3p0properties = null;
			logger.debug("【JDBC】:  加载c3p0  配置文件失败 ");
		}

		for (String str : dbConfig) {

			PropertiesUtil pu = new PropertiesUtil(classesPath + str);
			DB db = new DB();

			db.setKey(pu.getString("KEY"));
			db.setDriver(pu.getString("DRIVER"));
			db.setIp(pu.getString("IP"));
			db.setPort(pu.getString("PORT"));
			db.setDataName(pu.getString("DATA_NAME"));
			db.setEncodeing(pu.getString("ENCODEING"));
			db.setUserName(pu.getString("USERNAME"));
			db.setPassword(pu.getString("PASSWORD"));
			db.setTransactionIsolation(pu.getString("TRANSACTION_ISOLATION"));
			dBcache.put(db.getKey(), db);

			Class<?> DataSources;
			try {
				DataSources = Class.forName("com.mchange.v2.c3p0.DataSources");
			} catch (ClassNotFoundException e) {
				DataSources = null;
				logger.info("【JDBC】: 没有添加c3p0的jar包 , DataSources 加载失败");
			}

			if (c3p0properties != null && DataSources != null) {
				// DataSource ds;
				try {
					// 加载驱动类
					Class.forName(db.getDriver());
					String jdbcUrl = "jdbc:mysql://" + db.getIp() + ":" + db.getPort() + "/" + db.getDataName() + "?useUnicode=true&characterEncoding=" + db.getEncodeing();
					Properties p = new Properties();

					p.setProperty("user", db.getUserName());
					p.setProperty("password", db.getPassword());

					Method unpooledDataSource = DataSources.getMethod("unpooledDataSource", String.class, Properties.class);

					DataSource ds = (DataSource) unpooledDataSource.invoke(null, jdbcUrl, p);
					Method pooledDataSource = DataSources.getMethod("pooledDataSource", DataSource.class, Properties.class);
					ds = (DataSource) pooledDataSource.invoke(null, ds, p);
					 
					dss.put(db.getKey(), ds);
					logger.info("【JDBC】: 已添加c3p0连接池 ;数据库" + db.getDataName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		dataSources = dss;
	}

	/**
	 * 获取数据库连接
	 * 
	 * @param dbKey 代表连接数据库参数的关键字
	 *            
	 * @return 数据库连接
	 */
	public static synchronized Connection getConnection(String dbKey) {

		Connection connection = null;
		if (c3p0properties != null && dataSources != null && dataSources.size() != 0) {
			try {
				connection = dataSources.get(dbKey).getConnection();
				/*
				 * //设置事务隔离级别：
				 * Connection.TRANSACTION_NONE （0） :指示事务不受支持的常量。(注意，不能使用 ，因为它指定了不受支持的事务。) （不支持事务）
				 * Connection.TRANSACTION_READ_UNCOMMITTED （1） :指示可以发生脏读 (dirty read)、不可重复读和虚读 (phantom read) 的常量。
				 * Connection.TRANSACTION_READ_COMMITTED （2）:指示不可以发生脏读的常量；不可重复读和虚读可以发生。
                 * Connection.TRANSACTION_REPEATABLE_READ （4） :指示不可以发生脏读和不可重复读的常量；虚读可以发生。  (JDBC 默认值)
                 * Connection.TRANSACTION_SERIALIZABLE （8）: 指示不可以发生脏读、不可重复读和虚读的常量。
				 * */
				//connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); 
				String ransactionIsolation = dBcache.get(dbKey).getTransactionIsolation();
				if(ransactionIsolation != null){
					if(ransactionIsolation.equals("TRANSACTION_NONE")){//0
						connection.setTransactionIsolation(Connection.TRANSACTION_NONE);
					}else if(ransactionIsolation.equals("TRANSACTION_READ_UNCOMMITTED")){//1
						connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
					}else if(ransactionIsolation.equals("TRANSACTION_SERIALIZABLE")){//8
						connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
					}if(ransactionIsolation.equals("TRANSACTION_READ_COMMITTED")){//2
						connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
					}
				}
				int n = connection.getTransactionIsolation();
				logger.error("【JDBC】数据库["+dBcache.get(dbKey).getDataName()+"]的事务隔离级别为："+n);
			} catch (SQLException e) {
				logger.error("【JDBC】: 获取数据库连接失败 ");
				e.printStackTrace();
				return null;
			}
		} else {
			DB db = DBs.dBcache.get(dbKey);
			String DRIVER = db.getDriver();
			String IP = db.getIp();
			String PORT = db.getPort();
			String DATA_NAME = db.getDataName();
			String ENCODEING = db.getEncodeing();
			String USERNAME = db.getUserName();
			String PASSWORD = db.getPassword();

			String mysqlUrl = "jdbc:mysql://" + IP + ":" + PORT + "/" + DATA_NAME + "?useUnicode=true&characterEncoding=" + ENCODEING;

			try {
				Class.forName(DRIVER);
			} catch (ClassNotFoundException e) {
				logger.info("【JDBC】: 加载数据库驱动失败 ");
				return null;
			}
			try {
				connection = DriverManager.getConnection(mysqlUrl, USERNAME, PASSWORD);
			} catch (SQLException e) {
				logger.info("【JDBC】: 数据库连接失败 ");
				return null;
			}

		}

		return connection;
	}

	/**
	 * 关闭连接资源
	 * 
	 * @param objs 含有colse()方法的对象集合
	 *            
	 * 
	 */
	public static void close(Object... objs) {
		for (Object obj : objs) {
			try {
				if (obj != null) {
					Method method = obj.getClass().getMethod("close");
					if (method != null) {
						method.invoke(obj);
					}
				}
			} catch (Exception e) {
				logger.info("【JDBC】: 关闭数据库资源失败 ");
			}
		}
	}

	 
	/**
	 * 扫描数据库配置文件
	 * @return List  配置文件名称 集合.
	 */
	private static List<String> getDbConfig() {
		File file = new File(classesPath);
		String test[];
		test = file.list();
		List<String> dbCongig = new ArrayList<>();
		for (int i = 0; i < test.length; i++) {

			String fileName = test[i];
			if (fileName.contains(DB_PROPERTIES)) {
				dbCongig.add(fileName);
			}
		}

		return dbCongig;

	}

	/**
	 * 获取 源码所在路径
	 * 
	 * @return
	 */
	private String getClassesPath() {

		String path = this.getClass().getResource("/").getPath();
		File file = new File(path);
		String classesPath = file.toString() + File.separatorChar;
		classesPath=classesPath.replace("%20", STRING.SPACING);
		logger.debug("【JDBC】: classesPath=" + classesPath ); 
		return classesPath;

		/*
		 * String jarFilePath =
		 * this.getClass().getProtectionDomain().getCodeSource().getLocation().
		 * getFile(); File file = new File(jarFilePath); String classesPath =
		 * file.toString() + File.separatorChar; System.out.println(
		 * "[JDBC classesPath] :"+classesPath); return classesPath;
		 */

	}
}


