package com.sooncode.jdbc;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

public class MiniJdbc__Test {
	 private static Logger logger = Logger.getLogger("MiniJdbc__Test.class");
    @Test
	public void executeUpdate(){

    	 
    		MiniJdbc jdbc = new MiniJdbc("MYSQL", "127.0.0.1", "3306", "jdbc", "UTF-8", "root", "hechenwe@gmail.com");
    	 
    	    String sql = "SELECT * FROM USER ";
    	    
    	    List<Map<String,Object>> map = jdbc.executeQuery(sql, null);
    	    logger.info(map);
		
	}
}
