package com.unclezs.novel.app.core.http.loader;

import android.content.Context;

import com.xuexiang.xhttp2.subsciber.impl.IProgressLoader;

/**
 * 创建进度加载者
 *
 * @author blog.unclezs.com
 * @since 2019-07-02 12:51
 */
public final class ProgressLoader {

  private static IProgressLoaderFactory sIProgressLoaderFactory = new MiniProgressLoaderFactory();

  private ProgressLoader() {
    throw new UnsupportedOperationException("u can't instantiate me...");
  }

  public static void setIProgressLoaderFactory(IProgressLoaderFactory sIProgressLoaderFactory) {
    ProgressLoader.sIProgressLoaderFactory = sIProgressLoaderFactory;
  }

  /**
   * 创建进度加载者
   *
   * @param context
   * @return
   */
  public static IProgressLoader create(Context context) {
    return sIProgressLoaderFactory.create(context);
  }

  /**
   * 创建进度加载者
   *
   * @param context
   * @param message 默认提示信息
   * @return
   */
  public static IProgressLoader create(Context context, String message) {
    return sIProgressLoaderFactory.create(context, message);
  }
}
