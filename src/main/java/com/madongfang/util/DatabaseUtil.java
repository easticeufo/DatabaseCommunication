package com.madongfang.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseUtil {

	public DatabaseUtil(@Value("${database.driver-class}")String driverClass) throws ClassNotFoundException {
		super();
		Class.forName(driverClass);
	}

	public ExecResult execSql(String sql) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		ExecResult execResult = new ExecResult();
		execResult.setAffectedRow(-1);
		execResult.setAutoKey(-1);
		
		try {
			conn = connectDatabase();
			stmt = conn.createStatement();
	
			stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			
			rs = stmt.getGeneratedKeys();
			if (rs != null)
			{
				if (rs.next())
				{
					execResult.setAutoKey(rs.getInt(1));
				}
			}
			
			execResult.setAffectedRow(stmt.getUpdateCount());
			
			logger.info("sql={}, execResult={}", sql, execResult);
			
			return execResult;
		}
		finally 
		{
			if (rs != null)
			{
				rs.close();
				rs = null;
			}
			
			if (stmt != null)
			{
				stmt.close();
				stmt = null;
			}
			
			if (conn != null)
			{
				conn.close();
				conn = null;
			}
		}
	}
	
	public static class ExecResult
	{
		public int getAffectedRow() {
			return affectedRow;
		}
		public void setAffectedRow(int affectedRow) {
			this.affectedRow = affectedRow;
		}
		public int getAutoKey() {
			return autoKey;
		}
		public void setAutoKey(int autoKey) {
			this.autoKey = autoKey;
		}
		
		@Override
		public String toString() {
			return "ExecResult [affectedRow=" + affectedRow + ", autoKey=" + autoKey + "]";
		}
		
		private int affectedRow; // 影响的行数
		private int autoKey; // 自动加一的键
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${database.url}")
	private String url;
	
	@Value("${database.username}")
	private String username;
	
	@Value("${database.password}")
	private String password;
	
	private Connection connectDatabase() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}
}
