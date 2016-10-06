package com.example.vizzycontroller.Tools;

import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.example.vizzycontroller.MainActivity;

import java.util.Locale;

/**
 * Created by rafael on 02-08-2016.
 */
public class VizzyTalk {
    private Context context;
    private TextToSpeech speech;
    private Vibrator vibrator;
    Handler messageHandler = new Handler();

    public VizzyTalk(Context context, Vibrator vibrator){
        this.context=context;
        this.vibrator=vibrator;

        speech=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR)
                    speech.setLanguage(Locale.UK);
            }
        });
    }

    public void talk(final String speechString) {
        Runnable doTalk = new Runnable() {
            public void run() {
                if (MainActivity.audio)
                    speech.speak(speechString, TextToSpeech.QUEUE_FLUSH, null);
                Toast.makeText(context, speechString, Toast.LENGTH_SHORT).show();
            }
        };
        messageHandler.post(doTalk);
    }

    public void vibrate(int miliseconds){
        vibrator.vibrate(miliseconds);
    }

    public void shutdown(){
        speech.stop();
        speech.shutdown();
    }
}
