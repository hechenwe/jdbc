package com.sooncode.jdbc;

 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sooncode.jdbc.dao.JdbcDao;
import com.sooncode.jdbc.dao.JdbcDaoFactory;
import com.sooncode.jdbc.sql.condition.And;
import com.sooncode.jdbc.sql.condition.Cond;
import com.sooncode.jdbc.sql.condition.Conditions;
import com.sooncode.jdbc.sql.condition.Or;
import com.sooncode.jdbc.sql.condition.OrderBy;
import com.sooncode.jdbc.sql.condition.Sort;
import com.sooncode.jdbc.sql.condition.sign.EqualSign;
import com.sooncode.jdbc.sql.condition.sign.DateFormatSign;
import com.sooncode.jdbc.sql.condition.sign.LikeSign;
import com.sooncode.jdbc.sql.condition.sign.NullSign;
import com.sooncode.jdbc.util.Pager;

import example.entity.User;
 
 
public class JdbcDao_Test {
    private static Logger logger = Logger.getLogger("JdbcDaoTest.class");
	private JdbcDao jdbcDao = JdbcDaoFactory.getJdbcDao();
	@Test
	public void get(){
		
		User u = new  User();
		u.setName("EJLKSDJFLS");
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
	public void delete (){
		User u = new User ();
		u.setId(345);
		long n = jdbcDao.delete(u);
		logger.info(n);
	}
	@Test 
	public void update (){
		User u = new User ();
		u.setId(1072);
		u.setName("ni mei");
		u.setAge(234);
		u.setCreateDate(new Date());
		u.setField1("LSDJFSDKHLS");
		Long n = jdbcDao.update(u);
		logger.info(n);
	}
	@Test 
	public void update2 (){
		User u = new User ();
		//u.setId(1072);
		u.setName("ni mei");
		
		User newU = new User();
		newU.setAge(100);
		Long n = jdbcDao.update(u,newU);
		logger.info(n);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void gets2(){
		 
		Cond name = new Cond("name", LikeSign.LIKE, "AAA");
		Cond id = new Cond("id", new Integer[]{1079,1080,1081});
		Cond age = new Cond("age",1,100);
		Cond pass = new  Cond("pass",LikeSign.LIKE,"h");
		
		And nameANDid = new And(name,id);
		Or ageORpass = new Or(age,pass);
		
		Cond o = new And(nameANDid,ageORpass);
		
		Cond o2 = new And(name,id).and(new Or(age,pass));
		
		
		Cond o3 = new And(new And(name,id),new Or(age,pass)); 
		
		Cond a = new And(name ,age );
		
		Cond o4 = new Or(name,id,age,pass).and(new Cond("note", LikeSign.LIKE, "haha"));
		
		List<User> list  =   (List<User>) jdbcDao.gets(User.class, a );
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
		u1.setName("AFJLSLDFJSFSF");
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
		Cond id = new Cond("id", new Integer[]{1079,1080,1081});
		Cond age = new Cond("age",EqualSign.GT,3);
		Cond pass = new  Cond("pass",LikeSign.LIKE,"h");
		
		And nameANDid = new And(name,id);
		Or ageORpass = new Or(age,pass);
		
		Cond o = new And(nameANDid,ageORpass);
		
		Cond o2 = new And(name,id).and(new Or(age,pass));
		
		
		Cond o3 = new And(new And(name,id),new Or(age,pass)); 
		
		Cond a = new And(name,id,age,pass);
		
		Cond o4 = new Or(name,id,age,pass).and(new Cond("note", LikeSign.LIKE, "haha"));
		
		Cond creatDate = new Cond("createDate",DateFormatSign.yyyy_MM,"2016-09");
		 
		Cond creatDate2 = new Cond("createDate",new DateFormatSign("%Y-%d"),"2016-25").and(new Cond("id", NullSign.IS_NOT_NULL)).orderBy(new OrderBy("id", Sort.ASC),new OrderBy("name", Sort.DESC));
		
		Pager<?> p = jdbcDao.getPager(pagerNumber, pagerSize,User.class, creatDate2);
		
		logger.info(p);
		
		 
		
	}
}
