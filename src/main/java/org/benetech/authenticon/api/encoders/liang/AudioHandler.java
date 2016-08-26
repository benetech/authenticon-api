package org.benetech.authenticon.api.encoders.liang;

import java.util.Base64;
import java.util.Base64.Encoder;
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
		byte[] bytesOfMessage = fingerprint.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(bytesOfMessage);
		BigInteger bigIntX = new BigInteger(1,digest);
		BigInteger bigIntY = new BigInteger("ffffffffffffffffffffffffffffffff", 16);
		BigDecimal bigDecX = new BigDecimal(bigIntX);
		BigDecimal bigDecY = new BigDecimal(bigIntY);
		BigDecimal res = bigDecX.divide(bigDecY,5,RoundingMode.HALF_UP);
		Double hashtext = res.doubleValue();


		return ResponseEntity
				.ok()
		        .contentType(MediaType.parseMediaType("text/html"))
		        .body("<h1>"+fingerprint+" "+(hashtext*1000)+"</h1>");

	}
}
	


