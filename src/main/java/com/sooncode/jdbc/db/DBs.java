package com.sooncode.jdbc.db;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * 数据库
 * 
 * @author pc
 *
 */
public class DBs {
	/** 数据源缓存 */
	private static Map<String, DB> dBcache = new HashMap<>();

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
	 * @param dbKey
	 *            代表连接数据库参数的关键字
	 * @return 数据库连接
	 */
	public static synchronized Connection getConnection(String dbKey) {

		Connection connection = null;
		if (c3p0properties != null && dataSources != null && dataSources.size() != 0) {
			try {
				connection = dataSources.get(dbKey).getConnection();
				connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
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
	 * @param objs
	 *            含有colse()方法的对象集合
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

	/** 扫描数据库配置文件 */
	private static List<String> getDbConfig() {
		File file = new File(classesPath);
		String test[];
		test = file.list();
		List<String> dbCongig = new ArrayList<>();
		for (int i = 0; i < test.length; i++) {

			String fileName = test[i];
			if (fileName.contains("_db.properties")) {
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

/**
 * 配置文件 读取工具类
 * 
 * @author pc
 *
 */
class PropertiesUtil {
	/**
	 * 配置文件所在路径
	 */
	private String filePath;

	public PropertiesUtil(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * 根据Key读取Value
	 * 
	 * @param filePath
	 *            配置文件 (默认在src下)
	 * 
	 * @param key
	 *            关键字
	 * @return 值
	 */
	public String getString(String key) {
		Properties p = new Properties();
		try {
			InputStreamReader in = new InputStreamReader(new FileInputStream(this.filePath), "utf-8");
			p.load(in);
			String value = p.getProperty(key);
			return value.trim();

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据Key读取Value
	 * 
	 * @param filePath
	 *            配置文件 (默认在src下)
	 * 
	 * @param key
	 *            关键字
	 * 
	 * @return int
	 */
	public Integer getInt(String key) {
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(this.filePath));
			properties.load(in);
			String value = properties.getProperty(key);
			Integer val = Integer.parseInt(value.trim());
			return val;

		} catch (Exception e) {

			return null;
		}
	}

}
