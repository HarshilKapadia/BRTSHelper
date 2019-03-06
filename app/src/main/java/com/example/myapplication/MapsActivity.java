package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.se.omapi.Session;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    int no_of_stations=40, no_of_bus=4, no_of_routes=4;
    Station stations[];
    Bus b[];
    Route r[];
    Integer routesPossible[]=new Integer[5000], tripsPossible[]=new Integer[5000];int countOfRoutes=0, countOfTrips=0;
    int min=0;
    ArrayList<String> tempDataRepresentation = new ArrayList<String>();
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }
    int addMilitaryTime(int a, int b)
    {
        int a_hr = a/100, b_hr = b/100, a_min = a%100, b_min = b%100;
        a_min+=b_min;
        if(a_min>60)
        {
            a_min%=60;
            a_hr=(a_hr+1)%24;
        }
        a_hr+=b_hr;
        a_hr%=24;
        a=a_hr*100+ a_min;
        return a;
    }
    int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stations=Station.getStations(getAssets());
        r=Route.getRoutes(getAssets());
        b = Bus.getBuses(getAssets());
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //mapFragment.getView().setVisibility(View.GONE);
        mapFragment.getMapAsync(this);
        SharedPreferences sharedPreferences = getSharedPreferences("resources", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("no_of_stations", no_of_stations);
        editor.putInt("no_of_bus", no_of_bus);
        editor.putInt("no_of_routes", no_of_routes);
        editor.commit();
        destTextView = (SearchView) findViewById(R.id.destination);
        sourceTextView = (SearchView) findViewById(R.id.source);
        listView = (ListView) findViewById(R.id.temp);

        sourceTextView.clearFocus();
        destTextView.clearFocus();
        listView.setVisibility(View.GONE);
        list = new ArrayList<>();
        list =Station.getStationNames();


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);
        sourceTextView.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listView.setVisibility(View.VISIBLE);

            }
        });
        destTextView.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listView.setVisibility(View.VISIBLE);

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            public void onItemClick(AdapterView<?> parent, View clickView,
                                    int position, long id) {
                String val =(String) parent.getItemAtPosition(position);
                if(sourceTextView.hasFocus())
                {
                    sourceTextView.setQuery(val,false);
                    sourceTextView.clearFocus();
                }
                else
                {
                    destTextView.setQuery(val,false);
                    destTextView.clearFocus();
                }
                listView.setVisibility(View.GONE);
            }

        });
        sourceTextView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                if(list.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    //Toast.makeText(MainActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listView.setVisibility(View.VISIBLE);
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        destTextView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                if(list.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    //Toast.makeText(MainActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listView.setVisibility(View.VISIBLE);
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }


    int d=1;String mySrc=null;

    int srcNode, destNode;
    SearchView destTextView;
    SearchView sourceTextView;
    ArrayList <Bus> busesAvailable = new ArrayList<Bus>();
    ArrayList <String> routes = new ArrayList<String>();

    ListView listView;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    ArrayList<String> routeStationNames=new ArrayList<>();


    public void destEntered(View view) {
        busesAvailable.clear();
        tempDataRepresentation.clear();
        destTextView = (SearchView) findViewById(R.id.destination);
        sourceTextView = (SearchView) findViewById(R.id.source);
        Log.i("destination", destTextView.getQuery().toString());
        Log.i("source", sourceTextView.getQuery().toString());

        if (destTextView.getQuery().toString().trim().equalsIgnoreCase(sourceTextView.getQuery().toString().trim())) {
            Toast.makeText(getApplicationContext(), "Source and destination cannot be same.", Toast.LENGTH_LONG).show();
        }
        else{
        mySrc = (sourceTextView.getQuery().toString().trim().length() > 0) ? (sourceTextView.getQuery().toString()) : stations[min].name;

        if (destTextView.getQuery().toString().trim().length() > 0) {


            for (int i = 0; i < no_of_routes; i++) {
                d = 1;
                int currentHr = (Calendar.getInstance().getTime().getHours()) % 24;
                int currentMin = (Calendar.getInstance().getTime().getMinutes()) % 60;
                int currentTime = currentHr * 100 + currentMin;
                int j;
                for (j = 0; j < r[i].no_of_nodes; j++) {
                    if (r[i].nodes[j].equals(mySrc)) {
                        srcNode = j;
                        d = 0;
                        break;
                    }
                }
                for (; j < r[i].no_of_nodes; j++) {
                    if (d == 0 && r[i].nodes[j].equals(destTextView.getQuery().toString())) {

                        for (int k = 0; k < no_of_bus; k++) {
                            if (b[k].routeId == r[i].routeId) {

                                for (int l = 0; l < b[k].no_of_trips; l++) {

                                    if (addMilitaryTime(b[k].arr_at_src[l], r[b[k].routeId].offset[srcNode]) > currentTime) {
                                        busesAvailable.add(b[k]);
                                        Log.i("mysrc", mySrc);
                                        routesPossible[countOfRoutes++] = i;
                                        tripsPossible[countOfTrips++] = l;
                                        tempDataRepresentation.add("Bus number: " + b[k].busNumber + "\nArrival: " + b[k].arr_at_src[l]);
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }


        }


        for (int i = 0; i < busesAvailable.size(); i++)
            Log.i("Bus no: ", (busesAvailable.get(i).busNumber));
        final ListView listView = (ListView) findViewById(R.id.buses);
        ArrayAdapter<Bus> arrayAdapter = new ArrayAdapter<Bus>(MapsActivity.this, android.R.layout.simple_list_item_1, busesAvailable);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            public void onItemClick(AdapterView<?> parent, View clickView,
                                    int position, long id) {
                routes.clear();
                routeStationNames.clear();
                int i;
                for (i = 0; i < r[busesAvailable.get(position).routeId].no_of_nodes; i++)
                    if (r[busesAvailable.get(position).routeId].nodes[i].equals(mySrc))
                        break;
                while (!r[busesAvailable.get(position).routeId].nodes[i].equals(destTextView.getQuery().toString()) && i < 33) {
                    routes.add(r[busesAvailable.get(position).routeId].nodes[i] + "\nArrival: " + addMilitaryTime(busesAvailable.get(position).arr_at_src[tripsPossible[position]], r[busesAvailable.get(position).routeId].offset[i]));
                    routeStationNames.add(r[busesAvailable.get(position).routeId].nodes[i]);
                    i++;
                }
                routeStationNames.add(r[busesAvailable.get(position).routeId].nodes[i]);
                routes.add(r[busesAvailable.get(position).routeId].nodes[i] + "\nArrival: " + addMilitaryTime(busesAvailable.get(position).arr_at_src[tripsPossible[position]], r[busesAvailable.get(position).routeId].offset[i]));
                i++;
                /*ListView listofstation=(ListView) findViewById(R.id.route);
                ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_list_item_1, routes);
                listofstation.setAdapter(arrayAdapter1);*/
                SharedPreferences sharedPreferences = getSharedPreferences("resources", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                LinkedHashSet<String> routesSet = new LinkedHashSet<String>(routes);
                LinkedHashSet<String> routeStaions = new LinkedHashSet<>(routeStationNames);
                editor.putStringSet("journey", routesSet);
                editor.putStringSet("routeStationNames", routeStaions);
                editor.commit();
                Intent intent = new Intent(MapsActivity.this, JourneyActivity.class);
                intent.putExtra("journeyNodes", routes);
                intent.putExtra("routeStationNames", routeStationNames);
                MapsActivity.this.startActivity(intent);
            }

        });
    }


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

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



       locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener= new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                mMap.clear();

                LatLng userLoc = new LatLng(location.getLatitude(),location.getLongitude());
                double d1, d2;
                d1 = Math.abs(distFrom(stations[0].latitude, stations[0].longnitude, location.getLatitude(), location.getLongitude()));
                for(int i=1;i<no_of_stations;i++) {

                    d2 = Math.abs(distFrom(stations[i].latitude, stations[i].longnitude, location.getLatitude(), location.getLongitude()));
                    if(d2<d1) {
                        min = i;
                        d1 = d2;
                    }
                }
                Log.i("min ",String.valueOf(min));
               Toast.makeText(getApplicationContext(), stations[min].name, Toast.LENGTH_LONG).show();


                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc));
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
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100000,0,locationListener);

        }
        // Add a marker in Sydney and move the camera



    }
}
