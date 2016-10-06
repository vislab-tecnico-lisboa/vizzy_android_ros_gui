package com.example.vizzylearning;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.example.vizzylearning.NodesROS.ControllerNode;
import com.example.vizzylearning.NodesROS.ImageNode;
import com.example.vizzylearning.NodesROS.NodeTools;
import com.example.vizzylearning.Tools.AccSensor;
import com.example.vizzylearning.Tools.VizzyTalk;
import com.example.vizzylearning.Tools.VoiceRecognition;
import com.github.rosjava.android_remocons.common_tools.apps.RosAppActivity;

import org.ros.android.view.RosImageView;
import org.ros.android.view.RosTextView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.io.FileOutputStream;

import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

/**
 * Created by rafael on 25-07-2016.
 */
public class MainActivity extends RosAppActivity implements RecognitionListener {
    public static SpeechRecognizer recognizer;
    public static AccSensor mAccSensor;
    public static SensorManager mSensorManager;
    public static VizzyTalk mVizzyTalk;
    public static ControllerNode mControllerNode;
    public static boolean audio=false, voice=false, connection=false;
    public static ImageView imageView2;
    private RosImageView<sensor_msgs.CompressedImage> rosImageNode;
    private ToggleButton toggleVoice;

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    public static final String EXTRA_MESSAGE = "com.example.vizzylearning.MESSAGE";
    private static final int REQUEST_CODE = 10;

    public MainActivity() {
        super("Vizzy", "Vizzy");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setDefaultMasterName(getString(R.string.default_robot));
        setDashboardResource(R.id.top_bar);
        setMainWindowResource(R.layout.activity_main);
        mVizzyTalk=new VizzyTalk(getApplicationContext(),(Vibrator) getSystemService(Context.VIBRATOR_SERVICE));
        super.onCreate(savedInstanceState);

        mControllerNode = new ControllerNode();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccSensor = new AccSensor(MainActivity.this);

        final ToggleButton toggleAudio=(ToggleButton)findViewById(R.id.toggleAudio);
        toggleAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                audio=isChecked;
                if(isChecked)
                    mVizzyTalk.talk("Now you can hear my voice");
                else
                    mVizzyTalk.talk("You can still read these messages");
            }
        });

        final ToggleButton toggleController=(ToggleButton)findViewById(R.id.toggleController);
        toggleController.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mVizzyTalk.talk("Move the device to move my platform");
                    mAccSensor.register();
                }
                else {
                    mVizzyTalk.talk("The controller is OFF");
                    mAccSensor.unRegister();
                    mControllerNode.sendMovement(0,0,(byte) 1);
                    imageView2.setImageBitmap(Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888));
                }
            }
        });

        toggleVoice=(ToggleButton)findViewById(R.id.toggleVoice);
        toggleVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                voice=isChecked;
                if (isChecked)
                    VoiceRecognition.startRecognitionLoop();
                else
                    recognizer.stop();
            }
        });

        Button buttonLearn=(Button)findViewById(R.id.buttonLearn);
        buttonLearn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                mVizzyTalk.talk("Teach me an object");
                mControllerNode.sendSavImCMD();
                Drawable d = rosImageNode.getDrawable();
                Bitmap bmp = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bmp);
                d.draw(canvas);
                String filename = "bitmap.png";
                try {
                    FileOutputStream stream = MainActivity.this.openFileOutput(filename, Context.MODE_PRIVATE);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();
                    bmp.recycle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MainActivity.this, DefineGrasp.class);
                intent.putExtra(EXTRA_MESSAGE, filename);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        VoiceRecognition.runRecognizerSetup(MainActivity.this,this);

        View imageView = findViewById(R.id.imageView1);
        rosImageNode=ImageNode.createNode(imageView);
        rosImageNode.setTopicName("/vizzy/l_camera/image_color/compressed");

        imageView2 = (ImageView)findViewById(R.id.imageView2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            float[] result = data.getFloatArrayExtra("RESULT");
            mControllerNode.sendGraspCMD(result);
            String resultString= " ";
            for(int k=0;k<result.length;k++)
                resultString=resultString+String.valueOf(result[k])+" ";
            Log.d("Vizzy",resultString);
            mVizzyTalk.talk("Returned");
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void init(final NodeMainExecutor nodeMainExecutor) {
        super.init(nodeMainExecutor);

        final NodeConfiguration nodeConfiguration= NodeTools.getDeafultNodeConfiguration(getMasterUri());
        nodeMainExecutor.execute(mControllerNode, nodeConfiguration);

        nodeConfiguration.setNodeName("androidApp/imageNode");
        nodeMainExecutor.execute(rosImageNode, nodeConfiguration);
        connection=true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                VoiceRecognition.runRecognizerSetup(MainActivity.this,this);
            else
                finish();
        }
    }

    @Override
    protected void onDestroy() {
        mAccSensor.unRegister();
        mVizzyTalk.shutdown();
        super.onDestroy();
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis!= null)
            VoiceRecognition.answer(hypothesis);
        toggleVoice.setChecked(false);
    }

    @Override
    public void onEndOfSpeech() {
        recognizer.stop();
    }

    @Override
    public void onTimeout() {
        recognizer.stop();
    }

    @Override
    public void onError(Exception error) {
        Log.d("Vizzy",error.getMessage());
        recognizer.stop();
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
    }

    @Override
    public void onBeginningOfSpeech() {
    }
}




