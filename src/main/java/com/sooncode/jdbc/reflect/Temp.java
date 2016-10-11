package com.sooncode.jdbc.reflect;

 

public class Temp <E>{
public String EclassName;
	 
public Temp(){
	this.EclassName = Genericity.getGenericity(this.getClass(), 0);
}
}
