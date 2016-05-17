package org.benetech.authenticon.api.encoders.threeicons;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import org.benetech.authenticon.api.VisualizeFingerprintController;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import sun.awt.image.ToolkitImage;

public class ThreeIconsHandler {

	public ResponseEntity<?> visualizeIcons(String methodUrl, JSONObject encodingMethod, String fingerprint, String part) throws Exception 
	{
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
			return create3IconResponse(iconsJson, matchingIconNames);
	}

	private ResponseEntity<?> create3IconResponse(JSONObject iconsJson, ArrayList<String> matchingIconNames) throws Exception 
	{
		ArrayList<String> iconPaths = new ArrayList<String>();
		for (String iconName : matchingIconNames) 
		{
			JSONObject json = iconsJson.optJSONObject(iconName);
			String iconFileName = json.getString("filename");
			iconPaths.add(iconFileName);
		}

		InputStream imageInputStream = renderSingleImageFromPaths(iconPaths);

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
		ArrayList<String> randomIconNames = getRandomIconNames(3, allIconNames);
		//TODO This needs to cache (with 24h expiry) and extract image if available 
		return randomIconNames;
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
	
	private InputStream renderSingleImageFromPaths(ArrayList<String> imageFileNames) throws Exception
	{
		final int NUMBER_OF_COLUMNS = 3;
		final int BUFFER_BETWEEN_ICONS = 15;

		BufferedImage result = new BufferedImage(480, 480, BufferedImage.TYPE_INT_ARGB);
		Graphics g = result.getGraphics();

		int x = 0; 
		int y = 0;
		for (String iconFileName : imageFileNames) 
		{
			ToolkitImage scaledDownImage = getScaledDownBufferedImage(iconFileName);
			g.drawImage(scaledDownImage, x, y, Color.WHITE, null);
			
			int moveXByAmount = result.getWidth() / NUMBER_OF_COLUMNS;
			x += moveXByAmount;
			
			boolean shouldCreateNewRow = x > result.getWidth();
			if(shouldCreateNewRow){
				x = 0;
				y += scaledDownImage.getHeight();
				y += BUFFER_BETWEEN_ICONS;
			}
		}
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(result, "png", outputStream);

		byte[] byteArray = outputStream.toByteArray();
		outputStream.close();
		
		return new ByteArrayInputStream(byteArray);
	}

	private ToolkitImage getScaledDownBufferedImage(String iconFileName) throws IOException {
		ClassPathResource classPathResource = new ClassPathResource("/3icons/icons/" + iconFileName);
		InputStream inputStream = classPathResource.getInputStream();
		BufferedImage bufferedImage = ImageIO.read(inputStream);
		inputStream.close();
		
		return (ToolkitImage) bufferedImage.getScaledInstance(150, 180, Image.SCALE_SMOOTH);
	}
}
