package com.unclezs.novel.app.views.fragment.profile;

import com.unclezs.novel.app.R;
import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.base.NullPresenter;
import com.unclezs.novel.app.utils.Utils;
import com.unclezs.novel.app.views.activity.RuleManagerActivity;
import com.unclezs.novel.app.views.fragment.components.AboutFragment;
import com.unclezs.novel.app.views.fragment.components.DownloadConfigFragment;
import com.unclezs.novel.app.views.fragment.components.SponsorFragment;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xutil.app.ActivityUtils;

import butterknife.BindView;

/**
 * @author blog.unclezs.com
 * @since 2019-10-30 00:18
 */
@Page(anim = CoreAnim.none)
@SuppressWarnings("NonConstantResourceId")
public class ProfileFragment extends BaseFragment<NullPresenter> implements SuperTextView.OnSuperTextViewClickListener {
    @BindView(R.id.menu_rule_manager)
    SuperTextView ruleManager;
    @BindView(R.id.menu_about)
    SuperTextView menuAbout;
    @BindView(R.id.menu_feedback)
    SuperTextView menuFeedback;
    @BindView(R.id.menu_sponsor)
    SuperTextView menuSponsor;
    @BindView(R.id.menu_download_config)
    SuperTextView menuDownloadConfig;

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
        return R.layout.fragment_profile;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

    }

    @Override
    protected void initListeners() {
        menuAbout.setOnSuperTextViewClickListener(this);
        ruleManager.setOnSuperTextViewClickListener(this);
        menuFeedback.setOnSuperTextViewClickListener(this);
        menuSponsor.setOnSuperTextViewClickListener(this);
        menuDownloadConfig.setOnSuperTextViewClickListener(this);
    }

    @Override
    public void onClick(SuperTextView view) {
        final int id = view.getId();
        switch (id) {
            case R.id.menu_rule_manager:
                ActivityUtils.startActivity(RuleManagerActivity.class);
                break;
            case R.id.menu_feedback:
                Utils.goWeb(requireContext(), getString(R.string.url_feedback));
                break;
            case R.id.menu_about:
                openNewPage(AboutFragment.class);
                break;
            case R.id.menu_sponsor:
                openNewPage(SponsorFragment.class);
                break;
            case R.id.menu_download_config:
                openNewPage(DownloadConfigFragment.class);
                break;
            default:
                break;
        }
    }
}
