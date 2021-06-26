package com.unclezs.novel.app.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2021/06/26 23:26
 */
@UtilityClass
public class PermissionHelper {

    public static void onGrantedDiskPermission(Activity activity, Runnable callback) {
        // 运行设备>=Android 11.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            new MaterialDialog.Builder(activity)
                .title("下载需要获取文件存储权限")
                .positiveText("确定")
                .negativeText("取消")
                .onPositive((d, w) -> activity.startActivityForResult(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION), 101))
                .cancelable(true).show();
        } else {
            callback.run();
        }
    }
}
