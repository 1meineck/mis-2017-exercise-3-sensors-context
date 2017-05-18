package mis3.musicplayer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


//

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    String statusString = "Standing";
    TextView status;
    TextView credits;

    private MediaPlayer mediaPlayer;


    private SensorManager mSensorManager;
    private Sensor mSensor;


    private long curTime;
    private long lastUpdate = 0;
    private int timeChanged = 0; //Counter used to display graphs

    private double[] xArray; // Array that holds m -> real part of fft
    private double[] yArray; // irreal part of fft
    private double[] array;  // array that holds our fft data

    private int arrayCounter = 0;
    private int FFT_WINDOW_SIZE = 64; // initial FFT Window Size
    private int FFT_SAMPLE_RATE = 1; // initial FFT Sample Rate
    private float CYCLING_MIN = 2;
    private float CYCLING_MAX = 8;
    private float JOGGING_MIN = 8;
    private float JOGGING_MAX =100;

    private float speed; // speed in m/s

    Context context;

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 42;

    // Music for Jogging - "Goodbye to Spring" by Josh Woodward. Free download: http://joshwoodward.com/
    // published under Creative Commons 4.0 Attribution
    String creditsJoggingString = "Music - \"Goodbye to Spring\" by Josh Woodward. \nl Free download: http://joshwoodward.com/ \nl published under Creative Commons 4.0 Attribution";

    // Music for Cycling - "Go" by Josh Woodward. Free download: http://joshwoodward.com/
    // published under Creative Commons 4.0 Attribution
    String creditsCyclingString = "Music - \"Go\" by Josh Woodward. \nl Free download: http://joshwoodward.com/ \nl published under Creative Commons 4.0 Attribution";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                speed = location.getSpeed();
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

        // Everything for the permissions was inspired by:
        // https://developer.android.com/training/permissions/requesting.html
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);


        } else {
            Toast.makeText(this, "Location Permission accepted", Toast.LENGTH_SHORT).show();


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);


        }


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        array = new double[FFT_WINDOW_SIZE];
        Arrays.fill(array, 0);

        xArray = new double[FFT_WINDOW_SIZE];
        yArray = new double[FFT_WINDOW_SIZE];
        Arrays.fill(yArray, 0);


        context = this;


        status = (TextView) findViewById(R.id.status);
        credits = (TextView) findViewById(R.id.credits);


        mSensorManager.registerListener(this, mSensor, mSensorManager.SENSOR_DELAY_FASTEST);

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


                } else {

                    Toast.makeText(this, "Location Permission denied", Toast.LENGTH_SHORT).show();

                }
                return;

            }

        }

    }

    // Code for accelerometer inspired by: https://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            double m = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0));

            float speed = 0;

            curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > FFT_SAMPLE_RATE) {

                lastUpdate = curTime;
                timeChanged = timeChanged + 1;

                if (arrayCounter < FFT_WINDOW_SIZE) {
                    xArray[arrayCounter] = m;
                    arrayCounter = arrayCounter + 1;

                } else if (arrayCounter == FFT_WINDOW_SIZE) {

                    array = fftCalculator(xArray, yArray);

                    // We decided to use an average of the values in our fft array to compare to our threshold because it is more robust to stray values.
                    double average = calculateArrayAverage(array);

                    //Speed must be smaller than 8m/s for the music player to start playing, to make sure it is only used for jogging or cycling
                    if (speed < 8) {
                        // We wanted to add a speed constraint for cycling, however, since we could not test it, we decided to leave it out for now
                        // Cycling needs a lower threshold than jogging, for the average movement while cycling is lower than when jogging.
                        // Without the speed constraint, we decided to make them mutually exclusive. This should be adapted, when speed can be tested and it works.
                        //Cycling:
                        if (average > CYCLING_MIN && average < CYCLING_MAX) {

                            if (statusString == "Standing") {
                                statusString = "Cycling";
                                status.setText(statusString);
                                credits.setText(creditsJoggingString);

                                mediaPlayer = MediaPlayer.create(context, R.raw.go);
                                mediaPlayer.setLooping(true);
                                mediaPlayer.start();
                            } else if (statusString == "Jogging") {
                                mediaPlayer.release();
                                statusString = "Cycling";
                                status.setText(statusString);
                                credits.setText(creditsCyclingString);

                                mediaPlayer = MediaPlayer.create(context, R.raw.go);
                                mediaPlayer.setLooping(true);
                                mediaPlayer.start();
                            }
                            // see above
                            //Jogging:
                        } else if (average > JOGGING_MIN && average < JOGGING_MAX) {
                            if (statusString == "Standing") {
                                statusString = "Jogging";
                                status.setText(statusString);
                                credits.setText(creditsJoggingString);

                                mediaPlayer = MediaPlayer.create(context, R.raw.goodbyetospring);
                                mediaPlayer.setLooping(true);
                                mediaPlayer.start();
                            } else if (statusString == "Cycling") {
                                mediaPlayer.release();
                                statusString = "Jogging";
                                status.setText(statusString);
                                credits.setText(creditsJoggingString);

                                mediaPlayer = MediaPlayer.create(context, R.raw.goodbyetospring);
                                mediaPlayer.setLooping(true);
                                mediaPlayer.start();
                            }
                            //Different things than jogging or cycling
                        } else {
                            statusString = "Standing";
                            status.setText(statusString);
                            credits.setText("");
                            if (mediaPlayer != null) {
                                mediaPlayer.release();
                            }

                        }


                        Arrays.fill(xArray, 0);
                        Arrays.fill(array, 0);
                        Arrays.fill(yArray, 0);
                        arrayCounter = 0;
                    }
                }
            }


        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // inspired by J Wang(Nov 4 '15) https://stackoverflow.com/questions/9272232/fft-library-in-android-sdk)
    public double[] fftCalculator(double[] x, double[] y) {
        if (x.length != y.length) return null;
        FFT fft = new FFT(x.length);
        fft.fft(x, y);
        double[] fftMag = new double[x.length];
        for (int i = 1; i < x.length; i++) {
            fftMag[i] = Math.sqrt(Math.pow(x[i], 2) + Math.pow(y[i], 2));
        }
        return fftMag;
    }

    private double calculateArrayAverage(double[] x) {
        double sum = 0;
        for (int i = 1; i < x.length; i++) {
            sum += x[i];
        }
        double average = sum / (x.length - 1);
        return average;
    }


}
