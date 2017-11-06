package dumazy.clement.tpgooglemaps;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Clément Dumazy
 */

public class MarkerListener implements GoogleMap.OnMarkerClickListener {
    private Activity activity;

    MarkerListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent i = new Intent(activity, MarkerDetail.class);
        i.putExtra("title", marker.getTitle());
        i.putExtra("snippet", marker.getSnippet());
        activity.startActivity(i);
        return true;
    }
}