package com.example.vizzylearning.Tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.vizzylearning.MainActivity;
import com.example.vizzylearning.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by rafael on 27-07-2016.
 */
public class AccSensor implements SensorEventListener {
    private boolean outOfBoundsPrev=true;
    private static double LIMIT=8;
    private static byte MODE=1;
    public double velocity=0, turn=0;
    public boolean registered=false;
    private Context context;

    public AccSensor(Context context){
        this.context=context;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] accValues=event.values;
        velocity=accValues[2]/LIMIT;
        turn=accValues[1]/LIMIT;
        boolean outOfBounds=false;
        if(accValues[1]>LIMIT || accValues[1]<-LIMIT || accValues[2]>LIMIT || accValues[2]<-LIMIT) {
            outOfBounds = true;
            velocity=0;
            turn=0;
        }
        if(!outOfBounds)
            MainActivity.mControllerNode.sendMovement(velocity,turn,MODE);
        else
            if(!outOfBoundsPrev) {
                MainActivity.mControllerNode.sendMovement(velocity,turn,MODE);
                MainActivity.mVizzyTalk.talk("Controller is out of bounds");
                MainActivity.mVizzyTalk.vibrate(200);
            }
        outOfBoundsPrev = outOfBounds;
        drawControllers();
    }

    public void unRegister() {
        MainActivity.mSensorManager.unregisterListener(this);
        registered=false;
    }

    public void register() {
        Sensor mAccelerometer = MainActivity.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        MainActivity.mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        registered=true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void drawControllers(){
        Bitmap mutableBitmap = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);
        Canvas canvasW = new Canvas(mutableBitmap);
        Bitmap bitmapWheel = BitmapFactory.decodeResource(context.getResources(), R.mipmap.wheel);
        float rotation = (float) (90 * MainActivity.mAccSensor.turn);
        int imagSize = mutableBitmap.getHeight() / 4;
        int offset = imagSize / 5;
        int[] imagPos = {offset, mutableBitmap.getHeight() - imagSize - offset};
        canvasW.rotate(rotation, imagPos[0] + imagSize / 2, imagPos[1] + imagSize / 2);
        Rect rect = new Rect(imagPos[0], imagPos[1], imagPos[0] + imagSize, imagPos[1] + imagSize);
        canvasW.drawBitmap(bitmapWheel, null, rect, null);
        Canvas canvasS = new Canvas(mutableBitmap);
        Bitmap bitmapSpeed = BitmapFactory.decodeResource(context.getResources(), R.mipmap.speedometer);
        imagPos[0] = mutableBitmap.getWidth() - imagSize - offset;
        rect = new Rect(imagPos[0], imagPos[1], imagPos[0] + imagSize, imagPos[1] + imagSize);
        canvasS.drawBitmap(bitmapSpeed, null, rect, null);
        Canvas canvasN = new Canvas(mutableBitmap);
        Bitmap bitmapNail = BitmapFactory.decodeResource(context.getResources(), R.mipmap.needle);
        imagPos[0] = (int) (imagPos[0] + imagSize * 0.2);
        imagPos[1] = (int) (imagPos[1] + imagSize * 0.2);
        imagSize = (int) (imagSize * 0.6);
        rect = new Rect(imagPos[0], imagPos[1], imagPos[0] + imagSize, imagPos[1] + imagSize);
        rotation = (float) (180 * MainActivity.mAccSensor.velocity);
        canvasN.rotate(rotation, imagPos[0] + imagSize / 2, imagPos[1] + imagSize / 2);
        canvasN.drawBitmap(bitmapNail, null, rect, null);
        MainActivity.imageView2.setImageBitmap(mutableBitmap);
    }
}
