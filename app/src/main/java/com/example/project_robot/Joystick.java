package com.example.project_robot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Daniel on 7/25/2016.
 */
public class Joystick extends View implements View.OnTouchListener {

    private boolean Init=true;
    private float drawX;
    private float drawY;
    private float percentageX;
    private float percentageY;
    private float centerX;
    private float centerY;
    private float bigRadius;
    private float smallRadius;
    private final JoystickListener JListener;
    private Canvas Surface;

    public float getY() {
        return percentageY;
    }

    public float getX() {
        return percentageX;
    }

    public Joystick(Context context, AttributeSet attributes) {
        super(context, attributes);
        setOnTouchListener(this);
        JListener = (JoystickListener) context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Surface=canvas;
        if(Init){
            centerX = getWidth() / 2;
            centerY = getHeight() / 2;
            bigRadius = Math.min(getWidth(), getHeight()) / 3;
            smallRadius = Math.min(getWidth(), getHeight()) / 5;
            drawX=centerX;
            drawY=centerY;
            Init=false;
        }
        drawJoystick(drawX, drawY);
    }

    private void drawJoystick(float newX, float newY) {
        Paint colors = new Paint();
        Surface.drawColor(Color.TRANSPARENT); // Clear the BG

        colors.setARGB(255, 100, 100, 100);
        Surface.drawCircle(centerX, centerY, bigRadius, colors);

        colors.setARGB(255, 50, 50, 50);
        Surface.drawCircle(newX, newY, smallRadius, colors);
    }

    public boolean onTouch(View v, MotionEvent e) {
        if (e.getAction() != e.ACTION_UP) {
            float displacement = (float) Math.sqrt((Math.pow(e.getX() - centerX, 2)) + Math.pow(e.getY() - centerY, 2));
            if (displacement < bigRadius) {
                percentageX = (e.getX() - centerX) / bigRadius;
                percentageY = -(e.getY() - centerY) / bigRadius;
                drawX=e.getX();
                drawY=e.getY();
            } else {
                float ratio = bigRadius / displacement;
                float constrainedX = centerX + (e.getX() - centerX) * ratio;
                float constrainedY = centerY + (e.getY() - centerY) * ratio;
                percentageX = (constrainedX - centerX) / bigRadius;
                percentageY = -(constrainedY - centerY) / bigRadius;
                drawX=constrainedX;
                drawY=constrainedY;
            }
        } else {
            percentageX = 0;
            percentageY = 0;
            drawX=centerX;
            drawY=centerY;
        }
        invalidate();
        JListener.onJoystickMoved();
        return true;
    }

    public interface JoystickListener {
        void onJoystickMoved();
    }
}
