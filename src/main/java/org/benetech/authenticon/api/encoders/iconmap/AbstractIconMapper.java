package org.benetech.authenticon.api.encoders.iconmap;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.springframework.core.io.ClassPathResource;

import sun.awt.image.ToolkitImage;

abstract public class AbstractIconMapper {

	private static final int IMAGE_WIDTH = 90;
	private static final int IMAGE_HEIGHT = 75;
	private static final int BUFFER_BETWEEN_ICONS = 20;
	
	protected InputStream renderSingleImageFromPaths(ArrayList<String> imageFileNames) throws Exception {
		BufferedImage resultImage = createResultsImage(imageFileNames);
		int x = 0; 
		int y = 0;
		for (String iconFileName : imageFileNames) 
		{
			ToolkitImage scaledDownImage = getScaledDownBufferedImage(iconFileName);
			resultImage.getGraphics().drawImage(scaledDownImage, x, y, Color.WHITE, null);
			
			int moveXByAmount = resultImage.getWidth() / getImageColumnCount();
			x += moveXByAmount;
			
			boolean shouldCreateNewRow = (x + scaledDownImage.getWidth()) > resultImage.getWidth();
			if(shouldCreateNewRow){
				x = 0;
				y += scaledDownImage.getHeight();
				y += BUFFER_BETWEEN_ICONS;
			}
		}
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(resultImage, "png", outputStream);

		byte[] byteArray = outputStream.toByteArray();
		outputStream.close();
		
		return new ByteArrayInputStream(byteArray);
	}
	
	private BufferedImage createResultsImage(ArrayList<String> imageFileNames) {
		int resultImageWidth = (getImageColumnCount() * IMAGE_WIDTH) + (BUFFER_BETWEEN_ICONS * getImageColumnCount());
		int imageCount = imageFileNames.size();
		int roundedUpNumberOfRows = (int) Math.ceil((double)imageCount / getImageColumnCount());
		int resultImageHeight = (roundedUpNumberOfRows * IMAGE_HEIGHT) + (BUFFER_BETWEEN_ICONS * roundedUpNumberOfRows);
		
		return new BufferedImage(resultImageWidth, resultImageHeight, BufferedImage.TYPE_INT_ARGB);
	}
	
	private ToolkitImage getScaledDownBufferedImage(String iconFileName) throws IOException {
		ClassPathResource classPathResource = new ClassPathResource(getIconDirectory() + iconFileName);
		InputStream inputStream = classPathResource.getInputStream();
		BufferedImage bufferedImage = ImageIO.read(inputStream);
		inputStream.close();
		
		return (ToolkitImage) bufferedImage.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
	}

	abstract protected String getIconDirectory();
	
	abstract protected int getImageColumnCount();
}
