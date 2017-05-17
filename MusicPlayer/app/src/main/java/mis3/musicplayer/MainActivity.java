package mis3.musicplayer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


//

public class MainActivity extends AppCompatActivity {

    String statusString = "Standing";
    Button startJogging;
    Button startCycling;
    Button stopAll;
    TextView status;
    TextView credits;

    Context context;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 42;

    // Music for Jogging - "Goodbye to Spring" by Josh Woodward. Free download: http://joshwoodward.com/
    // published under Creative Commons 4.0 Attribution
    String creditsJoggingString = "Music - \"Goodbye to Spring\" by Josh Woodward. \nl Free download: http://joshwoodward.com/ \nl published under Creative Commons 4.0 Attribution";

    // Music for Cycling - "Go" by Josh Woodward. Free download: http://joshwoodward.com/
    // published under Creative Commons 4.0 Attribution
    String creditsCyclingString = "Music - \"Go\" by Josh Woodward. \nl Free download: http://joshwoodward.com/ \nl published under Creative Commons 4.0 Attribution";

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

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

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);


        }else {
            Toast.makeText(this, "Location Permission accepted", Toast.LENGTH_SHORT).show();
            //ToDo: Location stuff
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);



        }



        context = this;

        startJogging = (Button) findViewById(R.id.startJogging);
        startCycling = (Button) findViewById(R.id.startCycling);
        stopAll = (Button) findViewById(R.id.stopAll);

        status = (TextView) findViewById(R.id.status);
        credits = (TextView) findViewById(R.id.credits);

        stopAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusString = "Standing";
                status.setText(statusString);
                credits.setText("");
                mediaPlayer.release();

            }
        });
        startJogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusString =="Standing"){
                    statusString = "Jogging";
                    status.setText(statusString);
                    credits.setText(creditsJoggingString);

                    mediaPlayer = MediaPlayer.create(context, R.raw.goodbyetospring);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                else if (statusString =="Cycling"){
                    mediaPlayer.release();
                    statusString = "Jogging";
                    status.setText(statusString);
                    credits.setText(creditsJoggingString);

                    mediaPlayer = MediaPlayer.create(context, R.raw.goodbyetospring);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
            }
        });

        startCycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusString =="Standing"){
                    statusString = "Cycling";
                    status.setText(statusString);
                    credits.setText(creditsJoggingString);

                    mediaPlayer = MediaPlayer.create(context, R.raw.go);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                else if (statusString =="Jogging"){
                    mediaPlayer.release();
                    statusString = "Cycling";
                    status.setText(statusString);
                    credits.setText(creditsCyclingString);

                    mediaPlayer = MediaPlayer.create(context, R.raw.go);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
            }
        });



    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Location Permission accepted", Toast.LENGTH_SHORT).show();
                    //ToDo location stuff

                } else {

                    Toast.makeText(this, "Location Permission denied", Toast.LENGTH_SHORT).show();


                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
