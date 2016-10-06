package com.example.vizzycontroller.Tools;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.vizzycontroller.MainActivity;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

/**
 * Created by rafael on 04-08-2016.
 */
public class VoiceRecognition {
    private static final String COMMANDS_LABEL   = "commands";
    private static final String COMMAND_HELLO    = "hello little robot";
    private static final String COMMAND_BYE      = "good bye little robot";
    private static final String COMMAND_THANKS   = "thank you little robot";
    private static final String COMMAND_OFFICE   = "show me the office";
    private static final String COMMAND_LAB      = "show me the laboratory";
    private static final String COMMAND_ROOM     = "show me the meeting room";
    private static final String COMMAND_ELEVATOR = "show me the elevator";
    private static final String COMMAND_BATHROOM = "show me the bathroom";

    static public void answer(Hypothesis hypothesis){
        String text = hypothesis.getHypstr();
        if (text!= null) {
            String speech = " ";
            byte command = 0;
            if (text.equals(COMMAND_HELLO)) {
                speech = "Hello i am Vizzy";
                command = 1;
            }
            if (text.equals(COMMAND_BYE)) {
                speech = "See you later";
                command = 2;
            }
            if (text.equals(COMMAND_THANKS)) {
                speech = "You are welcome";
                command = 3;
            }
            if (text.equals(COMMAND_OFFICE)) {
                speech = "I will go to the office";
                command = 4;
            }
            if (text.equals(COMMAND_LAB)) {
                speech = "I will go to the laboratory";
                command = 5;
            }
            if (text.equals(COMMAND_ROOM)) {
                speech = "I will go to the meeting room";
                command = 6;
            }
            if (text.equals(COMMAND_ELEVATOR)) {
                speech = "I will go to the elevator";
                command = 7;
            }
            if (text.equals(COMMAND_BATHROOM)) {
                speech = "I will go to the bathroom";
                command = 8;
            }
            if (command > 0) {
                MainActivity.mControllerNode.sendVoiceCMD(command);
                MainActivity.mVizzyTalk.talk(speech);
            }
            Log.d("Vizzy","You said     : " + text);
            Log.d("Vizzy","The answer is: " + speech);
        }
    }

    static public void startRecognitionLoop() {
        MainActivity.recognizer.stop();
        if(MainActivity.voice)
            MainActivity.recognizer.startListening(COMMANDS_LABEL, 2500);
    }

    static public void runRecognizerSetup(final Activity activity, final RecognitionListener listener) {
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(activity);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir,listener);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null)
                    Log.d("Vizzy","Failed to init recognizer " + result);
                else
                    startRecognitionLoop();
            }
        }.execute();
    }

    private static void setupRecognizer(File assetsDir, RecognitionListener listener) throws IOException {
        MainActivity.recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setRawLogDir(assetsDir)
                .setKeywordThreshold(0.5f)
                .setFloat("-vad_threshold", 3)
                .getRecognizer();
        MainActivity.recognizer.addListener(listener);

        File commandsGrammar = new File(assetsDir, "commands.gram");
        MainActivity.recognizer.addGrammarSearch(COMMANDS_LABEL, commandsGrammar);
    }
}
