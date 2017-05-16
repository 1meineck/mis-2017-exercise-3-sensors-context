package com.example.mis.assignment3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by annika on 16.05.17.
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
        paintFFT.setStrokeWidth(2);

    }

    public void setPoints (double[] n){
     //   pathFFT.moveTo(y-1, x);
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
