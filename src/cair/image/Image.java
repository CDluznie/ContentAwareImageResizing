package cair.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public class Image {
	
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
	 * Return the height of the image
	 * @param image Input image
	 * @return the height of the image
	 * @see getWidth
	 **/
	public static int getHeight(int[][] image) {
		return image.length;
	}

	/**
	 * Return the width of the image
	 * @param image Input image
	 * @return the width of the image
	 * @see getHeight
	 **/
	public static int getWidth(int[][] image) {
		return image[0].length;
	}

	/**
	 * Get an array containing the image of a PNG file
	 * @param path Path to the input file
	 * @return the array containing the image
	 * @throws IOException Input/Output error
	 * @see write
	 **/
	public static int[][] read(Path path) throws IOException {
		BufferedImage imag = ImageIO.read(new File(path.toString()));
		int width = imag.getWidth(), height = imag.getHeight();
		int[][] image = new int[height][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Color color = new Color(imag.getRGB(i, j));
				image[j][i] = (color.getRed() + color.getGreen() + color.getBlue())/3;
			}
		}
		return image;
	}

	/**
	 * Save the color array as a PNG file
	 * @param image Input image
	 * @param filename Ouput file name
	 * @throws IOException Input/Output error
	 * @see read
	 **/
	public static void write(int[][] image, String filename) throws IOException {
		int width = getWidth(image), height = getHeight(image);
		BufferedImage imag = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				imag.setRGB(
						i,
						j, 
						new Color(image[j][i], image[j][i], image[j][i], 255).getRGB()
					);
			}
		}	
	    ImageIO.write(imag, EXTENSION, new File(filename + "." + EXTENSION));
	}

}
