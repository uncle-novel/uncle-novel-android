package com.unclezs.novel.app.manager;

import android.content.Context;

import java.io.File;

import cn.hutool.core.io.FileUtil;
import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2021/05/23 0:31
 */
@UtilityClass
public class ResourceManager {
    private static File fileDir;

    public static void init(Context context) {
        fileDir = context.getFilesDir();
    }


    /**
     * APP专属资源文件
     *
     * @param path 路径
     * @return 文件
     */
    public static File file(String path) {
        return FileUtil.file(fileDir, path);
    }

    public static String fileString(String path) {
        return FileUtil.readUtf8String(file(path));
    }


    public static void saveString(String content, String path) {
        FileUtil.writeUtf8String(content, file(path));
    }

    public static void saveString(String content, File file) {
        FileUtil.writeUtf8String(content, file);
    }
}
