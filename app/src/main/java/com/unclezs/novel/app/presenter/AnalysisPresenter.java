package com.unclezs.novel.app.presenter;

import android.os.Handler;
import android.os.Looper;

import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.spider.TocSpider;
import com.unclezs.novel.app.base.BasePresenter;
import com.unclezs.novel.app.manager.RuleManager;
import com.unclezs.novel.app.utils.rx.RxUtils;
import com.unclezs.novel.app.views.fragment.analysis.AnalysisFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import lombok.Getter;

/**
 * @author blog.unclezs.com
 * @date 2021/05/21 15:40
 */
public class AnalysisPresenter extends BasePresenter<AnalysisFragment> {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final AtomicBoolean loading = new AtomicBoolean(false);
    private Disposable analysisDisposable;
    private TocSpider spider;
    @Getter
    private AnalyzerRule rule;
    @Getter
    private Novel novel;
    private String url;

    public void doAnalysis(String url) {
        this.url = url;
        if (analysisDisposable != null && !analysisDisposable.isDisposed()) {
            analysisDisposable.dispose();
            spider.cancel();
        }
        rule = RuleManager.getOrDefault(url);
        spider = new TocSpider(rule);
        spider.setOnNewItemAddHandler(chapter -> handler.post(() -> view.addMore(chapter)));
        loading.set(true);
        loadMore(true);
    }

    public void loadMore(boolean first) {
        if (analysisDisposable != null && !analysisDisposable.isDisposed()) {
            return;
        }
        // 执行解析
        analysisDisposable = Completable.create(emitter -> {
            if (first) {
                spider.toc(url);
                novel = spider.getNovel();
                if (StringUtils.isBlank(rule.getName())) {
                    rule.setName(novel.getTitle());
                }
            } else {
                spider.loadMore();
            }
            // 自动翻页
            while (Boolean.TRUE.equals(rule.getToc().getAutoNext()) && spider.hasMore() && loading.get()) {
                spider.loadMore();
            }
            loading.set(false);
            emitter.onComplete();
        }).compose(o -> RxUtils.threadTrans(o, view))
            .doOnError(Throwable::printStackTrace)
            .doFinally(() -> view.analysisFinished(spider.hasMore())).subscribe();
    }

    public boolean isLoading() {
        return loading.get();
    }
}
