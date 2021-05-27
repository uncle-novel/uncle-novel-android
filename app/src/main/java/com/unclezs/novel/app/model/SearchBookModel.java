package com.unclezs.novel.app.model;

import android.annotation.SuppressLint;

import androidx.lifecycle.LifecycleOwner;

import com.unclezs.novel.analyzer.common.concurrent.ThreadUtils;
import com.unclezs.novel.analyzer.common.page.AbstractPageable;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.spider.SearchSpider;
import com.unclezs.novel.app.manager.RuleManager;
import com.unclezs.novel.app.utils.rx.RxLifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cn.hutool.core.collection.ListUtil;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author blog.unclezs.com
 * @date 2021/05/21 0:16
 */
public class SearchBookModel {
    private final LifecycleOwner owner;
    private final StateListener stateListener;
    private final List<SearchSpider> searchers = new ArrayList<>();
    private final Scheduler scheduler;
    private final AtomicInteger counter = new AtomicInteger();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String keyword;

    public SearchBookModel(LifecycleOwner owner, StateListener listener) {
        this.owner = owner;
        this.stateListener = listener;
        scheduler = Schedulers.from(ThreadUtils.newFixedThreadPoolExecutor(5, "searcher-thread"));
    }

    public void doSearch(String keyword, boolean audio) {
        this.keyword = keyword;
        if (!searchers.isEmpty()) {
            compositeDisposable.dispose();
            compositeDisposable = new CompositeDisposable();
            searchers.forEach(AbstractPageable::cancel);
        }
        searchers.clear();
        List<AnalyzerRule> rules = audio ? RuleManager.audioSearchRules() : RuleManager.textSearchRules();
        counter.set(rules.size());
        for (AnalyzerRule rule : rules) {
            SearchSpider searcher = new SearchSpider(ListUtil.of(rule));
            searcher.setOnNewItemAddHandler(stateListener::addMore);
            searchers.add(searcher);
            searchOrLoadMore(searcher, false);
        }
    }

    public synchronized void loadMore() {
        compositeDisposable.dispose();
        compositeDisposable = new CompositeDisposable();
        counter.set(searchers.size());
        searchers.forEach(searchSpider -> searchOrLoadMore(searchSpider, true));
    }

    @SuppressLint("CheckResult")
    public synchronized void searchOrLoadMore(SearchSpider searcher, boolean more) {
        if (searcher.hasMore()) {
            Completable.create(emitter -> {
                if (more) {
                    searcher.loadMore();
                } else {
                    searcher.search(keyword);
                }
                emitter.onComplete();
            }).subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bindLifecycle(owner))
                .doOnSubscribe(compositeDisposable::add)
                .doFinally(() -> {
                    // 如果没有更多了，直接移除
                    if (!searcher.hasMore()) {
                        searchers.remove(searcher);
                    }
                    if (counter.decrementAndGet() == 0) {
                        stateListener.onFinished(hasMore());
                    }
                })
                .subscribe();
        }
    }

    private boolean hasMore() {
        return searchers.stream().anyMatch(AbstractPageable::hasMore);
    }

    public interface StateListener {
        void onFinished(boolean hasMore);

        void addMore(Novel novel);
    }
}
