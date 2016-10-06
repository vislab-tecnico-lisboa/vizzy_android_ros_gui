package com.example.vizzylearning.Tools.OpenGLObject;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGLSurfaceView extends GLSurfaceView {

    public MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);
        initView();
    }

    public MyGLSurfaceView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        initView();
    }

    private void initView(){
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8,8,8,8,16,0);
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
    }
}