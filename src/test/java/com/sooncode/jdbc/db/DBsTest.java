package com.sooncode.jdbc.db;

 

import java.sql.Connection;

import org.junit.Test;
 
 

 
public class DBsTest {
	
	@Test
	public void getConnectionTest() {
	
		Connection con = DBs.getConnection("default");
		
	}
	
	 
}
