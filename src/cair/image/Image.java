package cair.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

import cair.graph.SeamCarving;

public class Image {
	
	private int width;
	private int height;
	private final BufferedImage image;
	private final int[][] grey;
	
	/**
	 * Processed format file
	 **/
	public static String EXTENSION = "png";
	
	/**
	 * 
	 * @param image
	 */
	private Image(BufferedImage image) {
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.image = image;
		this.grey = extractValueFromRgb(image);
	}
	
	/**
	 * Return the height of the image
	 * @return the height of the image
	 * @see getWidth
	 **/
	public int getHeight() {
		return height;
	}

	/**
	 * Return the width of the image
	 * @return the width of the image
	 * @see getHeight
	 **/
	public int getWidth() {
		return width;
	}
	
	/**
	 * Compute the horizontal gradient of an image
	 * @return the gradient of the image
	 * @throws IllegalArgumentException image.width &le; 1
	 * @see SeamCarving#toGraph
	 **/
	public int[][] horizontalGradient () {
		if (width <= 1) {
			throw new IllegalArgumentException("width = " + width + " must be > 1");
		}
		int[][] gradient = new int[height][width];
		for (int i = 0; i < height; i++) {
			gradient[i][0] = Math.abs(grey[i][0] - grey[i][1]);
			for (int j = 1; j < width-1; j++) {
				gradient[i][j] = Math.abs(grey[i][j] - (grey[i][j-1] + grey[i][j+1])/2);
			}
			gradient[i][width-1] = Math.abs(grey[i][width-1] - grey[i][width-2]);
		}
		return gradient;
	}
	
	/**
	 * Compute the horizontal gradient of an image
	 * @return the gradient of the image
	 * @throws IllegalArgumentException image.width &le; 1
	 * @see SeamCarving#toGraph
	 **/
	public int[][] verticalGradient () {
		if (height <= 1) {
			throw new IllegalArgumentException("height = " + height + " must be > 1");
		}
		int[][] gradient = new int[height][width];
		for (int j = 0; j < width; j++) {
			gradient[0][j] = Math.abs(grey[0][j] - grey[1][j]);
			for (int i = 1; i < height-1; i++) {
				gradient[i][j] = Math.abs(grey[i][j] - (grey[i-1][j] + grey[i+1][j])/2);
			}
			gradient[height-1][j] = Math.abs(grey[height-1][j] - grey[height-2][j]);
		}
		return gradient;
	}

	/**
	 * TODO
	 * 
	 * @see SeamCarving#contentAwareResizing
	 **/
	public void removePixelsWidth (int[] positions) {
		for (int i = 0; i < height; i++) {
			for (int j = positions[i]; j < width-1; j++) {
				image.setRGB(j, i, image.getRGB(j+1, i));
				grey[i][j] = grey[i][j+1];
			}
		}
		width--;
	}
	
	/**
	 * TODO
	 * 
	 * @see SeamCarving#contentAwareResizing
	 **/
	public void removePixelsHeight (int[] positions) {
		for (int j = 0; j < width; j++) {
			for (int i = positions[j]; i < height-1; i++) {
				image.setRGB(j, i, image.getRGB(j, i+1));
				grey[i][j] = grey[i+1][j];
			}
		}
		height--;
	}

	/**
	 * Get an array containing the image of a PNG file
	 * @param path Path to the input file
	 * @return the array containing the image
	 * @throws IOException Input/Output error
	 * @see write
	 **/
	public static Image read(Path path) throws IOException {
		BufferedImage image = ImageIO.read(new File(path.toString()));
		return new Image(image);
	}

	/**
	 * Save the color array as a PNG file
	 * @param filename Ouput file name
	 * @throws IOException Input/Output error
	 * @see read
	 **/
	public void write(String filename) throws IOException {
	    ImageIO.write(image.getSubimage(0, 0, width, height), EXTENSION, new File(filename + '.' + Image.EXTENSION));
	}

	private static int[][] extractValueFromRgb(BufferedImage image) {
		int width = image.getWidth(), height = image.getHeight();
		int[][] grey = new int[height][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Color color = new Color(image.getRGB(i, j));
				grey[j][i] = (color.getRed() + color.getGreen() + color.getBlue())/3;
			}
		}
		return grey;
	}
	
}
