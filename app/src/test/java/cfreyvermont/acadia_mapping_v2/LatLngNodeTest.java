package cfreyvermont.acadia_mapping_v2;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;

/**
 * Created by Christian on 12/30/2015.
 *
 * Tests the LatLngNode class and methods.
 */
public class LatLngNodeTest extends TestCase {
    private LatLngNode n1 = new LatLngNode(1,new LatLng(45, 45));
    private LatLngNode n2 = new LatLngNode(2,new LatLng(45, 45), true, "CIO");
    private LatLngNode n3 = new LatLngNode(3,new LatLng(55, 55), true, "CIO");
    private LatLngNode n4 = new LatLngNode(4,new LatLng(55, 55), true, "CIO");

    public void setUp() throws Exception {
        super.setUp();

    }

    public void testIsVertexEqual() throws Exception {
        assertEquals(false, n1.isVertexEqual(n2));
        assertEquals(false, n2.isVertexEqual(n3));
        assertEquals(true, n3.isVertexEqual(n4));
    }
}