package cair.graph;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Flow graph
 **/
public class Graph {

	private final ArrayList<ArrayList<Edge>> adjacenyList;

	/**
	 * Construct an empty flow graph
	 * @param vertexCount Maximum number of vertex in the graph
	 * @throws IllegalArgumentException vertexCount &le; 0
	 **/
	public Graph(int vertexCount) {
		if (vertexCount < 0) {
			throw new IllegalArgumentException("vertexCount = " + vertexCount + " must be > 0");
		}
		adjacenyList = new ArrayList<>(vertexCount);
		for (int v = 0; v < vertexCount; v++) {
			adjacenyList.add(new ArrayList<>());
		}
	}
	
	/**
	 * Return the number of edges in the graph
	 * @return the number of edges in the graph
	 * @see Graph#edges
	 **/
	public int vertices() {
		return adjacenyList.size();
	}

	/**
	 * Add an edge to the graph
	 * @param edge The edge to add to the graph
	 * @throws IllegalArgumentException edge.getFrom() &lt; 0
	 * @throws IllegalArgumentException edge.getFrom() &ge; vertices()
	 * @throws IllegalArgumentException edge.getTo() &lt; 0
	 * @throws IllegalArgumentException edge.getTo() &ge; vertices()
	 * @see Graph#edges
	 * @see Graph#adjacent
	 * @see Graph#vertices
	 * @see Edge#getFrom
	 * @see Edge#getTo
	 **/
	public void addEdge(Edge edge) {
		if (edge.getFrom() < 0 || edge.getFrom() >= vertices()) {
			throw new IllegalArgumentException("edge.from = " + edge.getFrom() + " must be >= 0 and < vertices() = " + vertices());
		}
		if (edge.getTo() < 0 || edge.getTo() >= vertices()) {
			throw new IllegalArgumentException("edge.to = " + edge.getTo() + " must be >= 0 and < vertices() = " + vertices());
		}
		adjacenyList.get(edge.getFrom()).add(edge);
		adjacenyList.get(edge.getTo()).add(edge);
	}

	/**
	 * Get an iterator on the edges that start or end to a particular vertex
	 * @param vertex The starting or eding vertex of the iterated edges
	 * @return an iterator on the edges
	 * @throws IllegalArgumentException vertex &lt; 0 
	 * @throws IllegalArgumentException vertex &ge; vertices()
	 * @see Graph#edges
	 * @see Graph#from
	 * @see Graph#addEdge
	 * @see Graph#vertices
	 **/
	public Iterable<Edge> adjacent(int vertex) {
		if (vertex < 0 || vertex >= vertices()) {
			throw new IllegalArgumentException("vertex = " + vertex + " must be >= 0 and < vertices() = " + vertices());
		}
		return adjacenyList.get(vertex);
	}

	/**
	 * Get an iterator on the edges that start from a particular vertex
	 * @param vertex The starting vertex of the iterated edges
	 * @returnan iterator on the edges
	 * @throws IllegalArgumentException vertex &lt; 0 
	 * @throws IllegalArgumentException vertex &ge; vertices()
	 * @see Graph#edges
	 * @see Graph#adjacent
	 * @see Graph#addEdge
	 * @see Graph#vertices
	 **/
	public Iterable<Edge> from(int vertex) {
		if (vertex < 0 || vertex >= vertices()) {
			throw new IllegalArgumentException("vertex = " + vertex + " must be >= 0 and < vertices() = " + vertices());
		}
		return adjacenyList.get(vertex).stream().filter(e -> e.getFrom() == vertex).collect(Collectors.toList());
	}
	
	/**
	 * Return an iterator on all the edges of the graph
	 * @return an iterator on all the edges of the graph
	 * @see Graph#adjacent
	 * @see Graph#from
	 * @see Graph#addEdge
	 **/
	public Iterable<Edge> edges() {
		ArrayList<Edge> list = new ArrayList<>();
		for (int vertex = 0; vertex < vertices(); vertex++) {
			for (Edge edge : from(vertex)) {
				list.add(edge);
			}
		}
		return list;
	}
	
	/**
	 * Get the edge between two vertex
	 * @param from Starting vertex of the edge
	 * @param to Ending vertex of the edge
	 * @return the edge between two vertex
	 * @throws NoSuchElementException (from,to) &notin; <b>G</b>
	 * @see Graph#addEdge
	 * @see Graph#edges
	 **/
	public Edge getEdge(int from, int to) {
		return adjacenyList.get(from).stream().filter((e) -> e.getTo() == to).findFirst().get();
	}
	
	/**
	 * Save the flow graph into a .dot file<br>
	 * Here <a href="http://sandbox.kidstrythisathome.com/erdos/" target="_blank">the link</a> to visualize the graph
	 * @param path Output file name
	 * @param printUnused <b>true</b> if we want to process the empty edge, <b>false</b> otherwise
	 * @throws IOException Input/Output error
	 * @see Graph#writeFile(Path)
	 **/
	public void writeFile(Path path, boolean printUnused) throws IOException {
		try(BufferedWriter writer = Files.newBufferedWriter(path);
				PrintWriter printer = new PrintWriter(writer)) {
			printer.println("digraph G{");
			for (Edge e : edges()) {
				if (!printUnused && e.getUsed() <= 0) {
					continue;
				}
				printer.println(e.getFrom() + "->" + e.getTo() + "[label=\"" + e.getUsed() + "/" + e.getCapacity() + "\"];");
			}
			printer.println("}");
		}
	}

	/**
	 * Save the flow graph into a .dot file<br>
	 * Here <a href="http://sandbox.kidstrythisathome.com/erdos/" target="_blank">the link</a> to visualize the graph.<br>
	 * All the edges, even the empty edge, are processed.
	 * @param path Output file name
	 * @throws IOException Input/Output error
	 * @see Graph#writeFile(Path, boolean)
	 **/
	public void writeFile(Path path) throws IOException {
		writeFile(path, true);
	}	

}
