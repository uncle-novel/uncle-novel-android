package com.unclezs.novel.app.utils.rx;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.disposables.CompositeDisposable;

/**
 * @author blog.unclezs.com
 * @date 2021/05/21 0:32
 */
public class RxLifecycle<T> implements LifecycleObserver, ObservableTransformer<T, T>, CompletableTransformer, SingleTransformer<T,T> {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static <T> RxLifecycle<T> bindLifecycle(LifecycleOwner owner) {
        RxLifecycle<T> lifecycle = new RxLifecycle<>();
        owner.getLifecycle().addObserver(lifecycle);
        return lifecycle;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @NotNull
    @Override
    public ObservableSource<T> apply(@NotNull Observable<T> upstream) {
        return upstream.doOnSubscribe(compositeDisposable::add);
    }

    @NotNull
    @Override
    public CompletableSource apply(@NotNull Completable upstream) {
        return upstream.doOnSubscribe(compositeDisposable::add);
    }

    @NotNull
    @Override
    public SingleSource<T> apply(@NotNull Single<T> upstream) {
        return upstream.doOnSubscribe(compositeDisposable::add);
    }
}
