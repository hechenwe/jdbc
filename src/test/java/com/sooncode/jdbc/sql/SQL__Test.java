package com.sooncode.jdbc.sql;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sooncode.jdbc.reflect.modle.Student;
import com.sooncode.jdbc.sql.condition.Cond;
import com.sooncode.jdbc.sql.condition.sign.LikeSign;

public class SQL__Test {
	private static Logger logger = Logger.getLogger("SQL__Test.class");

	@Test
	public void Select() {

		SQL sql = new SQL();

		sql.SELECT(Student.class).FROM(Student.class)
				.WHERE(new Cond("id", LikeSign.L_LIKE, "he").and(new Cond("name", LikeSign.L_LIKE, "21343")));
		logger.info(sql);
	}

}
