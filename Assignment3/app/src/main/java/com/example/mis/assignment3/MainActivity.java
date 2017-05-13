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

public class MainActivity extends AppCompatActivity implements SensorEventListener{


    private static final int SHAKE_THRESHOLD = 600;
    private TextView textView;
    private Button startStop;
    private boolean started = false;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private  long curTime;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z, last_m;
    private int timeChanged = 0;

    private int SCALE_FACTOR = 50;

    DrawGraph ourGraph;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ourGraph = (DrawGraph) findViewById(R.id.drawGraph);
        ourGraph.setBackgroundColor(Color.DKGRAY);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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
        mSensorManager.registerListener(this, mSensor, mSensorManager.SENSOR_DELAY_NORMAL);

    }

    // Code for accelerometer inspired by: https://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float m = 0;

            float speed = 0;

            curTime = System.currentTimeMillis();

            timeChanged = timeChanged + 2;

            ourGraph.addPointX(timeChanged, x*SCALE_FACTOR + ourGraph.getHeight()/2);
            ourGraph.addPointY(timeChanged, y*SCALE_FACTOR + ourGraph.getHeight()/2);
            ourGraph.addPointZ(timeChanged, z*SCALE_FACTOR + ourGraph.getHeight()/2);
            ourGraph.addPointM(timeChanged, m*SCALE_FACTOR + ourGraph.getHeight()/2);

            ourGraph.invalidate();

         //   setContentView(ourGraph);

            if ((curTime -lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                speed = Math.abs(x + y + z - last_x - last_y - last_z)/diffTime *10000;

                if (speed > SHAKE_THRESHOLD) {

                }





            last_x = x;
            last_y = y;
            last_z = z;

            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, mSensorManager.SENSOR_DELAY_NORMAL);
    }
}
