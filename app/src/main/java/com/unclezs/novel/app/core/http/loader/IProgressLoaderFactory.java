package com.unclezs.novel.app.core.http.loader;

import android.content.Context;

import com.xuexiang.xhttp2.subsciber.impl.IProgressLoader;

/**
 * IProgressLoader的创建工厂实现接口
 *
 * @author blog.unclezs.com
 * @since 2019-11-18 23:17
 */
public interface IProgressLoaderFactory {


  /**
   * 创建进度加载者
   *
   * @param context
   * @return
   */
  IProgressLoader create(Context context);


  /**
   * 创建进度加载者
   *
   * @param context
   * @param message 默认提示
   * @return
   */
  IProgressLoader create(Context context, String message);
}
