package com.unclezs.novel.app.core.webview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.just.agentweb.widget.IWebLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.unclezs.novel.app.R;

/**
 * 定义支持下来回弹的WebView
 *
 * @author blog.unclezs.com
 * @since 2019/1/5 上午2:01
 */
public class WebLayout implements IWebLayout {

  private final SmartRefreshLayout mSmartRefreshLayout;
  private final WebView mWebView;

  public WebLayout(Activity activity) {
    mSmartRefreshLayout = (SmartRefreshLayout) LayoutInflater.from(activity).inflate(R.layout.fragment_pulldown_web, null);
    mWebView = mSmartRefreshLayout.findViewById(R.id.webView);
  }

  @NonNull
  @Override
  public ViewGroup getLayout() {
    return mSmartRefreshLayout;
  }

  @Nullable
  @Override
  public WebView getWebView() {
    return mWebView;
  }


}
