package com.example.recipeSE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.recipeSE.login.MainActivity;
import com.example.recipeSE.savedRecipes.SavedRecipesFragment;
import com.example.recipeSE.search.SearchActivity;
import com.example.recipeSE.search.SearchBarFragment;
import com.example.recipeSE.shoppinglist.ShoppigListFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowMarkets extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DrawerLayout drawer;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_markets);
        drawer = findViewById(R.id.drawernavbar);
        //configure toolbar
        MaterialToolbar mToolbar = findViewById(R.id.topAppBar);
        mToolbar.setTitle(Html.fromHtml("<font color='#ffffff'>Tuna</font>"));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        NavigationView navigationView =  findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_search) {
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.menu_savedrecipes)
                {
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    intent.putExtra("frommap","savedrecipes");
                    startActivity(intent);

                }
                else if (id == R.id.menu_shoppinglist)
                {
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    intent.putExtra("frommap","shoppinglist");
                    startActivity(intent);
                }
                else if (id == R.id.menu_map)
                {
                    drawer.closeDrawer(Gravity.LEFT); //close the sidebar
                    Intent intent = new Intent(getApplicationContext(), ShowMarkets.class);
                    startActivity(intent);

                }
                else if (id == R.id.menu_logout)
                {
                    //TODO: cancellare variabile sessione e fare logout
                }
                return true;
            }
        } );

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(getApplicationContext(), "AIzaSyC7uc8dUyn5YQUeLfDvjqpEUXJJAUMqzf4");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }

        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lon = location.getLongitude();
                            LatLng me = new LatLng(lat, lon);

                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                            loadNearByPlaces(lat, lon);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(me, 16);
                            mMap.animateCamera(cameraUpdate);


                        }
                    }


                });

        mFusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Exception e) {
                // TODO: Handle the error.
                Log.i("Errore nel getLastLocation: ", e.getMessage());
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(getApplicationContext(), ShowMarkets.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }
    }




    private void loadNearByPlaces(double latitude, double longitude) {
        String googlePlacesUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                                 +latitude+","+longitude+ "&radius=500&types=supermarket&sensor=true&key="
                                 +"AIzaSyC7uc8dUyn5YQUeLfDvjqpEUXJJAUMqzf4";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, googlePlacesUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        parseLocationResult(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });

        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);

    }

    private void parseLocationResult(JSONObject result) {
        try {
            JSONArray jsonArray = result.getJSONArray("results");

            if (result.getString("status").equalsIgnoreCase("OK")) {

                mMap.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject place = jsonArray.getJSONObject(i);
                    String placeName = "Default name";
                    if (!place.isNull("name")) {
                        placeName = place.getString("name");
                    }
                    double latitude = place.getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lat");
                    double longitude = place.getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng");


                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latLng = new LatLng(latitude, longitude);
                    markerOptions.position(latLng);
                    markerOptions.title(placeName);
                    mMap.addMarker(markerOptions);
                }

                Toast.makeText(getBaseContext(), jsonArray.length() + " Supermarkets found!",
                        Toast.LENGTH_LONG).show();
            } else if (result.getString("status").equalsIgnoreCase("ZERO_RESULTS")) {
                Toast.makeText(getBaseContext(), "No Supermarket found in 500M radius!!!",
                        Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
            Log.e("TAG", "parseLocationResult: Error=" + e.getMessage());
        }
    }





}
