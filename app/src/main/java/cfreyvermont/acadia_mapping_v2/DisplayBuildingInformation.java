package cfreyvermont.acadia_mapping_v2;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class DisplayBuildingInformation extends AppCompatActivity {
    BuildingInfoDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_display_building_information);
        expand(findViewById(R.id.display_building_layout));
        Intent intent = getIntent();
        String building_code = intent.getStringExtra("buildingCode");

        db = new BuildingInfoDB(getApplicationContext());
        ContentValues cv = db.selectRecordByCode(building_code);

        if (cv != null) {
            String data[] = new String[cv.keySet().size()];
            data[0] = cv.getAsString(DatabaseHelper.COLUMN_NAME_BUILDING_NAME) + ": " +
                    cv.getAsString(DatabaseHelper.COLUMN_NAME_BUILDING_CODE);
            data[1] = cv.getAsString(DatabaseHelper.COLUMN_NAME_BUILDING_HOURS);
            data[2] = cv.getAsString(DatabaseHelper.COLUMN_NAME_BUILDING_WEBSITE);
            data[3] = cv.getAsString(DatabaseHelper.COLUMN_NAME_BUILDING_PHONE);
            data[4] = cv.getAsString(DatabaseHelper.COLUMN_NAME_BUILDING_NOTES);

            TextView name = (TextView) findViewById(R.id.building_name);
            name.setText(data[0]);
            TextView hours = (TextView) findViewById(R.id.building_hours);
            hours.setText(data[1]);
            TextView website = (TextView) findViewById(R.id.building_website);
            website.setText(data[2]);
            TextView phone = (TextView) findViewById(R.id.building_phone);
            phone.setText(data[3]);
            if (data[4].length() >= 1) {
                RelativeLayout relativeLayout = (RelativeLayout)
                        findViewById(R.id.notes_placeholder);
                View v = getLayoutInflater().inflate(R.layout.building_notes, relativeLayout);
                TextView textView = (TextView) v.findViewById(R.id.building_notes);
                textView.setText(data[4]);
            }
        }
    }

    /**
     * This expands the layout in a nice way from the top of the screen.
     * From http://stackoverflow.com/a/13381228
     *
     * @param v the view to animate.
     */
    public static void expand (final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }
            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(targetHeight / v.getContext().getResources()
                .getDisplayMetrics().density));
        v.startAnimation(a);
    }
}
