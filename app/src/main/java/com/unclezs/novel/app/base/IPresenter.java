package com.unclezs.novel.app.base;

import android.view.View;

/**
 * @author blog.unclezs.com
 * @date 2021/05/21 12:06
 */
public interface IPresenter {
    void attachView(IView view);

    void detachView();
}
