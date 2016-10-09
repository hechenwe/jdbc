package example.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.chainsaw.Main;

import com.sooncode.jdbc.Jdbc;
import com.sooncode.jdbc.JdbcFactory;
import com.sooncode.jdbc.sql.Parameter;


import example.entity.User;

/***
 * 
 * @author pc
 *
 */
public class UserDao {

	private Jdbc jdbc = JdbcFactory.getJdbc();
	
	
	
	 
	public List<User> getUserByAge(int age){
		String sql = "SELECT * FROM USER WHERE AGE=?";
		Map<Integer,Object> map = new HashMap<>();
		map.put(1, age);
		Parameter parameter = new Parameter();
		parameter.setParams(map);
		parameter.setReadySql(sql);
		 
		@SuppressWarnings("unchecked")
		List<User> list =(List<User>) jdbc.executeQuerys(parameter, User.class);
		
		return list;
	}
	
	 
	public static void main(String[] args) {
		
		List<Object> oes = new ArrayList<>();
		
		List <User> user = new ArrayList<>();
		
		user =   (List) oes;
	}
}
