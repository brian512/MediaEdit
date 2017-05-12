package com.brian.ffmpegcmd.util;

import android.text.TextUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by huamm on 2017/3/21 0021.
 */

public class FileUtil {

    public static void writeFile(String path, String content, boolean append) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(path, append);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        boolean ret = false;

        File file = new File(path);
        if (file.exists() && file.isFile()) {
            ret = file.delete();
        }
        return ret;
    }
}
