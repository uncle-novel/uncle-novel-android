package com.unclezs.novel.app.utils.sdkinit;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;

import com.unclezs.novel.app.App;
import com.unclezs.novel.app.BuildConfig;
import com.unclezs.novel.app.R;
import com.unclezs.novel.app.utils.update.CustomUpdateFailureListener;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.easy.EasyUpdate;
import com.xuexiang.xupdate.utils.UpdateUtils;
import com.xuexiang.xutil.resource.ResUtils;

/**
 * XUpdate 版本更新 SDK 初始化
 *
 * @author blog.unclezs.com
 * @since 2019-06-18 15:51
 */
public final class XUpdateInit {

    private XUpdateInit() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void init(Application application) {
        EasyUpdate.getUpdateConfig(application)
            .setIsDebug(App.isDebug())
            // 默认设置只在wifi下检查版本更新
            .setIsWifiOnly(false)
            // 默认设置使用get请求检查版本
            .setIsGet(true)
            // 默认设置非自动模式，可根据具体使用配置
            .setIsAutoMode(false)
            // 设置默认公共请求参数
            .setParam("versionCode", UpdateUtils.getVersionCode(application))
            .setParam("appKey", application.getPackageName());
    }

    /**
     * 进行版本更新检查
     */
    public static void checkUpdate(Context context, boolean needErrorTip) {
        XUpdate.get().setOnUpdateFailureListener(new CustomUpdateFailureListener(needErrorTip));
        XUpdate.newBuild(context)
            .updateUrl(BuildConfig.APP_UPDATE_URL)
            .promptThemeColor(ResUtils.getColor(R.color.update_theme_color))
            .promptButtonTextColor(Color.WHITE)
            .promptTopResId(R.mipmap.bg_update_top)
            .promptWidthRatio(0.7F)
            .update();

    }
}
