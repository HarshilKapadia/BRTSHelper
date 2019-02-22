package com.example.myapplication;

import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class Bus {
    int busId, routeId;
    String busNumber;
    Integer arr_at_src[], dep_from_src[];int no_of_trips;
    static int no_of_buses=1, count=0;
    static Bus[] buses = new Bus[no_of_buses];
    private Bus(){}
    public static Bus[] getBuses(AssetManager asset)
    {
        if(count==0)
        {
            try {
                InputStream is = asset.open("busdata.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String json = new String(buffer, "UTF-8");
                JSONObject obj = new JSONObject(json);
                JSONObject stobj = obj.getJSONObject("busdata");
                JSONArray arr = stobj.getJSONArray("buses");
                for (int i = 0; i < no_of_buses; i++)
                {
                    buses[i] = new Bus();
                    buses[i].busId=arr.getJSONObject(i).getInt("busId");
                    buses[i].routeId = arr.getJSONObject(i).getInt("routeId");
                    buses[i].busNumber = arr.getJSONObject(i).getString("busNumber");
                    buses[i].no_of_trips = arr.getJSONObject(i).getInt("no_of_trips");
                    JSONArray arrival = arr.getJSONObject(i).getJSONArray("arr_at_src");
                    JSONArray departure = arr.getJSONObject(i).getJSONArray("dep_from_src");
                    buses[i].arr_at_src = new Integer[buses[i].no_of_trips];
                    buses[i].dep_from_src = new Integer[buses[i].no_of_trips];
                    for(int j=0;j<buses[i].no_of_trips;j++)
                    {

                        buses[i].arr_at_src[j] = arrival.getInt(j);
                        buses[i].dep_from_src[j] = departure.getInt(j);
                    }
                    Log.i("busDatavsj",buses[i].busNumber);
                    count=1;
                }
            }
            catch(Exception e){}

        }
        return buses;
    }
    public String toString()
    {
        return this.busNumber;
    }
}
