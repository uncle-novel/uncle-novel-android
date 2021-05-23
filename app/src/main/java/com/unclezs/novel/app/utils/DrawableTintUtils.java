package com.unclezs.novel.app.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

import lombok.experimental.UtilityClass;

/**
 * Drawable 颜色工具
 *
 * @author blog.unclezs.com
 * @date 2021/5/19 23:25
 */
@UtilityClass
public class DrawableTintUtils {

    /**
     * Drawable 颜色转化类
     *
     * @param drawable 源Drawable
     * @param color    资源
     * @return 改变颜色后的Drawable
     */
    public static Drawable tintDrawable(@NonNull Drawable drawable, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    /**
     * Drawable 颜色转化类
     *
     * @param drawable 源Drawable
     * @param colors   颜色
     * @return 改变颜色后的Drawable
     */
    public static Drawable tintListDrawable(@NonNull Drawable drawable, ColorStateList colors) {
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }
}

