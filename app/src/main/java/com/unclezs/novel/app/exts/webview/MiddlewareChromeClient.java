package com.unclezs.novel.app.exts.webview;

import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebView;

import com.just.agentweb.core.client.MiddlewareWebChromeBase;

/**
 * WebChrome（WebChromeClient主要辅助WebView处理JavaScript的对话框、网站图片、网站title、加载进度等）中间件
 * 【浏览器】
 *
 * @author blog.unclezs.com
 * @since 2019/1/4 下午11:31
 */
public class MiddlewareChromeClient extends MiddlewareWebChromeBase {

  public MiddlewareChromeClient() {

  }

  @Override
  public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
    return super.onJsAlert(view, url, message, result);
  }

  @Override
  public void onProgressChanged(WebView view, int newProgress) {
    super.onProgressChanged(view, newProgress);
  }
}
