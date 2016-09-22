package com.sooncode.jdbc;

import java.util.Hashtable;

public class JdbcFactory {

	public static Hashtable<String,Jdbc> jdbcs = new Hashtable<>();
	
	private JdbcFactory(){}
	
	public static Jdbc getJdbc(){
		Jdbc jdbc = jdbcs.get("default");
		if(jdbc==null){
			jdbc = new Jdbc();
			jdbcs.put("default", jdbc);
		}
		return jdbc;
	}
	public static Jdbc getJdbc(String dbKey){
		Jdbc jdbc = jdbcs.get(dbKey);
		if(jdbc==null){
			jdbc = new Jdbc(dbKey);
			jdbcs.put(dbKey, jdbc);
		}
		return jdbc;
	}
}
