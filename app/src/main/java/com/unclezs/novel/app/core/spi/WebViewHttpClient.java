package com.unclezs.novel.app.core.spi;

import android.annotation.SuppressLint;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.request.spi.HttpProvider;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.xuexiang.xutil.XUtil;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
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
    public String content(RequestParams params) throws IOException {
        AtomicReference<String> result = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<WebView> webViewRef = new AtomicReference<>();
        XUtil.runOnUiThread(() -> {
            WebView webView = new WebView(XUtil.getContext());
            webViewRef.set(webView);
            // 非默认UA 则设置UA
            if (StringUtils.isNotBlank(params.getUrl()) || !params.getHeader(RequestParams.USER_AGENT).equals(RequestParams.USER_AGENT_DEFAULT_VALUE)) {
                webView.getSettings().setUserAgentString(params.getHeader(RequestParams.USER_AGENT));
            }
            webView.getSettings().setJavaScriptEnabled(true);
            // 设置cookie
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setCookie(UrlUtils.getSite(params.getUrl()), params.getHeader(RequestParams.COOKIE));
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    // 有脚本则执行脚本
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            XUtil.runOnUiThread(() -> {
                                if (StringUtils.isNotBlank(params.getScript())) {
                                    webView.evaluateJavascript(params.getScript(), ret -> {
                                        result.set(StringUtils.removeQuote(ret));
                                        countDownLatch.countDown();
                                    });
                                } else {
                                    webView.evaluateJavascript("document.documentElement.outerHTML", html -> {
                                        result.set(StringEscapeUtils.unescapeJson(html));
                                        countDownLatch.countDown();
                                    });
                                }
                            });
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, params.getDynamicDelayTime() == null ? 500L : params.getDynamicDelayTime());
                }
            });
            webView.loadUrl(params.getUrl());
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
        }
        return result.get();
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
