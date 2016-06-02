package org.benetech.authenticon.api.encoders.iconmap;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.springframework.core.io.ClassPathResource;

import sun.awt.image.ToolkitImage;

abstract public class AbstractIconMapper {

	private static final int COLUMN_COUNT = 4;
	private static final int IMAGE_WIDTH = 90;
	private static final int IMAGE_HEIGHT = 75;
	private static final int BUFFER_BETWEEN_ICONS = 20;
	
	protected BufferedImage createResultsImage(ArrayList<String> imageFileNames) {
		int resultImageWidth = (COLUMN_COUNT * IMAGE_WIDTH) + (BUFFER_BETWEEN_ICONS * COLUMN_COUNT);
		int imageCount = imageFileNames.size();
		int roundedUpNumberOfRows = (int) Math.ceil((double)imageCount / COLUMN_COUNT);
		int resultImageHeight = (roundedUpNumberOfRows * IMAGE_HEIGHT) + (BUFFER_BETWEEN_ICONS * roundedUpNumberOfRows);
		
		return new BufferedImage(resultImageWidth, resultImageHeight, BufferedImage.TYPE_INT_ARGB);
	}
	
	protected ToolkitImage getScaledDownBufferedImage(String iconFileName) throws IOException {
		ClassPathResource classPathResource = new ClassPathResource("/icon-map/icons/" + iconFileName);
		InputStream inputStream = classPathResource.getInputStream();
		BufferedImage bufferedImage = ImageIO.read(inputStream);
		inputStream.close();
		
		return (ToolkitImage) bufferedImage.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
	}
}
