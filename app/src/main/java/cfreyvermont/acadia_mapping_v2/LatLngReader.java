package cfreyvermont.acadia_mapping_v2;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Christian on 11/4/2015. Reads in the LatLng of the
 * buildings that we have mapped from a file.
 *
 * We use JSON to structure the data.
 */
class LatLngReader {
    private JSONObject jso;

    /**
     * Creates a new JSON Object that provides an object representing
     * all of the building points.
     * @param is The input stream to the resource.
     */
    public LatLngReader(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String json;
        try {
            while ((json = reader.readLine()) != null) {
                sb.append(json);
            }
            jso = new JSONObject(sb.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads in the points that are stored at /res/raw/buildingpoints.json and
     * returns an array map where the Key is the building code, and the Value
     * is a polygonOption containing that buildings outline points.
     *
     * @return A map containing the building codes and corresponding points.
     */
    public Map<String, PolygonOptions> getAllBuildings() {
        Map<String, PolygonOptions> buildingPolygons = new ArrayMap<>();
        try {
            JSONArray codes = jso.getJSONArray("code");

            /* iterates through each of the buildings */
            for (int i = 0; i < codes.length(); i++)
            {
                JSONObject buildingCodes = codes.getJSONObject(i);
                Iterator<String> iterator = buildingCodes.keys();
                /* gets the inside of each building */
                while (iterator.hasNext())
                {
                    String code = iterator.next();
                    List<LatLng> buildingEdges =
                            getLatLngs(buildingCodes, code);

                    buildingPolygons.put(code,
                            new PolygonOptions().addAll(buildingEdges));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return buildingPolygons;
    }

    /**
     * Gets the points for a building, specified by the building code.
     * @param code The code of the searched for building
     * @return the list of points of the building, null if no building found.
     */
    public List<LatLng> getPointsByCode(String code) {
        try {
            JSONArray buildingsArray = jso.getJSONArray("code");
            for (int i = 0; i < buildingsArray.length(); i++) {
                /* Searching for the correct building */
                JSONObject buildingsObj = buildingsArray.getJSONObject(i);
                Iterator<String> iterator = buildingsObj.keys();
                while (iterator.hasNext()) {
                    String currentCode = iterator.next();

                    if (currentCode.equals(code)) {
                        return getLatLngs(buildingsObj, code);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /* No points found for the particular code */
        return null;
    }

    @NonNull
    /**
     * Getting the points for a given building.
     */
    private List<LatLng> getLatLngs(JSONObject building,
                                    String code) throws JSONException {
        List<LatLng> buildingEdges = new ArrayList<>();
        JSONArray latLngPoints = building.getJSONArray(code);
                    /* iterates through each point in the building */
        for (int j = 0; j < latLngPoints.length(); j++)
        {
            JSONObject point = latLngPoints.getJSONObject(j);
            buildingEdges.add(new LatLng(point.getDouble("lat"),
                    point.getDouble("lng")));
        }
        return buildingEdges;
    }
}
