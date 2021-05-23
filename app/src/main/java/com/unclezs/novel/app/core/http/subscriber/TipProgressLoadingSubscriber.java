package com.unclezs.novel.app.core.http.subscriber;

import androidx.annotation.NonNull;

import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.utils.XToastUtils;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.model.XHttpRequest;
import com.xuexiang.xhttp2.subsciber.ProgressLoadingSubscriber;
import com.xuexiang.xhttp2.subsciber.impl.IProgressLoader;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.common.logger.Logger;

/**
 * 带错误toast提示和加载进度条的网络请求订阅
 *
 * @author blog.unclezs.com
 * @since 2019-11-18 23:11
 */
public abstract class TipProgressLoadingSubscriber<T> extends ProgressLoadingSubscriber<T> {

  /**
   * 记录一下请求的url,确定出错的请求是哪个请求
   */
  private String mUrl;

  public TipProgressLoadingSubscriber() {
    super();
  }

  public TipProgressLoadingSubscriber(BaseFragment fragment) {
    super(fragment.getProgressLoader());
  }

  public TipProgressLoadingSubscriber(IProgressLoader iProgressLoader) {
    super(iProgressLoader);
  }

  public TipProgressLoadingSubscriber(@NonNull XHttpRequest req) {
    this(req.getUrl());
  }

  public TipProgressLoadingSubscriber(String url) {
    super();
    mUrl = url;
  }

  @Override
  public void onError(ApiException e) {
    super.onError(e);
    XToastUtils.error(e);
    if (!StringUtils.isEmpty(mUrl)) {
      Logger.e("网络请求的url:" + mUrl, e);
    } else {
      Logger.e(e);
    }
  }
}
