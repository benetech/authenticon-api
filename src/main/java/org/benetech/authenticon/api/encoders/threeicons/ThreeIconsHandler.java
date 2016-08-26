package org.benetech.authenticon.api.encoders.threeicons;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import org.benetech.authenticon.api.VisualizeFingerprintController;
import org.benetech.authenticon.api.encoders.iconmap.AbstractIconMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ThreeIconsHandler extends AbstractIconMapper {

	public ResponseEntity<?> visualizeIcons(String methodUrl, JSONObject encodingMethod, String fingerprint, String part) throws Exception 
	{
		fingerprint = padFingerprint(fingerprint,3);
		JSONObject iconsJson = VisualizeFingerprintController.loadJsonFile("icons.json");
		ArrayList<String> allIconNames = new ArrayList<String>();
		allIconNames.addAll(iconsJson.keySet());
		
		ArrayList<String> matchingIconNames = new ArrayList<String>();
		if (fingerprint == null) 
			matchingIconNames.addAll(iconsJson.keySet());
		else 
			matchingIconNames.addAll(getIconNamesForFingerprint(fingerprint, allIconNames));
		
		if (methodUrl.endsWith("textOnly=true")) 
			return createTextOnlyResponse(matchingIconNames);
		else 
			return create3IconResponse(iconsJson, matchingIconNames, methodUrl);
	}

	private ResponseEntity<?> create3IconResponse(JSONObject iconsJson, ArrayList<String> matchingIconNames, String methodUrl) throws Exception 
	{
		ArrayList<String> iconPaths = new ArrayList<String>();
		ArrayList<String> iconDescs = new ArrayList<String>();
		for (String iconName : matchingIconNames)
		{
			JSONObject json = iconsJson.optJSONObject(iconName);
			String iconFileName = json.getString("filename");
			String iconDesc = iconName;
			iconPaths.add(iconFileName);
			iconDescs.add(iconDesc);
		}

		InputStream imageInputStream = renderSingleImageFromPaths(iconPaths, methodUrl,iconDescs);

		return ResponseEntity
				.ok()
				.contentLength(imageInputStream.available())
				.contentType(MediaType.IMAGE_PNG)
				.body(new InputStreamResource(imageInputStream));
	}

	private ResponseEntity<?> createTextOnlyResponse(ArrayList<String> matchingIconNames) 
	{
		JSONArray encodingMethodsArray = new JSONArray();
		for (String iconName : matchingIconNames)
		{
			encodingMethodsArray.put(iconName + "\n");
		}
		
		return ResponseEntity
				.ok()
		        .contentType(MediaType.parseMediaType("application/json"))
		        .body(encodingMethodsArray.toString());
	}
	
	private ArrayList<String> getIconNamesForFingerprint(String fingerprint, ArrayList<String> allIconNames) 
	{
		fingerprint = padFingerprint(fingerprint,3);
		ArrayList<String> imagesForFingerprint = VisualizeFingerprintController.getIconNamesForFingerprint(fingerprint);
		if (imagesForFingerprint.isEmpty())
		{
			ArrayList<String> randomIconNames = getRandomIconNames(3, allIconNames);
			VisualizeFingerprintController.updateCache(fingerprint, randomIconNames);

			return randomIconNames;
		}
	
		return imagesForFingerprint;
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
	
	private ArrayList<String> getRandomIconNames(int number, ArrayList<String> allImageNames) 
	{
		ArrayList<String> randomIconNames = new ArrayList<String>();
		int index = 0;
		while (index < number) 
		{
			String nextRandomIconName = getUniqueRandomIconName(allImageNames, randomIconNames);			
			randomIconNames.add(nextRandomIconName);
			++index;
		}
		
		return randomIconNames;
	}

	private String getUniqueRandomIconName(ArrayList<String> allImageNames, ArrayList<String> randomImageNames) {
		Random random = new Random();
		String uniqueRandomeImageName = null;
		while (true) 
		{
			uniqueRandomeImageName = allImageNames.get(random.nextInt(allImageNames.size() - 1));
			if (!randomImageNames.contains(uniqueRandomeImageName))
				return uniqueRandomeImageName;
		}
	}

	@Override
	protected String getIconDirectory() {
		return "/3icons/icons/";
	}

	@Override
	protected int getImageColumnCount() {
		return 3;
	}	
}
