package com.sooncode.jdbc.util.create_entity;
/**
 * 主键
 * @author sooncode
 *
 */

public class PrimaryKey {
	
	/**
	 * 主键名称
	 */
	private String primaryKeyName; 
	
	/**
	 * 主键对应的属性名称
	 */
	private String primaryPropertyName;
	
	/**
	 * 数据库数据类型
	 */
	private String databaseDataType;
	
	/**
	 * java数据类型
	 */
	private String javaDataType;
	
	/**
	 * 主键的注释
	 */
	private String primaryKeyRemarks;
	
	/**
	 * 主键的序列号（联合主键）
	 */
	private short primaryKeySerial;// 主键的序列号（联合主键）
	
	//----------------------------------------------------------------------------------

	public String getPrimaryKeyName() {
		return primaryKeyName;
	}

	public void setPrimaryKeyName(String primaryKeyName) {
		this.primaryKeyName = primaryKeyName;
	}

	public String getPrimaryPropertyName() {
		return primaryPropertyName;
	}

	public void setPrimaryPropertyName(String primaryPropertyName) {
		this.primaryPropertyName = primaryPropertyName;
	}

	public String getDatabaseDataType() {
		return databaseDataType;
	}

	public void setDatabaseDataType(String databaseDataType) {
		this.databaseDataType = databaseDataType;
	}

	public String getJavaDataType() {
		return javaDataType;
	}

	public void setJavaDataType(String javaDataType) {
		this.javaDataType = javaDataType;
	}

	public String getPrimaryKeyRemarks() {
		return primaryKeyRemarks;
	}

	public void setPrimaryKeyRemarks(String primaryKeyRemarks) {
		this.primaryKeyRemarks = primaryKeyRemarks;
	}

	public short getPrimaryKeySerial() {
		return primaryKeySerial;
	}

	public void setPrimaryKeySerial(short primaryKeySerial) {
		this.primaryKeySerial = primaryKeySerial;
	}
	
	
 
}
