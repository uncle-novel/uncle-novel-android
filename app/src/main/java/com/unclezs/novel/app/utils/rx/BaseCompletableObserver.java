package com.unclezs.novel.app.utils.rx;

import org.jetbrains.annotations.NotNull;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

/**
 * @author blog.unclezs.com
 * @date 2021/05/21 1:00
 */
public class BaseCompletableObserver implements CompletableObserver {
    @Override
    public void onSubscribe(@NotNull Disposable d) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(@NotNull Throwable e) {

    }
}
