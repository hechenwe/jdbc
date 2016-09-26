package com.sooncode.jdbc.sql.verification;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * SQL 验证
 * @author pc
 *
 */
public class SqlVerification_Test {
	
	private static final Logger logger = Logger.getLogger("SqlVerification_Test");
    @Test
	public void isSelectSql(){
    	
    	String sql = "SELECT * FROM USER";
    	
    	boolean b = SqlVerification.isSelectSql(sql);
    	logger.info(b);
    }
    @Test
    public void isUpdateSql(){
    	
    	String sql = "INSERT * FROM USER";
    	
    	boolean b = SqlVerification.isUpdateSql(sql);
    	logger.info(b);
    }
	 
}
