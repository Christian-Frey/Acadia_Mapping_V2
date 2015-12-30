package cfreyvermont.acadia_mapping_v2;

import com.google.android.gms.maps.model.LatLng;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

/**
 * Created by Christian on 12/29/2015.
 * Handles creating and providing directions to the map. We use a
 * SimpleWeightedGraph because it allows us to weight the edges based
 * on walking time, so we can find the fastest route between buildings. The
 * graph is undirected, which assumes walking times are the same in
 * both directions.
 */
public class DirectionsGraph {
    SimpleWeightedGraph<LatLngNode, DefaultWeightedEdge> graph;

    public DirectionsGraph() {
        graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
    }

    /**
     * Adding the vertices to the graph, given a specific input file.
     *
     * @param is The inputStream to a file containing the vertices.
     */
    public void addVerticesFromFile(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String vertex;
        try {
            while ((vertex = reader.readLine()) != null) {
                LatLngNode node = stringToNode(vertex);
                if (node != null) {
                    graph.addVertex(node);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the edges to the graph, given a specific input file.
     *
     * @param is the InputStream to a file containing the edges.
     */
    public void addEdgeFromFile(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String edge;
        try {
            while ((edge = reader.readLine()) != null) {
                stringToEdge(edge);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a LatLngNode from a given input string, if possible.
     * It assumes the string is in the following order, separated by commas,
     * without spaces in between the values.
     *      Int vertexNumber: The number of the vertex, an arbitrary number.
     *      Double lat: The latitude of the vertex.
     *      Double lng: The longitude of the vertex.
     *      Boolean isEndNode: (Optional) If the node is at a building door.
     *      String buildingCode: (Optional) The 3 letter code for the building.
     *
     * @param s The string to parse.
     * @return the node created, null if the string could not be parsed.
     */
    public LatLngNode stringToNode(String s) {
        String[] stringArr = s.split(",");
        double lat, lng;
        int vertexNumber;
        boolean isEndNode;

        /* String not formatted correctly */
        if ((stringArr.length != 3 ) && (stringArr.length != 5)) {
            return null;
        }

        try {
            vertexNumber = Integer.valueOf(stringArr[0]);
            lat = Double.valueOf(stringArr[1]);
            lng = Double.valueOf(stringArr[2]);
        } catch (NumberFormatException e) { /* Strings were not numbers */
            return null;
        }

        /* Checking if values are outside the required range */
        if ((90 < lat) || (lat < -90) || (180 <= lng) || lng < -180) {
            return null;
        }
        if (stringArr.length == 3) {
            return new LatLngNode(vertexNumber, new LatLng(lat, lng));
        } else { // The length must be 4
            // returns true if the string is not null and = "true", ignores case
            isEndNode = Boolean.valueOf(stringArr[3]);
            if (stringArr[4].length() != 3) { //Not a code
                return null;
            }
            return new LatLngNode(vertexNumber, new LatLng(lat, lng),
                    isEndNode, stringArr[4]);
        }
    }

    /**
     * Converts a string to an edge, if possible, and adds the edge to
     * the graph.
     *
     * @param s The string to convert.
     */
    public void stringToEdge(String s) {
        String[] stringArr = s.split(",");
        int n1, n2, weight;

        if (stringArr.length != 3) {
            return;
        }

        try {
            /* v1 and v2 are interchangeable, since the graph is undirected */
            n1 = Integer.valueOf(stringArr[0]);
            n2 = Integer.valueOf(stringArr[1]);
            weight = Integer.valueOf(stringArr[2]);
        } catch (NumberFormatException e) {
            return;
        }

        LatLngNode v1 = containsVertex(n1);
        LatLngNode v2 = containsVertex(n2);

        if ((v1 != null) && (v2 != null)) {
            DefaultWeightedEdge edge = graph.addEdge(v1, v2);
            graph.setEdgeWeight(edge, weight);
        }
    }

    /**
     * Checks to see if a given vertex is in the graph. If so, it returns
     * the vertex. If not, it returns null.
     * @param vertexNumber The number of the vertex to check.
     * @return The node if found, null if not.
     */
    public LatLngNode containsVertex(int vertexNumber) {
        Set<LatLngNode> vertexSet = graph.vertexSet();
        for (LatLngNode node : vertexSet) {
            if (node.vertexNumber == vertexNumber) {
                return node;
            }
        }
        return null;
    }
}
