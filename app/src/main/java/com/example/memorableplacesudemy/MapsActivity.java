package com.example.memorableplacesudemy;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    centerMapOnLoction(lastKnownLocation, "location");
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setTitle("Add A Location");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       mMap.setOnMapLongClickListener(this);
        Intent intent = getIntent();
        if (intent.getIntExtra("placeholder", 0) == 0) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    centerMapOnLoction(location, "Your Loaction");
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLoction(lastKnownLocation,"location");
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }else{
            Location placeLocation =new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.locations.get(intent.getIntExtra("placenumber",0)).latitude);
            placeLocation.setLongitude(MainActivity.locations.get(intent.getIntExtra("placenumber",0)).longitude);
            centerMapOnLoction(placeLocation,MainActivity.listOfPlaces.get(intent.getIntExtra("placenumber",0)));
        }
    }
    public void centerMapOnLoction(Location location,String title) {
        if (location != null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,12));
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.addMarker((new MarkerOptions().position(latLng)).title("Your new memorable place"));
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        String adress="";
        try{
            List<Address> ListAdresses=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(ListAdresses !=null && ListAdresses.size()>0){
                if(ListAdresses.get(0).getThoroughfare()!=null){
                    if(ListAdresses.get(0).getSubThoroughfare()!=null){
                        adress+=ListAdresses.get(0).getSubThoroughfare() +" ";
                    }
                    adress+=ListAdresses.get(0).getThoroughfare()+"";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(adress.equals("")){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm yyyy-MM-dd");
            adress+=simpleDateFormat.format(new Date());
        }

        MainActivity.listOfPlaces.add(adress);
        MainActivity.locations.add(latLng);
        MainActivity.arrayAdapter.notifyDataSetChanged();
       SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.memorableplacesudemy", Context.MODE_PRIVATE);
        try {
            ArrayList<String> latitude=new ArrayList<>();
            ArrayList<String> longitude=new ArrayList<>();

            for(LatLng coord:MainActivity.locations){
                latitude.add(Double.toString(coord.latitude));
                longitude.add(Double.toString(coord.longitude));
            }
            sharedPreferences.edit().putString("places",ObjectSerializer.serialize(MainActivity.listOfPlaces)).apply();
            sharedPreferences.edit().putString("lats",ObjectSerializer.serialize(latitude)).apply();
            sharedPreferences.edit().putString("longs",ObjectSerializer.serialize(longitude)).apply();
  } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText( this, adress, Toast.LENGTH_SHORT).show();

    }

}
