package com.example.vizzylearning.NodesROS;

import com.example.vizzylearning.MainActivity;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import std_msgs.Bool;
import std_msgs.Float32MultiArray;
import std_msgs.Int16MultiArray;

/**
 * Created by rafael on 27-07-2016.
 */
public class ControllerNode extends AbstractNodeMain{
    private Publisher<nav2d_operator.cmd> publisherData;
    private Publisher<std_msgs.Int8> publisherVoice;
    private Publisher<std_msgs.Float32MultiArray> publisherGrasp;
    private Publisher<std_msgs.Bool> publisherSavIm;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("androidApp/controllerNode");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        publisherData = connectedNode.newPublisher("cmd", nav2d_operator.cmd._TYPE);
        publisherVoice = connectedNode.newPublisher("voiceCMD", std_msgs.Int8._TYPE);
        publisherGrasp = connectedNode.newPublisher("graspCMD", Float32MultiArray._TYPE);
        publisherSavIm = connectedNode.newPublisher("savImCMD", Bool._TYPE);
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

    public void sendGraspCMD(float[] grasp){
        if(MainActivity.connection) {
            std_msgs.Float32MultiArray graspMsg = publisherGrasp.newMessage();
            graspMsg.setData(grasp);
            publisherGrasp.publish(graspMsg);
        }
    }

    public void sendSavImCMD(){
        if(MainActivity.connection) {
            std_msgs.Bool savImMsg = publisherSavIm.newMessage();
            savImMsg.setData(true);
            publisherSavIm.publish(savImMsg);
        }
    }
}