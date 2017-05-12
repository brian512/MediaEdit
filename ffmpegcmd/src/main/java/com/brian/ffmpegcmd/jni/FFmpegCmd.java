package com.brian.ffmpegcmd.jni;

import android.util.Log;

import com.brian.ffmpegcmd.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ffmpeg命令封装
 * Created by huamm on 16/6/24.
 */
public class FFmpegCmd {
    public static final String TAG = "FFmpegUtils";
    private static final int R_SUCCESS = 0;
    private static final int R_FAILED = -1;
    private static final String STR_DEBUG_PARAM = "-d";

    private static boolean sEnableDebug = false;

    private static boolean sIsBusy = false;

    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("ffmpeg_cmd");
    }

    private native static int run(String[] cmd);

    private static int runSafely(String[] cmd) {
        int result = -1;
        long time = System.currentTimeMillis();
        try {
            result = run(cmd);
            if (sEnableDebug) {
                Log.e("FFmpegCmd", "time=" + (System.currentTimeMillis() - time));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void runSyn(ArrayList<String> cmds, final OnCompletionListener callback) {
        if (sEnableDebug) {
            cmds.add(STR_DEBUG_PARAM);
        }
        if (sIsBusy) {
            throw new IllegalStateException("ffmpeg is busy");
        }
        final String[] commands = cmds.toArray(new String[cmds.size()]);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int result = runSafely(commands);
                callbackResult(result, callback);
            }
        };

        sIsBusy = true;
        new Thread(runnable).start();
    }

    public static void setEnableDebug(boolean enable) {
        sEnableDebug = enable;
    }

    /**
     * Muxing video stream and audio stream.
     * This interface is quite complex which is only for adding audio effect.
     *
     * @param srcVideoName Input video file name.
     * @param fvVolume     Input video volume, should not be negative, default is 1.0f.
     * @param srcAudioName Input audio file name.
     * @param faVolume     Input audio volume, should not be negative, default is 1.0f.
     * @param desVideoName Output video file name.
     * @param callback     Completion callback.
     * @return Negative : Failed
     * else : Success.
     */
    public static boolean mixAV(String srcVideoName, float fvVolume, String srcAudioName, float faVolume,
                                String desVideoName, OnCompletionListener callback) {
        if (srcAudioName == null || srcAudioName.length() <= 0
                || srcVideoName == null || srcVideoName.length() <= 0
                || desVideoName == null || desVideoName.length() <= 0) {
            return false;
        }

        ArrayList<String> cmds = new ArrayList<>();
        cmds.add("ffmpeg");
        cmds.add("-i");
        cmds.add(srcVideoName);
        cmds.add("-i");
        cmds.add(srcAudioName);

        //Copy Video Stream
        cmds.add("-c:v");
        cmds.add("copy");
        cmds.add("-map");
        cmds.add("0:v:0");

        //Deal With Audio Stream
        cmds.add("-strict");
        cmds.add("-2");

        if (fvVolume <= 0.001f) {
            //Replace audio stream
            cmds.add("-c:a");
            cmds.add("aac");

            cmds.add("-map");
            cmds.add("1:a:0");

            cmds.add("-shortest");

            if (faVolume < 0.99 || faVolume > 1.01) {
                cmds.add("-vol");
                cmds.add(String.valueOf((int) (faVolume * 100)));
            }
        } else if (fvVolume > 0.001f && faVolume > 0.001f) {
            //Merge audio streams
            cmds.add("-filter_complex");
            cmds.add(String.format("[0:a]aformat=sample_fmts=fltp:sample_rates=48000:channel_layouts=stereo,volume=%f[a0]; " +
                    "[1:a]aformat=sample_fmts=fltp:sample_rates=48000:channel_layouts=stereo,volume=%f[a1];" +
                    "[a0][a1]amix=inputs=2:duration=first[aout]", fvVolume, faVolume));

            cmds.add("-map");
            cmds.add("[aout]");

        } else {
            Log.w(TAG, String.format(Locale.getDefault(), "Illigal volume : SrcVideo = %.2f, SrcAudio = %.2f", fvVolume, faVolume));
            if (callback != null) {
                callback.onCompletion(R_FAILED == 1);
            }
        }

        cmds.add("-f");
        cmds.add("mp4");
        cmds.add("-y");
        cmds.add("-movflags");
        cmds.add("faststart");
        cmds.add(desVideoName);

        runSyn(cmds, callback);
        return true;
    }


    public static void setPlaybackSpeed(String srcVideoName, float speed, String desVideoName, OnCompletionListener callback) {
        ArrayList<String> cmds = new ArrayList<>();
        cmds.add("ffmpeg");
        cmds.add("-i");
        cmds.add(srcVideoName);

        cmds.add("-y");
        cmds.add("-filter_complex");
        cmds.add("[0:v]setpts=" + speed + "*PTS[v];[0:a]atempo=" + 1 / speed + "[a]");
        cmds.add("-map");
        cmds.add("[v]");
        cmds.add("-map");
        cmds.add("[a]");
        cmds.add(desVideoName);

        runSyn(cmds, callback);
    }

    public static void rotateVideo(String srcVideoName, String desVideoName, OnCompletionListener callback) {
        ArrayList<String> cmds = new ArrayList<>();
        cmds.add("ffmpeg");
        cmds.add("-i");
        cmds.add(srcVideoName);

        cmds.add("-vf");
//        cmds.add("transpose=1:portrait");
        cmds.add("rotate=PI/2");
        cmds.add(desVideoName);

        runSyn(cmds, callback);
    }

    public static void addWaterMark(String srcVideoName, String waterMarkPath,
                                   String desVideoName, OnCompletionListener callback) {
        ArrayList<String> cmds = new ArrayList<>();
        cmds.add("ffmpeg");
        cmds.add("-i");
        cmds.add(srcVideoName);
        cmds.add("-i");
        cmds.add(waterMarkPath);

        cmds.add("-y");
        cmds.add("-filter_complex");
        cmds.add("[0:v][1:v]overlay=main_w-overlay_w-10:main_h-overlay_h-10[out]");
        cmds.add("-map");
        cmds.add("[out]");
        cmds.add("-map");
        cmds.add("0:a");
        cmds.add("-codec:a"); // keep audio
        cmds.add("copy");
        cmds.add(desVideoName);

        runSyn(cmds, callback);
    }

    public static void buildGif(String videoPath, String gifPath, OnCompletionListener callback) {
        ArrayList<String> cmds = new ArrayList<>();
        cmds.add("ffmpeg");
        cmds.add("-i");
        cmds.add(videoPath);

        cmds.add("-f");
        cmds.add("gif");
        cmds.add(gifPath);

        runSyn(cmds, callback);
    }

    public static boolean combineVideo(List<String> videoPathList, String desVideoName) {
        String tmpFile = "/sdcard/videolist.txt";
        String content = "ffconcat version 1.0\n";

        for (String path : videoPathList) {
            content += "\nfile " + path;
        }

        FileUtil.writeFile(tmpFile, content, false);

        ArrayList<String> cmds = new ArrayList<>();
        cmds.add("ffmpeg");
        cmds.add("-y");

        cmds.add("-safe");
        cmds.add("0");

        cmds.add("-f");
        cmds.add("concat");

        cmds.add("-i");
        cmds.add(tmpFile);


        cmds.add("-c");
        cmds.add("copy");

        cmds.add(desVideoName);

        if (sEnableDebug) {
            cmds.add(STR_DEBUG_PARAM);
        }

        String[] commands = cmds.toArray(new String[cmds.size()]);
        int result = runSafely(commands);
        FileUtil.deleteFile(tmpFile);

        return result == 1;
    }

    private static void callbackResult(int result, OnCompletionListener listener) {
        Log.d("FFmpegCmd", "result=" + result);
        if (listener != null) {
            listener.onCompletion(result == 1); // 处理成功返回1
        }
        sIsBusy = false;
    }

    public interface OnCompletionListener {
        void onCompletion(boolean result);
    }
}
