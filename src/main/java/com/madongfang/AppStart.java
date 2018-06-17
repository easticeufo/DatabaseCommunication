package com.madongfang;

import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.madongfang.tcp.ServerThread;
import com.madongfang.util.CommonUtil;
import com.madongfang.util.DatabaseUtil;

@Component
public class AppStart implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		logger.info("AppStart start");
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket(port);
			Socket socket = null;
			
			while (true)
			{
				socket = serverSocket.accept();
				new ServerThread(commonUtil, databaseUtil, socket, password).start();
			}
			
		} finally {
			serverSocket.close();
			logger.info("AppStart stop");
		}
		
	}

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${tcpServer.port}")
	private int port;
	
	@Value("${tcpServer.password}")
	private String password;
	
	@Autowired
	private DatabaseUtil databaseUtil;
	
	@Autowired
	private CommonUtil commonUtil;
	
}
