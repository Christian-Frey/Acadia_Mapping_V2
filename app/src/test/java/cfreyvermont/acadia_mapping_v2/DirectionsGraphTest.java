package cfreyvermont.acadia_mapping_v2;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Christian on 12/30/2015.
 * Test file for a DirectionsGraph.
 */
public class DirectionsGraphTest extends TestCase {
    DirectionsGraph graph = new DirectionsGraph();
    private LatLngNode n1 = new LatLngNode(1, new LatLng(45, 45));
    private LatLngNode n2 = new LatLngNode(2, new LatLng(45, 45), true, "CIO");


    public void setUp() throws Exception {
        super.setUp();

    }

    public void testAddVerticesFromFile() throws Exception {
        System.out.println("testAddVerticesFromFile");
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("vertex.csv");
        graph.addVerticesFromFile(is);
        Set<LatLngNode> set = graph.graph.vertexSet();
        for (LatLngNode node : set) {
            System.out.println(node.toString());
        }

    }

    public void testStringToNode() throws Exception {
        assertEquals(true, graph.stringToNode("2,45,45,TrUe,CIO").isVertexEqual(n2));
        assertEquals(true, graph.stringToNode("1,45,45").isVertexEqual(n1));
        assertEquals(false, graph.stringToNode("3,45,45,true,BAC").isVertexEqual(n1));
        assertEquals(false, graph.stringToNode("2,45.0002,43,true,CIO").isVertexEqual(n2));
        assertEquals(false, n1.isVertexEqual(null));
        assertEquals(null, graph.stringToNode("100, 45, true,CIO"));
        assertEquals(null, graph.stringToNode("45.34,83.21, false, BAC"));
    }

    public void testAddEdgeFromFile() throws Exception {
        System.out.println("testAddEdgeFromFile:");
        testAddVerticesFromFile(); // We need vertices to add the edges to.

        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("edge.csv");
        graph.addEdgeFromFile(is);

        Set<DefaultWeightedEdge> set = graph.graph.edgeSet();
        for (DefaultWeightedEdge edge : set) {
            System.out.println(edge.toString());
        }
    }

    public void testStringToEdge() throws Exception {
        // Tested during the usage of addEdgeFromFile
    }
}