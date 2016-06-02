package org.benetech.authenticon.api.encoders.iconmap;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.json.JSONObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import sun.awt.image.ToolkitImage;

abstract public class AbstractIconMapHandler extends AbstractIconMapper{

	private static final int COLUMN_COUNT = 4;
	private static final int IMAGE_WIDTH = 90;
	private static final int IMAGE_HEIGHT = 75;
	private static final int BUFFER_BETWEEN_ICONS = 20;

	public ResponseEntity<?> visualizeIcons(String methodUrl, JSONObject encodingMethod, String fingerprint, String part) throws Exception {

		int groupCount = calculateNumberOfGroups(fingerprint);
		ArrayList<String> allIconFileNames = generateIconFileNames(10000);
		ArrayList<String> groupedFingerprint = getFingerprintGroups(fingerprint, groupCount);
		ArrayList<String> matchingIconFilenames = matchGroupToIconFilename(allIconFileNames, groupedFingerprint);
		
		InputStream imageInputStream = renderSingleImageFromPaths(matchingIconFilenames);

		return ResponseEntity
				.ok()
				.contentLength(imageInputStream.available())
				.contentType(MediaType.IMAGE_PNG)
				.body(new InputStreamResource(imageInputStream));
	}

	private int calculateNumberOfGroups(String fingerprint) {
		return (int) Math.ceil((double)fingerprint.length() / getMappingCount());
	}
	
	private ArrayList<String> matchGroupToIconFilename(ArrayList<String> allIconFileNames, ArrayList<String> groupedFingerprints) {
		ArrayList<String> matchingFileNames = new ArrayList<>();
		for (int index = 0; index < groupedFingerprints.size(); ++index) {			
			String iconName = groupedFingerprints.get(index);
			String iconFilename = String.format("%04d.png", Integer.parseInt(iconName));
			matchingFileNames.add(iconFilename);
		}
		
		return matchingFileNames;
	}

	private ArrayList<String> generateIconFileNames(int iconCount) {
		ArrayList<String> iconFileNames = new ArrayList<>();
		for (int index = 0; index < iconCount; ++index) {
			String iconFileName = String.format("%04d.png", index);
			iconFileNames.add(iconFileName);
		}
		
		return iconFileNames;
	}

	private ArrayList<String> getFingerprintGroups(String fingerprint, int groupCount) throws Exception {
		String paddedFingerprint = padFingerprintToBeCreateEventNumberedGroups(fingerprint, groupCount);		
		ArrayList<String> fingerprintGroups = splitFingerprintIntoGroups(paddedFingerprint, groupCount);
		
		verifyGroupingWasSuccessfull(paddedFingerprint, fingerprintGroups);

		return fingerprintGroups;
	}

	private void verifyGroupingWasSuccessfull(String paddedFingerprint, ArrayList<String> fingerprintGroups) throws Exception {
		StringBuffer assembledFromGroupsFingerprint = new StringBuffer(); 
		for (String fingerprintGroup : fingerprintGroups) {
			assembledFromGroupsFingerprint.append(fingerprintGroup);
		}
		
		if (!paddedFingerprint.equals(assembledFromGroupsFingerprint.toString()))
			throw new Exception("Fingerprint was not grouped correctly.  Padded fingerprint= " + paddedFingerprint + " assmebled fingerprint = " + assembledFromGroupsFingerprint);
	}

	private String padFingerprintToBeCreateEventNumberedGroups(String fingerprint, int groupCount) {
		int numberOfGroups = (int)Math.ceil((double)fingerprint.length() / groupCount);
		final String PADDING_VALUE = "0";
		while (fingerprint.length() < (numberOfGroups * groupCount)) {
			fingerprint = fingerprint + PADDING_VALUE;
		}
		
		return fingerprint;
	}

	private ArrayList<String> splitFingerprintIntoGroups(String fingerprint, int groupCount) {
		int start = 0;
		ArrayList<String> groups = new ArrayList<>();
		while (start < fingerprint.length()) {
			String subFingerprint = fingerprint.substring(start, start + groupCount);
			groups.add(subFingerprint);
			start += groupCount;
		}
		
		return groups;
	}
	
	abstract protected int getMappingCount();	
}
