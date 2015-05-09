package it.polimi.guardian.authorityapp;

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
    Event eventToShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);
        eventToShow = (Event) getIntent().getSerializableExtra("eventDescription");
    }

    @Override
    protected void onStart() {
        super.onStart();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapf);
        map = mapFragment.getMap();
        map.setOnMarkerClickListener(this);
        map.setOnCameraChangeListener(this);

        PostionOnMap();
        DrawJobMarker();

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
        double[] a = getLocation();
        lat = a[0];
        lng = a[1];
    }

    public double[] getLocation()
    {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

        Location l = null;
        for (int i = 0; i < providers.size(); i++) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null)
                break;
        }
        double[] gps = new double[2];

        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        return gps;
    }
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;//returns true to override default marker behaviour.in this way info window of a marker is not shown
    }
}
