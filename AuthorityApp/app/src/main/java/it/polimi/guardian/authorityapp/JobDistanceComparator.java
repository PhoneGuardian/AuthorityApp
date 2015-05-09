package it.polimi.guardian.authorityapp;

import android.content.Context;

import java.util.Comparator;

/**
 * Created by Nemanja on 09/05/2015.
 */
public class JobDistanceComparator implements Comparator<Job> {
    Context context;
    double userLat;
    double userLng;
    LocationUtility locationUtility;

    public JobDistanceComparator(Context ctx, double myLatitude, double myLongitude) {
        context = ctx;
        userLat = myLatitude;
        userLng = myLongitude;
        locationUtility = new LocationUtility(ctx);
    }

    @Override
    public int compare(Job j1, Job j2) {
        //distance sa prvim, distance sa drugim, pa njihova razlika
        double lat1 = j1.getEvent().getLat();
        double lng1 = j1.getEvent().getLng();
        double distance1 = locationUtility.calculateDistance(userLat, userLng, lat1, lng1);

        double lat2 = j2.getEvent().getLat();
        double lng2 = j2.getEvent().getLng();
        double distance2 = locationUtility.calculateDistance(userLat, userLng, lat2, lng2);

        double diff = distance1 - distance2;

        if(diff < 0) return -1;
        else if(diff == 0) return 0;
        else return 1;
    }

}
