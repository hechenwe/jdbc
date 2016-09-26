package com.sooncode.jdbc.sql.verification;

/**
 * SQL 验证
 * 
 * @author pc
 *
 */
public class SqlVerification {
	private static final String SELECT = "SELECT ";
	private static final String UPDATE = "UPDATE ";
	private static final String DELETE = "DELETE ";
	private static final String INSERT = "INSERT ";

	public static boolean isSelectSql(String sql) {
		if (sql != null) {
			sql = sql.trim().toUpperCase();
			StringBuffer sb = new StringBuffer(sql);
			int n = sb.indexOf(SELECT);
			if (n == 0) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static boolean isUpdateSql(String sql) {
		if (sql != null) {
			sql = sql.trim().toUpperCase();
			StringBuffer sb = new StringBuffer(sql);
			int n = sb.indexOf(UPDATE);
			if (n == 0) {
				return true;
			} else {
				n = sb.indexOf(INSERT);
				if (n == 0) {
					return true;
				} else {
					n = sb.indexOf(DELETE);
					if (n == 0) {
						return true;
					} else {
						return false;
					}
				}
			}
		}
		int i = Integer.MAX_VALUE;
		return false;
	}
}
