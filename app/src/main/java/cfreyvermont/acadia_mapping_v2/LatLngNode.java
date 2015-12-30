package cfreyvermont.acadia_mapping_v2;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Christian on 12/30/2015.
 * The nodes for the graph providing directions will be an extension of
 * a LatLng point, and also includes if a node is a building, and what
 * building it is.
 */
public class LatLngNode {
    public final int vertexNumber; // A unique identifier for the vertex
    public LatLng latLng;
    public final boolean isEndNode;
    public final String buildingCode;


    /**
     * Creates a vertex where the point is not a building node.
     * @param latLng the position of the vertex
     */
    public LatLngNode(int vertexNumber, LatLng latLng) {
        this(vertexNumber, latLng, false, "");
    }

    /**
     * Creates a vertex.
     * @param latLng The position of the vertex
     * @param isEndNode If the vertex is a building
     * @param code the building code. null if vertex is not a building.
     */
    public LatLngNode(int vertexNumber, LatLng latLng, boolean isEndNode,
                      String code) {
        this.vertexNumber = vertexNumber;
        this.latLng = latLng;
        this.isEndNode = isEndNode;
        if (isEndNode) {
            this.buildingCode = code;
        } else {
            this.buildingCode = "";
        }
    }

    /**
     * Checks if two vertexes are equal. Uses the .equals function from the
     * LatLng library. We can ignore the vertex number, as we want to see if
     * the vertex features are identical.
     * @param node the node you want to compare.
     * @return true if the nodes are equal, false if not.
     */
    public boolean isVertexEqual(LatLngNode node) {
        if (node != null) {
            boolean equalPoints = this.latLng.equals(node.latLng);
            boolean equalEndNode = this.buildingCode.equals(node.buildingCode);
            boolean equalBuildingCode = (this.isEndNode == node.isEndNode);

            return equalPoints && equalEndNode && equalBuildingCode;
        }
        return false;
    }

    /**
     * Overriding the toString method gives us a human-readable version of
     * all of the contents of the object, for debugging purposes.
     * @return A string containing the details of the object.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.latLng.toString());
        if (!isEndNode) {
            return sb.toString();
        }
        sb.append(", isEndNode: ").append(this.isEndNode);
        sb.append(", BuildingCode: ").append(this.buildingCode);
        return sb.toString();
    }
}
