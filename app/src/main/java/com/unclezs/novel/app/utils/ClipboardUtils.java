package com.unclezs.novel.app.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.unclezs.novel.app.App;
import com.xuexiang.xutil.XUtil;

import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2021/05/25 13:37
 */
@UtilityClass
public class ClipboardUtils {
    public String get() {
        ClipboardManager manager = (ClipboardManager) XUtil.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null) {
            return null;
        }
        ClipData clip = manager.getPrimaryClip();
        if (clip.getItemCount() > 0) {
            return clip.getItemAt(0).getText().toString();
        }
        return null;
    }

    public void set(String value) {
        ClipboardManager manager = (ClipboardManager) XUtil.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null) {
            return;
        }
        manager.setPrimaryClip(ClipData.newPlainText(null, value));
    }
}
