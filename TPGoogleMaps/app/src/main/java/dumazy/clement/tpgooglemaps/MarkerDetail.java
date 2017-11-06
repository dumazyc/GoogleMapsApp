package dumazy.clement.tpgooglemaps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

/**
 * Created by Clément Dumazy
 */

public class MarkerDetail extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_details);
        TextView direction = (TextView) findViewById(R.id.TV_direction);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            direction.setText(Html.fromHtml(getIntent().getStringExtra("title"), Html.FROM_HTML_MODE_LEGACY));
        } else {
            direction.setText(Html.fromHtml(getIntent().getStringExtra("title")));
        }

        TextView distance_temps = (TextView) findViewById(R.id.TV_distance_temps);
        distance_temps.setText(getIntent().getStringExtra("snippet"));
    }

}
