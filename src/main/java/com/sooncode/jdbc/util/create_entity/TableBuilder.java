package com.sooncode.jdbc.util.create_entity;

 
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
 
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;


 
import com.sooncode.jdbc.util.T2E;


public class TableBuilder {
	/**
	 * 数据库连接对象
	 */
	private DatabaseMetaData databaseMetaData;

	 
	private String dataBaseName;

	public TableBuilder(String ip, String port, String user, String password, String dataBaseName) {
		try {
			databaseMetaData = this.getConnection(ip, port, user, password, dataBaseName).getMetaData();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		this.dataBaseName = dataBaseName;
	}

	public Table getTable(String tableName) {
		Table t = new Table();

		try {

			String[] types = { "Table" };
			// 数据库名称/ userName/ 表名称 / 类型
			ResultSet tableSet = databaseMetaData.getTables(this.dataBaseName, null, tableName, types);//
			while (tableSet.next()) { // 遍历数据库的表
				// String tableName =
				// tableSet.getString("TABLE_NAME").toUpperCase();
				String tableRemarks = tableSet.getString("REMARKS");// 表注释

				t.setTableName(tableName);
				t.setTableRemarks(tableRemarks);
			}

		} catch (Exception e) {

			try {
				((Connection) databaseMetaData).close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			e.printStackTrace();
		}

		return t;
	}

	/**
	 * 查询数据库表的所有字段 构造 “字段属性模型”
	 * 
	 * @param databaseName
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public Map<String,Column> getColumns(String tableName) {

		try {

			Map<String,Column> columnes = new HashMap<>();
			ResultSet columnSet = databaseMetaData.getColumns(this.dataBaseName, "%", tableName, "%");
			while (columnSet.next()) { // 遍历某个表的字段

				String columnRemarks = columnSet.getString("REMARKS");

				String columnName = columnSet.getString("COLUMN_NAME".toUpperCase());
				String columnType = columnSet.getString("TYPE_NAME");
				int dataType = Integer.parseInt(columnSet.getString("DATA_TYPE"));

				String javaDataType = Jdbc2Java.getJavaData().get(Jdbc2Java.getJdbcData().get("" + dataType));

				String isAutoinCrement = columnSet.getString("IS_AUTOINCREMENT");

				Column column = new Column();
				column.setColumnName(columnName.toUpperCase());
				column.setPropertyName(T2E.toField(columnName));
				column.setDatabaseDataType(columnType);
				column.setJavaDataType(javaDataType);
				column.setColumnRemarks(columnRemarks);
				column.setIsAutoinCrement(isAutoinCrement);
				column.setColumnLength(column.getColumnName().length());
				column.setPropertyLength(column.getPropertyName().length());

				columnes.put(column.getColumnName(),column);

			}

			return columnes;
		} catch (Exception e) {

			return null;
		}

	}

	public PrimaryKey getPrimaryKey(String tableName) {

		PrimaryKey primaryKey = new PrimaryKey();

		ResultSet primaryKeyResultSet;
		try {
			primaryKeyResultSet = this.databaseMetaData.getPrimaryKeys(this.dataBaseName, null, tableName);
			while (primaryKeyResultSet.next()) { // 遍历某个表的主键
				String primaryKeyName = primaryKeyResultSet.getString("COLUMN_NAME");
				String primaryKeySerial = primaryKeyResultSet.getString("KEY_SEQ");
				String primaryKeyDataType = "";
				primaryKey.setPrimaryPropertyName(T2E.toField(primaryKeyName));
				primaryKey.setPrimaryKeyName(primaryKeyName.toUpperCase());
				primaryKey.setDatabaseDataType(primaryKeyDataType);
				primaryKey.setPrimaryKeySerial(Short.parseShort(primaryKeySerial));
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return primaryKey;
	}

	private Connection getConnection(String ip, String port, String user, String password, String dataBaseName) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Properties properties = new Properties();
			properties.setProperty("user", user);
			properties.setProperty("password", password);
			properties.setProperty("remarks", "true");
			properties.setProperty("useInformationSchema", "true");

			String url = "jdbc:mysql://" + ip + ":" + port + "/" + dataBaseName + "?useUnicode=true&characterEncoding=UTF8";

			Connection connection = DriverManager.getConnection(url, properties);
			if (connection != null) {
				return connection;
			} else {
				return null;
			}

		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}

	}
	
	
    /**
     * 獲取 實體類代碼  
     * @param tableName 表名稱
     * @return 實體類代碼
     */
	public String  getEntityClassCode(String tableName){
		Table t = getTable(tableName);
		Map<String,Column> columns = getColumns(tableName);
		PrimaryKey pk = getPrimaryKey(tableName);
		 
		Column pkColumn = columns.get(pk.getPrimaryKeyName());
		 
		String code ="";
		String importString = "\r\n";
		  importString += "import java.io.Serializable;\r\n";
		String an = "/**\r\n";
		         an+="*"+t.getTableRemarks()+"\r\n";
		         an+="* @author hechen \r\n";
		         an+="* \r\n";
		         an+="*/ \r\n";
		String classString = "public class "+T2E.toClassName( t.getTableName()) +" implements Serializable{ \r\n" ;
		       classString +="\t private static final long serialVersionUID = 1L;\r\n";
		       
		String pkString = "\t /** "+pkColumn.getColumnRemarks()+" */ \r\n";
		      pkString += "\t private "+pkColumn.getJavaDataType()+" "+pkColumn.getPropertyName()+"; \r\n";
		      code =  importString+  an+ classString+ pkString;
		       
		String propertys = "";
		String getSetString = "";
		for(Entry<String, Column> en:columns.entrySet()){
			Column c = en.getValue();
			//----------------get set ---------------
			getSetString +="\t /** "+c.getColumnRemarks()+" */\r\n";
			getSetString +="\t public "+c.getJavaDataType()+" get"+T2E.toClassName(c.getColumnName())+"() { \r\n";
			getSetString +="\t \t return "+c.getPropertyName()+";\r\n";
			getSetString +="\t }\r\n";
			getSetString +="\t /** "+c.getColumnRemarks()+" */\r\n";
			getSetString +="\t public void set"+T2E.toClassName(c.getColumnName())+"("+c.getJavaDataType()+" "+c.getPropertyName()+") {\r\n";
			getSetString +="\t \t this."+c.getPropertyName()+" = "+c.getPropertyName()+";\r\n";
			getSetString +="\t }\r\n";
			getSetString +="\r\n";
			
			if(c.getPropertyName().equals(pkColumn.getPropertyName())){
				continue;
			}
			String remarks = "\t /** "+c.getColumnRemarks()+" */\r\n";
			String coluString = "\t private " + c.getJavaDataType()+ " " + c.getPropertyName()+ " ;\r\n";
			remarks +=   coluString;
			propertys +=  remarks;
			
			 
		}
		 
		code = code +propertys +"\r\n" +getSetString +"}\r\n";
		return code;
	}

	 

}
