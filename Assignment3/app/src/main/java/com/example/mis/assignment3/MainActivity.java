package com.example.mis.assignment3;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private static final int SHAKE_THRESHOLD = 600;
    DrawGraph ourGraph;
    private TextView textView;
    private Button startStop;
    private boolean started = false;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long curTime;
    private long lastUpdate = 0;
    private int timeChanged = 0;
    private double[] xArray;
    private double[] yArray;
    private double[] array;
    private int arrayCounter = 0;
    private int SCALE_FACTOR = 20;
    private int FFT_WINDOW = 64;
    private FFT mFFT;
    private DrawFFT ourFFT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        startStop = (Button) findViewById(R.id.startStop);

        ourGraph = (DrawGraph) findViewById(R.id.drawGraph);
        ourGraph.setBackgroundColor(Color.GRAY);

        ourFFT = (DrawFFT) findViewById(R.id.drawFFT);
        ourFFT.setBackgroundColor(Color.GRAY);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mFFT = new FFT(2 * (int) FFT_WINDOW);

        array = new double[2 * FFT_WINDOW];
        Arrays.fill(array, 0);

        xArray = new double[2 * FFT_WINDOW];
        yArray = new double[2 * FFT_WINDOW];
        Arrays.fill(yArray, 0);


       /* startStop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (started == true){
                    started = false;
                    startStop.setText("Start");

                }
                else  {
                    started = true;
                    startStop.setText("Stop");
                }
            }
        });*/
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

            timeChanged = timeChanged + 2;

            //Drawing of untransformed acceleration data
            ourGraph.addPointX(timeChanged, x * SCALE_FACTOR + ourGraph.getHeight() / 2 - 100);
            ourGraph.addPointY(timeChanged, y * SCALE_FACTOR + ourGraph.getHeight() / 2 - 100);
            ourGraph.addPointZ(timeChanged, z * SCALE_FACTOR + ourGraph.getHeight() / 2 - 100);
            ourGraph.addPointM(timeChanged, (float) m * SCALE_FACTOR + ourGraph.getHeight() / 2 - 100);
            ourGraph.invalidate();

            if (arrayCounter < 2 * FFT_WINDOW) {
                xArray[arrayCounter] = m;
                arrayCounter = arrayCounter + 1;
                startStop.setText(""+arrayCounter);

            } else if (arrayCounter == 2 * FFT_WINDOW) {
                //float lastX = (float) array[array.length-1];
                array = fftCalculator(xArray, yArray);
                ourFFT.setPoints(array);
                ourFFT.invalidate();
                arrayCounter = 0;
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
    public double[] fftCalculator(double[] x, double[] y){
        if (x.length != y.length) return null;
        FFT fft = new FFT(x.length);
        fft.fft(x, y);
        double[] fftMag = new double[x.length];
        for (int i = 1; i<x.length; i++){
            fftMag[i] = Math.sqrt(Math.pow(x[i], 2) + Math.pow(x[i], 2));
        }
        return fftMag;
    }

}
