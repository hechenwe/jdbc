package com.sooncode.jdbc.sql.condition;

import java.util.HashMap;
import java.util.Map;

import com.sooncode.jdbc.constant.SQL_KEY;
import com.sooncode.jdbc.constant.STRING;
import com.sooncode.jdbc.sql.Parameter;

public class Or extends Cond {
	 
	public Or(Cond A,Cond B){
		super();
		Parameter a = A.parameter;  
		Parameter b = B.parameter; 
        
		Parameter p = new Parameter();
		
		String sql =SQL_KEY.L_BRACKET + a.getReadySql() + SQL_KEY.OR + b.getReadySql()+ SQL_KEY.R_BRACKET ;//" )";
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
	
	public Or(Cond ... conds){
		super();
		
		if(conds.length ==1){
			this.or(conds[0]);
		}else if(conds.length == 2){
			this.or(conds[0],conds[1]);
		}else if(conds.length >= 3){
			String sql = new String ();//   "( "+ a.getReadySql() + " AND " + b.getReadySql()+" )";
			Map<Integer,Object> param = new HashMap<>();
			Parameter p = new Parameter();
			sql= sql + SQL_KEY.L_BRACKET ;//"( ";
			int n = 1;
			for (Cond c : conds) {
				Parameter cParam = c.parameter;
				if(n==1){
					sql = sql + STRING.SPACING  + cParam.getReadySql();
					
				}else{
					sql = sql + SQL_KEY.OR  + cParam.getReadySql();
				}
				n++;
				for (int i =1;i <= cParam.getParams().size();i++) {
					Object value = cParam.getParams().get(i);
					param.put(param.size()+1, value);
				}
			}
			sql = sql + SQL_KEY.R_BRACKET ;//" )";
			p.setReadySql(sql);
			p.setParams(param);
			this.parameter = p;
		} 
		 
		
	}
 
}
