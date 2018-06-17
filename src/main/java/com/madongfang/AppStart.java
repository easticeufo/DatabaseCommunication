package com.madongfang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.madongfang.util.DatabaseUtil;

@Component
public class AppStart implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		logger.info("AppStart start");
		
		logger.info("AppStart stop");
	}

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private DatabaseUtil databaseUtil;
}
