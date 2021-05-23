package com.unclezs.novel.app.base;

/**
 * @author blog.unclezs.com
 * @date 2021/05/21 12:09
 */
public abstract class BasePresenter<V extends IView> implements IPresenter {
    protected V view;

    @Override
    @SuppressWarnings("unchecked")
    public void attachView(IView view) {
        this.view = (V) view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }
}
