package com.example.mis.assignment3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by annika on 13.05.17.
 */



public class DrawGraph extends View {
    Path pathX = new Path();
    Path pathY = new Path();
    Path pathZ = new Path();
    Path pathM = new Path();

    Paint paintM = new Paint();
    Paint paintX = new Paint();
    Paint paintY = new Paint();
    Paint paintZ = new Paint();

    public DrawGraph(Context context) {
        super(context);
        init(context);
    }

    public DrawGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawGraph(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        pathM.moveTo(400, 400);
        pathX.moveTo(800, 800);
        pathY.moveTo(800, 800);
        pathZ.moveTo(800, 800);

        pathM.lineTo(800, 800);


        paintM.setColor(Color.WHITE);
        paintX.setColor(Color.RED);
        paintY.setColor(Color.GREEN);
        paintZ.setColor(Color.BLUE);

        paintM.setStyle(Paint.Style.STROKE);
        paintX.setStyle(Paint.Style.STROKE);
        paintY.setStyle(Paint.Style.STROKE);
        paintZ.setStyle(Paint.Style.STROKE);

        paintM.setStrokeWidth(2);
        paintX.setStrokeWidth(2);
        paintY.setStrokeWidth(2);
        paintZ.setStrokeWidth(2);

    }

    public void addPointX(float x, float y){
        pathX.lineTo(x, y);
    }
    public void addPointY(float x, float y){
        pathY.lineTo(x, y);
    }
    public void addPointZ(float x, float y){
        pathZ.lineTo(x, y);
    }
    public void addPointM(float x, float y){
        pathM.lineTo(x, y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(pathX, paintX);
        canvas.drawPath(pathY, paintY);
        canvas.drawPath(pathZ, paintZ);
        canvas.drawPath(pathM, paintM);

    }
}
