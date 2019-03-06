package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class TravellingActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    ArrayList<String> journey;
    Station stations[];
    int no_of_stations;
    ArrayList<String> routeStationNames;
    ArrayList<String> tempRouteStationNames;

    //temporary
    ListView distance;
    ArrayList<String> al;
    ArrayAdapter<String> arrayAdapter;
    int j=0,k=0;
    double tempLatitude,tempLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travelling2);
        //txtLat = (TextView) findViewById(R.id.textview1);

        stations=Station.getStations(getAssets());
        SharedPreferences sharedPreferences;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sharedPreferences = getSharedPreferences("resources", MODE_PRIVATE);
        journey = (ArrayList<String>) getIntent().getSerializableExtra("journeyNodes");
        routeStationNames=(ArrayList<String>) getIntent().getSerializableExtra("routeStationNames");
        no_of_stations = sharedPreferences.getInt("no_of_stations", 0);
        tempRouteStationNames=(ArrayList<String>) getIntent().getSerializableExtra("routeStationNames");
        for(int i=0;i<tempRouteStationNames.size();i++)
        {
            Log.i("temppp",tempRouteStationNames.get(i));
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                int temp=0;
                distance= findViewById(R.id.progress);
                al=new ArrayList<String>();
                j=0;
                while(j<routeStationNames.size())
                {
                    for(int i=0;i<no_of_stations;i++)
                    {

                        Log.i("station i:",i+" "+stations[i].name+" j:"+j+" "+routeStationNames.get(j));

                        if((stations[i].name).equalsIgnoreCase(routeStationNames.get(j)))
                        {

                            Log.i("distance",Float.toString(distFrom(stations[i].latitude,stations[i].longnitude, location.getLatitude(),location.getLongitude()))+""+stations[i].name);
                            if(distFrom(stations[i].latitude,stations[i].longnitude, location.getLatitude(),location.getLongitude())>10) {
                                Log.i("added",routeStationNames.get(j));
                                al.add(routeStationNames.get(j) + "\n" + String.format("%,.2f", ((distFrom(stations[i].latitude, stations[i].longnitude, location.getLatitude(), location.getLongitude())/1000)))+"km");
                                Log.i("locationchanged", stations[i].name + "\n" + distFrom(stations[i].latitude, stations[i].longnitude, location.getLatitude(), location.getLongitude())+""+routeStationNames.get(j));

                                j++;
                                break;
                            }
                            else
                            {
                                j=0;
                                Log.i("removed",routeStationNames.get(j));
                                routeStationNames.remove(0);


                                for(int x=0;x<routeStationNames.size();x++)
                                {
                                    Log.i("insidetemppp",routeStationNames.get(x));
                                }
                                if(routeStationNames.size()==0)
                                {
                                    TextView textView=findViewById(R.id.textView4);
                                    textView.setText("YOU HAVE REACHED TO YOUR DESTINATION.");
                                }
                                //k++;
                                break;
                            }

                        }
                        if(j==routeStationNames.size()) {
                            j=0;k=0;
                            break;
                        }
                    }
                }

                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,al);
                distance.setAdapter(arrayAdapter);

                /*int i=1;
                while(i<routeStationNames.size())
                {
                    int j;float dist;
                    for(j=0;j<no_of_stations;j++)
                    {
                        if(stations[j].name.equals(routeStationNames.get(i)))
                            break;
                    }
                    if(j==no_of_stations)
                        j--;
                    int count = 0;
                    if((dist = distFrom(stations[j].latitude,stations[j].longnitude, location.getLatitude(),location.getLongitude()))>50)
                    {
                        if(routeStationNames.size()-i>=2)
                        {
                            //Display next two nodes
                            TextView nextNode = (TextView)findViewById(R.id.textView);
                            Log.i("abcd","travelling");
                            nextNode.setText("Station name: "+routeStationNames.get(i)+"\nDistance: "+Float.toString(dist));


                        }
                        else
                        {
                            //destination
                            TextView nextNode = (TextView)findViewById(R.id.textView);
                            nextNode.setText("Station name: "+routeStationNames.get(i)+"\nDistance: "+Float.toString(dist));
                        }
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    i++;
                }*/

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,1,locationListener);

    }
    public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }
}

