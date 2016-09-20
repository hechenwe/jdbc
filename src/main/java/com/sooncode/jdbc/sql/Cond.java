package com.sooncode.jdbc.sql;

import java.util.HashMap;
import java.util.Map;

import com.sooncode.jdbc.util.T2E;

public class Cond {
 
	protected Parameter parameter;
 
	protected Cond() {
  
	}
	
	
	public Cond(String key,Sign sign,Object value) {
		String sql = new String ();
		if(sign.name().equals("LIKE")){
			 sql = T2E.toColumn(key) +" "+ sign.getSign() + " ? ";
			 value = "%"+value+"%";
		}else{
			 sql = T2E.toColumn(key) +" "+ sign.getSign() + " ? ";
		}
		 
		Map<Integer, Object> param = new HashMap<>();
		param.put(1, value);
		this.parameter = new Parameter();
		this.parameter.setReadySql(sql);
		this.parameter.setParams(param);
		
	}
	public Cond(String key,Sign sign,Object[] values) {
		String signStr = sign.name();
		if(signStr.equals("IN")){
			String in = "( ";
			
			Map<Integer, Object> param = new HashMap<>();
			for (int i = 1;i <= values.length;i++) {
				param.put(i, values[i-1]);
				if(i==1){
					in = in + " ? ";
				}else{
					in = in + " ,? ";
				}
			}
			in = in + " )";
			String sql = T2E.toColumn(key) +" IN " + in;
			this.parameter = new Parameter();
			this.parameter.setReadySql(sql);
			this.parameter.setParams(param);
			
		}
		
	}
	public Parameter getParameter(){
		return this.parameter;
	}
	
}
