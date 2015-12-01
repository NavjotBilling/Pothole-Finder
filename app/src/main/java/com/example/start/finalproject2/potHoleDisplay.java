package com.example.start.finalproject2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class potHoleDisplay extends AppCompatActivity {
    ArrayList<potHole> potHoleList = new ArrayList<potHole>();
    databaseHelper dbHelper = new databaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pot_hole_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStart(){
        super.onStart();
        //read information from file

        //populate a small array list

        //get the list view information
        potHoleList = dbHelper.getAllPotholes();
        Log.d("error", ""+potHoleList);
        ListView potholes = (ListView) findViewById(R.id.potHoleList);
        potholeAdapter adapter = new potholeAdapter(this, potHoleList);
        //set the data to be displayed by the list view
        potholes.setAdapter(adapter);

    }
}
