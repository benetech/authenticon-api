package org.benetech.authenticon.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.benetech.authenticon.api.encoders.iconmap.IconMap10GroupsHandler;
import org.benetech.authenticon.api.encoders.iconmap.IconMap14GroupsHandler;
import org.benetech.authenticon.api.encoders.liang.AudioHandler;
import org.benetech.authenticon.api.encoders.threeicons.ThreeIconsHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class VisualizeFingerprintController {

	private static final long MILLIS_PER_HOUR = 3600000;
	private static final long EXPIRE_IN_24_HOUR = 24; 
	private static final long EXPIRE_TIME_IN_MILLIS = EXPIRE_IN_24_HOUR * MILLIS_PER_HOUR;
	private static PassiveExpiringMap<String, ArrayList<String>> fingerprintToImageNamesCache;
	
	static {
		fingerprintToImageNamesCache = new PassiveExpiringMap<>(EXPIRE_TIME_IN_MILLIS);
	}
	
	public static PassiveExpiringMap<String, ArrayList<String>> getFingerprintToIconCache()
	{
		return fingerprintToImageNamesCache;
	}
	
	public static void updateCache(String key, ArrayList<String> imageNames)
	{
		fingerprintToImageNamesCache.put(key, imageNames);
	}
	
	public static ArrayList<String> getIconNamesForFingerprint(String fingerprint) 
	{
		PassiveExpiringMap<String, ArrayList<String>> fingerprintToImageNamesCache = getFingerprintToIconCache();
		if (fingerprintToImageNamesCache.containsKey(fingerprint)) {
			System.out.println("INFO: Fingerprint was reuse from cache.  Fingerprint = " + fingerprint);
			return fingerprintToImageNamesCache.get(fingerprint);
		}
				
		return new ArrayList<String>();
	}
	
	@CrossOrigin
	@RequestMapping(value = {"/"}, method = RequestMethod.GET, produces = {"image/png; application/json; charset=UTF-8"})
    public @ResponseBody ResponseEntity<?> visualizeFingerprint(@RequestParam(value="method", required=false) String method,
    												 			@RequestParam(value="fingerprint", required=false) String fingerprint,
    												 			@RequestParam(value="cols", required=false) String cols,
    												 			@RequestParam(value="part", required=false) String part) throws Exception
	{
		
		if (fingerprint == null && method == null && part == null) {			
			return ResponseEntity
					.ok()
			        .contentType(MediaType.parseMediaType("application/json"))
			        .body(returnEncodingMethods());
		}
		else if (fingerprint == null && method != null) 
			return ResponseEntity
					.ok()
			        .contentType(MediaType.parseMediaType("application/json"))
			        .body(returnEncodingMethodInfo(method));
		
		else if (fingerprint != null && method != null) 
			return redirectTo(method, fingerprint, part);

		return ResponseEntity.ok().build();
	}
	
	private String returnEncodingMethods() throws Exception 
	{
		JSONObject jsonEncodingMethods = getEncodingMethods();
		Set<String> keys = jsonEncodingMethods.keySet();
		JSONArray encodingMethodsArray = new JSONArray();
		for (String key : keys)
		{
			JSONObject jsonEncodingMethod = (JSONObject) jsonEncodingMethods.get(key);
			JSONObject newJson = new JSONObject();
			newJson.put("id", key);
			newJson.put("name", jsonEncodingMethod.get("name"));
			newJson.put("description", jsonEncodingMethod.get("description"));
			newJson.put("parts", jsonEncodingMethod.get("parts"));
			newJson.put("type", jsonEncodingMethod.get("type"));

			encodingMethodsArray.put(newJson);
		}
		
		return encodingMethodsArray.toString();		
	}
	
	private String returnEncodingMethodInfo(String method) throws Exception
	{
		JSONObject jsonEncodingMethods = getEncodingMethods();
		JSONObject encodingMethodsDescription = new JSONObject();
		
		JSONObject jsonEncodingMethod = (JSONObject) jsonEncodingMethods.get(method);
		encodingMethodsDescription.put("description", jsonEncodingMethod.get("description"));
		
		return encodingMethodsDescription.toString();	
	}
	
	private ResponseEntity<?> redirectTo(String method, String fingerprint, String part) throws Exception 
	{
		JSONObject jsonEncodingMethods = getEncodingMethods();
		JSONObject encodingMethod = jsonEncodingMethods.optJSONObject(method);
		if (encodingMethod == null)
			return ResponseEntity.noContent().build();
		
		String methodUrl = encodingMethod.getString("url");

		//TODO should be redirecting to match PHP
		if (methodUrl.startsWith("encoders/threeicons/ThreeIconsHandler"))
			return new ThreeIconsHandler().visualizeIcons(methodUrl, encodingMethod, fingerprint, part);
		
		if (methodUrl.startsWith("encoders/iconmap/10IconMapHandler"))
			return new IconMap10GroupsHandler().visualizeIcons(methodUrl, encodingMethod, fingerprint, part);
		
		if (methodUrl.startsWith("encoders/iconmap/14IconMapHandler"))
			return new IconMap14GroupsHandler().visualizeIcons(methodUrl, encodingMethod, fingerprint, part);
		
		if (methodUrl.startsWith("encoders/iconmap/Audio"))
			return new AudioHandler().visualizeIcons(methodUrl, encodingMethod, fingerprint, part);

		return ResponseEntity.noContent().build(); 
	}
	
	private JSONObject getEncodingMethods() throws Exception
	{
		return loadJsonFile("encoding-methods.json");
	}

	public static JSONObject loadJsonFile(String jsonFileName) throws IOException {
		Resource resource = new ClassPathResource(jsonFileName);
		InputStream resourceInputStream = resource.getInputStream();
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceInputStream));
		
		String read;
		while((read=bufferedReader.readLine()) != null) {
		    stringBuilder.append(read);   
		}

		bufferedReader.close();
		
		return new JSONObject(stringBuilder.toString());
	}	
}
