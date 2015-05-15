package it.polimi.guardian.authorityapp;

import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class MapActivity  extends FragmentActivity implements OnMarkerClickListener,GoogleMap.OnCameraChangeListener {

    GoogleMap map;
    double lat;
    double lng;
    boolean notificationFlag = false;
    Event eventToShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        onNewIntent(getIntent());


    }

    @Override
    protected void onStart() {
        super.onStart();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapf);
        map = mapFragment.getMap();
        map.setOnMarkerClickListener(this);
        map.setOnCameraChangeListener(this);

        if(!notificationFlag)
            PostionOnMap();
        else
        {
            LatLng ll = new LatLng(lat,lng);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
        }
        DrawJobMarker();

    }

    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("notificationFlag")) {
                notificationFlag = extras.getBoolean("notificationFlag", false);
            }

                lat = extras.getDouble("lat", 0);
                lng = extras.getDouble("lng", 0);
                eventToShow = (Event) extras.getSerializable("eventDescription");

        }
    }

    private void DrawJobMarker() {
        if (this.map == null) {
            Toast.makeText(MapActivity.this,"Google Maps not Available", Toast.LENGTH_LONG).show();
        }
        map.clear();
        String userType = User.getInstance().getType();
        if(userType.equals("E"))
        {
            com.google.android.gms.maps.model.Marker m = map
                    .addMarker(new MarkerOptions()
                            .position(new LatLng(this.lat,this.lng))
                            .title("Emergency")
                            .snippet(eventToShow.getDescription())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.emergency_pin)));
            m.hideInfoWindow();
        } else if(userType.equals("F"))
        {
            com.google.android.gms.maps.model.Marker m = map
                    .addMarker(new MarkerOptions()
                            .position(new LatLng(this.lat,this.lng))
                            .title("Fire")
                            .snippet(eventToShow.getDescription())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.flame_pin)));
            m.hideInfoWindow();
        } else if(userType.equals("P"))
        {
            com.google.android.gms.maps.model.Marker m = map
                    .addMarker(new MarkerOptions()
                            .position(new LatLng(this.lat,this.lng))
                            .title("Police")
                            .snippet(eventToShow.getDescription())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.police_pin)));
            m.hideInfoWindow();
        } else {
            Toast.makeText(this, "userType didn't match to any chars E,F or P", Toast.LENGTH_SHORT).show();
        }
    }

    public void PostionOnMap()
    {
        if (lat == 0 && lng == 0)
            GetCurrentLocation();

        LatLng ll = new LatLng(lat,lng);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
        map.setMyLocationEnabled(true);
    }
    private void GetCurrentLocation()
    {
        double[] a = new LocationUtility(this).getLocation();
        lat = a[0];
        lng = a[1];
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;//returns true to override default marker behaviour.in this way info window of a marker is not shown
    }
}
