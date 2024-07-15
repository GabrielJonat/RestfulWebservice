package com.example.demo.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.example.demo.models.User;

public class DataLoader {
    public static void main(String[] args) {
    	 
    	        User user = new User();
    	        user.setRole("ADMIN");
    	        String userInfo = user.toString();
    	        System.out.print(userInfo.substring(userInfo.indexOf("role")+5,userInfo.indexOf("createdAt")-2));
    	        
    	        String originalString = "Your input string here";
    	        MessageDigest digest;
				try {
					digest = MessageDigest.getInstance("SHA-256");
					byte[] encodedHash = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));

	    	        // Convert the hash to hexadecimal
	    	        String hashedValue = bytesToHex(encodedHash);
	    	        System.out.println("SHA-256 hash: " + hashedValue);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	        
    	    }

    	    private static String bytesToHex(byte[] hash) {
    	        StringBuilder hexString = new StringBuilder(2 * hash.length);
    	        for (int i = 0; i < hash.length; i++) {
    	            String hex = Integer.toHexString(0xff & hash[i]);
    	            if (hex.length() == 1) {
    	                hexString.append('0');
    	            }
    	            hexString.append(hex);
    	        }
    	        System.out.println(hexString.toString());
    	        return hexString.toString();
    	    }
    	                	    
}
