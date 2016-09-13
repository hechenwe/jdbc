package com.sooncode.jdbc.util.create_entity;

 
import java.util.Map;
 

 

public class Table implements Cloneable {

	/** 数据库表名 */
	private String tableName;
	 
	
	/** 表注释 */
	private String tableRemarks;
 
	
	/** 所有字段 */
	private Map<String, Column> columns;
	
	/** 主键 */
	private PrimaryKey primaryKey;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableRemarks() {
		return tableRemarks;
	}

	public void setTableRemarks(String tableRemarks) {
		this.tableRemarks = tableRemarks;
	}

	 

	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}

	public Map<String, Column> getColumns() {
		return columns;
	}

	public void setColumns(Map<String, Column> columns) {
		this.columns = columns;
	}
	
	
	 

	
	 

}
