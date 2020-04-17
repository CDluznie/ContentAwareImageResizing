package cair.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public class Image {
	
	private final int width;
	private final int height;
	private final int[][] imageArray;
	
	private Image(BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();
		imageArray = new int[height][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Color color = new Color(image.getRGB(i, j));
				imageArray[j][i] = (color.getRed() + color.getGreen() + color.getBlue())/3;
			}
		}

	
	}
	
	public Image(int[][] image) {
		width = getWidth(image);
		height = getHeight(image);
		imageArray = image;
	}
	
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
	
	public int[][] getArray() {
		return imageArray;
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
	 * @param image Input image
	 * @param filename Ouput file name
	 * @throws IOException Input/Output error
	 * @see read
	 **/
	public static void write(Image image, String filename) throws IOException {
		BufferedImage imag = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < image.width; i++) {
			for (int j = 0; j < image.height; j++) {
				imag.setRGB(
						i,
						j, 
						new Color(image.imageArray[j][i], image.imageArray[j][i], image.imageArray[j][i], 255).getRGB()
					);
			}
		}	
	    ImageIO.write(imag, EXTENSION, new File(filename + '.' + Image.EXTENSION));
	}

}
