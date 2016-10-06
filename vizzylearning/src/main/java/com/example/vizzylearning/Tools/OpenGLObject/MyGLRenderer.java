package com.example.vizzylearning.Tools.OpenGLObject;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Polygon mPolygon;
    private float[] mRotationMatrixX = new float[16];
    private float[] mRotationMatrixY = new float[16];
    private float[] mRotationMatrixZ = new float[16];
    private float[] mRotationMatrix = new float[16];
    private float[] mTranslationMatrix = new float[]{1f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f};
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private static double RADIANS_TO_DEGREES=180.0/3.1416;

    public MyGLRenderer(){
        Matrix.setRotateM(mRotationMatrixX, 0, 0f, 1f, 0f, 0f);
        Matrix.setRotateM(mRotationMatrixY, 0, 0f, 0f, 1f, 0f);
        Matrix.setRotateM(mRotationMatrixZ, 0, 0f, 0f, 0f, 1f);

        Matrix.multiplyMM(mRotationMatrix, 0, mRotationMatrixY, 0, mRotationMatrixZ, 0);
        Matrix.multiplyMM(mRotationMatrix, 0, mRotationMatrixX, 0, mRotationMatrix, 0);

        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 4f, 0f, 0f, 0f, 0f, 1f, 0f);
        float ratio = 640f/480f;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        mPolygon = new Polygon();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        synchronized (this) {
            float[] mMVPMatrix = new float[16];

            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

            Matrix.multiplyMM(mRotationMatrix, 0, mRotationMatrixY, 0, mRotationMatrixZ, 0);
            Matrix.multiplyMM(mRotationMatrix, 0, mRotationMatrixX, 0, mRotationMatrix, 0);

            float[] transformationMatrixPoligon = new float[16];

            Matrix.multiplyMM(transformationMatrixPoligon, 0, mTranslationMatrix, 0, mRotationMatrix, 0);
            Matrix.multiplyMM(transformationMatrixPoligon, 0, mMVPMatrix, 0, transformationMatrixPoligon, 0);



            float[]transformationMatrixLight = new float[16];

            float[] mLightModelMatrix = new float[]{1f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f};

            Matrix.multiplyMM(transformationMatrixLight, 0, mViewMatrix, 0, mLightModelMatrix, 0);
            Matrix.multiplyMM(transformationMatrixLight, 0, mProjectionMatrix, 0, transformationMatrixLight, 0);


            mPolygon.setVPMatrixPoligon(transformationMatrixPoligon);
            mPolygon.setVPMatrixLight(transformationMatrixLight);
            mPolygon.setPositionLight(new float[]{mLightModelMatrix[12],mLightModelMatrix[13],mLightModelMatrix[14]});
            mPolygon.draw();
        }
    }

    public void addDeltaAngleX(float angleDX) {
        float[] mRotationMatrixDX = new float[16];
        Matrix.setRotateM(mRotationMatrixDX, 0, angleDX, 1f, 0f, 0f);
        Matrix.multiplyMM(mRotationMatrixX, 0, mRotationMatrixDX, 0, mRotationMatrixX, 0);
    }

    public void addDeltaAngleY(float angleDY) {
        float[] mRotationMatrixDY = new float[16];
        Matrix.setRotateM(mRotationMatrixDY, 0, angleDY, 0f, 1f, 0f);
        Matrix.multiplyMM(mRotationMatrixY, 0, mRotationMatrixDY, 0, mRotationMatrixY, 0);
    }

    public void setAngleZ(float angleZ) {
        Matrix.setRotateM(mRotationMatrixZ, 0, angleZ , 0f, 0f, 1f);
    }

    public void setPosition(short pX, short pY){
        mTranslationMatrix[12]= (pX/640f)*(2*1.77f)-1.77f;
        mTranslationMatrix[13]=-(pY/480f)*(2*1.33f)+1.33f;

        Log.d("Vizzy","tX:" + pX + ", ty:" + pY);
    }

    public double getAngleX(){
        return -Math.atan2(mRotationMatrixX[9],mRotationMatrixX[5])*RADIANS_TO_DEGREES;
    }

    public double getAngleY(){
        return -Math.atan2(mRotationMatrixY[2],mRotationMatrixY[0])*RADIANS_TO_DEGREES;
    }

    public double getAngleZ(){
        return -Math.atan2(mRotationMatrixZ[4],mRotationMatrixZ[0])*RADIANS_TO_DEGREES;
    }

    public float[] getQuaternion(){

        float[] newRotMat = new float[16];
        float[] newRotMatX = new float[16];
        float[] newRotMatY = new float[16];
        float[] newRotMatZ = new float[16];

        Matrix.setRotateM(newRotMatX, 0, (float)getAngleZ(), 1f, 0f, 0f);
        Matrix.setRotateM(newRotMatY, 0, (float)getAngleY(), 0f, 1f, 0f);
        Matrix.setRotateM(newRotMatZ, 0,-(float)getAngleX(), 0f, 0f, 1f);

        Matrix.multiplyMM(newRotMat, 0, newRotMatY , 0, newRotMatZ, 0);
        Matrix.multiplyMM(newRotMat, 0, newRotMatX , 0, newRotMat, 0);

        float[] auxRot = new float[16];
        Matrix.setRotateM(auxRot, 0, 180f, 0f, 1f, 0f);
        Matrix.multiplyMM(newRotMat, 0, auxRot, 0, newRotMat, 0);
        Matrix.setRotateM(auxRot, 0,  90f, 1f, 0f, 0f);
        Matrix.multiplyMM(newRotMat, 0, auxRot, 0, newRotMat, 0);

        return matrix2quaternion(newRotMat);
    }

    private float[] matrix2quaternion(float[] mRotation){
        double m00=mRotation[0];
        double m10=mRotation[1];
        double m20=mRotation[2];
        double m01=mRotation[4];
        double m11=mRotation[5];
        double m21=mRotation[6];
        double m02=mRotation[8];
        double m12=mRotation[9];
        double m22=mRotation[10];
        double S,qw,qx,qy,qz;

        double tr = m00 + m11 + m22;

        if (tr > 0.0) {
            S = Math.sqrt(tr+1.0) * 2.0;
            qw = 0.25 * S;
            qx = (m21 - m12) / S;
            qy = (m02 - m20) / S;
            qz = (m10 - m01) / S;
        } else if ((m00 > m11)&(m00 > m22)) {
            S = Math.sqrt(1.0 + m00 - m11 - m22) * 2.0;
            qw = (m21 - m12) / S;
            qx = 0.25 * S;
            qy = (m01 + m10) / S;
            qz = (m02 + m20) / S;
        } else if (m11 > m22) {
            S = Math.sqrt(1.0 + m11 - m00 - m22) * 2.0;
            qw = (m02 - m20) / S;
            qx = (m01 + m10) / S;
            qy = 0.25 * S;
            qz = (m12 + m21) / S;
        } else {
            S = Math.sqrt(1.0 + m22 - m00 - m11) * 2.0;
            qw = (m10 - m01) / S;
            qx = (m02 + m20) / S;
            qy = (m12 + m21) / S;
            qz = 0.25 * S;
        }

        return new float[]{(float)qx, (float)qy, (float)qz, (float)qw};
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float)width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f);
    }

    public static int compileShader(final int shaderType, final String shaderSource) {
        int shaderHandle = GLES20.glCreateShader(shaderType);
        if (shaderHandle != 0) {
            GLES20.glShaderSource(shaderHandle, shaderSource);
            GLES20.glCompileShader(shaderHandle);
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                Log.e("Compile", "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }
        if (shaderHandle == 0)
            throw new RuntimeException("Error creating shader.");

        return shaderHandle;
    }

    public static int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes) {
        int programHandle = GLES20.glCreateProgram();
        if (programHandle != 0) {
            GLES20.glAttachShader(programHandle, vertexShaderHandle);
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);
            if (attributes != null) {
                final int size = attributes.length;
                for (int i = 0; i < size; i++)
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
            }
            GLES20.glLinkProgram(programHandle);
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                Log.e("Compile", "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }
        if (programHandle == 0)
            throw new RuntimeException("Error creating program.");

        return programHandle;
    }
}