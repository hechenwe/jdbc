package com.sooncode.jdbc.dao;

import java.util.Hashtable;

public class JdbcDaoFactory {
	private static Hashtable<String, JdbcDao> daos = new Hashtable<>();
	
	private JdbcDaoFactory() {//不容许创建对象
	}

	public static JdbcDao getJdbcDao(String dbKey) {

		JdbcDao dao = daos.get(dbKey);

		if (dao == null) {
			dao = new JdbcDao(dbKey);
			daos.put(dbKey, dao);
		}
		return dao;

	}

	public static JdbcDao getJdbcDao() {

		JdbcDao dao = daos.get("default");

		if (dao == null) {
			dao = new JdbcDao();
			daos.put("default", dao);
		}
		return dao;

	}
}
