package mis3.musicplayer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import java.lang.reflect.Array;
import java.util.Arrays;


//

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    String statusString = "Standing";
    Button startJogging;
    Button startCycling;
    Button stopAll;
    TextView status;
    TextView credits;

    private SensorManager mSensorManager;
    private Sensor mSensor;


    private long curTime;
    private long lastUpdate = 0;
    private int timeChanged = 0; //Counter used to display graphs

    private double[] xArray; // Array that holds m -> real part of fft
    private double[] yArray; // irreal part of fft
    private double[] array;  // array that holds our fft data

    private int arrayCounter = 0;
    private int SCALE_FACTOR = 20;
    private int FFT_WINDOW_SIZE = 64; // initial FFT Window Size
    private int FFT_SAMPLE_RATE = 1; // initial FFT Sample Rate

    private float speed;
    private double threshold;

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
                speed = location.getSpeed();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                credits.setText("" + speed + ", " + latitude + ", " + longitude);


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
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }

            }
        });
        startJogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        startCycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });


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
                    double average = calculateArrayAverage(array);
                    if (average > 1 && average < 4) { //ToDo add speed for cycling

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
                    } else if (average > 4) {
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
