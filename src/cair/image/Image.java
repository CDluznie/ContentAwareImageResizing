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
	private final int[][] imageArray;
	private final BufferedImage image;
	
	/**
	 * Processed format file
	 **/
	public static String EXTENSION = "png";
	
	/**
	 * Smallest possible image height
	 **/
	public static int MIN_HEIGHT = 60;
	/**
	 * Smallest possible image width
	 **/
	public static int MIN_WIDTH = 60;

	/**
	 * 
	 * @param image
	 */
	private Image(BufferedImage image) {
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.image = image;
		
		
		imageArray = new int[height][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Color color = new Color(image.getRGB(i, j));
				imageArray[j][i] = (color.getRed() + color.getGreen() + color.getBlue())/3;
			}
		}

	
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
	 * Compute the interest array of an image<br>
	 * It will contains the contrast values of the image.
	 * @return the interest array of the image
	 * @throws IllegalArgumentException image.width &le; 1
	 * @see SeamCarving#toGraph
	 **/
	public int[][] interest () {
		if (width <= 1) {
			throw new IllegalArgumentException("width = " + width + " must be > 1");
		}
		int[][] interest = new int[height][width];
		for (int i = 0; i < height; i++) {
			interest[i][0] = Math.abs(imageArray[i][0] - imageArray[i][1]);
			for (int j = 1; j < width-1; j++) {
				interest[i][j] = Math.abs(imageArray[i][j] - (imageArray[i][j-1] + imageArray[i][j+1])/2);
			}
			interest[i][width-1] = Math.abs(imageArray[i][width-1] - imageArray[i][width-2]);
		}
		return interest;
	}

	/**

	
	 * TODO
	 * 
	 * 
	 * @return the new images with the deleted pixels
	 * @see SeamCarving#fordFulkerson
	 * @see Image#writepgm
	 **/
	public void removePixelsWidth (int[] positions) {
		for (int i = 0; i < height; i++) {
			for (int j = positions[i]; j < width-1; j++) {
				imageArray[i][j] = imageArray[i][j+1];
			}
		}
		
		
		for (int i = 0; i < height; i++) {
			for (int j = positions[i]; j < width-1; j++) {
				image.setRGB(j, i, image.getRGB(j+1, i));
			}
		}
		
		width--;
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

}
