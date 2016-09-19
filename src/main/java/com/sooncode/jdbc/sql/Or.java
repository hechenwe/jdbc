package com.sooncode.jdbc.sql;

public class Or extends Cond {

	public Or(Cond A,Cond B){
		super("( "+A.expression + " OR "+B.expression +" )");
	}
	
	public static void main(String[] args) {
		
		Cond c1 = new Cond("1=1");
		Cond c2 = new Cond("A=20");
		Cond c3 = new Cond("C=5");
		Cond c4 = new Cond("D>7");
		Cond c5 = new Cond("E <= 127");
		Cond c6 = new Cond("F <> 127");
		
		And a1 = new And(c1, c2).and(c6);
		
		Or o1 = new Or(c3, c4);
		
		And and = new And(a1, o1).and(c5);
		
		System.out.println("---"+ and.expression);
	}
}
