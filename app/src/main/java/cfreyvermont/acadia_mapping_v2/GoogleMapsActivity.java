package cfreyvermont.acadia_mapping_v2;

import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    BuildingInfoDB db;
    private Map<String, Polygon> polygonList = new ArrayMap<>();
    boolean IS_WINDOW_OPEN = false;

    /* Things to do
     * Add walking directions based on current location
     * Add support for local Wolfville businesses
     * Allow for user-submitted details about buildings
     * Add support for class schedules and optimal routes between classes
     * Add information on room locations within buildings
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new BuildingInfoDB(getApplicationContext());

        /* Adding the information to the database building information */
        addInfoToDatabase();

        setContentView(R.layout.activity_google_maps);

        /* Obtain the SupportMapFragment and get notified when the map is ready to be used. */
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * creates a database containing the information on each of the buildings.
     */
    private void addInfoToDatabase() {
        int size = db.getSize();
        final Resources resource = getResources();
        InputStream is = resource.openRawResource(R.raw.buildinginformation);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        /* Reading through the file to count the number of lines. */
        int numLines = 0;
        try {
            /* Will keep the mark (beginning) for 10MB of data read. */
            reader.mark(10000000);
            while (reader.readLine() != null) {
                numLines++;
            }
        } catch (IOException e) {
            Log.e("IOException:", e.getMessage());
        }

        if (size < numLines) {
        /* There are new entries in the file that we can add. */
            try {
                //Going back to the beginning.
                reader.reset();
                /* consuming the lines that are already in the DB. */
                for (int i = 0; i < (size); i++) {
                    reader.readLine();
                }

                String line;
                while ((line = reader.readLine()) != null){
                    String[] tuple = TextUtils.split(line, "\\|");
                    long result = db.createRecord(tuple);
                    if (result == -1) {
                        Log.i("Error:", "Insert not completed");
                    }
                    else {
                        Log.i("Inserted:", tuple[1]);
                    }
                }
            } catch (IOException e) {
                Log.e("IOException:", e.getMessage());
            } catch (Exception e) {
                Log.i("Exception", e.getMessage());
            }
        }
    }

    /**
     * Draws the polygons that are stored in the json file found at res/raw/buildingpoints
     */
    private void drawPolygons() {
        LatLngReader reader = new LatLngReader(getResources().openRawResource(
                R.raw.buildingpoints));
        Map <String, PolygonOptions> polygonOptionsMap = reader.getBuildings();

        for (Map.Entry<String, PolygonOptions> entry : polygonOptionsMap.entrySet()) {
            entry.getValue().strokeWidth(3);
            Polygon i = map.addPolygon(entry.getValue());
            polygonList.put(entry.getKey(), i);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng target = new LatLng(45.088845, -64.366850);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(target)
                .bearing(165)
                .zoom(17)
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        map.setBuildingsEnabled(false);
        drawPolygons();

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mapClicked(latLng);
            }
        });
    }

    private void mapClicked(LatLng point) {
        for (Map.Entry<String, Polygon> code : polygonList.entrySet()) {
            /* Did they click on a building? */
            if (PolyUtil.containsLocation(point, code.getValue().getPoints(), false)) {
                Log.d("Opening Building:", code.getKey());
                openInformation(code.getKey());
            }
            else { // They did not.
                closeInformation();
            }
        }
    }

    private void openInformation(String code) {
        // Creating a new fragment transaction to add it to the activity.
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        // Passing the building name to the fragment
        Bundle bundle = new Bundle();
        bundle.putString("name", code);

        // Creating the fragment, adding the information, and adding it to the stack.
        DisplayBuildingInformation fragment = new DisplayBuildingInformation();
        fragment.setArguments(bundle);
        transaction.replace(R.id.info_placeholder, fragment);
        transaction.addToBackStack("info").commit();

        IS_WINDOW_OPEN = true;
    }

    private void closeInformation() {
        // If there is an information window open, close it.
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
        IS_WINDOW_OPEN = false;
    }
}