package com.sooncode.jdbc.sql;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * 参数模型
 * 
 * @author pc
 *
 */
public class Parameter_Test {
	private static Logger logger = Logger.getLogger("Parameter_Test.class");
	@Test
	public void getSql() {
		Parameter p = new Parameter();
		p.setReadySql("SELECT * FROM USER WHERE NAME=? AND ID=? AND AGE =?");
		Map<Integer,Object> map = new HashMap<>();
		map.put(1,"hechen");
		map.put(2,67);
		map.put(3,28);
		p.setParams(map);
	
		logger.info(p.getSql());
	}
	
	@Test
	public void isException (){
		Parameter p = new Parameter();
		p.setReadySql("SELECT * FROM USER WHERE NAME=? AND ID=? AND AGE =?");
		Map<Integer,Object> map = new HashMap<>();
		map.put(1,"hechen");
		map.put(2,67);
		map.put(3,28);
		map.put(4,"ha");
		p.setParams(map);
		
		logger.info(p.isNotException());
	}

}
