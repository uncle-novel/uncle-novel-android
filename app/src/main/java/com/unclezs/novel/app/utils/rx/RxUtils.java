package com.unclezs.novel.app.utils.rx;

import androidx.lifecycle.LifecycleOwner;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2021/05/21 0:26
 */
@UtilityClass
public class RxUtils {

    @NotNull
    public static <T> Observable<T> threadTrans(Observable<T> upstream) {
        return upstream.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NotNull
    public static <T> Observable<T> threadTrans(Observable<T> upstream, LifecycleOwner owner) {
        return threadTrans(upstream).compose(RxLifecycle.bindLifecycle(owner));
    }

    @NotNull
    public static <T> Single<T> threadTrans(Single<T> upstream) {
        return upstream.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NotNull
    public static <T> Single<T> threadTrans(Single<T> upstream, LifecycleOwner owner) {
        return threadTrans(upstream).compose(RxLifecycle.bindLifecycle(owner));
    }

    @NotNull
    public static Completable threadTrans(Completable upstream) {
        return upstream.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NotNull
    public static Completable threadTrans(Completable upstream, LifecycleOwner owner) {
        return threadTrans(upstream).compose(RxLifecycle.bindLifecycle(owner));
    }
}
