package com.sooncode.jdbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sooncode.jdbc.JdbcDao;
 
import com.sooncode.jdbc.sql.Conditions;
import com.sooncode.jdbc.sql.Sign;
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
	@Test
	public void gets4Conditions(){
		
		User u = new  User();
		u.setName("AAA");
		Conditions c = new Conditions(u);
		c.setCondition("name", Sign.LIKE);
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
}
