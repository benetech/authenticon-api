package org.benetech.authenticon.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MainController {
	
	@RequestMapping(value = {"", "/", }, method = RequestMethod.GET)
    public @ResponseBody String sayHello() throws Exception{
		
		String fingerprint = "";
		String method = "";
		String part = "";
		
		if (fingerprint == null && method == null && part == null) {
			returnEncodingMethods();
		}
		else if (fingerprint == null && method != null) {
			returnEncodingMethodInfo(method);
		} else if (fingerprint != null && method != null) {
			redirectTo(method, fingerprint, part);
		}
		
		JSONObject jsonEncodingMethods = getEncodingMethods();
		Set<String> keys = jsonEncodingMethods.keySet();
		for (String key : keys){
			System.out.println(key + " :::: " + jsonEncodingMethods.get(key));
		}
		
		return Integer.toString(jsonEncodingMethods.length());
	}
	
	private void returnEncodingMethods() 
	{
//		header("Content-Type: application/json");
//		String[] methodIds = array_keys(encodingMethods);
//		methods = array();
//		for (String methodId : methodIds) {
//			encodingMethod = encodingMethods[methodId];
//			method = (Object) [
//				"id" => $methodId,
//				"name" => $encodingMethod->name,
//				"description" => $encodingMethod->description,
//				"parts" => $encodingMethod->parts,
//				"type" => $encodingMethod->type
//			];
//			array_push($methods, $method);
//		}
//		echo json_encode($methods);
	}
	
	private void returnEncodingMethodInfo(String method) {
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
	
	private void redirectTo(String method, String fingerprint, String part) {
//		String encodingMethods;
//		
//		String encodingMethod = encodingMethods[method];
//		targetUrl = encodingMethod->url;
//		if (strpos($targetUrl, "?") === false)
//			$targetUrl = $targetUrl + "?";
//		else
//			$targetUrl = $targetUrl + "&";
//		$targetUrl = $targetUrl + "fingerprint=" + urlencode($fingerprint);
//		if ($part != null)
//			$targetUrl = $targetUrl + "&part=".$part;
//		header("Location: " + $targetUrl);
//		die();
	}

	
	private JSONObject getEncodingMethods() throws Exception
	{
		Resource resource = new ClassPathResource("encoding-methods.json");
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
