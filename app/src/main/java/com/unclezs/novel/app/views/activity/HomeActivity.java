package com.unclezs.novel.app.views.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.unclezs.novel.app.R;
import com.unclezs.novel.app.base.BaseActivity;
import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.utils.ClipboardUtils;
import com.unclezs.novel.app.utils.Utils;
import com.unclezs.novel.app.utils.XToastUtils;
import com.unclezs.novel.app.views.fragment.analysis.AnalysisFragment;
import com.unclezs.novel.app.views.fragment.download.DownloadFragment;
import com.unclezs.novel.app.views.fragment.other.AboutFragment;
import com.unclezs.novel.app.views.fragment.other.DownloadConfigFragment;
import com.unclezs.novel.app.views.fragment.other.SearchBookFragment;
import com.unclezs.novel.app.views.fragment.other.SponsorFragment;
import com.unclezs.novel.app.views.fragment.profile.ProfileFragment;
import com.xuexiang.xui.adapter.FragmentAdapter;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.common.ClickUtils;
import com.xuexiang.xutil.common.CollectionUtils;

import butterknife.BindView;

/**
 * Home
 *
 * @author blog.unclezs.com
 * @date 2021/5/25 14:49
 */
@SuppressWarnings("NonConstantResourceId")
public class HomeActivity extends BaseActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener, ClickUtils.OnClick2ExitListener, Toolbar.OnMenuItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    /**
     * 底部导航栏
     */
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;
    /**
     * 侧边栏
     */
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private String[] titles;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initListeners();
    }

    @Override
    protected boolean isSupportSlideBack() {
        return false;
    }

    @SuppressWarnings("rawtypes")
    private void initViews() {
        titles = ResUtils.getStringArray(R.array.home_titles);
        toolbar.setTitle(titles[0]);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(this);

        initHeader();

        //主页内容填充
        BaseFragment[] fragments = new BaseFragment[]{
            new AnalysisFragment(),
            new DownloadFragment(),
            new ProfileFragment()
        };
        FragmentAdapter<BaseFragment> adapter = new FragmentAdapter<>(getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(titles.length - 1);
        viewPager.setAdapter(adapter);
    }

    private void initHeader() {
        navView.setItemIconTintList(null);
        View headerView = navView.getHeaderView(0);
        LinearLayout navHeader = headerView.findViewById(R.id.nav_header);

        navHeader.setOnClickListener(this);
    }

    protected void initListeners() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 侧边栏点击事件
        navView.setNavigationItemSelectedListener(this::handleNavigationItemClicked);
        //主页事件监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MenuItem item = bottomNavigation.getMenu().getItem(position);
                toolbar.setTitle(item.getTitle());
                item.setChecked(true);
                updateSideNavStatus(item);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    /**
     * 处理侧边栏点击事件
     *
     * @param menuItem
     * @return
     */
    private boolean handleNavigationItemSelected(@NonNull MenuItem menuItem) {
        int index = CollectionUtils.arrayIndexOf(titles, menuItem.getTitle());
        if (index != -1) {
            toolbar.setTitle(menuItem.getTitle());
            viewPager.setCurrentItem(index, false);
            return true;
        }
        return false;
    }

    /**
     * 处理侧边栏点击事件
     *
     * @param menuItem 菜单
     * @return /
     */
    private boolean handleNavigationItemClicked(@NonNull MenuItem menuItem) {
        if (menuItem.isCheckable()) {
            drawerLayout.closeDrawers();
            return handleNavigationItemSelected(menuItem);
        } else {
            switch (menuItem.getItemId()) {
                case R.id.nav_about:
                    openNewPage(AboutFragment.class);
                    break;
                case R.id.nav_feedback:
                    Utils.goWeb(this, getString(R.string.url_feedback));
                    break;
                case R.id.nav_sponsor:
                    openNewPage(SponsorFragment.class);
                    break;
                case R.id.nav_site:
                    Utils.goWeb(this, getString(R.string.url_project_site));
                    break;
                case R.id.nav_qq:
                    Utils.goWeb(this, getString(R.string.url_add_qq_group));
                    break;
                case R.id.nav_rule_manager:
                    ActivityUtils.startActivity(RuleManagerActivity.class);
                    break;
                case R.id.nav_search:
                    openNewPage(SearchBookFragment.class);
                    break;
                case R.id.nav_download:
                    openNewPage(DownloadConfigFragment.class);
                    break;
                case R.id.nav_exit:
                    DialogLoader.getInstance().showConfirmDialog(this, getString(R.string.lab_logout_confirm), getString(R.string.lab_yes),
                        (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        },
                        getString(R.string.lab_no), (dialog, which) -> dialog.dismiss()
                    );
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    /**
     * toolbar点击事件处理
     *
     * @param item /
     * @return /
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            SearchBookFragment fragment = getPage(SearchBookFragment.class);
            if (fragment == null) {
                openNewPage(SearchBookFragment.class);
            } else {
                openPage(SearchBookFragment.class);
            }
        }
        return false;
    }

    /**
     * 页面点击事件处理
     *
     * @param v /
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nav_header) {
            ClipboardUtils.set(getString(R.string.app_mp));
            XToastUtils.success("已复制，去微信搜索关注吧~");
        }
    }

    /**
     * 底部导航栏点击事件
     *
     * @param menuItem /
     * @return /
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int index = CollectionUtils.arrayIndexOf(titles, menuItem.getTitle());
        if (index != -1) {
            toolbar.setTitle(menuItem.getTitle());
            viewPager.setCurrentItem(index, false);

            updateSideNavStatus(menuItem);
            return true;
        }
        return false;
    }

    /**
     * 更新侧边栏菜单选中状态
     *
     * @param menuItem /
     */
    private void updateSideNavStatus(MenuItem menuItem) {
        MenuItem side = navView.getMenu().findItem(menuItem.getItemId());
        if (side != null) {
            side.setChecked(true);
        }
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ClickUtils.exitBy2Click(2000, this);
        }
        return true;
    }

    @Override
    public void onRetry() {
        XToastUtils.toast("再按一次退出程序");
    }

    @Override
    public void onExit() {
        XUtil.exitApp();
    }


}
