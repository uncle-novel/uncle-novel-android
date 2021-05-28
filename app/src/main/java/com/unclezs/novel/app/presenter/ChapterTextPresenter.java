package com.unclezs.novel.app.presenter;

import android.annotation.SuppressLint;

import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.spider.NovelSpider;
import com.unclezs.novel.app.base.BasePresenter;
import com.unclezs.novel.app.manager.RuleManager;
import com.unclezs.novel.app.utils.rx.RxUtils;
import com.unclezs.novel.app.views.fragment.components.ChapterTextFragment;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;

/**
 * @author blog.unclezs.com
 * @date 2021/05/22 13:39
 */
public class ChapterTextPresenter extends BasePresenter<ChapterTextFragment> {
    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void loadContent(Chapter chapter) {
        String url = chapter.getUrl();
        view.showLoading(true);
        Single.create((SingleEmitter<String> emitter) -> {
            NovelSpider spider = new NovelSpider(RuleManager.getOrDefault(url));
            String content = spider.content(url);
            emitter.onSuccess(content);
        }).compose(s -> RxUtils.threadTrans(s, view))
            .doFinally(() -> view.showLoading(false))
            .subscribe(content -> {
                chapter.setContent(content);
                view.onContentLoaded(true, chapter);
            }, e -> view.onContentLoaded(false, chapter));
    }
}
