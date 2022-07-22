package io.agora.rte.extension.bytedance.example;

import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceUtils {
    /**
     * 递归拷贝Asset目录中的文件到rootDir中
     * <p>
     * Recursively copy the files in the Asset directory to rootDir
     */
    public static void copyFileOrDir(AssetManager assets, String assetsName,
                                     String destPath) throws IOException {
        File destFile = new File(destPath, assetsName);
        if (destFile.exists()) {
            return;
        }
        String[] files = assets.list(assetsName);
        if (files != null && files.length > 0) {
            if (!destFile.exists()) {
                if (!destFile.mkdirs()) {
                    throw new IOException("failed to create dir: " + destFile);
                }
            }
            for (String name : files) {
                copyFileOrDir(assets, assetsName + File.separator + name, destPath);
            }
        } else {
            try (InputStream is = assets.open(assetsName)) {
                copyFile(is, destFile);
            }
        }
    }

    private static void copyFile(InputStream is, File destFile) throws IOException {
        if (destFile.exists()) {
            return;
        }
        File parentFile = destFile.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            if (!parentFile.mkdirs()) {
                throw new IOException("failed to create dir: " + parentFile);
            }
        }
        try (FileOutputStream fos = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) >= 0) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.flush();
        }
    }
}
