package cfreyvermont.acadia_mapping_v2;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplayBuildingInformation extends Fragment {
    BuildingInfoDB db;
    Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.display_building_information,
                                container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        String code = "";
        if (bundle != null) {
            code = bundle.getString("name");
        }

        db = new BuildingInfoDB(getContext());
        ContentValues cv = db.selectRecordByCode(code);

        if (cv != null) {
            TextView name = (TextView) v.findViewById(R.id.building_name);
            name.setText(cv.getAsString(DatabaseHelper.COLUMN_NAME_BUILDING_NAME));

            TextView purpose = (TextView) v.findViewById(R.id.building_purpose);
            purpose.setText(cv.getAsString(DatabaseHelper.COLUMN_NAME_BUILDING_PURPOSE));

            TextView hours = (TextView) v.findViewById(R.id.building_hours);
            hours.setText(cv.getAsString(DatabaseHelper.COLUMN_NAME_BUILDING_HOURS));

            TextView website = (TextView) v.findViewById(R.id.building_website);
            website.setText(cv.getAsString(DatabaseHelper.COLUMN_NAME_BUILDING_WEBSITE));

            TextView description = (TextView) v.findViewById(R.id.building_description);
            description.setText(cv.getAsString(DatabaseHelper.COLUMN_NAME_BUILDING_NOTES));
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
