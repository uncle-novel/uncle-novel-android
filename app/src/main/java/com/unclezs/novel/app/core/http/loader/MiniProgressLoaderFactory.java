package com.unclezs.novel.app.core.http.loader;

import android.content.Context;

import com.xuexiang.xhttp2.subsciber.impl.IProgressLoader;

/**
 * 迷你加载框创建工厂
 *
 * @author blog.unclezs.com
 * @since 2019-11-18 23:23
 */
public class MiniProgressLoaderFactory implements IProgressLoaderFactory {

  @Override
  public IProgressLoader create(Context context) {
    return new MiniLoadingDialogLoader(context);
  }

  @Override
  public IProgressLoader create(Context context, String message) {
    return new MiniLoadingDialogLoader(context, message);
  }
}
