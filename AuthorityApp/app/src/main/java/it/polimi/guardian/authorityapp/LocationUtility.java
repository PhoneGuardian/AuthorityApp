package it.polimi.guardian.authorityapp;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.util.List;

/**
 * Created by Nemanja on 09/05/2015.
 */
public class LocationUtility {
    private Context context;

    public LocationUtility(Context ctx) {
        context = ctx;
    }

    public double[] getLocation()
    {
        Location l = this.getLocationObject();
        double[] gps = new double[2];

        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        return gps;
    }

    public Location getLocationObject() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

        Location l = null;
        for (int i = 0; i < providers.size(); i++) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null)
                break;
        }
        return l;
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000; // earth’s radius (mean radius = 6,371km) in meters
        double lat1Radians = Math.toRadians(lat1);
        double lat2Radians = Math.toRadians(lat2);
        double latDiffRadians = Math.toRadians(lat2-lat1);
        double lonDiffRadians = Math.toRadians(lon2-lon1);

        double a = Math.sin(latDiffRadians/2) * Math.sin(latDiffRadians/2) +
                Math.cos(lat1Radians) * Math.cos(lat2Radians) *
                        Math.sin(lonDiffRadians/2) * Math.sin(lonDiffRadians/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double d = R * c;
        return Math.abs(d);
    }
}
