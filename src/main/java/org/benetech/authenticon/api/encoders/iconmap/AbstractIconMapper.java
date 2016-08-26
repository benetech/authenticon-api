package org.benetech.authenticon.api.encoders.iconmap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.springframework.core.io.ClassPathResource;


abstract public class AbstractIconMapper {

	private static final int IMAGE_WIDTH = 100;
	private static final int IMAGE_HEIGHT = 100;
	private static final int BUFFER_BETWEEN_ICONS = 10;
	private static final int TEXT_SPACE = 17;
	
	
	protected InputStream renderSingleImageFromPaths(ArrayList<String> imageFileNames, String methodUrl, ArrayList<String> iconDescs) throws Exception {
		BufferedImage resultImage = createResultsImage(imageFileNames);
		int x = 0; 
		int y = 0;
		int imageCount = imageFileNames.size();
		for (int i = 0; i < imageCount; i++) 
		{
			BufferedImage scaledDownImage = getScaledDownBufferedImage(imageFileNames.get(i));
			x = (i%(getImageColumnCount()))*(IMAGE_WIDTH+BUFFER_BETWEEN_ICONS);	
			y = ((int) Math.floor((double)i/getImageColumnCount())*(scaledDownImage.getHeight()+BUFFER_BETWEEN_ICONS+TEXT_SPACE));	
			resultImage.getGraphics().drawImage(scaledDownImage, x, y, Color.WHITE, null);
			Graphics2D g2d = resultImage.createGraphics();
	        g2d.drawImage(resultImage, 0, 0, null);
	        g2d.setPaint(Color.white);
	        g2d.setFont(new Font("SansSerif", Font.PLAIN, 14));
	        String s = iconDescs.get(i);
	        FontMetrics fm = g2d.getFontMetrics();
	        int tx = fm.stringWidth(s)/2;
	        g2d.drawString(s, x+((IMAGE_WIDTH/2)-tx), y+IMAGE_HEIGHT+14);
	        g2d.dispose();
		}
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(resultImage, "png", outputStream);

		byte[] byteArray = outputStream.toByteArray();
		outputStream.close();
		
		return new ByteArrayInputStream(byteArray);
	}
	
	
	private BufferedImage createResultsImage(ArrayList<String> imageFileNames) {
		int resultImageWidth = (getImageColumnCount() * IMAGE_WIDTH) + (BUFFER_BETWEEN_ICONS * (getImageColumnCount()-1));
		int imageCount = imageFileNames.size();
		int roundedUpNumberOfRows = (int) Math.ceil((double)imageCount / getImageColumnCount());
		int resultImageHeight = (roundedUpNumberOfRows * IMAGE_HEIGHT) + (BUFFER_BETWEEN_ICONS * (roundedUpNumberOfRows));
		
		return new BufferedImage(resultImageWidth, resultImageHeight+(roundedUpNumberOfRows*TEXT_SPACE), BufferedImage.TYPE_INT_ARGB);
	}
	
	private BufferedImage getScaledDownBufferedImage(String iconFileName) throws IOException {
		ClassPathResource classPathResource = new ClassPathResource(getIconDirectory() + iconFileName);
		InputStream inputStream = classPathResource.getInputStream();
		BufferedImage original = ImageIO.read(inputStream);
		inputStream.close();
		
		BufferedImage resized = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, original.getType());
		Graphics2D g = resized.createGraphics();
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHints(rh);
		g.drawImage(original, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, 0, 0, original.getWidth(), original.getHeight(), null);
		g.dispose();
		
		
		return (BufferedImage) resized;
	}

	abstract protected String getIconDirectory();
	
	abstract protected int getImageColumnCount();
}
