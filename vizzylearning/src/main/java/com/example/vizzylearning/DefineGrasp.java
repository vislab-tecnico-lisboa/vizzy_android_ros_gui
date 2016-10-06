package com.example.vizzylearning;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.vizzylearning.Tools.OpenGLObject.MyGLSurfaceView;

import java.io.FileInputStream;

public class DefineGrasp extends AppCompatActivity {
    public static TextView boxVal,posVal,rotVal;
    public static int function=0;
    private float X0=0,Y0=0,X1=639,Y1=479,pX=320,pY=240,rotX=0,rotY=0,rotZ=1;
    private ImageView mImageView2;
    private ToggleButton mButton1,mButton2,mButton3,mButton4;
    private SeekBar mSeekBar;
    private float mPreviousX = 0, mPreviousY = 0;
    private static final float TOUCH_SCALE_FACTOR = 180.0f / 480;
    private static final int REQUEST_CODE = 10;
    private static final double SCALE=0.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_define_grasp);

        Bitmap bmp = null;
        String filename = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE);
        try {
            FileInputStream inputStream = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageView mImageView1 = (ImageView)findViewById(R.id.Image1);
        mImageView1.setImageBitmap(bmp);

        boxVal=(TextView) findViewById(R.id.boxVal);
        posVal=(TextView) findViewById(R.id.posVal);
        rotVal=(TextView) findViewById(R.id.rotVal);

        final MyGLSurfaceView myGLSurfaceView = (MyGLSurfaceView) findViewById(R.id.GLSurface);

        mButton1 =(ToggleButton) findViewById(R.id.toggleButton1);
        mButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mButton1.isChecked()) {
                    unchecked();
                    function = 1;
                }
            }
        });

        mButton2 =(ToggleButton) findViewById(R.id.toggleButton2);
        mButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mButton2.isChecked()) {
                    unchecked();
                    function = 2;
                }
            }
        });

        mButton3 =(ToggleButton) findViewById(R.id.toggleButton3);
        mButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mButton3.isChecked()) {
                    unchecked();
                    function=3;
                    mSeekBar.setVisibility(View.VISIBLE);
                }
            }
        });

        mButton4 =(ToggleButton) findViewById(R.id.toggleButton4);
        mButton4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mButton4.isChecked()) {
                    unchecked();
                    function = 4;
                    Intent intent = new Intent();
                    float[] q = myGLSurfaceView.mRenderer.getQuaternion();
                    intent.putExtra("RESULT", new float[]{X0,Y0,X1,Y1,pX,pY,q[0],q[1],q[2],q[3]});
                    setResult(REQUEST_CODE,intent);
                    finish();
                }
            }
        });

        mImageView2 = (ImageView)findViewById(R.id.Image2);
        mImageView2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eventID= MotionEventCompat.getActionMasked(event);
                switch (function) {
                    case 1:
                        if (eventID==MotionEvent.ACTION_DOWN) {
                            X0 = (short)(event.getX()*SCALE);
                            Y0 = (short)(event.getY()*SCALE);
                        }
                        else if(eventID==MotionEvent.ACTION_MOVE) {
                            X1 = (short)(event.getX()*SCALE);
                            Y1 = (short)(event.getY()*SCALE);
                            drawBox();
                            boxVal.setText(X0 + ", " + Y0 + ", " + X1 + ", " + Y1);
                        }
                        if (eventID==MotionEvent.ACTION_UP) {
                            pX=limit(pX,X0,X1);
                            pY=limit(pY,Y0,Y1);
                            myGLSurfaceView.mRenderer.setPosition((short)pX,(short)pY);
                            myGLSurfaceView.requestRender();
                        }
                        mPreviousX = 0;
                        mPreviousY = 0;
                        break;
                    case 2:
                        if (eventID==MotionEvent.ACTION_DOWN || eventID==MotionEvent.ACTION_MOVE) {
                            pX=limit((short)(event.getX()*SCALE),X0,X1);
                            pY=limit((short)(event.getY()*SCALE),Y0,Y1);
                            myGLSurfaceView.mRenderer.setPosition((short)pX,(short)pY);
                            myGLSurfaceView.requestRender();
                            posVal.setText(pX + ", " + pY);
                        }
                        mPreviousX = 0;
                        mPreviousY = 0;
                        break;
                    case 3:
                        float scaledX = (float)(event.getY()*SCALE) * TOUCH_SCALE_FACTOR;
                        float scaledY = (float)(event.getX()*SCALE) * TOUCH_SCALE_FACTOR;
                        if(eventID== MotionEvent.ACTION_MOVE){
                            myGLSurfaceView.mRenderer.addDeltaAngleX(scaledX - mPreviousX);
                            myGLSurfaceView.mRenderer.addDeltaAngleY(scaledY - mPreviousY);
                            rotX=(int)myGLSurfaceView.mRenderer.getAngleX();
                            rotY=(int)myGLSurfaceView.mRenderer.getAngleY();
                            rotVal.setText(rotX + ", " + rotY + ", " +rotZ);
                        }
                        myGLSurfaceView.requestRender();
                        mPreviousX = scaledX;
                        mPreviousY = scaledY;
                        break;
                }
                return true;
            }
        });

        mSeekBar=(SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                myGLSurfaceView.mRenderer.setAngleZ(progress-180);
                rotZ=(int)myGLSurfaceView.mRenderer.getAngleZ();
                rotVal.setText(rotX + ", " + rotY + ", " +rotZ);
                myGLSurfaceView.requestRender();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        drawBox();
    }

    public void drawBox(){
        Bitmap bmp = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawRect(X0, Y0, X1, Y1, paint);
        mImageView2.setImageBitmap(bmp);
    }

    private void unchecked(){
        switch (function){
            case 1:
                mButton1.setChecked(false);
                break;
            case 2:
                mButton2.setChecked(false);
                break;
            case 3:
                mButton3.setChecked(false);
                mSeekBar.setVisibility(View.INVISIBLE);
                break;
            case 4:
                mButton4.setChecked(false);
                break;
        }
    }

    private short limit(short value, short min, short max){
        short out=value;
        if(value<min)
            out=min;
        if(value>max)
            out=max;
        return out;
    }

    private float limit(float value, float min, float max){
        float out=value;
        if(value<min)
            out=min;
        if(value>max)
            out=max;
        return out;
    }
}
