package com.example.vizzylearning.Tools.OpenGLObject;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Polygon {
    protected int mPerVertexProgramHandle, mPointProgramHandle;
    private float[] mVPMatrixPoligon, mVPMatrixLight,mLightPosInEyeSpace;
    private FloatBuffer vertexBuffer, colorBuffer, normalBuffer;
    private ShortBuffer vertexIndexBuffer,normalsIndexBuffer;
    private static final int COORDS_PER_VERTEX = 3,COLORS_PER_VERTEX = 4,NORMALS_PER_VERTEX = 3;
/*
    String vertexShaderCode =
            "uniform mat4 u_MVPMatrix;" +
                    "uniform mat4 u_MVMatrix;" +
                    "uniform vec3 u_LightPos;" +
                    "attribute vec4 a_Position;" +
                    "attribute vec4 a_Color;" +
                    "attribute vec3 a_Normal;" +
                    "varying vec4 v_Color;" +
                    "void main() {" +
                    "  vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);" +
                    "  vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));" +
                    "  float distance = length(u_LightPos - modelViewVertex);" +
                    "  vec3 lightVector = normalize(u_LightPos - modelViewVertex);" +
                    "  float diffuse = max(dot(modelViewNormal, lightVector), 0.1);" +
                    "  diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));" +
                    "  v_Color = a_Color * diffuse; " +
                    "  gl_Position = u_MVPMatrix * a_Position;" +
                    "}";
*/

    String vertexShaderCode =
            "uniform mat4 u_MVPMatrix;" +
                    "attribute vec4 a_Position;" +
                    "attribute vec4 a_Color;" +
                    "varying vec4 v_Color;" +
                    "void main() {" +
                    "  v_Color = a_Color; " +
                    "  gl_Position = u_MVPMatrix * a_Position;" +
                    "}";

    String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 v_Color;" +
                    "void main() {" +
                    "  gl_FragColor = v_Color;" +
                    "}";

    String pointVertexShader =
            "uniform mat4 u_MVPMatrix;" +
                    "attribute vec4 a_Position;" +
                    "void main(){" +
                    "   gl_Position = u_MVPMatrix * a_Position;" +
                    "   gl_PointSize = 5.0;" +
                    "}";

    final String pointFragmentShader =
            "precision mediump float;" +
                    "void main(){" +
                    "   gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);" +
                    "}";

    public Polygon() {

        ByteBuffer bb = ByteBuffer.allocateDirect(VERTEX.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(VERTEX);
        vertexBuffer.position(0);

        ByteBuffer cb = ByteBuffer.allocateDirect(COLORS.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(COLORS);
        colorBuffer.position(0);

        ByteBuffer nb = ByteBuffer.allocateDirect(NORMALS.length * 4);
        nb.order(ByteOrder.nativeOrder());
        normalBuffer = nb.asFloatBuffer();
        normalBuffer.put(NORMALS);
        normalBuffer.position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        vertexIndexBuffer = dlb.asShortBuffer();
        vertexIndexBuffer.put(VERTEX_INDEX);
        vertexIndexBuffer.position(0);

        ByteBuffer dlb2 = ByteBuffer.allocateDirect(NORMALS_INDEX.length * 2);
        dlb2.order(ByteOrder.nativeOrder());
        normalsIndexBuffer = dlb2.asShortBuffer();
        normalsIndexBuffer.put(NORMALS_INDEX);
        normalsIndexBuffer.position(0);

        mPerVertexProgramHandle = GLES20.glCreateProgram();

        int vertexShaderHandle = MyGLRenderer.compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShaderHandle = MyGLRenderer.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mPerVertexProgramHandle = MyGLRenderer.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[] {"a_Position",  "a_Color", "a_Normal"});

        final int pointVertexShaderHandle = MyGLRenderer.compileShader(GLES20.GL_VERTEX_SHADER, pointVertexShader);
        final int pointFragmentShaderHandle = MyGLRenderer.compileShader(GLES20.GL_FRAGMENT_SHADER, pointFragmentShader);
        mPointProgramHandle = MyGLRenderer.createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle,
                new String[] {"a_Position"});

    GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glDepthMask(true);
    }

    public void draw() {
        GLES20.glUseProgram(mPerVertexProgramHandle);

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_MVPMatrix");
        int mLightPosHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_LightPos");
        int mPositionHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Position");
        int mColorHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Color");
        int mNormalHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Normal");


        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                COORDS_PER_VERTEX*4, vertexBuffer);

        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, COLORS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                COLORS_PER_VERTEX*4, colorBuffer);

        GLES20.glEnableVertexAttribArray(mNormalHandle);
        GLES20.glVertexAttribPointer(mNormalHandle, NORMALS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                NORMALS_PER_VERTEX*4, normalBuffer);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mVPMatrixPoligon, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length, GLES20.GL_UNSIGNED_SHORT, vertexIndexBuffer);

/*
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, VERTEX_INDEX.length, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
*/


/*        GLES20.glUseProgram(mPointProgramHandle);

        int pointMVPMatrixHandle = GLES20.glGetUniformLocation(mPointProgramHandle, "u_MVPMatrix");
        int pointPositionHandle = GLES20.glGetAttribLocation(mPointProgramHandle, "a_Position");
        GLES20.glVertexAttrib3f(pointPositionHandle, 0, 0, 0);
        GLES20.glDisableVertexAttribArray(pointPositionHandle);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mVPMatrixLight, 0);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
*/
    }

    public void setVPMatrixPoligon(float[] mvpMatrix){
        mVPMatrixPoligon=mvpMatrix;
    }

    public void setVPMatrixLight(float[] mvpMatrix){
        mVPMatrixLight=mvpMatrix;
    }

    public void setPositionLight(float[] mLightPosInEyeSpace){
        this.mLightPosInEyeSpace=mLightPosInEyeSpace;
    }

    private final static float[] VERTEX=new float[]{
            -0.141631f, +0.043873f, +0.014923f,
            -0.141631f, +0.144798f, +0.014923f,
            -0.135513f, +0.038671f, -0.003823f,
            -0.135513f, +0.150000f, -0.003823f,
            -0.109521f, +0.038671f, -0.083463f,
            -0.109521f, +0.150000f, -0.083463f,
            -0.099882f, -0.150000f, -0.195860f,
            -0.099882f, -0.100000f, -0.195860f,
            -0.099882f, -0.070000f, -0.195860f,
            -0.099882f, -0.015000f, -0.195860f,
            -0.099882f, +0.015000f, -0.195860f,
            -0.099882f, +0.070000f, -0.195860f,
            -0.099882f, +0.100000f, -0.195860f,
            -0.099882f, +0.150000f, -0.195860f,
            -0.097537f, -0.150000f, +0.701362f,
            -0.097537f, +0.150000f, +0.701362f,
            -0.084165f, +0.039732f, -0.000000f,
            -0.084165f, +0.148939f, -0.000000f,
            -0.082917f, +0.038671f, -0.003823f,
            -0.082917f, +0.150000f, -0.003823f,
            -0.072357f, -0.150000f, -0.154119f,
            -0.072357f, -0.100000f, -0.154119f,
            -0.072357f, -0.070000f, -0.154119f,
            -0.072357f, -0.015000f, -0.154119f,
            -0.072357f, +0.015000f, -0.154119f,
            -0.072357f, +0.070000f, -0.154119f,
            -0.072357f, +0.100000f, -0.154119f,
            -0.072357f, +0.150000f, -0.154119f,
            -0.061988f, +0.038671f, -0.067950f,
            -0.061988f, +0.150000f, -0.067950f,
            -0.004670f, -0.117719f, +0.121705f,
            -0.003680f, +0.072786f, +0.116524f,
            -0.003440f, +0.117292f, +0.114031f,
            -0.003086f, +0.117906f, +0.111818f,
            +0.000000f, +0.056068f, +0.058863f,
            +0.000697f, -0.117292f, +0.114031f,
            +0.004326f, -0.150000f, -0.264577f,
            +0.004326f, -0.100000f, -0.264577f,
            +0.004326f, -0.070000f, -0.264577f,
            +0.004326f, -0.015000f, -0.264577f,
            +0.004326f, +0.015000f, -0.264577f,
            +0.004326f, +0.070000f, -0.264577f,
            +0.004326f, +0.100000f, -0.264577f,
            +0.004326f, +0.150000f, -0.264577f,
            +0.006919f, -0.150000f, -0.206394f,
            +0.006919f, -0.100000f, -0.206394f,
            +0.006919f, -0.070000f, -0.206394f,
            +0.006919f, -0.015000f, -0.206394f,
            +0.006919f, +0.015000f, -0.206394f,
            +0.006919f, +0.070000f, -0.206394f,
            +0.006919f, +0.100000f, -0.206394f,
            +0.006919f, +0.150000f, -0.206394f,
            +0.026378f, +0.056068f, +0.077311f,
            +0.026378f, +0.127483f, +0.077311f,
            +0.071585f, -0.117292f, +0.114031f,
            +0.071585f, +0.117292f, +0.114031f,
            +0.083121f, -0.150000f, -0.003823f,
            +0.083121f, +0.150000f, -0.003823f,
            +0.104008f, -0.150000f, -0.033688f,
            +0.104008f, +0.150000f, -0.033688f,
            +0.104008f, -0.100000f, -0.067223f,
            +0.104008f, -0.070000f, -0.067223f,
            +0.104008f, -0.015000f, -0.067223f,
            +0.104008f, +0.015000f, -0.067223f,
            +0.104008f, +0.070000f, -0.067223f,
            +0.104008f, +0.100000f, -0.067223f,
            +0.104008f, -0.150000f, -0.154119f,
            +0.104008f, -0.100000f, -0.154119f,
            +0.104008f, -0.070000f, -0.154119f,
            +0.104008f, -0.015000f, -0.154119f,
            +0.104008f, +0.015000f, -0.154119f,
            +0.104008f, +0.070000f, -0.154119f,
            +0.104008f, +0.100000f, -0.154119f,
            +0.104008f, +0.150000f, -0.154119f,
            +0.154008f, -0.150000f, -0.003823f,
            +0.154008f, +0.150000f, -0.003823f,
            +0.154008f, -0.100000f, -0.067223f,
            +0.154008f, -0.070000f, -0.067223f,
            +0.154008f, -0.015000f, -0.067223f,
            +0.154008f, +0.015000f, -0.067223f,
            +0.154008f, +0.070000f, -0.067223f,
            +0.154008f, +0.100000f, -0.067223f,
            +0.154008f, -0.150000f, -0.183984f,
            +0.154008f, -0.100000f, -0.183984f,
            +0.154008f, -0.070000f, -0.183984f,
            +0.154008f, -0.015000f, -0.183984f,
            +0.154008f, +0.015000f, -0.183984f,
            +0.154008f, +0.070000f, -0.183984f,
            +0.154008f, +0.100000f, -0.183984f,
            +0.154008f, +0.150000f, -0.183984f,
            +0.190284f, -0.150000f, +0.701362f,
            +0.190284f, +0.150000f, +0.701362f
    };

    private final static float[] COLORS=new float[]{
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.5f, 0.1f, 0.1f, 1.0f,
            0.5f, 0.1f, 0.1f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.5f, 0.1f, 0.1f, 1.0f,
            0.5f, 0.1f, 0.1f, 1.0f,
            0.5f, 0.1f, 0.1f, 1.0f,
            0.5f, 0.1f, 0.1f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.5f, 0.1f, 0.1f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.5f, 0.1f, 0.1f, 1.0f,
            0.5f, 0.1f, 0.1f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.1f, 0.1f, 0.5f, 1.0f,
            0.5f, 0.1f, 0.1f, 1.0f,
            0.5f, 0.1f, 0.1f, 1.0f
    };

    private final static short[] VERTEX_INDEX=new short[]{
            59, 73, 72, 71, 70, 64, 64, 70, 63,
            72, 65, 59, 59, 65, 64, 59, 64, 58,
            58, 64, 63, 58, 63, 62, 67, 66, 60,
            60, 66, 58, 60, 58, 61, 61, 58, 62,
            61, 62, 68, 68, 62, 69, 74, 82, 83,
            84, 85, 77, 77, 85, 78, 83, 76, 74,
            74, 76, 77, 74, 77, 75, 75, 77, 78,
            75, 78, 79, 88, 89, 81, 81, 89, 75,
            81, 75, 80, 80, 75, 79, 80, 79, 87,
            87, 79, 86, 73, 51, 72, 72, 51, 50,
            51, 27, 50, 50, 27, 26, 27, 13, 26,
            26, 13, 12, 13, 43, 12, 12, 43, 42,
            43, 89, 42, 42, 89, 88, 69, 47, 68,
            68, 47, 46, 47, 23, 46, 46, 23, 22,
            23, 9 , 22, 22, 9 , 8 , 9 , 39, 8 ,
            8 , 39, 38, 39, 85, 38, 38, 85, 84,
            49, 48, 71, 71, 48, 70, 25, 24, 49,
            49, 24, 48, 11, 10, 25, 25, 10, 24,
            41, 40, 11, 11, 40, 10, 87, 86, 41,
            41, 86, 40, 31, 30, 14, 33, 31, 32,
            32, 31, 14, 32, 14, 15, 16, 17, 34,
            34, 17, 53, 34, 53, 52, 35, 54, 30,
            30, 54, 90, 30, 90, 14, 56, 74, 35,
            35, 74, 54, 20, 6 , 44, 44, 6 , 36,
            44, 36, 66, 66, 36, 82, 66, 82, 58,
            58, 82, 74, 58, 74, 56, 13, 27, 43,
            43, 27, 51, 43, 51, 89, 89, 51, 73,
            89, 73, 75, 75, 73, 59, 75, 59, 57,
            91, 90, 55, 55, 90, 54, 15, 14, 91,
            91, 14, 90, 31, 33, 0 , 0 , 33, 1 ,
            5 , 4 , 3 , 3 , 4 , 2 , 3 , 2 , 1 ,
            1 , 2 , 0 , 4 , 5 , 28, 28, 5 , 29,
            17, 16, 19, 19, 16, 18, 19, 18, 29,
            29, 18, 28, 57, 59, 58, 35, 30, 31,
            35, 31, 56, 56, 31, 52, 56, 52, 58,
            58, 52, 53, 58, 53, 57, 45, 44, 67,
            67, 44, 66, 21, 20, 45, 45, 20, 44,
            7 , 6 , 21, 21, 6 , 20, 37, 36, 7 ,
            7 , 36, 6 , 83, 82, 37, 37, 82, 36,
            74, 75, 54, 54, 75, 55, 29, 5 , 19,
            19, 5 , 3 , 75, 57, 55, 55, 57, 53,
            19, 3 , 17, 17, 3 , 1 , 17, 1 , 53,
            53, 1 , 33, 53, 33, 55, 55, 33, 32,
            15, 91, 32, 32, 91, 55, 18, 2 , 28,
            28, 2 , 4 , 34, 31, 16, 16, 31, 0 ,
            16, 0 , 18, 18, 0 , 2 , 34, 52, 31,
            79, 78, 63, 63, 78, 62, 24, 10, 48,
            48, 10, 40, 48, 40, 70, 70, 40, 86,
            70, 86, 63, 63, 86, 79, 62, 78, 69,
            69, 78, 85, 69, 85, 47, 47, 85, 39,
            47, 39, 23, 23, 39, 9 , 77, 76, 61,
            61, 76, 60, 22, 8 , 46, 46, 8 , 38,
            46, 38, 68, 68, 38, 84, 68, 84, 61,
            61, 84, 77, 60, 76, 67, 67, 76, 83,
            67, 83, 45, 45, 83, 37, 45, 37, 21,
            21, 37, 7 , 81, 80, 65, 65, 80, 64,
            26, 12, 50, 50, 12, 42, 50, 42, 72,
            72, 42, 88, 72, 88, 65, 65, 88, 81,
            64, 80, 71, 71, 80, 87, 71, 87, 49,
            49, 87, 41, 49, 41, 25, 25, 41, 11
    };

    private final static float[] NORMALS=new float[]{
            -1.000000f, +0.000000f, -0.000000f,
            -0.987408f, +0.000000f, -0.158193f,
            -0.950652f, +0.000000f, -0.310258f,
            -0.834833f, +0.000000f, +0.550503f,
            -0.819472f, +0.000000f, -0.573119f,
            -0.573119f, +0.000000f, +0.819472f,
            -0.550503f, +0.000000f, -0.834833f,
            -0.474076f, +0.000000f, +0.880484f,
            -0.183844f, -0.947154f, +0.262869f,
            +0.000000f, +0.000000f, +1.000000f,
            +0.000000f, -0.963578f, +0.267427f,
            +0.000000f, +0.963578f, +0.267427f,
            +0.000000f, -1.000000f, -0.000000f,
            +0.000000f, +1.000000f, -0.000000f,
            +0.000000f, -0.998453f, -0.055604f,
            +0.000000f, +0.998453f, -0.055604f,
            +0.000000f, +0.000000f, -1.000000f,
            +0.310258f, +0.000000f, -0.950652f,
            +0.474076f, +0.000000f, -0.880484f,
            +0.550503f, +0.000000f, +0.834833f,
            +0.573119f, +0.000000f, -0.819472f,
            +0.819472f, +0.000000f, +0.573119f,
            +0.950652f, +0.000000f, +0.310258f,
            +0.980183f, +0.000000f, -0.198094f,
            +1.000000f, +0.000000f, -0.000000f
    };

    private final static short[] NORMALS_INDEX=new short[]{
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 24, 24, 24,
            24, 24, 24, 24, 24, 24, 24, 24, 24,
            24, 24, 24, 24, 24, 24, 24, 24, 24,
            24, 24, 24, 24, 24, 24, 24, 24, 24,
            24, 24, 24, 24, 24, 24, 24, 24, 24,
            24, 24, 24, 7, 7, 7, 7, 7, 7,
            19, 19, 19, 19, 19, 19, 3, 3, 3,
            3, 3, 3, 6, 6, 6, 6, 6, 6,
            18, 18, 18, 18, 18, 18, 7, 7, 7,
            7, 7, 7, 19, 19, 19, 19, 19, 19,
            3, 3, 3, 3, 3, 3, 6, 6, 6,
            6, 6, 6, 18, 18, 18, 18, 18, 18,
            7, 7, 7, 7, 7, 7, 19, 19, 19,
            19, 19, 19, 3, 3, 3, 3, 3, 3,
            6, 6, 6, 6, 6, 6, 18, 18, 18,
            18, 18, 18, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 20, 20, 20,
            20, 20, 20, 20, 20, 20, 14, 14, 14,
            14, 14, 14, 14, 14, 14, 10, 10, 10,
            10, 10, 10, 12, 12, 12, 12, 12, 12,
            12, 12, 12, 12, 12, 12, 12, 12, 12,
            12, 12, 12, 12, 12, 12, 13, 13, 13,
            13, 13, 13, 13, 13, 13, 13, 13, 13,
            13, 13, 13, 13, 13, 13, 13, 13, 13,
            23, 23, 23, 23, 23, 23, 9, 9, 9,
            9, 9, 9, 5, 5, 5, 5, 5, 5,
            2, 2, 2, 2, 2, 2, 2, 2, 2,
            2, 2, 2, 17, 17, 17, 17, 17, 17,
            22, 22, 22, 22, 22, 22, 22, 22, 22,
            22, 22, 22, 4, 4, 4, 4, 4, 4,
            4, 4, 4, 4, 4, 4, 4, 4, 4,
            4, 4, 4, 4, 4, 4, 7, 7, 7,
            7, 7, 7, 19, 19, 19, 19, 19, 19,
            3, 3, 3, 3, 3, 3, 6, 6, 6,
            6, 6, 6, 18, 18, 18, 18, 18, 18,
            21, 21, 21, 21, 21, 21, 13, 13, 13,
            13, 13, 13, 11, 11, 11, 11, 11, 11,
            11, 11, 11, 11, 11, 11, 11, 11, 11,
            11, 11, 11, 11, 11, 11, 11, 11, 11,
            15, 15, 15, 15, 15, 15, 12, 12, 12,
            12, 12, 12, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 8, 8, 8,
            16, 16, 16, 16, 16, 16, 12, 12, 12,
            12, 12, 12, 12, 12, 12, 12, 12, 12,
            12, 12, 12, 12, 12, 12, 13, 13, 13,
            13, 13, 13, 13, 13, 13, 13, 13, 13,
            13, 13, 13, 13, 13, 13, 16, 16, 16,
            16, 16, 16, 12, 12, 12, 12, 12, 12,
            12, 12, 12, 12, 12, 12, 12, 12, 12,
            12, 12, 12, 13, 13, 13, 13, 13, 13,
            13, 13, 13, 13, 13, 13, 13, 13, 13,
            13, 13, 13, 16, 16, 16, 16, 16, 16,
            12, 12, 12, 12, 12, 12, 12, 12, 12,
            12, 12, 12, 12, 12, 12, 12, 12, 12,
            13, 13, 13, 13, 13, 13, 13, 13, 13,
            13, 13, 13, 13, 13, 13, 13, 13, 13
    };
}