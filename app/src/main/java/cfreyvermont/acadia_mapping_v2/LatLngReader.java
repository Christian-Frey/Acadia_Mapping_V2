package cfreyvermont.acadia_mapping_v2;

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
public class LatLngReader {
    JSONObject jso;
    public LatLngReader(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String json, result;
        try {
            while ((json = reader.readLine()) != null) {
                sb.append(json);
            }
            result = sb.toString();
            jso = new JSONObject(result);
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
    public Map<String, PolygonOptions> getBuildings() {
        Map<String, PolygonOptions> buildingPolygons = new ArrayMap<>();
        try {
            JSONObject buildings = jso.getJSONObject("Buildings");
            JSONArray codes = buildings.getJSONArray("code");

            /* iterates through each of the buildings */
            for (int i = 0; i < codes.length(); i++)
            {
                JSONObject buildingCodes = codes.getJSONObject(i);
                Iterator<String> iterator = buildingCodes.keys();
                /* gets the inside of each building */
                while (iterator.hasNext())
                {
                    String building_code = iterator.next();
                    List<LatLng> buildingEdges = new ArrayList<>();
                    JSONArray latLngPoints = buildingCodes.getJSONArray(building_code);
                    /* iterates through each point in the building */
                    for (int j = 0; j < latLngPoints.length(); j++)
                    {
                        JSONObject point = latLngPoints.getJSONObject(j);
                        buildingEdges.add(new LatLng(point.getDouble("lat"),
                                point.getDouble("lng")));
                    }
                    buildingPolygons.put(building_code,
                                         new PolygonOptions().addAll(buildingEdges));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return buildingPolygons;
    }
}
