package com.example.vizzycontroller.NodesROS;

import android.view.View;

import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.MessageCallable;
import org.ros.android.view.RosImageView;
import org.ros.android.view.RosTextView;
import org.ros.node.NodeConfiguration;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;

/**
 * Created by rafael on 27-07-2016.
 */
public class NodeTools {
    static public NodeConfiguration getDeafultNodeConfiguration(URI masterURI){
        NodeConfiguration nodeConfiguration = null;
        try {
            Thread.sleep(1000);
            java.net.Socket socket = new java.net.Socket(masterURI.getHost(), masterURI.getPort());
            java.net.InetAddress local_network_address = socket.getLocalAddress();
            socket.close();
            nodeConfiguration = NodeConfiguration.newPublic(local_network_address.getHostAddress(), masterURI);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nodeConfiguration;
    }

    static public RosTextView<std_msgs.String> defaultRosTextViewNode(View view, java.lang.String topicName){
        RosTextView<std_msgs.String> rosTextNode = (RosTextView<std_msgs.String>) view;
        rosTextNode.setTopicName(topicName);
        rosTextNode.setMessageType(std_msgs.String._TYPE);
        rosTextNode.setMessageToStringCallable(new MessageCallable<java.lang.String, std_msgs.String>() {
            @Override
            public java.lang.String call(std_msgs.String message) {
                return message.getData();
            }
        });
        return rosTextNode;
    }

    static public RosImageView<sensor_msgs.CompressedImage> defaultRosImageViewNode(View view){
        RosImageView<sensor_msgs.CompressedImage> rosViewNode = (RosImageView<sensor_msgs.CompressedImage>) view;
        rosViewNode.setMessageType(sensor_msgs.CompressedImage._TYPE);
        rosViewNode.setMessageToBitmapCallable(new BitmapFromCompressedImage());
        return rosViewNode;
    }
}
