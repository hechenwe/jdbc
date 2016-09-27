package com.sooncode.jdbc;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.sooncode.jdbc.constant.STRING;
import com.sooncode.jdbc.reflect.RObject;
import com.sooncode.jdbc.util.T2E;

public class ToEntity {

	private ToEntity() {
	}

	/**
	 * 抓取实体对象
	 * 
	 * @param list
	 * @param clas
	 * @return List对象 ,或简单对象
	 */
	public static List<?> findEntityObject(List<Map<String, Object>> list, Class<?> clas) {
		if (list == null || list.size() == 0) {
			return new LinkedList<>();
		}
		List<Object> objects = new LinkedList<>();
		String tableName = T2E.toColumn(clas.getSimpleName());
		for (Map<String, Object> map : list) {
			RObject rObj = new RObject(clas);
			Field[] fields = clas.getDeclaredFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				String columnName = T2E.toColumn(field.getName());
				String key = tableName + STRING.UNDERLINE + columnName;

				Object value = map.get(key);
				if (value == null) {
					value = map.get(columnName);
					if (value == null) {
						continue;
					}
				}
				rObj.invokeSetMethod(fieldName, value);
			}

			Object object = rObj.getObject();
			if (objects.size() >= 1 && object.toString().equals(objects.get(objects.size() - 1).toString())) {
				continue;
			}
			objects.add(object);

		}
		return objects;
	}

	/**
	 * Map 转换成 实体对象
	 * 
	 * @param map
	 * @param clas
	 *            实体类
	 * @return 实体对象
	 */
	public static Object toEntityObject(Map<String, Object> map, Class<?> clas) {
		String tableName = T2E.toColumn(clas.getSimpleName());

		RObject rObj = new RObject(clas);
		List<Field> fields = rObj.getFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			String columnName = T2E.toColumn(field.getName());
			String key = tableName + STRING.UNDERLINE + columnName;
			Object value = map.get(key);
			if (value == null) {
				value = map.get(columnName);
				if (value == null) {
					continue;
				}
			}
			rObj.invokeSetMethod(fieldName, value);
		}
		if (isNull(rObj.getObject()) == false) {
			return null;
		} else {
			return rObj.getObject();
		}
	}

	/**
	 * 验证 object是否为空 或 其属性是否全为空
	 * 
	 * @param object
	 *            被验证的实体对象
	 * 
	 * @return object为空返回 false ; object中的所有属性对应的值为空返回false.
	 */
	public static boolean isNull(Object object) {
		if (object == null) {
			return false;
		}
		// obj的属性值不全为null
		RObject rObj = new RObject(object);
		Map<String, Object> files = rObj.getFiledAndValue();
		boolean b = false;
		for (Map.Entry<String, Object> en : files.entrySet()) {
			if (en.getValue() == null) {
				b = b || false;
			} else {
				b = b || true;
			}
		}

		if (b == false) {
			return false;
		} else {
			return true;
		}
	}
}
