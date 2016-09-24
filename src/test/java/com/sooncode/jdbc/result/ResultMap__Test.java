package com.sooncode.jdbc.result;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

public class ResultMap__Test {
	private static Logger logger = Logger.getLogger("ResultMap__Test.class");
	
	@Test 
	public void get(){
		
		Map<String,Object> map = new HashMap<>();
		map.put("Name","hechen");
		map.put("id",67);
		map.put("age",28);
		map.put("NIKE_NAME","ha");
		map.put("USER_ADRESS_ADRESS_URL","北京");
		ResultMap rmap = new ResultMap(map);
		Object obj = rmap.get("userAdress.adressUrl") ;
		Object obj2 = rmap.get("name") ;
		logger.info(obj2);	
		 
	}
}
