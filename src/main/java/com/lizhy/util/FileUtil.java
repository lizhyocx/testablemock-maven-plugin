package com.lizhy.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author: lizhiyang03
 * @description:
 * @date: 2022/7/22 10:10
 */
public class FileUtil {
    public static void writeToFile(File destFile, String content) throws IOException {
        String path = destFile.getAbsolutePath();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (destFile.exists()) {
            destFile.delete();
        }
        destFile.createNewFile();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(destFile))) {
            bw.write(content);
            bw.flush();
        }
    }
}
