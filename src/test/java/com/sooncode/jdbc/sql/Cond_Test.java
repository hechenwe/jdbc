package com.sooncode.jdbc.sql;
 
import org.apache.log4j.Logger;

import com.sooncode.jdbc.sql.condition.And;
import com.sooncode.jdbc.sql.condition.Cond;
import com.sooncode.jdbc.sql.condition.Or;
import com.sooncode.jdbc.sql.condition.sign.CommonSign;
import com.sooncode.jdbc.sql.condition.sign.LikeSign;
 

public class Cond_Test{
	private static final Logger logger = Logger.getLogger("Cond_Test");
	public static void main(String[] args) {
		
		Cond c1 = new Cond("name", LikeSign.LIKE, "hechen");
		Cond c2 = new Cond("age", CommonSign.GT, 18);
		
		Cond c3 = new Cond("sex", CommonSign.LT, 24);
		
		Cond c4 = new Cond("id", CommonSign.IN, new String []{"1","2"});
		 
		
		And a1 = new And(c1, c2).and(c3);
		Or o1 = new Or(a1, c1).or(c4);
		Parameter p = o1.getParameter();
		logger.info(p.getReadySql());
		logger.info(p.getSql());
		logger.info(p.getParams());
		
		
	}

}
