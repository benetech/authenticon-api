package org.benetech.authenticon.api.encoders.iconmap;
import java.io.InputStream;
import java.util.ArrayList;
import org.json.JSONObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


abstract public class AbstractIconMapHandler extends AbstractIconMapper{

	private static final int COLUMN_COUNT = 6;

	public ResponseEntity<?> visualizeIcons(String methodUrl, JSONObject encodingMethod, String fingerprint, String part) throws Exception {

		int groupCount = 3;
		ArrayList<String> allIconFileNames = generateIconFileNames(10000);
		ArrayList<String> groupedFingerprint = getFingerprintGroups(fingerprint, groupCount);
		ArrayList<String> matchingIconFilenames = matchGroupToIconFilename(allIconFileNames, groupedFingerprint);
		
		InputStream imageInputStream = renderSingleImageFromPaths(matchingIconFilenames, methodUrl);

		return ResponseEntity
				.ok()
				.contentLength(imageInputStream.available())
				.contentType(MediaType.IMAGE_PNG)
				.body(new InputStreamResource(imageInputStream));
	}
	
	private ArrayList<String> matchGroupToIconFilename(ArrayList<String> allIconFileNames, ArrayList<String> groupedFingerprints) {
		ArrayList<String> matchingFileNames = new ArrayList<>();
		for (int index = 0; index < groupedFingerprints.size(); ++index) {			
			String iconName = groupedFingerprints.get(index);
			String iconFilename = String.format("%04d.png", Integer.parseInt(iconName,16));
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
		
		verifyGroupingWasSuccessful(paddedFingerprint, fingerprintGroups);

		return fingerprintGroups;
	}

	private void verifyGroupingWasSuccessful(String paddedFingerprint, ArrayList<String> fingerprintGroups) throws Exception {
		StringBuffer assembledFromGroupsFingerprint = new StringBuffer(); 
		for (String fingerprintGroup : fingerprintGroups) {
			assembledFromGroupsFingerprint.append(fingerprintGroup);
		}
		
		if (!paddedFingerprint.equals(assembledFromGroupsFingerprint.toString()))
			throw new Exception("Fingerprint was not grouped correctly.  Padded fingerprint= " + paddedFingerprint + " assmebled fingerprint = " + assembledFromGroupsFingerprint);
	}

	private String padFingerprintToBeCreateEventNumberedGroups(String fingerprint, int groupCount) {
		int remainder = (int) fingerprint.length() % groupCount;
		if (remainder == 1) {
			fingerprint = new StringBuilder(fingerprint).insert(fingerprint.length()-1, "00").toString();
		} else if (remainder == 2) {
			fingerprint = new StringBuilder(fingerprint).insert(fingerprint.length()-2, "0").toString();
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

	@Override
	protected String getIconDirectory() {
		return "/icon-map/icons/";
	}
	
	@Override
	protected int getImageColumnCount() {
		return COLUMN_COUNT;
	}
	
	abstract protected int getMappingCount();	
}
