package com.unclezs.novel.app.utils.rx;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author blog.unclezs.com
 * @date 2021/05/20 14:54
 */
public class BaseObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(@NotNull Disposable d) {

    }

    @Override
    public void onNext(@NotNull T t) {

    }

    @Override
    public void onError(@NotNull Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
