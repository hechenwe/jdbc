package com.sooncode.jdbc.sql;
 
import java.util.HashMap;
import java.util.Map;
 

public class And extends Cond{
	
	
	public And(Cond A,Cond B){
		super();
		Parameter a = A.parameter;  
		Parameter b = B.parameter; 
		Parameter p = new Parameter();
		String sql = "( "+ a.getReadySql() + " AND " + b.getReadySql()+" )";
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
	
	
	public And(Cond ... conds){
		super();
		
		if(conds.length ==1){
			this.and(conds[0]);
		}else if(conds.length == 2){
			this.and(conds[0],conds[1]);
		}else if(conds.length >= 3){
			String sql = new String ();//   "( "+ a.getReadySql() + " AND " + b.getReadySql()+" )";
			Map<Integer,Object> param = new HashMap<>();
			Parameter p = new Parameter();
			  
			for (Cond c : conds) {
				Parameter cParam = c.parameter;
				
				
				p.setReadySql(sql);
				for (int i =1;i <= cParam.getParams().size();i++) {
					Object value = cParam.getParams().get(i);
					param.put(param.size()+1, value);
				}
				p.setParams(param);
				this.parameter = p;
			}
		}else{
			
		}
		
		
		
		
		
	}
	
	public And and(Cond A){
		return new And(this,A);
	}
	public And and(Cond A,Cond B){
		return new And(A,B);
	}
	
	
	public Or or(Cond A){
		return new Or(this,A);
	}
	public Or or(Cond A,Cond B){
		return new Or(A,B);
	}
	
}
