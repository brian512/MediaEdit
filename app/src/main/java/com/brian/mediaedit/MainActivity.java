package com.brian.mediaedit;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.brian.recorder.jni.FFmpegCmd;

public class MainActivity extends AppCompatActivity {

    private static final int FLAG_CHOOSE_FILE = 1;


    private TextView mFilePathView;

    private VideoView mVideoView;

    private String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FFmpegCmd.setEnableDebug(true);

        mFilePathView = (TextView) findViewById(R.id.file_path);
        mVideoView = (VideoView) findViewById(R.id.videoview);

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.start();
            }
        });

        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });

        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setPlaybackSpeed();
                return false;
            }
        });

        Button button = (Button) findViewById(R.id.choose_file);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                startActivityForResult(intent, FLAG_CHOOSE_FILE);
            }
        });

    }

    private void setPlaybackSpeed() {
        final String destPath = mFilePath + ".mp4";
        FFmpegCmd.setPlaybackSpeed(mFilePath, 2f, destPath, new FFmpegCmd.OnCompletionListener() {
            @Override
            public void onCompletion(boolean result) {
                Log.d("MainActivity", "result=" + result);
                mFilePathView.setText(destPath);
                mVideoView.setVideoPath(destPath);
                mVideoView.start();
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FLAG_CHOOSE_FILE) {
                Uri uri = data.getData();
                Log.d("MainActivity", "uri=" + uri.toString());
                mFilePath = ContentUriUtil.getPath(this, uri);
                Log.d("MainActivity", "filePath=" + mFilePath);


                mFilePathView.setText(mFilePath);
                mVideoView.setVideoURI(uri);
                mVideoView.start();
            }
        }
    }
}
