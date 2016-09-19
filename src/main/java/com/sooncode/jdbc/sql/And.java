package com.sooncode.jdbc.sql;

public class And extends Cond{

	public And(Cond A,Cond B){
		super("( "+A.expression + " AND "+B.expression+" )");
	}
	
	public And and(Cond A){
		
		this.expression = super.expression + " AND " + A.expression;
		return this;
	}
}
