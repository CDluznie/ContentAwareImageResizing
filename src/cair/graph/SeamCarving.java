package cair.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

/**
 * All the method to perform the content aware image resizing
 **/
public abstract class SeamCarving {

	/**
	 * Processed format file
	 **/
	public static String EXTENSION = "pgm";
	
	/**
	 * Constant defining the value +&infin; in files
	 **/
	public static int INFINITY = 256;
	
	/**
	 * Constant defining the absence of predecessor in the parents array during the breadth-first search
	 **/
	public static int BFS_PARENT_NONE = -1;
	
	/**
	 * Smallest possible image width
	 **/
	public static int MIN_WIDTH = 60;
	
	/**
	 * Smallest possible image height
	 **/
	public static int MIN_HEIGHT = 60;
	
	/**
	 * Return the height of the image
	 * @param image Input image
	 * @return the height of the image
	 * @see SeamCarving#getWidth
	 **/
	public static int getHeight(int[][] image) {
		return image.length;
	}

	/**
	 * Return the width of the image
	 * @param image Input image
	 * @return the width of the image
	 * @see SeamCarving#getHeight
	 **/
	public static int getWidth(int[][] image) {
		return image[0].length;
	}
	
	/**
	 * Get an array containing the image of a pgm file
	 * @param path Path to the input file
	 * @return the array containing the image
	 * @throws IOException Input/Output error
	 * @see SeamCarving#writepgm
	 **/
	public static int[][] readpgm(Path path) throws IOException {
		try(BufferedReader reader = Files.newBufferedReader(path)) {
			reader.readLine();
			String line = reader.readLine();
			while (line.startsWith("#")) {
				line = reader.readLine();
			}
			Scanner scanner = new Scanner(line);
			int width = scanner.nextInt();
			int height = scanner.nextInt();
			line = reader.readLine();
			scanner = new Scanner(line);
			scanner.nextInt();
			int[][] im = new int[height][width];
			scanner = new Scanner(reader);
			int count = 0;
			while (count < height * width) {
				im[count / width][count % width] = scanner.nextInt();
				count++;
			}
			return im;
		}
	}
	
	/**
	 * Save the color array as a pgm file
	 * @param image Input image
	 * @param filename Ouput file name
	 * @throws IOException Input/Output error
	 * @see SeamCarving#readpgm
	 **/
	public static void writepgm(int[][] image, String filename) throws IOException {
		int width = getWidth(image), height = getHeight(image);
		FileWriter fw = new FileWriter(new File (filename));
		fw.write("P2");
		fw.write('\n');
		fw.write(String.valueOf(width));
		fw.write(' ');
		fw.write(String.valueOf(height));
		fw.write('\n');
		fw.write(String.valueOf(INFINITY - 1));
		fw.write('\n');
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				fw.write(String.valueOf(image[i][j]));
				fw.write(' ');
			}
			fw.write('\n');
		}
		fw.close();
	}

	/**
	 * Compute the interest array of an image<br>
	 * It will contains the contrast values of the image.
	 * @param image Input image
	 * @return the interest array of the image
	 * @throws IllegalArgumentException image.width &le; 1
	 * @see SeamCarving#toGraph
	 **/
	public static int[][] interest (int[][] image) {
		int width = getWidth(image), height = getHeight(image);
		if (width <= 1) {
			throw new IllegalArgumentException("width = " + width + " must be > 1");
		}
		int[][] interest = new int[height][width];
		for (int i = 0; i < height; i++) {
			interest[i][0] = Math.abs(image[i][0] - image[i][1]);
			for (int j = 1; j < width-1; j++) {
				interest[i][j] = Math.abs(image[i][j] - (image[i][j-1] + image[i][j+1])/2);
			}
			interest[i][width-1] = Math.abs(image[i][width-1] - image[i][width-2]);
		}
		return interest;
	}
	
	/**
	 * Generate a graph from an interest array
	 * @param itr Interest array
	 * @return the associated graph
	 * @see SeamCarving#interest
	 * @see SeamCarving#fordFulkerson
	 **/
	public static Graph toGraph(int[][] itr) {
		int width = getWidth(itr), height = getHeight(itr);
		int u, v;
		Graph g = new Graph(width*height + 2);
		for (int i = 0; i < height; i++) {
			g.addEdge(new Edge(0, i + 1, INFINITY, 0));
			g.addEdge(new Edge(i + (width - 1)*height + 1, width*height + 1, itr[i][width-1], 0));
		}
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width - 1; j++) {
				u = i + j*height + 1;
				v = i + (j+1)*height + 1;
				g.addEdge(new Edge(u, v, itr[i][j], 0));
				g.addEdge(new Edge(v, u, INFINITY, 0));
				if (i > 0) {
					g.addEdge(new Edge(v - 1, u, INFINITY, 0));
				}
				if (i < height - 1) {
					g.addEdge(new Edge(v + 1, u, INFINITY, 0));
				}
			}
		}
		return g;
	}
	
	/**
	 * Perform a breadth-first search on a flow graph from a root to a target<br>
	 * The parents array is filled as things progress such that
	 * the <b>i</b>-th value contains the parent index of the <b>i</b> vertex of the path.<br>
	 * If <b>i</b> did not have any parent then the <b>i</b>-th value will contain <b>{@link #BFS_PARENT_NONE}</b>.
	 * @param gitr The graphe to browse
	 * @param root The origin of the path
	 * @param target The destination of the path
	 * @param parents Parents array
	 * @return <b>true</b> if the graph is saturated, <b>false</b> otherwise
	 * @see SeamCarving#fordFulkerson
	 **/
	public static boolean bfs(Graph gitr, int root, int target, int[] parents) {
		Queue<Integer> vertices = new LinkedList<>();
		int vert;
		vertices.add(root);
		while (!vertices.isEmpty()) {
			vert = vertices.remove();
			if (vert == target) {
				return false;
			}
			for (Edge edge : gitr.from(vert)) {
				if (parents[edge.getTo()] == BFS_PARENT_NONE && edge.isFree()) {
					parents[edge.getTo()] = vert;
					vertices.add(edge.getTo());
				}
			}
		}
		return true;
	}
	
	/**
	 * Perform the <b>Ford-Fulkerson</b> algorithm on the graph.<br>
	 * At the end of the method, the graph is staturated,
	 * the cut <b>(S,T)</b> is determined and
	 * we can send the list of vertices <b>u</b> &isin; <b>S</b> such that :<br>
	 * <b>v</b> &isin; <b>T</b> and (<b>u</b>,<b>v</b>) &isin; <b>A</b>,
	 * corresponding of the pixels we can get rid.
	 * @param gitr Graph to process
	 * @return the list of pixels to remove
	 * @see SeamCarving#toGraph
	 * @see SeamCarving#bfs
	 * @see SeamCarving#removePixels
	 **/
	public static List<Integer> fordFulkerson(Graph gitr) {
		ArrayList<Integer> result = new ArrayList<>();
		LinkedList<Edge> path;
		Edge edge;
		int root = 0, target = gitr.vertices() - 1;
		int[] parents = new int[gitr.vertices()];
		int minimum;
		Arrays.fill(parents, BFS_PARENT_NONE);
		while (!bfs(gitr, root, target, parents)) {
			path = new LinkedList<>();
			minimum = INFINITY;
			for (int v = target; v != root; v = parents[v]) {
				edge = gitr.getEdge(parents[v], v);
				path.addFirst(edge);
				minimum = Math.min(minimum, edge.getFreeFlow());
			}
			for (Edge e : path) {
				e.fill(minimum);
			}
			Arrays.fill(parents, BFS_PARENT_NONE);
		}
		for (Edge e : gitr.edges()) {
			if (parents[e.getFrom()] != BFS_PARENT_NONE && parents[e.getTo()] == BFS_PARENT_NONE) {
				result.add(e.getFrom());
			}
		}
		return result;
	} 
	
	/**
	 * Delete in the image the pixels indexed by the vertices in the list<br>
	 * @param image The image to process
	 * @param vertices Pixels verticies to delete
	 * @return the new images with the deleted pixels
	 * @see SeamCarving#fordFulkerson
	 * @see SeamCarving#writepgm
	 **/
	public static int[][] removePixels (int[][] image, List<Integer> vertices) {
		int width = getWidth(image), height = getHeight(image);
		int[] pixels = new int[height];
		int[][] newImage = new int[height][width-1];
		for (int v : vertices) {
			pixels[(v-1)%height] = (v-1)/height;
		}
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < pixels[i]; j++) {
				newImage[i][j] = image[i][j];
			}
			for (int j = pixels[i]; j < width-1; j++) {
				newImage[i][j] = image[i][j+1];
			}
		}
		return newImage;
	}
	
}
