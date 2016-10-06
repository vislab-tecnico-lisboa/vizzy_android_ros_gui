package com.example.vizzycontroller.NodesROS;

import android.util.Log;
import android.view.View;

import com.example.vizzycontroller.MainActivity;

import org.ros.android.MessageCallable;
import org.ros.android.view.RosTextView;

/**
 * Created by rafael on 02-08-2016.
 */
public class IDInfoNode {

    static public RosTextView<std_msgs.String> createNode(View view){
        RosTextView<std_msgs.String> rosIDInfoTextNode= NodeTools.defaultRosTextViewNode(view,"babbler");

        rosIDInfoTextNode.setMessageToStringCallable(new MessageCallable<java.lang.String, std_msgs.String>() {
            @Override
            public java.lang.String call(std_msgs.String message) {
                java.lang.String textString= "I am following no one";
                if(MainActivity.follower) {
                    if (MainActivity.followingID > -1) {
                        MainActivity.followingID=-1;
                        MainActivity.mVizzyTalk.talk(textString);
                    } else {
                        int receivedID = Integer.parseInt(message.getData().substring(7, message.getData().length()));
                        if (receivedID < 1000) {
                            MainActivity.followingID=receivedID;
                            textString = "I am following person " + receivedID;
                            MainActivity.mVizzyTalk.talk(textString);
                        } else
                            MainActivity.mVizzyTalk.talk("You did not choose someone");
                    }
                }
                else {
                    MainActivity.followingID=-1;
                    textString = " ";
                }
                Log.d("Vizzy","ID");
                return textString;
            }
        });

        return rosIDInfoTextNode;
    }
}
