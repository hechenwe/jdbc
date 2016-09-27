package com.sooncode.jdbc.dao;

import java.util.Hashtable;

import com.sooncode.jdbc.constant.DATA;

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

		JdbcDao dao = daos.get(DATA.DEFAULT_KEY);

		if (dao == null) {
			dao = new JdbcDao();
			daos.put(DATA.DEFAULT_KEY, dao);
		}
		return dao;

	}
}
