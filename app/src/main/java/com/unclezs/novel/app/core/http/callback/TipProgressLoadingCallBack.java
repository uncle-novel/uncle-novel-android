package com.unclezs.novel.app.core.http.callback;

import androidx.annotation.NonNull;

import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.utils.XToastUtils;
import com.xuexiang.xhttp2.callback.ProgressLoadingCallBack;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.model.XHttpRequest;
import com.xuexiang.xhttp2.subsciber.impl.IProgressLoader;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.common.logger.Logger;

/**
 * 带错误toast提示和加载进度条的网络请求回调
 *
 * @author blog.unclezs.com
 * @since 2019-11-18 23:16
 */
public abstract class TipProgressLoadingCallBack<T> extends ProgressLoadingCallBack<T> {
  /**
   * 记录一下请求的url,确定出错的请求是哪个请求
   */
  private String mUrl;

  public TipProgressLoadingCallBack(BaseFragment fragment) {
    super(fragment.getProgressLoader());
  }

  public TipProgressLoadingCallBack(IProgressLoader iProgressLoader) {
    super(iProgressLoader);
  }

  public TipProgressLoadingCallBack(@NonNull XHttpRequest req, IProgressLoader iProgressLoader) {
    this(req.getUrl(), iProgressLoader);
  }

  public TipProgressLoadingCallBack(String url, IProgressLoader iProgressLoader) {
    super(iProgressLoader);
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
