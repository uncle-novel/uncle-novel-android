package com.unclezs.novel.app.base;

import android.content.Context;
import android.os.Bundle;

import androidx.lifecycle.LifecycleOwner;

import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.core.CoreSwitchBean;
import com.xuexiang.xrouter.facade.service.SerializationService;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.slideback.SlideBack;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * 基础容器Activity
 *
 * @author XUE
 * @since 2019/3/22 11:21
 */
public class BaseActivity<P extends IPresenter> extends XPageActivity implements IView {

    /**
     * 是否支持侧滑返回
     */
    public static final String KEY_SUPPORT_SLIDE_BACK = "key_support_slide_back";
    protected P presenter;
    Unbinder unbinder;

    @Override
    protected void attachBaseContext(Context newBase) {
        //注入字体
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initStatusBarStyle();
        super.onCreate(savedInstanceState);
        unbinder = ButterKnife.bind(this);
        registerSlideBack();

        presenter = createPresenter();
        attachView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachView();
    }

    /**
     * 绑定pv
     */
    private void attachView() {
        if (presenter != null) {
            presenter.attachView(this);
        }
    }

    /**
     * 解绑pv
     */
    private void detachView() {
        if (presenter != null) {
            presenter.detachView();
        }
    }

    /**
     * 创建presenter
     *
     * @return presenter
     */
    private P createPresenter() {
        // 创建presenter
        return null;
    }

    /**
     * 初始化状态栏的样式
     */
    protected void initStatusBarStyle() {

    }

    /**
     * 打开fragment
     *
     * @param clazz          页面类
     * @param addToBackStack 是否添加到栈中
     * @return 打开的fragment对象
     */
    @SuppressWarnings("unchecked")
    public <T extends XPageFragment> T openPage(Class<T> clazz, boolean addToBackStack) {
        CoreSwitchBean page = new CoreSwitchBean(clazz)
            .setAddToBackStack(addToBackStack);
        return (T) openPage(page);
    }

    /**
     * 打开fragment
     *
     * @return 打开的fragment对象
     */
    @SuppressWarnings("unchecked")
    public <T extends XPageFragment> T openNewPage(Class<T> clazz) {
        CoreSwitchBean page = new CoreSwitchBean(clazz)
            .setNewActivity(true);
        return (T) openPage(page);
    }

    /**
     * 切换fragment
     *
     * @param clazz 页面类
     * @return 打开的fragment对象
     */
    public <T extends XPageFragment> T switchPage(Class<T> clazz) {
        return openPage(clazz, false);
    }

    /**
     * 序列化对象
     *
     * @param object
     * @return
     */
    public String serializeObject(Object object) {
        return XRouter.getInstance().navigation(SerializationService.class).object2Json(object);
    }

    @Override
    protected void onRelease() {
        unbinder.unbind();
        unregisterSlideBack();
        super.onRelease();
    }

    /**
     * 注册侧滑回调
     */
    protected void registerSlideBack() {
        if (isSupportSlideBack()) {
            SlideBack.with(this)
                .haveScroll(true)
                .edgeMode(ResUtils.isRtl() ? SlideBack.EDGE_RIGHT : SlideBack.EDGE_LEFT)
                .callBack(this::popPage)
                .register();
        }
    }

    /**
     * 注销侧滑回调
     */
    protected void unregisterSlideBack() {
        if (isSupportSlideBack()) {
            SlideBack.unregister(this);
        }
    }

    /**
     * @return 是否支持侧滑返回
     */
    protected boolean isSupportSlideBack() {
        CoreSwitchBean page = getIntent().getParcelableExtra(CoreSwitchBean.KEY_SWITCH_BEAN);
        return page == null || page.getBundle() == null || page.getBundle().getBoolean(KEY_SUPPORT_SLIDE_BACK, true);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public LifecycleOwner getLifecycleOwner() {
        return this;
    }
}
