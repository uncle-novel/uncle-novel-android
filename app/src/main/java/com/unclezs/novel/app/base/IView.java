package com.unclezs.novel.app.base;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

/**
 * @author blog.unclezs.com
 * @date 2021/05/21 12:06
 */
public interface IView {
    Context getContext();

    LifecycleOwner getLifecycleOwner();
}
