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

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import lombok.Getter;

/**
 * @author blog.unclezs.com
 * @date 2021/05/21 15:40
 */
public class AnalysisPresenter extends BasePresenter<AnalysisFragment> {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Disposable analysisDisposable;
    private TocSpider spider;
    @Getter
    private AnalyzerRule rule;
    @Getter
    private Novel novel;

    public void doAnalysis(String url) {
        if (analysisDisposable != null && !analysisDisposable.isDisposed()) {
            analysisDisposable.dispose();
            spider.cancel();
        }
        rule = RuleManager.getOrDefault(url);
        spider = new TocSpider(rule);
        spider.setOnNewItemAddHandler(chapter -> handler.post(() -> view.addMore(chapter)));
        // 执行解析
        analysisDisposable = Completable.create(emitter -> {
            spider.toc(url);
            novel = spider.getNovel();
            rule.setName(novel.getTitle());
            emitter.onComplete();
        }).compose(o -> RxUtils.threadTrans(o, view))
            .doFinally(() -> view.analysisFinished()).subscribe();
    }
}
