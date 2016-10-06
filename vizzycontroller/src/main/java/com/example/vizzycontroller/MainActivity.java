package com.example.vizzycontroller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.example.vizzycontroller.NodesROS.ControllerNode;
import com.example.vizzycontroller.NodesROS.IDInfoNode;
import com.example.vizzycontroller.NodesROS.ImageNode;
import com.example.vizzycontroller.NodesROS.NodeTools;
import com.example.vizzycontroller.Tools.AccSensor;
import com.example.vizzycontroller.Tools.VizzyTalk;
import com.example.vizzycontroller.Tools.VoiceRecognition;
import com.github.rosjava.android_remocons.common_tools.apps.RosAppActivity;

import org.ros.android.view.RosImageView;
import org.ros.android.view.RosTextView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

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
    public static boolean audio=false, voice=false, connection=false, follower=false;
    public static int followingID=-1;
    public static ImageView imageView2;
    private RosTextView<std_msgs.String> rosInfoTextNode;
    private RosImageView<sensor_msgs.CompressedImage> rosImageNode;
    private ToggleButton toggleVoice;

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

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

        ToggleButton toggleAudio=(ToggleButton)findViewById(R.id.toggleAudio);
        toggleAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                audio=isChecked;
                if(isChecked)
                    mVizzyTalk.talk("Now you can hear my voice");
                else
                    mVizzyTalk.talk("You can still read these messages");
            }
        });

        ToggleButton toggleController=(ToggleButton)findViewById(R.id.toggleController);
        toggleController.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mVizzyTalk.talk("Move the device to move my platform");
                    mAccSensor.register();
                }
                else {
                    mVizzyTalk.talk("The controller is off");
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

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        VoiceRecognition.runRecognizerSetup(MainActivity.this,this);

        imageView2 = (ImageView) findViewById(R.id.imageView2);

        final View infoTextView = findViewById(R.id.infoText);
        rosInfoTextNode= IDInfoNode.createNode(infoTextView);

        View imageView = findViewById(R.id.imageView1);
        rosImageNode= ImageNode.createNode(imageView);
        rosImageNode.setTopicName("/vizzy/l_camera/image_color/compressed");
    }

    @Override
    protected void init(final NodeMainExecutor nodeMainExecutor) {
        super.init(nodeMainExecutor);

        final NodeConfiguration nodeConfiguration= NodeTools.getDeafultNodeConfiguration(getMasterUri());
        nodeMainExecutor.execute(mControllerNode, nodeConfiguration);

        nodeConfiguration.setNodeName("androidApp/infoTextNode");
        nodeMainExecutor.execute(rosInfoTextNode, nodeConfiguration);

        nodeConfiguration.setNodeName("androidApp/imageNode");
        nodeMainExecutor.execute(rosImageNode, nodeConfiguration);

        ToggleButton toggleFollowing=(ToggleButton)findViewById(R.id.toggleFollowing);
        toggleFollowing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                follower=isChecked;
                if(isChecked){
                    mVizzyTalk.talk("Tap a box so i follow that person");
                    nodeMainExecutor.shutdownNodeMain(rosImageNode);
                    rosImageNode.setTopicName("/image_out_tracker/compressed");
                    nodeMainExecutor.execute(rosImageNode, nodeConfiguration);
                }
                else{
                    mVizzyTalk.talk("Following is off");
                    nodeMainExecutor.shutdownNodeMain(rosImageNode);
                    rosImageNode.setTopicName("/vizzy/l_camera/image_color/compressed");
                    nodeMainExecutor.execute(rosImageNode, nodeConfiguration);
                    mControllerNode.sendCoordinates(new short[]{-1,-1});
                }
            }
        });
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




