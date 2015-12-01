package com.example.start.finalproject2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by 100461439 on 11/30/2015.
 */
public class databaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_FILENAME= "potHoles.db";
    public static final String TABLE_NAME="potHoles";
    public static int numEntries = 0;
    public static final String CREATE_STATEMENT = "CREATE TABLE " + TABLE_NAME + "(" +
            " potHoleId integer primary key,"+
            " latitude double not null,"+
            " longitude double not null,"+
            " locationName text not null,"+
            " numSpotted integer not null"+")";
    public static final String DROP_STATEMENT = "DROP TABLE " + TABLE_NAME;
    public databaseHelper(Context context){
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {database.execSQL(CREATE_STATEMENT);}

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        database.execSQL(DROP_STATEMENT);
        database.execSQL(CREATE_STATEMENT);
    }

    public potHole createPotHole(double latitude, double longitude, String location){
        //check if the pothole was reported earlier
        ArrayList<potHole> potholes =  getAllPotholes();
        if (!potholes.isEmpty()) {
            for (potHole pothole : potholes) {
                if (pothole.getLatitude() == latitude && pothole.getLongitude()==longitude){
                    //pothole was seen before
                    pothole.setNumSpotted(pothole.getNumSpotted()+1);
                }
            }
        }
        //add the pothole
        boolean found = false;
        int testing = 0;
        potHole potholePrime = null;
        for(potHole pothole: potholes){
            //find an empty id
            if(pothole.getId()>testing){
                //found a missing id
                potholePrime = new potHole(testing, 1, latitude, longitude, location);
                found = true;
                break;
            }else{
                testing=pothole.getId()+1;
            }
        }
        if(!found){
            potholePrime = new potHole(testing, 1, latitude, longitude, location);
        }

        //add to the database
        SQLiteDatabase database = this.getWritableDatabase();

        //insert the data into the database
        ContentValues values = new ContentValues();
        values.put("potHoleId", potholePrime.getId());
        values.put("latitude", potholePrime.getLatitude());
        values.put("longitude", potholePrime.getLongitude());
        values.put("locationName", potholePrime.getLocationName());
        values.put("numSpotted", potholePrime.getNumSpotted());
        long id = database.insert(TABLE_NAME, null, values);
        Log.i("DatabaseAccess", "addedProduct: id" + id);
        numEntries++;
        return potholePrime;

    }

    public potHole getPotHole(int index){
        ArrayList<potHole> potholes = getAllPotholes();
        //if nothing remains
        if(potholes.isEmpty()){
            potHole empty = new potHole(0,0,0,0,"wisconsin");
            return empty;
        }
        return potholes.get(index);
    }

    public ArrayList<potHole> getAllPotholes(){
        ArrayList<potHole> potholes = new ArrayList();

        SQLiteDatabase database = this.getWritableDatabase();
        String[] columns = new String[] { "potHoleId", "latitude","longitude","locationName","numSpotted" };
        Cursor cursor = database.query(TABLE_NAME, columns, "", new String[]{}, "", "", "");
        try{
            cursor.moveToFirst();
            do{
                int potHoleId = Integer.parseInt(cursor.getString(0));
                double latitude = Double.parseDouble(cursor.getString(1));
                double longitude = Double.parseDouble(cursor.getString(2));
                String locName = cursor.getString(3);
                int numSpotted = Integer.parseInt(cursor.getString(4));

                //compress the data into the object
                potHole pothole = new potHole(potHoleId, numSpotted,latitude, longitude, locName);

                //add the new pothole to the array list
                potholes.add(pothole);

                //advance the cursor position
                cursor.moveToNext();
            }while(!cursor.isAfterLast());
        }catch(Exception e){
            Log.d("error", "no potholes in list");
        }
        Log.d("DatabaseAccess", "getAllPotholes: num: "+potholes.size());
        //ensure the number of entries is correctly recorded
        numEntries = potholes.size();
        return potholes;
    }
}
