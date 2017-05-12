package com.example.mis.assignment3;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView textView;
    private Button startStop;

    private boolean started = false;

    private SensorManager mSensorManager;
    private Sensor mSensor;

//    private DrawGraph drawGraphs;

    private Canvas canvas = new Canvas();

    private  long curTime;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z, last_m;
    private Path pathX, pathY, pathZ, pathM;

    private static final int SHAKE_THRESHOLD = 600;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        startStop = (Button) findViewById(R.id.startStop);
        SurfaceView surface = (SurfaceView) findViewById(R.id.surface);
        surface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                pathM = new Path();
                pathX = new Path();
                pathY = new Path();
                pathZ = new Path();

                pathX.moveTo(0, 0);

                Paint paintM = new Paint();
                Paint paintX = new Paint();
                Paint paintY = new Paint();
                Paint paintZ = new Paint();


                paintM.setColor(Color.WHITE);
                paintX.setColor(Color.RED);
                paintY.setColor(Color.GREEN);
                paintZ.setColor(Color.BLUE);

                pathM.lineTo(curTime, last_m);
                pathX.lineTo(curTime, last_x);
                pathY.lineTo(curTime, last_y);
                pathZ.lineTo(curTime, last_z);

                pathX.close();

                Canvas canvas = holder.lockCanvas();
                canvas.drawPath(pathX, paintX);
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        startStop.setOnClickListener(new View.OnClickListener(){
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
        });
        mSensorManager.registerListener(this, mSensor, mSensorManager.SENSOR_DELAY_NORMAL);

    }

  /* public class DrawGraph extends View{

       private Path pathX, pathY, pathZ, pathM;

       public DrawGraph(Context context) {
            super(context);
           pathM = new Path();
           pathX = new Path();
           pathY = new Path();
           pathZ = new Path();

        }

        @Override
        protected void onDraw(Canvas canvas){
            super.onDraw(canvas);

            Paint paintM = new Paint();
            Paint paintX = new Paint();
            Paint paintY = new Paint();
            Paint paintZ = new Paint();


            paintM.setColor(Color.WHITE);
           paintX.setColor(Color.RED);
            paintY.setColor(Color.GREEN);
            paintZ.setColor(Color.BLUE);

            pathM.lineTo(curTime, last_m);
            pathX.lineTo(curTime, last_x);
            pathY.lineTo(curTime, last_y);
            pathZ.lineTo(curTime, last_z);


          //  canvas.drawPath(pathM, paintM);
           // canvas.drawPath(pathX, paintX);
           // canvas.drawPath(pathY, paintY);
           // canvas.drawPath(pathZ, paintZ);


        }


    }*/

    // Code inspired by: https://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125

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

            if ((curTime -lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                speed = Math.abs(x + y + z - last_x - last_y - last_z)/diffTime *10000;

                if (speed > SHAKE_THRESHOLD) {

                }

            //    drawGraphs.draw(canvas);

                textView.setText("X: " + x + ", Y: " + y + ", Z: " + z + ", Speed: " + speed);

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
