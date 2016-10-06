package com.example.vizzylearning.NodesROS;

import android.view.View;

import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.view.RosImageView;

import sensor_msgs.CompressedImage;

/**
 * Created by rafael on 02-08-2016.
 */
public class ImageNode {

    static public RosImageView<CompressedImage> createNode(View view) {

        RosImageView<CompressedImage> rosImageNode = NodeTools.defaultRosImageViewNode(view);
        rosImageNode.setMessageToBitmapCallable(new BitmapFromCompressedImage());

        return rosImageNode;
    }
}
