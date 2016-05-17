package org.benetech.authenticon.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

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
	
	@CrossOrigin
	@RequestMapping(value = {"/"}, method = RequestMethod.GET, produces = {"image/png; application/json; charset=UTF-8"})
    public @ResponseBody ResponseEntity<?> visualizeFingerprint(@RequestParam(value="method", required=false) String method,
    												 			@RequestParam(value="fingerprint", required=false) String fingerprint,
    												 			@RequestParam(value="part", required=false) String part) throws Exception
	{
		if (fingerprint == null && method == null && part == null) {			
			return ResponseEntity
					.ok()
			        .contentType(MediaType.parseMediaType("application/json"))
			        .body(returnEncodingMethods());
		}
		else if (fingerprint == null && method != null) 
			return returnEncodingMethodInfo(method);
		
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
	
	private ResponseEntity<?> returnEncodingMethodInfo(String method) throws Exception
	{
		throw new Exception("-------------------------- returnEncodingMethodInfo has not been implemented! method = " + method);
//		global $encodingMethods;
//		header("Content-Type: application/json");
//		$encodingMethod = $encodingMethods[$method];
//		if ($encodingMethod == null)
//			$encodingMethod = array(
//				"description" => "Error. No such method as "".$method.""",
//				"parts" => 0
//			);
//		echo json_encode($encodingMethod);
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
