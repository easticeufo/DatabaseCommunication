package com.madongfang.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;

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
			byte[] info = new byte[256];
			
			while (true)
			{
				RequestData requestData = getPackage(in);
				if (requestData == null)
				{
					continue;
				}
				
				Arrays.fill(info, (byte)0);
				switch (requestData.getType()) {
				case TYPE_SQL:
					try {
						DatabaseUtil.ExecResult sqlResult = databaseUtil.execSql(new String(requestData.getBody(), "UTF-8"));
						out.write(commonUtil.intToByteArray(0));
						System.arraycopy(commonUtil.intToByteArray(sqlResult.getAffectedRow()), 0, info, 0, 4);
						System.arraycopy(commonUtil.intToByteArray(sqlResult.getAutoKey()), 0, info, 4, 4);
					} catch (SQLException e) {
						logger.warn("SQLException:{}", e.getMessage());
						byte[] messageByteArray = e.getMessage().getBytes();
						int copylen = messageByteArray.length < info.length ? messageByteArray.length : (info.length - 1);
						System.arraycopy(messageByteArray, 0, info, 0, copylen);
						out.write(commonUtil.intToByteArray(-1));
					}
					out.write(info);
					
					break;

				default:
					logger.warn("unknown request type: type={}", requestData.getType());
					break;
				}
			}
		} 
		catch (IOException e) 
		{
			logger.warn("TCP disconnect");
			logger.debug("IOException:", e);
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
	
	private RequestData getPackage(InputStream inputStream) throws IOException
	{
		RequestData requestData = new RequestData();
		
		 /* 读取tcp头数据 */
		final int headlen = 40;
		byte[] head = new byte[headlen];
		int off = 0;
		int len = headlen;
		while (len > 0)
		{
			int readlen = inputStream.read(head, off, len);
			if (readlen < 0)
			{
				logger.warn("inputStream.read < 0");
				throw new IOException("inputStream.read < 0");
			}
			off += readlen;
			len -= readlen;
		}
		
		int type = commonUtil.byteArrayToInt(head);
		int bodylen = commonUtil.byteArrayToInt(head, 4);
		String authToken = new String(head, 8, 32);
		logger.debug("type={}, bodylen={}, authToken={}", type, bodylen, authToken);
		
		/* 读取tcp body数据 */
		byte[] body = new byte[bodylen];
		off = 0;
		len = bodylen;
		while (len > 0)
		{
			int readlen = inputStream.read(body, off, len);
			if (readlen < 0)
			{
				logger.warn("inputStream.read < 0");
				throw new IOException("inputStream.read < 0");
			}
			off += readlen;
			len -= readlen;
		}
		
		/* 校验 */
		byte[] passwordArray = password.getBytes();
		byte[] authArray = new byte[body.length + passwordArray.length];
		System.arraycopy(body, 0, authArray, 0, body.length);
		System.arraycopy(passwordArray, 0, authArray, body.length, passwordArray.length);
		logger.debug("authArray string={}", new String(authArray));
		if (!authToken.equals(commonUtil.md5(authArray)))
		{
			logger.warn("Authentication failure: authToken={}, authArray md5={}", commonUtil.md5(authArray));
			return null;
		}
		
		requestData.setAuthToken(authToken);
		requestData.setBody(body);
		requestData.setLength(bodylen);
		requestData.setType(type);
		
		return requestData;
	}
}
