package com.sooncode.jdbc;

import org.apache.log4j.Logger;
import org.junit.Test;

public class Jdbc_Test {
	private static Logger logger = Logger.getLogger("Jdbc_Test.class");
	private Jdbc jdbc = JdbcFactory.getJdbc("test");

	@Test
	public void executeProcedure() {
		String sql = "{call proc_name2(?,?)}";
		Integer in = 1;
		Object r = jdbc.executeProcedure(sql, in);
		logger.info(r);
	}
}
