package cair.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.IntConsumer;

import cair.image.Image;

/**
 * All the method to perform the content aware image resizing
 **/
public abstract class SeamCarving {

	/**
	 * Constant defining the value +&infin; in files
	 **/
	public static int INFINITY = 256;
	
	/**
	 * Constant defining the absence of predecessor in the parents array during the breadth-first search
	 **/
	public static int BFS_PARENT_NONE = -1;
	
	/**
	 * Generate a graph from an interest array
	 * @param image 
	 * @return the associated graph
	 * @see Image#interest
	 * @see SeamCarving#fordFulkerson
	 **/
	public static Graph toGraph(Image image) {
		int width = image.getWidth(), height = image.getHeight();
		int[][] interest = image.interest();
		int u, v;
		Graph g = new Graph(width*height + 2);
		for (int i = 0; i < height; i++) {
			g.addEdge(new Edge(0, i + 1, INFINITY, 0));
			g.addEdge(new Edge(i + (width - 1)*height + 1, width*height + 1, interest[i][width-1], 0));
		}
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width - 1; j++) {
				u = i + j*height + 1;
				v = i + (j+1)*height + 1;
				g.addEdge(new Edge(u, v, interest[i][j], 0));
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
	 * TODO
	 **/
	public static int[] verticesToPixelsPosition(Image image, List<Integer> vertices) {
		int height = image.getHeight();
		int[] positions = new int[height];
		for (int v : vertices) {
			positions[(v-1)%height] = (v-1)/height;
		}
		return positions;
	}
	
	/**
	 * TODO
	 * @param image
	 * @param numberColumn
	 * @param observer
	 * @return
	 */
	public static Image contentAwareResizing(Image image, int numberColumn, IntConsumer observer) {
		Image resultImage = image;
		Graph graph;
		List<Integer> graphCut;
		for (int i = 0; i < numberColumn; i++) {
			graph = SeamCarving.toGraph(resultImage);
			graphCut = SeamCarving.fordFulkerson(graph);
			resultImage.removePixelsWidth(verticesToPixelsPosition(resultImage, graphCut));
			observer.accept(i);
		}
		return resultImage;
	}
	
}
