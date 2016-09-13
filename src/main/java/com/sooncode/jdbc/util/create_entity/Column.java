package com.sooncode.jdbc.util.create_entity;

/**
 * 字段和属性
 * 
 * @author 臣
 *
 */
public class Column {
	
	/** 字段名称 */
	private String columnName;
	
	/** 字段的注释 */
	private String columnRemarks;
	
	/** 属性名称 */
	private String propertyName;
	
	/** 数据库的数据类型 */
	private String databaseDataType;
	
	/** java数据类型  */
	private String javaDataType;
	
	/** 字段是否自增，YES , NO */
	private String isAutoinCrement;
	
	/**字段是否受到唯一约束*/
	private String isOnly ;
	
    /**字段的长度*/
	private Integer columnLength ;
	
	/**属性的长度*/
	private Integer propertyLength;
	
	
	//---------------------------------------------------------------------------------------------------------------
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	//---------------------------------------------------------------------------------------------------------------
	public String getColumnRemarks() {
		return columnRemarks;
	}

	public void setColumnRemarks(String columnRemarks) {
		this.columnRemarks = columnRemarks;
	}
	//---------------------------------------------------------------------------------------------------------------
	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	//---------------------------------------------------------------------------------------------------------------
	public String getDatabaseDataType() {
		return databaseDataType;
	}

	public void setDatabaseDataType(String databaseDataType) {
		this.databaseDataType = databaseDataType;
	}
	//---------------------------------------------------------------------------------------------------------------
	public String getJavaDataType() {
		return javaDataType;
	}

	public void setJavaDataType(String javaDataType) {
		this.javaDataType = javaDataType;
	}
	//---------------------------------------------------------------------------------------------------------------
	public String getIsAutoinCrement() {
		return isAutoinCrement;
	}

	public void setIsAutoinCrement(String isAutoinCrement) {
		this.isAutoinCrement = isAutoinCrement;
	}
	//---------------------------------------------------------------------------------------------------------------
	public Integer getColumnLength() {
		return columnLength;
	}

	public void setColumnLength(Integer columnLength) {
		this.columnLength = columnLength;
	}
	//---------------------------------------------------------------------------------------------------------------
	public Integer getPropertyLength() {
		return propertyLength;
	}

	public void setPropertyLength(Integer propertyLength) {
		this.propertyLength = propertyLength;
	}
	//---------------------------------------------------------------------------------------------------------------
	public String getIsOnly() {
		return isOnly;
	}

	public void setIsOnly(String isOnly) {
		this.isOnly = isOnly;
	}
 
}
