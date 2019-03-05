package com.example.myapplication;

import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class Route {
    int routeId, no_of_nodes;Integer offset[];
    String nodes[];
    static int count=0, no_of_routes=4;
    static Route routes[] = new Route[no_of_routes];
    private Route(){}
    public static Route[] getRoutes(AssetManager asset)
    {
        if(count==0)
        {
            String json;
            try {
                InputStream is = asset.open("routedata.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
                JSONObject obj = new JSONObject(json);
                JSONObject stobj = obj.getJSONObject("routedata");
                JSONArray arr = stobj.getJSONArray("routes");
                for(int i=0;i<4;i++)
                {
                    Log.i("routeId", (Integer.toString( i)));
                    routes[i]=new Route();
                    routes[i].routeId=arr.getJSONObject(i).getInt("routeId");

                    routes[i].no_of_nodes=arr.getJSONObject(i).getInt("no_of_nodes");
                    Log.i("route", (Integer.toString( routes[i].no_of_nodes)));
                    JSONArray nodes = arr.getJSONObject(i).getJSONArray("nodes");
                    JSONArray offset = arr.getJSONObject(i).getJSONArray("offset");
                    routes[i].offset = new Integer[routes[i].no_of_nodes];
                    routes[i].nodes = new String[routes[i].no_of_nodes];
                    for(int j=0;j<routes[i].no_of_nodes;j++)
                    {
                        routes[i].nodes[j]= nodes.getString(j);
                        routes[i].offset[j] = offset.getInt(j);
                    }
                }
                count=1;
            }
            catch(Exception e)
            {
                Log.i("harshil", e.getMessage());
            }

        }

        return routes;
    }
}
