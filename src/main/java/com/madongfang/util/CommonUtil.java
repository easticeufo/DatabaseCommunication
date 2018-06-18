package com.madongfang.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CommonUtil {

	/**
	 * 随机字符生成
	 * 
	 * @param length
	 * @return
	 */
	public String getRandomStringByLength(int length) {  
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  
        Random random = new Random();  
        StringBuffer sb = new StringBuffer();  
        for (int i = 0; i < length; i++) {  
            int number = random.nextInt(base.length());  
            sb.append(base.charAt(number));  
        }  
        return sb.toString();
    }
	
	/**
	 * 将字符串进行MD5加密
	 * 
	 * @param str
	 * @return 32位小写形式的字符串
	 */
	public String md5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			return toHex(md.digest()).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			logger.error("catch Exception:", e);
			return null;
		}
	}
	
	/**
	 * 将字符串进行MD5加密
	 * 
	 * @param bytes
	 * @return 32位小写形式的字符串
	 */
	public String md5(byte[] bytes) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(bytes);
			return toHex(md.digest()).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			logger.error("catch Exception:", e);
			return null;
		}
	}
	
	public int byteArrayToInt(byte[] b) {
		return (b[3] & 0xFF) | ((b[2] & 0xFF) << 8) | ((b[1] & 0xFF) << 16) | ((b[0] & 0xFF) << 24);
	}
	
	public int byteArrayToInt(byte[] b, int off) {
		return (b[3+off] & 0xFF) | ((b[2+off] & 0xFF) << 8) | ((b[1+off] & 0xFF) << 16) | ((b[0+off] & 0xFF) << 24);
	}
	
	public byte[] intToByteArray(int a) {
		return new byte[] {(byte)((a >> 24) & 0xFF), (byte)((a >> 16) & 0xFF), (byte)((a >> 8) & 0xFF), (byte)(a & 0xFF)};
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 将16位byte[] 转换为32位String
	 * 
	 * @param buffer
	 * @return
	 */
	private String toHex(byte buffer[]) {
		StringBuffer sb = new StringBuffer(buffer.length * 2);
		for (int i = 0; i < buffer.length; i++) {
			sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));
			sb.append(Character.forDigit(buffer[i] & 15, 16));
		}

		return sb.toString();
	}
	
}
