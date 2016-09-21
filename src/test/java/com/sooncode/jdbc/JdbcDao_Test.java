package com.sooncode.jdbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sooncode.jdbc.JdbcDao;
import com.sooncode.jdbc.sql.condition.And;
import com.sooncode.jdbc.sql.condition.Cond;
import com.sooncode.jdbc.sql.condition.Conditions;
import com.sooncode.jdbc.sql.condition.Or;
import com.sooncode.jdbc.sql.condition.sign.CommonSign;
import com.sooncode.jdbc.sql.condition.sign.DateFormatSign;
import com.sooncode.jdbc.sql.condition.sign.LikeSign;
import com.sooncode.jdbc.sql.condition.sign.Sign;
import com.sooncode.jdbc.util.Pager;
import com.sooncode.usejdbc.entity.User;
 
public class JdbcDao_Test {
    private static Logger logger = Logger.getLogger("JdbcDaoTest.class");
	private JdbcDao jdbcDao = new JdbcDao();
	@Test
	public void get(){
		
		User u = new  User();
		u.setName("he or '1'='1' ");
		u = (User) jdbcDao.get(u);
		logger.info(u);
	}
	
	@Test
	public void gets(){
		
		User u = new  User();
		u.setName("AAA");
		@SuppressWarnings("unchecked")
		List<User> list  =   (List<User>) jdbcDao.gets(u);
		logger.info(list);
		
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void gets2(){
		 
		Cond name = new Cond("name", LikeSign.LIKE, "AA");
		Cond id = new Cond("id", CommonSign.IN, new Integer[]{1079,1080,1081});
		Cond age = new Cond("age",CommonSign.GT,3);
		Cond pass = new  Cond("pass",LikeSign.LIKE,"h");
		
		And nameANDid = new And(name,id);
		Or ageORpass = new Or(age,pass);
		
		Cond o = new And(nameANDid,ageORpass);
		
		Cond o2 = new And(name,id).and(new Or(age,pass));
		
		
		Cond o3 = new And(new And(name,id),new Or(age,pass)); 
		
		Cond a = new And(name,id,age,pass);
		
		Cond o4 = new Or(name,id,age,pass).and(new Cond("note", LikeSign.LIKE, "haha"));
		
		List<User> list  =   (List<User>) jdbcDao.gets(User.class, o4 );
		logger.info(list);
	}
	
	@Test
	public void gets4Conditions(){
		
		User u = new  User();
		u.setName("AAA");
		Conditions c = new Conditions(u);
		c.setCondition("name", LikeSign.LIKE);
		@SuppressWarnings("unchecked")
		List<User> list  =   (List<User>) jdbcDao.gets(u);
		logger.info(list);
		
	}
	
	@Test
	public void saveOrUpdates(){
		User u1 = new  User();
		u1.setId(1072);
		u1.setName("CCCCC");
		User u2 = new  User();
		u2.setId(2);
		u2.setName("DDDDD");
		List<User> list = new ArrayList<>();
		list.add(u1);
		list.add(u2);
		
		Boolean b = jdbcDao.saveOrUpdates(list);
		
		logger.info(b);
		
	}
	@Test
	public void saveOrUpdate(){
		User u1 = new  User();
		u1.setId(1078);
		u1.setName("EJLKSDJFLS");
		 
		Long b = jdbcDao.saveOrUpdate (u1);
		
		logger.info(b);
		
	}
	
	@Test
	public void save(){
		User u1 = new  User();
		u1.setName("AAA");
		Long b = jdbcDao.save(u1);
		
		logger.info(b);
		
	}
	
	@Test
	public void getPager(){
		Long pagerNumber = 1L;
		Long pagerSize = 10L;
		User u1 = new  User();
		u1.setName("AAA");
		Pager<?> p = jdbcDao.getPager(pagerNumber, pagerSize, u1);
		
		logger.info(p);
		
		
	}
	@Test
	public void getPager2(){
		Long pagerNumber = 1L;
		Long pagerSize = 10L;
		Cond name = new Cond("name", LikeSign.R_LIKE, "AA");
		Cond id = new Cond("id", CommonSign.IN, new Integer[]{1079,1080,1081});
		Cond age = new Cond("age",CommonSign.GT,3);
		Cond pass = new  Cond("pass",LikeSign.LIKE,"h");
		
		And nameANDid = new And(name,id);
		Or ageORpass = new Or(age,pass);
		
		Cond o = new And(nameANDid,ageORpass);
		
		Cond o2 = new And(name,id).and(new Or(age,pass));
		
		
		Cond o3 = new And(new And(name,id),new Or(age,pass)); 
		
		Cond a = new And(name,id,age,pass);
		
		Cond o4 = new Or(name,id,age,pass).and(new Cond("note", LikeSign.LIKE, "haha"));
		
		Cond creatDate = new Cond("createDate",DateFormatSign.yyyy_MM,"2016-09");
		Cond creatDate2 = new Cond("createDate",new DateFormatSign("%Y-%d"),"2016-25");
		
		Pager<?> p = jdbcDao.getPager(pagerNumber, pagerSize,User.class, creatDate2);
		
		logger.info(p);
		
		
	}
}
