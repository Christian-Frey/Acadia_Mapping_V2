package cfreyvermont.acadia_mapping_v2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserFeedback extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);
        BuildingInfoDB db = new BuildingInfoDB(getApplicationContext());

        Intent intent = getIntent();
        final String code = intent.getStringExtra(
                DisplayBuildingInformation.STRING_EXTRA_MESSAGE);

        /* Getting the buildings full name from the code. */
        ContentValues cv = db.selectRecordByCode(code);
        TextView textView = (TextView) findViewById(R.id.feedback_building_name);
        if (cv != null) {
            textView.setText(cv.getAsString(DatabaseHelper.COLUMN_NAME_BUILDING_NAME));
        }

        /* Adding a listener to the submit button */
        Button submitButton = (Button) findViewById(R.id.submit_feedback);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback(code);
            }
        });
    }

    /**
     * Submits feedback to the server so the app can be updated with the
     * correct information.
     */
    public void submitFeedback(String code) {
        Spinner spinner = (Spinner) findViewById(R.id.feature_select);
        String featureSelected = spinner.getSelectedItem().toString();

        TextView tvInput = (TextView) findViewById(R.id.user_feedback_input);
        String feedback = tvInput.getText().toString();

        if (hasNetworkConnection()) {
            new UploadFeedbackTask().execute(code, featureSelected, feedback);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Error: No Network Connection", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Tests network connectivity to the internet.
     *
     * @return True if the device has a connection, False otherwise.
     */
    public Boolean hasNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Checking for a network connection
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        // We don't have a network connection
        return false;
    }

    /**
     * Sends the feedback to the server in a new task, preventing the User
     * Interface from being locked by a slow network.
     */
    private class UploadFeedbackTask extends AsyncTask<String, Void, String> {
        private final String FEEDBACK_SERVER = getFeedbackServer();
        private final String UPLOAD_SUCCESS = "Feedback uploaded successfully!";
        private final String UPLOAD_ERROR = "Error Uploading Feedback";
        private final String READ_ERROR = "Error Reading File";

        @Override
        protected String doInBackground(String... vars) {
            try {
                if (uploadData(vars)) {
                    return UPLOAD_SUCCESS;
                } else {
                    return UPLOAD_ERROR;
                }
            } catch (Exception e) {
                return UPLOAD_ERROR;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), UPLOAD_SUCCESS,
                    Toast.LENGTH_SHORT).show();
            /* Closing the feedback page, returning the user to the building
             * information
             */
            UserFeedback.this.finish();
        }

        /**
         *  Uploading the data to the server.
         *
         * @param vars the data that will be send to the server.
         *             vars[0] = The building that has an error
         *             vars[1] = the feature that has an error
         *             vars[2] = the feedback from the user
         *
         * @return True on a successful upload, False otherwise.
         */
        private Boolean uploadData(String[] vars) {
            /* The user feedback is not required to be space free, so it must
             * be encoded. I'm choosing to use + for spaces.
             */
            String encodedFeedback = vars[2].replace(" ", "+");
            String dataToSend = "?buildingCode=" + vars[0] + "&feature=" +
                    vars[1] + "&feedback=" + encodedFeedback;

            /* There was an error reading the server file. */
            if (FEEDBACK_SERVER.equals(READ_ERROR)) {
                return false;
            }
            try {
                /* Connecting to the server, sending our data */
                URL url = new URL(FEEDBACK_SERVER + dataToSend);
                Log.d("URL connection to:", url.toString());
                HttpURLConnection conn = (HttpURLConnection)
                        url.openConnection();
                conn.connect();

                /* Checking the response for an HTTP 200: OK response */
                int responseCode = conn.getResponseCode();
                Log.d("HTTP response:", Integer.toString(responseCode));
                if (responseCode != 200) {
                    Toast.makeText(getApplicationContext(), UPLOAD_ERROR,
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (IOException e) {
                Log.e("IOException: ", e.getMessage());
                Toast.makeText(getApplicationContext(), UPLOAD_ERROR,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }

        /**
         * Reading the feedback server from a file.
         *
         * @return The server address, or error if it cannot be read.
         */
        public String getFeedbackServer() {
            final Resources resource = getResources();
            InputStream is = resource.openRawResource(R.raw.feedbackserver);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is));
            try {
                String server;
                if ((server = reader.readLine()) != null) {
                    return server;
                }
            } catch (IOException e) {
                Log.e("IOException: ", e.getMessage());
            }
            return READ_ERROR;
        }
    }
}
