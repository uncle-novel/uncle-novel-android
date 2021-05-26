package com.unclezs.novel.app.spi;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.request.spi.HttpProvider;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.xuexiang.xutil.XUtil;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author blog.unclezs.com
 * @date 2021/05/24 15:17
 */
public class WebViewHttpClient implements HttpProvider {
    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public String content(RequestParams requestParams) throws IOException {
        AtomicReference<String> result = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<WebView> webViewRef = new AtomicReference<>();
        XUtil.runOnUiThread(() -> {
            WebView webView = new WebView(XUtil.getContext());
            webViewRef.set(webView);
            webView.getSettings().setJavaScriptEnabled(true);
            // 设置cookie
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setCookie(UrlUtils.getSite(requestParams.getUrl()), requestParams.getHeader(RequestParams.COOKIE));
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    webView.evaluateJavascript("document.documentElement.outerHTML", html -> {
                        result.set(StringEscapeUtils.unescapeJson(html));
                        System.out.println(result.get());
                        countDownLatch.countDown();
                    });
                }
            });
            webView.loadUrl(requestParams.getUrl());
        });
        try {
            boolean success = countDownLatch.await(30000, TimeUnit.MILLISECONDS);
            if (success) {
                return result.get();
            } else {
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            XUtil.runOnUiThread(() -> webViewRef.get().destroy());
            XUtil.runOnUiThread(() -> webViewRef.get().destroy());
        }
        return result.get();
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
