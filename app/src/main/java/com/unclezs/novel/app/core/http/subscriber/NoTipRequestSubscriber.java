package com.unclezs.novel.app.core.http.subscriber;

import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.model.XHttpRequest;
import com.xuexiang.xhttp2.subsciber.BaseSubscriber;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.common.logger.Logger;

/**
 * 不带错误toast提示的网络请求订阅，只存储错误的日志
 *
 * @author blog.unclezs.com
 * @since 2019-11-18 23:11
 */
public abstract class NoTipRequestSubscriber<T> extends BaseSubscriber<T> {

  /**
   * 记录一下请求的url,确定出错的请求是哪个请求
   */
  private String mUrl;

  public NoTipRequestSubscriber() {

  }

  public NoTipRequestSubscriber(XHttpRequest req) {
    this(req.getUrl());
  }

  public NoTipRequestSubscriber(String url) {
    mUrl = url;
  }

  @Override
  public void onError(ApiException e) {
    if (!StringUtils.isEmpty(mUrl)) {
      Logger.e("网络请求的url:" + mUrl, e);
    } else {
      Logger.e(e);
    }
  }
}
