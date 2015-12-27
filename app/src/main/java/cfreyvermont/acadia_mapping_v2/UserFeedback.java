package cfreyvermont.acadia_mapping_v2;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserFeedback extends FragmentActivity {
    BuildingInfoDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);
        db = new BuildingInfoDB(getApplicationContext());

        Intent intent = getIntent();
        String code = intent.getStringExtra(DisplayBuildingInformation.STRING_EXTRA_MESSAGE);

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
                submitFeedback();
            }
        });
    }

    public void submitFeedback() {
        /* TODO: Add in functionality */
    }
}
