package com.example.start.finalproject2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    /** Declaration of variables */
    ProgressDialog pDialog;
    VideoView videoview;
    String VideoURL = "";

    /** Intent for the next activity */

    private void Continue (){
        Button about_button=(Button) findViewById(R.id.button);
        about_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }

        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        videoview = (VideoView) findViewById(R.id.videoView);

        // progress bar

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();


        /** Control features for the video player */


        try{
            MediaController mediacontroller = new MediaController(MainActivity.this);
            mediacontroller.setAnchorView(videoview);
            Uri video = Uri.parse(VideoURL);
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);
        }

        catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                videoview.start();
            }
        });

        /** Fetching Uri from Internet resources */

        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setMediaController(new android.widget.MediaController(MainActivity.this));
        Uri video = Uri.parse("http://www.androidbegin.com/tutorial/AndroidCommercial.3gp");
        videoView.setVideoURI(video);
        videoView.start();

        Continue();

    }


}
