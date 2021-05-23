package com.unclezs.novel.app.views.fragment.download;

import com.unclezs.novel.app.R;
import com.unclezs.novel.app.base.BaseFragment;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;

/**
 * @author blog.unclezs.com
 * @since 2019-10-30 00:19
 */
@Page(anim = CoreAnim.none)
public class DownloadFragment extends BaseFragment {

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rule_manager;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

    }

//    @OnClick(R.id.analysis)
//    public void to() {
//        PageOption.to(AnalysisFragment.class)
//            .setNewActivity(true)
//            .putBoolean(AnalysisFragment.KEY_SHOW_TITLE_BAR, true)
//            .putSerializable(AnalysisFragment.KEY_NOVEL_INFO, new Novel())
//            .setAnim(CoreAnim.slide)
//            .open(this);
//    }
}
