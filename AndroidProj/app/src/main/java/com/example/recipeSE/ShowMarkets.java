package com.example.recipeSE;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.example.recipeSE.login.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class ShowMarkets extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_markets);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Double lat = location.getLatitude();
                            Double lon = location.getLongitude();
                            LatLng me = new LatLng(lat, lon);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(me));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));



                            Geocoder geocoder = new Geocoder(getBaseContext());
                            List<Address> addresses = null;
                            try {
                                Log.i("Debug: ","entro nel try statement");
                                addresses = geocoder.getFromLocationName("supermercato", 10, lat-0.005, lon-0.005, lat+0.005, lon+0.005);
                                Log.i("Debug: ","ho fatto addresses");
                                Log.i("Debug: ",addresses.toString());
                                if (addresses != null && !addresses.equals("")){
                                    Log.i("Debug: ","entro nell'if statement");
                                    search(addresses,mMap);
                                }
                            } catch (Exception e) {
                                //todo: error message
                            }



                        }
                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getApplicationContext(), ShowMarkets.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        }
    }



    protected void search(List<Address> addresses, GoogleMap mMap) {
        Log.i("Debug: ","entro nella search func");
        Address address = addresses.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

        String addressText = String.format(
                "%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address
                        .getAddressLine(0) : "", address.getCountryName());

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(latLng);
        markerOptions.title(addressText);

        //map1.clear();
        mMap.addMarker(markerOptions);
        //map1.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //map1.animateCamera(CameraUpdateFactory.zoomTo(15));
        //locationTv.setText("Latitude:" + address.getLatitude() + ", Longitude:" + address.getLongitude());

        Log.i("Debug: ","esco dalla search");
    }



}
