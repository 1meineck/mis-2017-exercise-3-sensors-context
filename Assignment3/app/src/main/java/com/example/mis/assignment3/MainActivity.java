package com.example.mis.assignment3;


import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    //  View that draws our raw accelerometer data (x, y, z, m)
    private DrawGraph ourGraph;
    // View that draws our fft data
    private DrawFFT ourFFT;

    private Button startStop; //ToDo: delete Button! entirely (main & xml)

    // SeekBar to set the fft window size and sample rate
    private SeekBar seekBarWindowSize;
    private SeekBar seekBarSampleRate;
    private TextView textWindowSize;
    private TextView textSampleRate;

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
    private int FFT_WINDOW_SIZE = 16; // initial FFT Window Size
    private int FFT_SAMPLE_RATE = 100; // initial FFT Sample Rate


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        startStop = (Button) findViewById(R.id.startStop); // ToDo delete

        // Instantiating everything
        seekBarSampleRate = (SeekBar) findViewById(R.id.seekBarSampleRate);
        seekBarSampleRate.setMax(99);

        seekBarWindowSize = (SeekBar) findViewById(R.id.seekBarWindowSize);
        seekBarWindowSize.setMax(6);

        textSampleRate = (TextView) findViewById(R.id.textSampleRate);
        textSampleRate.setText("FFT Sample Rate: " + FFT_SAMPLE_RATE);

        textWindowSize = (TextView) findViewById(R.id.textWindowSize);
        textWindowSize.setText("FFT Window Size: " + FFT_WINDOW_SIZE);

        curTime = System.currentTimeMillis();

        ourGraph = (DrawGraph) findViewById(R.id.drawGraph);
        ourGraph.setBackgroundColor(Color.GRAY);

        ourFFT = (DrawFFT) findViewById(R.id.drawFFT);
        ourFFT.setBackgroundColor(Color.GRAY);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        array = new double[FFT_WINDOW_SIZE];
        Arrays.fill(array, 0);

        xArray = new double[FFT_WINDOW_SIZE];
        yArray = new double[FFT_WINDOW_SIZE];
        Arrays.fill(yArray, 0);


        // Listener for Window Size Listener
        seekBarWindowSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    arrayCounter = 0;
                    FFT_WINDOW_SIZE = 16;
                    textWindowSize.setText("FFT Window Size: " + FFT_WINDOW_SIZE);

                } else if (progress == 1) {
                    arrayCounter = 0;
                    FFT_WINDOW_SIZE = 32;
                    textWindowSize.setText("FFT Window Size: " + FFT_WINDOW_SIZE);
                } else if (progress == 2) {
                    arrayCounter = 0;
                    FFT_WINDOW_SIZE = 64;
                    textWindowSize.setText("FFT Window Size: " + FFT_WINDOW_SIZE);
                } else if (progress == 3) {
                    arrayCounter = 0;
                    FFT_WINDOW_SIZE = 256;
                    textWindowSize.setText("FFT Window Size: " + FFT_WINDOW_SIZE);
                } else if (progress == 4) {
                    arrayCounter = 0;
                    FFT_WINDOW_SIZE = 512;
                    textWindowSize.setText("FFT Window Size: " + FFT_WINDOW_SIZE);
                } else {
                    arrayCounter = 0;
                    FFT_WINDOW_SIZE = 1024;
                    textWindowSize.setText("FFT Window Size: " + FFT_WINDOW_SIZE);
                }


                array = new double[FFT_WINDOW_SIZE];
                Arrays.fill(array, 0);

                xArray = new double[FFT_WINDOW_SIZE];
                yArray = new double[FFT_WINDOW_SIZE];
                Arrays.fill(yArray, 0);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Listener for FFT Sample Rate SeekBar
        seekBarSampleRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                FFT_SAMPLE_RATE = 100 - progress;
                textSampleRate.setText("FFT Sample Rate: " + FFT_SAMPLE_RATE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSensorManager.registerListener(this, mSensor, mSensorManager.SENSOR_DELAY_FASTEST);

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

                //Drawing of untransformed acceleration data
                ourGraph.addPointX(timeChanged, x * SCALE_FACTOR + ourGraph.getHeight() / 2 - 100);
                ourGraph.addPointY(timeChanged, y * SCALE_FACTOR + ourGraph.getHeight() / 2 - 100);
                ourGraph.addPointZ(timeChanged, z * SCALE_FACTOR + ourGraph.getHeight() / 2 - 100);
                ourGraph.addPointM(timeChanged, (float) m * SCALE_FACTOR + ourGraph.getHeight() / 2 - 100);
                ourGraph.invalidate();

                if (arrayCounter < FFT_WINDOW_SIZE) {
                    xArray[arrayCounter] = m;
                    arrayCounter = arrayCounter + 1;
                    startStop.setText("" + arrayCounter);

                } else if (arrayCounter == FFT_WINDOW_SIZE) {
                    array = fftCalculator(xArray, yArray);
                    ourFFT.setPoints(array);
                    ourFFT.invalidate();
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

    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, mSensorManager.SENSOR_DELAY_NORMAL);
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

}
