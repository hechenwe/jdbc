package com.sooncode.jdbc.cache;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sooncode.jdbc.cache.Cache2;
import com.sooncode.jdbc.example.entity.Persion;
 
 

public class CacheTest {
	
    @Test
	public void putTest(){
		List<Persion> list = new ArrayList<>();
		Persion p1 = new Persion(1,"AAA");
		Persion p2 = new Persion(2,"BBB");
		Persion p3 = new Persion(3,"CCC");
	 
		list.add(p1);
		list.add(p2);
		list.add(p3);
		
		List<Persion> list2 = new ArrayList<>();
		Persion p12 = new Persion(1,"AAA");
		Persion p22 = new Persion(2,"BBB");
		Persion p32 = new Persion(3,"CCC");
	 
		list2.add(p12);
		list2.add(p22);
		list2.add(p32);
		
		Cache2.put(Persion.class, list);
		Cache2.put(Persion.class, list2);
		Persion p = new Persion();
		p.setId(2);
		p.setName("AAA");
		List<Persion> list3 = (List<Persion>) Cache2.get(p);
		
		
	}
}


