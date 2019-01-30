package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Station s[] = new Station[9];
    Bus b[] = new Bus[10];
    int busID = 0;
    Route r[] = new Route[3];
    int no_of_stations=9, no_of_bus=10, no_of_routes=3;
    int min=0;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String json = null;
        try {
            InputStream is = getAssets().open("stationdata.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);
            JSONObject stobj = obj.getJSONObject("stationData");
            JSONArray arr = stobj.getJSONArray("stations");
            for(int i=0;i<no_of_stations;i++)
            {
                s[i]=new Station();
                s[i].name = (String) arr.getJSONObject(i).getString("name");
                s[i].latitude = (Double) arr.getJSONObject(i).getDouble("latitide");
                s[i].longnitude = (Double) arr.getJSONObject(i).getDouble("longitude");
                Log.i("From JSON Station", s[i].name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            InputStream is = getAssets().open("busdata.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);
            JSONObject stobj = obj.getJSONObject("busData");
            JSONArray arr = stobj.getJSONArray("busdata");
            for(int i=0;i<no_of_bus;i++) {

                b[i]=new Bus();
                b[i].number = (String) arr.getJSONObject(i).getString("number");
                b[i].src= (String) arr.getJSONObject(i).getString("src");
                b[i].dest= (String) arr.getJSONObject(i).getString("dest");
                b[i].id = busID++;
                b[i].route_no = (Integer)arr.getJSONObject(i).getInt("route_no");
                Log.i("From JSON Bus", b[i].number);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            InputStream is = getAssets().open("routedata.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);
            JSONObject stobj = obj.getJSONObject("routeData");
            JSONArray arr = stobj.getJSONArray("routes");
            for(int i=0;i<no_of_routes;i++) {
                int n1 = arr.getJSONObject(i).getInt("intermediates");
                String s1[] = new String[n1];
                for(int j=0;j<n1;j++)
                {
                    s1[j]=arr.getJSONObject(i).getJSONArray("stations").getString(j);
                }
                r[i]=new Route(s1);
                r[i].number=n1;
                Log.i("From JSON Route", String.valueOf(r[i].number));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    int d=0;
    EditText destTextView;
    EditText sourceTextView;
    ArrayList <Bus> al = new ArrayList<Bus>();
    ArrayList <String> route = new ArrayList<String>();
    public void destEntered(View view) {
        al.clear();route.clear();
        destTextView = (EditText) findViewById(R.id.destination);
        sourceTextView = (EditText) findViewById(R.id.source);
        if (destTextView != null && sourceTextView.getText().toString().trim().length() > 0) {
            for (int i = 0; i < no_of_stations; i++) {
                Log.i("Dest index:", destTextView.getText().toString());
                if (s[i].name.toString().equals(destTextView.getText().toString())) {
                    d = i;
                    Log.i("Dest index:", Integer.toString(d));
                }

            }
            Log.i("source","entered");
            Log.i("MIN  : ",Integer.toString(min));
            for(int i=0;i<no_of_routes;i++)
            {
                int source=0, destination=0;
                for(int j=0;j<r[i].number;j++)
                {
                    if(source==0&&r[i].intermediates[j].equals(sourceTextView.getText().toString()))
                    {
                        source=1;

                    }
                    if(source==1 && destination==0 && r[i].intermediates[j].equals(s[d].name))
                    {
                        destination=1;
                        for(int k=0;k<no_of_bus;k++)
                        {
                            if(b[k].route_no == i) {
                                al.add(b[k]);

                            }
                        }
                        break;
                    }
                }
            }
        }
        else
        {
            for (int i = 0; i < no_of_stations; i++) {
                Log.i("Dest index:", destTextView.getText().toString());
                if (s[i].name.toString().equals(destTextView.getText().toString())) {
                    d = i;
                    Log.i("Dest index:", Integer.toString(d));
                }

            }
            Log.i("source","not entered");
            Log.i("MIN  : ",Integer.toString(min));
            for(int i=0;i<no_of_routes;i++)
            {
                int source=0, destination=0;
                for(int j=0;j<r[i].number;j++)
                {

                    if(source==0&&r[i].intermediates[j].equals(s[min].name.toString()))
                    {
                        source=1;

                    }
                    if(source==1 && destination==0 && r[i].intermediates[j].equals(s[d].name))
                    {
                        destination=1;
                        for(int k=0;k<no_of_bus;k++)
                        {
                            if(b[k].route_no == i)
                                al.add(b[k]);
                        }
                        break;
                    }
                }
            }
        }
        //sourceTextView.setText(s[min].name);


        Log.i("valueofd",Integer.toString(d));



        for (int i = 0; i < al.size(); i++)
            Log.i("Bus no: ", (al.get(i).number));
        final ListView listView = (ListView) findViewById(R.id.buses);
        ArrayAdapter<Bus> arrayAdapter=new ArrayAdapter<Bus>(MapsActivity.this, android.R.layout.simple_list_item_1, al);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View clickView,
                                    int position, long id) {
                    //Log.i("clicked",al.get(position).toString());
                route.clear();
                int add=0;
                for(int i=0;i<no_of_bus;i++)
                {

                    if(b[i].number == al.get(position).toString() )
                    {
                        Log.i("busnumber",b[i].number);
                        for(int j=0;j<r[b[i].route_no].number;j++)
                        {
                            //Log.i("finding source",r[b[i].route_no].intermediates[j]);
                            if(r[b[i].route_no].intermediates[j].equals(sourceTextView.getText().toString()))
                            {
                                //Log.i("finding source",r[b[i].route_no].intermediates[j]);
                                add=1;
                            }
                            if(r[b[i].route_no].intermediates[j].equals(destTextView.getText().toString()))
                            {
                                route.add(r[b[i].route_no].intermediates[j]);
                                break;

                            }
                            if(add==1)
                            {
                                route.add(r[b[i].route_no].intermediates[j]);
                                Log.i("routing",r[b[i].route_no].intermediates[j]);
                            }
                        }
                    }
                    if(add==1)
                        break;
                }
                for (int i = 0; i < route.size(); i++)
                    Log.i("routes: ", (route.get(i).toString()));

                ListView listofstation=(ListView) findViewById(R.id.route);
                ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_list_item_1, route);
                listofstation.setAdapter(arrayAdapter1);
            }

        });




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
                d1 = Math.abs(distFrom(s[0].latitude, s[0].longnitude, location.getLatitude(), location.getLongitude()));
                for(int i=1;i<no_of_stations;i++) {

                    d2 = Math.abs(distFrom(s[i].latitude, s[i].longnitude, location.getLatitude(), location.getLongitude()));
                    if(d2<d1) {
                        min = i;
                        d1 = d2;
                    }
                }
                Log.i("min ",String.valueOf(min));
               Toast.makeText(getApplicationContext(), s[min].name, Toast.LENGTH_LONG).show();


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
