package com.madongfang.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.madongfang.util.CommonUtil;
import com.madongfang.util.DatabaseUtil;

public class ServerThread extends Thread {

	public ServerThread(CommonUtil commonUtil, DatabaseUtil databaseUtil, Socket socket, String password) {
		super();
		this.commonUtil = commonUtil;
		this.databaseUtil = databaseUtil;
		this.socket = socket;
		this.password = password;
	}

	@Override
	public void run() {
		logger.info("ServerThread start");
		InetAddress address = socket.getInetAddress();
		logger.info("client address: {}", address.getHostAddress());
		
		InputStream in = null;
		OutputStream out = null;
		try 
		{
			in = socket.getInputStream();
			out = socket.getOutputStream();
			
			while (true)
			{
				RequestData requestData = getPackage(in);
				switch (requestData.getType()) {
				case TYPE_SQL:
					try {
						DatabaseUtil.ExecResult sqlResult = databaseUtil.execSql(new String(requestData.getBody(), "UTF-8"));
					} catch (SQLException e) {
						logger.warn("SQLException:", e);
					}
					
					break;

				default:
					logger.warn("unknown request type: type={}", requestData.getType());
					break;
				}
			}
		} 
		catch (IOException e) 
		{
			logger.error("IOException:", e);
		}
		finally 
		{
			try {
				if (in != null)
				{
					in.close();
				}
				
				if (out != null)
				{
					out.close();
				}
			} catch (IOException e) {
				logger.error("IOException:", e);
			}
			logger.info("ServerThread stop");
		}
		
	}

	private static final int TYPE_SQL = 1;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private CommonUtil commonUtil;
	
	private DatabaseUtil databaseUtil;
	
	private Socket socket;
	
	private String password;
	
	private RequestData getPackage(InputStream inputStream)
	{
		RequestData requestData = new RequestData();
		
		return requestData;
	}
}
