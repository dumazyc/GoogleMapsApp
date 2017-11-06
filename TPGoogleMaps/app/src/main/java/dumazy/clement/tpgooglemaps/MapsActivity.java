package dumazy.clement.tpgooglemaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Clément Dumazy
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int MY_LOCATION_REQUEST_CODE = 42;
    private GoogleMap map;
    private Context context;
    private int current_style = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_maps);
        /*WebView webview = findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("https://www.google.fr/maps/dir/Villeneuve-d'Ascq/Moscou,+Russie");*/

        /** Etape 2 (commenter la webview avant) **/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        /** Etape 3 **/

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_LOCATION_REQUEST_CODE);

        }

        /** Etape 7 **/
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setOnMarkerClickListener(new MarkerListener(this));

    }


    /**
     * Etape 3
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_LOCATION_REQUEST_CODE:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                } else {
                    Toast.makeText(this, "Impossible de vous localiser", Toast.LENGTH_SHORT).show();
                }

        }
    }


    public void calculRoute(View view) {
        String url = null;

        /** Etape 4 **/
        EditText et_ville_depart = findViewById(R.id.villeDepart);
        EditText et_ville_arrivee = findViewById(R.id.villeArrivee);
        String origin = String.valueOf(et_ville_depart.getText());
        String destination = String.valueOf(et_ville_arrivee.getText());

        map.clear();
        
        try {
            url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + URLEncoder.encode(origin.trim(), "UTF-8") + "&destination=" + URLEncoder.encode(destination.trim(), "UTF-8") + "&language=fr&key=" + getString(R.string.google_maps_key);
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, "Erreur encodage URL", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject resultObject;
                        try {
                            resultObject = new JSONObject(response);
                            JSONArray routesArray = resultObject.getJSONArray("routes");
                            JSONArray legsArray = routesArray.getJSONObject(0).getJSONArray("legs");
                            JSONArray stepsArray = legsArray.getJSONObject(0).getJSONArray("steps");
                            PolylineOptions route = new PolylineOptions().geodesic(true);
                            JSONObject start_location = legsArray.getJSONObject(0).getJSONObject("start_location");

                            route.add(new LatLng(start_location.getDouble("lat"), start_location.getDouble("lng")));

                            /** Etape 4 **/
                            /*String affichage_route = routesArray.getJSONObject(0).getJSONObject("overview_polyline").getString("points");
                            ArrayList<LatLng> etapes = new ArrayList<>(PolyUtil.decode(affichage_route));

                            for (LatLng point : etapes) {
                                route.add(point);
                            }*/

                            for (int i = 0; i < stepsArray.length(); i++) {


                                /** Etape 5 (cacher l'étape 4 ci-dessus)**/
                                String lieu = stepsArray.getJSONObject(i).getJSONObject("polyline").getString("points");
                                ArrayList<LatLng> etapes = new ArrayList<>(PolyUtil.decode(lieu));

                                for (LatLng point : etapes) {
                                    route.add(point);
                                }

                                /** Etape 6 **/
                               JSONObject marker = stepsArray.getJSONObject(i);
                                map.addMarker(new MarkerOptions().title(marker.getString("html_instructions")).snippet(marker.getJSONObject("distance").getString("text") + " - " + marker.getJSONObject("duration").getString("text")).position(new LatLng(marker.getJSONObject("start_location").getDouble("lat"), marker.getJSONObject("start_location").getDouble("lng"))));


                            }


                            map.addPolyline(route);


                            /** Etape 8 **/
                            JSONObject northeast = routesArray.getJSONObject(0).getJSONObject("bounds").getJSONObject("northeast");
                            JSONObject southwest = routesArray.getJSONObject(0).getJSONObject("bounds").getJSONObject("southwest");
                            LatLngBounds.Builder bounds_builder = new LatLngBounds.Builder();
                            bounds_builder.include(new LatLng(northeast.getDouble("lat"), northeast.getDouble("lng")));
                            bounds_builder.include(new LatLng(southwest.getDouble("lat"), southwest.getDouble("lng")));
                            LatLngBounds bounds = bounds_builder.build();

                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                            map.animateCamera(cu);
                        } catch (JSONException e) {
                            Toast.makeText(context, "Impossible de faire l'itinéraire", Toast.LENGTH_SHORT).show();
                            System.out.println(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Impossible de faire l'itinéraire", Toast.LENGTH_SHORT).show();

            }
        });
        queue.add(stringRequest);
    }


    /**
     * Etape 9
     **/
    public void change_theme(View view) {
        try {
            current_style = (current_style + 1) % 7;
            if (current_style == 6) {
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            } else {
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                boolean success = map.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, getResources().getIdentifier("style_json_" + current_style, "raw", getPackageName())));//R.raw.style_json_0

                if (!success) {
                    System.out.println("Style parsing failed.");
                }
            }
        } catch (Resources.NotFoundException e) {
            System.out.println("Can't find style. Error: " + e);
        }
    }
}
