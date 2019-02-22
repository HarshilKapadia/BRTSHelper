package com.example.myapplication;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.getActivity;

public class Station {

    double longnitude;
    double latitude;
    String name;
    static int no_of_stations =33 ;
    static Station stations[]=new Station[no_of_stations];
    static int count=0;
    private static ArrayList<String> stationNames = new ArrayList<>(no_of_stations);;
    private Station(){

    }
    public static Station[] getStations(AssetManager a)
    {

                if(count==0) {
                    String json = null;
                    try {
                        InputStream is = a.open("stationdata.json");
                        if (is == null)
                            Log.i("error : ", "not opened");
                        int size = is.available();
                        byte[] buffer = new byte[size];
                        is.read(buffer);
                        is.close();
                        json = new String(buffer, "UTF-8");
                        JSONObject obj = new JSONObject(json);
                        JSONObject stobj = obj.getJSONObject("stationData");
                        JSONArray arr = stobj.getJSONArray("stations");
                        for (int i = 0; i < no_of_stations; i++) {
                            Log.i("From JSON Station", "inside loop");
                            stations[i] = new Station();
                            stations[i].name = (String) arr.getJSONObject(i).getString("name");
                            stationNames.add(i, stations[i].name);
                            stations[i].latitude = (Double) arr.getJSONObject(i).getDouble("latitude");
                            stations[i].longnitude = (Double) arr.getJSONObject(i).getDouble("longitude");
                            Log.i("From JSON Station", stations[i].name);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    count=1;
                }
        return stations;
    }
    public static ArrayList<String> getStationNames()
    {
        return stationNames;
    }

}
