package com.brian.mediaedit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.joyodream.recorder.jni.FFmpegCmd;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FFmpegCmd.setEnableDebug(true);
        final String folder = "/storage/emulated/0/DCIM";

        FFmpegCmd.setPlaybackSpeed(folder + "/video.mp4", 2f, folder + "/video_new.mp4", new FFmpegCmd.OnCompletionListener() {
            @Override
            public void onCompletion(boolean result) {
                Log.d("MainActivity", "result=" + result);
            }
        });
    }
}
