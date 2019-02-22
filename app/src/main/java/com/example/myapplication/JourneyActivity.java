package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class JourneyActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    int countOfUsersInSession=0;
    ArrayList<String> journey;
    public void checkIn(View view)
    {
        EditText phoneNum = (EditText)findViewById(R.id.userPhone);
        Log.i("len",Integer.toString(phoneNum.getText().toString().trim().length()));
        if(phoneNum.getText().toString().trim().length()==0)
        {
            Toast.makeText(getApplicationContext(), "Please enter your phone number", Toast.LENGTH_LONG).show();
        }
        else
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Integer.toString(countOfUsersInSession++),phoneNum.getText().toString());
            Log.i("avfajkn", phoneNum.getText().toString());
            editor.commit();
            Intent intent = new Intent(JourneyActivity.this,TravellingActivity.class);
            intent.putExtra("journeyNodes", journey);
            intent.putExtra("routeStationNames",(ArrayList<String>) getIntent().getSerializableExtra("routeStationNames") );
            JourneyActivity.this.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("resources", MODE_PRIVATE);
       // List<String> s = (List<String>) sharedPreferences.getStringSet("journey", null);
        journey = (ArrayList<String>) getIntent().getSerializableExtra("journeyNodes");

        //journey = new ArrayList<String>(s);
        Log.i("FromNewAct", Integer.toString(journey.size()));
        ListView listofstation=(ListView) findViewById(R.id.selectedBusRoute);
        ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<String>(JourneyActivity.this, android.R.layout.simple_list_item_1, journey);
        listofstation.setAdapter(arrayAdapter1);


    }

}
