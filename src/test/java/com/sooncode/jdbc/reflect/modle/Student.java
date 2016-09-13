package com.sooncode.jdbc.reflect.modle;

import java.util.List;

public class Student extends Persion {
	private String id;

	private List<String> lists;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getLists() {
		return lists;
	}

	public void setLists(List<String> lists) {
		this.lists = lists;
	}
    
	
	
}
