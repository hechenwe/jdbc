package com.sooncode.jdbc;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sooncode.jdbc.util.create_entity.TableBuilder;

import util.Memory;
/**
 * 
 * @author pc
 *
 */
public class TimeTest {

	private static Logger logger =   Logger.getLogger(TimeTest.class);
	public static void main(String[] args) {
		/*long startMem = Memory.used();
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < 6000; i++) {
			double r = Math.random();
			map.put("jfslsdsdfsdfsdfsdfs" + r, "jfsldsfsdfsfsfdsfsd" + r);
		}
		long endMem = Memory.used();
		logger.info("【执行方法】  消耗的内存： " + (endMem - startMem) + " (bytes) / " + (endMem - startMem) / 8 + " (B) / " + (endMem - startMem) / (8 * 1024) + " (kB)");*/
	TableBuilder t = new TableBuilder("192.168.1.12", "3006", "root", "CIBNadmin!@#", "keep_mark_educational");
	
	logger.info(t.getEntityClassCode("edu_single_diagnosis_record"));
	
	
	
	
	}
	
	
	
	
	
}
