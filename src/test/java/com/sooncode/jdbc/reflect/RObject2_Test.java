package com.sooncode.jdbc.reflect;

 
import java.lang.reflect.Field;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sooncode.jdbc.reflect.modle.Student;
 

/**
 * 反射创建的对象
 * 
 * @author pc
 *
 */
public class RObject2_Test {
	public static Logger logger = Logger.getLogger("RObject.class");
	@Test
	public void getFields(){
		//Temp t = new Temp("com.sooncode.jdbc.reflect.modle.User");
		RObject2<Student> r = new RObject2<Student>("com.sooncode.jdbc.reflect.modle.User");
		List<Field> list = r.getFields();
		for (Field f : list) {
			logger.info(f.getName());
			
		}
		
		 
	}
	@Test
	public void hasField(){
		RObject r = new RObject(new Student());
		logger.info(r.hasField("name"));
		logger.info(r.hasField("id"));
	}
	@Test
	public void getListFieldName(){
		RObject r = new RObject(new Student());
		logger.info(r.getListFieldName(String.class));
		 
	}
	
	@Test
	public void getPk(){
		RObject r = new RObject(new Student());
		logger.info(r.getPk());
		
	}
	@Test
	public void getParameterizedType(){
		RObject2<Student> r = new RObject2<Student>("com.sooncode.jdbc.reflect.modle.User");	
		r.getParameterizedType();
	}
	 
}
