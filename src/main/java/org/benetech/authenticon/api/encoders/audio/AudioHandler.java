package org.benetech.authenticon.api.encoders.audio;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.*;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


public class AudioHandler {
	
	protected static final int SAMPLE_RATE = 16 * 1024;
	
	public AudioHandler() 
	{
	}

	public ResponseEntity<?> visualizeIcons(String methodUrl, JSONObject encodingMethod, String fingerprint, String part) throws Exception 
	{
		fingerprint = padFingerprint(fingerprint,3);
		byte[] bytesOfMessage = fingerprint.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(bytesOfMessage);
		BigInteger bigIntX = new BigInteger(1,digest);
		BigInteger bigIntY = new BigInteger("ffffffffffffffffffffffffffffffff", 16);
		BigDecimal bigDecX = new BigDecimal(bigIntX);
		BigDecimal bigDecY = new BigDecimal(bigIntY);
		BigDecimal res = bigDecX.divide(bigDecY,5,RoundingMode.HALF_UP);
		Double hashMul = res.doubleValue()*1000;
		
		String htmlout = "<p class=\"lead\">Press Below to Play Audio Sample</p><button class=\"btn btn-primary btn-lg\" onclick=\"play(\'"+fingerprint+"\',"+hashMul+")\">Play Sound</button>";

		return ResponseEntity
				.ok()
		        .contentType(MediaType.parseMediaType("text/html"))
		        .body(htmlout);

	}
	
	private String padFingerprint(String fingerprint, int groupCount) {
		int remainder = (int) fingerprint.length() % groupCount;
		if (remainder == 1) {
			fingerprint = new StringBuilder(fingerprint).insert(fingerprint.length()-1, "00").toString();
		} else if (remainder == 2) {
			fingerprint = new StringBuilder(fingerprint).insert(fingerprint.length()-2, "0").toString();
		}
		
		return fingerprint.toUpperCase();
	}
}
	


