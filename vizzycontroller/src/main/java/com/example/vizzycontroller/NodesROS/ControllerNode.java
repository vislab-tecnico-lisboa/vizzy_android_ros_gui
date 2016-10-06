package com.example.vizzycontroller.NodesROS;

import android.util.Log;

import com.example.vizzycontroller.MainActivity;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

/**
 * Created by rafael on 27-07-2016.
 */
public class ControllerNode extends AbstractNodeMain{
    private Publisher<nav2d_operator.cmd> publisherData;
    private Publisher<std_msgs.Int16MultiArray> publisherPixel;
    private Publisher<std_msgs.Int8> publisherVoice;


    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("androidApp/controllerNode");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        publisherData = connectedNode.newPublisher("cmd", nav2d_operator.cmd._TYPE);
        publisherPixel = connectedNode.newPublisher("pixelXY", std_msgs.Int16MultiArray._TYPE);
        publisherVoice = connectedNode.newPublisher("voiceCMD", std_msgs.Int8._TYPE);

    }

    public void sendMovement(double velocity, double turn, byte mode){
        if(MainActivity.connection) {
            nav2d_operator.cmd mNav2d = publisherData.newMessage();
            mNav2d.setVelocity(velocity);
            mNav2d.setTurn(turn);
            mNav2d.setMode(mode);
            publisherData.publish(mNav2d);
        }
    }

    public void sendVoiceCMD(byte command){
        if(MainActivity.connection) {
            std_msgs.Int8 voiceMsg = publisherVoice.newMessage();
            voiceMsg.setData(command);
            publisherVoice.publish(voiceMsg);
        }
    }

    public void sendCoordinates(short[] coordinates){
        if(MainActivity.connection) {
            std_msgs.Int16MultiArray coordinatesMsg = publisherPixel.newMessage();
            coordinatesMsg.setData(new short[]{coordinates[0], coordinates[1]});
            publisherPixel.publish(coordinatesMsg);
        }
    }
}