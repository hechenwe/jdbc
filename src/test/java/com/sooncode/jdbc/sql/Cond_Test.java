package com.sooncode.jdbc.sql;
 
import org.apache.log4j.Logger;
 

public class Cond_Test{
	private static final Logger logger = Logger.getLogger("Cond_Test");
	public static void main(String[] args) {
		
		Cond c1 = new Cond("name", Sign.LIKE, "hechen");
		Cond c2 = new Cond("age", Sign.GT, 18);
		
		Cond c3 = new Cond("sex", Sign.LT, 24);
		
		Cond c4 = new Cond("id", Sign.IN, new String []{"1","2"});
		 
		
		And a1 = new And(c1, c2).and(c3);
		Or o1 = new Or(a1, c1).or(c4);
		Parameter p = o1.getParameter();
		logger.info(p.getReadySql());
		logger.info(p.getSql());
		logger.info(p.getParams());
		
		
	}

}
