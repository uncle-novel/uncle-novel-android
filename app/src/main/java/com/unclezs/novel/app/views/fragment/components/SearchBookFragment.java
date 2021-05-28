package com.unclezs.novel.app.views.fragment.components;

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.R;
import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.db.entity.SearchRecord;
import com.unclezs.novel.app.presenter.SearchBookPresenter;
import com.unclezs.novel.app.utils.SettingUtils;
import com.unclezs.novel.app.utils.Utils;
import com.unclezs.novel.app.views.adapter.SearchBookAdapter;
import com.unclezs.novel.app.views.adapter.SearchRecordTagAdapter;
import com.unclezs.novel.app.views.fragment.components.BookDetailFragment;
import com.unclezs.novel.app.widget.SuperSearchView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.statelayout.StatefulLayout;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * @author blog.unclezs.com
 * @since 2019-12-02 23:32
 */
@Page(name = "搜索小说")
@SuppressWarnings("NonConstantResourceId")
public class SearchBookFragment extends BaseFragment<SearchBookPresenter> implements RecyclerViewHolder.OnItemClickListener<SearchRecord> {

    @BindView(R.id.history_list)
    RecyclerView historyView;
    @BindView(R.id.history_layout)
    LinearLayout historyLayout;
    @BindView(R.id.book_list)
    RecyclerView booksView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.state_layout)
    StatefulLayout stateLayout;
    private PopupMenu popupMenu;
    private SearchRecordTagAdapter searchRecordTagAdapter;
    private SuperSearchView searchView;
    private SearchBookAdapter searchBookAdapter;
    private String keyword;
    private boolean searchAudio = false;


    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle().setLeftClickListener(v -> {
            hideCurrentPageSoftInput();
            popToBack();
        });
        // 搜索框
        titleBar.setCustomTitle(createSearchView());
        // 类型选择
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_web_more) {
            @Override
            public void performAction(View view) {
                showPopupMenu(view);
            }
        });
        return titleBar;
    }

    private void showPopupMenu(View view) {
        if (popupMenu == null) {
            popupMenu = new PopupMenu(requireContext(), view, Gravity.START);
            popupMenu.inflate(R.menu.menu_search_book);
            popupMenu.getMenu().setGroupCheckable(R.id.novel_group, true, true);
            if (SettingUtils.isSearchAudio()) {
                popupMenu.getMenu().findItem(R.id.novel_audio).setChecked(true);
            } else {
                popupMenu.getMenu().findItem(R.id.novel_text).setChecked(true);
            }
            popupMenu.setOnDismissListener(menu -> {
                searchAudio = menu.getMenu().findItem(R.id.novel_audio).isChecked();
                SettingUtils.setSearchAudio(searchAudio);
            });
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                menuItem.setChecked(true);
                return true;
            });
        }
        popupMenu.show();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_book;
    }

    /**
     * 不自动隐藏软键盘
     *
     * @param ev /
     */
    @Override
    protected void onTouchDownAction(MotionEvent ev) {
        // do nothing
    }

    /**
     * 初始化页面
     */
    @Override
    protected void initViews() {
        searchAudio = SettingUtils.isSearchAudio();
        searchBookAdapter = new SearchBookAdapter();
        searchBookAdapter.setOnItemClickListener((itemView, item, position) -> openNovel(item));
        booksView.setLayoutManager(new LinearLayoutManager(getContext()));
        booksView.setAdapter(searchBookAdapter);

        historyView.setLayoutManager(Utils.getFlexboxLayoutManager(getContext()));
        searchRecordTagAdapter = new SearchRecordTagAdapter();
        historyView.setAdapter(searchRecordTagAdapter);
        refreshRecord();
    }

    private void openNovel(Novel novel) {
        openPage(BookDetailFragment.class, BookDetailFragment.INFO, novel);
    }

    @Override
    public SearchBookPresenter createPresenter() {
        return new SearchBookPresenter(this);
    }

    /**
     * 创建搜索框
     *
     * @return 父容器
     */
    private View createSearchView() {
        // 创建搜索框
        searchView = new SuperSearchView(requireContext());
        searchView.setQueryHint(getString(R.string.search_book_hint));
        LinearLayout layout = new LinearLayout(requireContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AutoSizeUtils.dp2px(requireContext(), 27));
        layout.addView(searchView, layoutParams);
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        // 初始化事件
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (StringUtils.isNotBlank(query)) {
                    doSearch(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (StringUtils.isBlank(searchView.getQuery())) {
                    showSearchLayout(false);
                }
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener((view, focused) -> {
            // 有焦点，显示搜索历史
            if (focused) {
                showSearchLayout(false);
                // 没有焦点，且query不为空，显示搜索页
            } else if (StringUtils.isNotBlank(searchView.getQuery())) {
                showSearchLayout(true);
            }
        });
        return layout;
    }

    /**
     * 显示搜索页结果还是搜索历史
     *
     * @param show true 显示搜索结果
     */
    public void showSearchLayout(boolean show) {
        if (show) {
            historyLayout.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            historyLayout.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 执行搜索
     *
     * @param keyword 关键词
     */
    private void doSearch(String keyword) {
        this.keyword = keyword;
        // 保存搜索历史
        presenter.saveQueryHistory(keyword);
        // 显示正文内容
        stateLayout.showLoading();
        // 初始化状态
        searchView.clearFocus();
        showSearchLayout(true);
        refreshLayout.resetNoMoreData();
        searchBookAdapter.clear();
        // 执行搜索
        presenter.doSearch(keyword, searchAudio);
    }

    /**
     * 初始化事件监听
     */
    @Override
    protected void initListeners() {
        searchRecordTagAdapter.setOnItemClickListener(this);
        // 上拉加载
        refreshLayout.setOnLoadMoreListener(layout -> presenter.loadMore());
    }

    /**
     * 搜索历史点击
     *
     * @param itemView 页面
     * @param item     项
     * @param position 索引
     */
    @Override
    public void onItemClick(View itemView, SearchRecord item, int position) {
        if (item != null) {
            searchView.setQuery(item.getContent(), true);
        }
    }

    /**
     * 清除搜索历史
     *
     * @param view 视图
     */
    @OnClick(R.id.iv_delete)
    public void onClearHistoryClicked(View view) {
        presenter.clearSearchRecord();
        searchRecordTagAdapter.clear();
    }

    /**
     * 刷新搜索历史
     */
    public void refreshRecord() {
        searchRecordTagAdapter.refresh(presenter.queryAllSearchRecord());
    }

    public void addMore(Novel novel) {
        searchView.post(() -> {
            if (searchBookAdapter.getData().isEmpty()) {
                stateLayout.showContent();
            }
            searchBookAdapter.load(novel);
        });
    }

    public void onSearchFinished(boolean hasMore) {
        if (searchBookAdapter.isEmpty()) {
            stateLayout.showEmpty(String.format("没有搜索到'%s'相关的小说", keyword));
        }
        if (hasMore) {
            refreshLayout.finishLoadMore();
        } else {
            refreshLayout.finishLoadMoreWithNoMoreData();
        }
    }
}
