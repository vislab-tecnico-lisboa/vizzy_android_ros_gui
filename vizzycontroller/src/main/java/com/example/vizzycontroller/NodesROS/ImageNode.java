package com.example.vizzycontroller.NodesROS;

import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;

import com.example.vizzycontroller.MainActivity;

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

        rosImageNode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MainActivity.follower)
                    if(MotionEventCompat.getActionMasked(event)==MotionEvent.ACTION_DOWN) {
                        MainActivity.mControllerNode.sendCoordinates(new short[]{(short) event.getX(), (short) event.getY()});
                    }
                return true;
            }
        });

        return rosImageNode;
    }
}
