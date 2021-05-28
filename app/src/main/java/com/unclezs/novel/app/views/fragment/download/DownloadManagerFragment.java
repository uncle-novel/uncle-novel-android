package com.unclezs.novel.app.views.fragment.download;

import android.annotation.SuppressLint;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.unclezs.novel.app.R;
import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.base.NullPresenter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.FragmentAdapter;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import butterknife.BindView;

/**
 * @author blog.unclezs.com
 * @since 2019-10-30 00:19
 */
@Page(anim = CoreAnim.none)
@SuppressLint("NonConstantResourceId")
public class DownloadManagerFragment extends BaseFragment<NullPresenter> {

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

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
        return R.layout.fragment_download_manager;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        FragmentAdapter<Fragment> adapter = new FragmentAdapter<>(getChildFragmentManager());
        adapter.addFragment(new DownloadingFragment(), "正在下载");
        adapter.addFragment(new DownloadRecordFragment(), "下载完成");
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
