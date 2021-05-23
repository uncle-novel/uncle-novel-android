package com.unclezs.novel.app.utils.sdkinit;

import android.app.Application;
import android.content.Context;

import com.unclezs.novel.app.App;
import com.unclezs.novel.app.utils.update.CustomUpdateDownloader;
import com.unclezs.novel.app.utils.update.CustomUpdateFailureListener;
import com.unclezs.novel.app.utils.update.XHttpUpdateHttpServiceImpl;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.utils.UpdateUtils;

/**
 * XUpdate 版本更新 SDK 初始化
 *
 * @author blog.unclezs.com
 * @since 2019-06-18 15:51
 */
public final class XUpdateInit {

  /**
   * 应用版本更新的检查地址
   */
  private static final String KEY_UPDATE_URL = "";

  private XUpdateInit() {
    throw new UnsupportedOperationException("u can't instantiate me...");
  }

  public static void init(Application application) {
    XUpdate.get()
            .debug(App.isDebug())
            //默认设置只在wifi下检查版本更新
            .isWifiOnly(false)
            //默认设置使用get请求检查版本
            .isGet(true)
            //默认设置非自动模式，可根据具体使用配置
            .isAutoMode(false)
            //设置默认公共请求参数
            .param("versionCode", UpdateUtils.getVersionCode(application))
            .param("appKey", application.getPackageName())
            //这个必须设置！实现网络请求功能。
            .setIUpdateHttpService(new XHttpUpdateHttpServiceImpl())
            .setIUpdateDownLoader(new CustomUpdateDownloader())
            //这个必须初始化
            .init(application);
  }

  /**
   * 进行版本更新检查
   */
  public static void checkUpdate(Context context, boolean needErrorTip) {
    XUpdate.newBuild(context).updateUrl(KEY_UPDATE_URL).update();
    XUpdate.get().setOnUpdateFailureListener(new CustomUpdateFailureListener(needErrorTip));
  }
}
