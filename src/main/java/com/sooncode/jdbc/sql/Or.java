package com.sooncode.jdbc.sql;

import java.util.HashMap;
import java.util.Map;

public class Or extends Cond {
	 
	public Or(Cond A,Cond B){
		super();
		Parameter a = A.parameter;  
		Parameter b = B.parameter; 
        
		Parameter p = new Parameter();
		
		String sql ="( "+ a.getReadySql() + " OR " + b.getReadySql()+ " )";
		p.setReadySql(sql);
		
		Map<Integer,Object> param = new HashMap<>();
		param.putAll(a.getParams()); 
		
		for (int i =1;i <= b.getParams().size();i++) {
			 Object value = b.getParams().get(i);
			 param.put(param.size()+1, value);
		}
		p.setParams(param);
		this.parameter = p;
	}
	
	public Or or(Cond A){
		return new Or(this,A);
	}
	public And and(Cond A){
		return new And(this,A);
	}
	
 
}
