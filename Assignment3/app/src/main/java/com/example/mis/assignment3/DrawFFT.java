package com.example.mis.assignment3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 *  This class visualizes the FFT transformed magnitude of the accelerometer data on a canvas
 */

public class DrawFFT extends View {

    public Path pathFFT = new Path();
    public Paint paintFFT = new Paint();
    private int y;

    public DrawFFT(Context context) {
        super(context);
        init(context);
    }

    public DrawFFT(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawFFT(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        paintFFT.setColor(Color.WHITE);
        paintFFT.setStyle(Paint.Style.STROKE);
        paintFFT.setStrokeWidth(1);

    }

    public void setPoints (double[] n){
     for(int i = 0; i<n.length-1; i++ ){
         float v = (float) n[i]+200;
         pathFFT.lineTo(y, v);
         y=y+1;
     }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(pathFFT, paintFFT);


    }
}
