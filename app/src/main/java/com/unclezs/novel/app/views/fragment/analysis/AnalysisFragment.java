package com.unclezs.novel.app.views.fragment.analysis;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionMenu;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.R;
import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.core.ChapterComparator;
import com.unclezs.novel.app.model.ChapterWrapper;
import com.unclezs.novel.app.presenter.AnalysisPresenter;
import com.unclezs.novel.app.utils.ClipboardUtils;
import com.unclezs.novel.app.utils.XToastUtils;
import com.unclezs.novel.app.views.adapter.ChapterListAdapter;
import com.unclezs.novel.app.views.fragment.components.BookDetailFragment;
import com.unclezs.novel.app.views.fragment.components.ChapterTextFragment;
import com.unclezs.novel.app.views.fragment.rule.RuleEditorFragment;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.statelayout.StatefulLayout;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hutool.core.collection.ListUtil;

/**
 * 首页动态
 *
 * @author blog.unclezs.com
 * @since 2019-10-30 00:15
 */
@Page(anim = CoreAnim.none, name = "小说解析", params = {AnalysisFragment.KEY_SHOW_TITLE_BAR})
@SuppressWarnings("NonConstantResourceId")
public class AnalysisFragment extends BaseFragment<AnalysisPresenter> {
    public static final String KEY_NOVEL_INFO = "novel";
    public static final String KEY_SHOW_TITLE_BAR = "showTitleBar";
    @BindView(R.id.analysis_input)
    SearchView analysisInput;
    @BindView(R.id.chapter_view)
    SwipeRecyclerView chapterView;
    @BindView(R.id.fab_menus)
    FloatingActionMenu fabMenu;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.state_layout)
    StatefulLayout stateLayout;
    private boolean showTitleBar;
    private ChapterListAdapter adapter;
    private TitleBar titleBar;

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        titleBar = super.initTitle();
        titleBar.setTitle(getString(R.string.analysis_novel));
        return titleBar;
    }

    @Override
    protected void initArgs() {
        super.initArgs();
        RxBus.get().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

    /**
     * 布局的资源id
     *
     * @return layoutID
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_analysis;
    }

    @Override
    public void onResume() {
        super.onResume();
        hideCurrentPageSoftInput();
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        Bundle bundle = getArguments();
        Novel novel = null;
        if (bundle != null) {
            this.showTitleBar = bundle.getBoolean(KEY_SHOW_TITLE_BAR);
            novel = (Novel) bundle.getSerializable(KEY_NOVEL_INFO);
        }
        titleBar.setVisibility(showTitleBar ? View.VISIBLE : View.GONE);

        analysisInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (UrlUtils.isHttpUrl(query)) {
                    doAnalysis(query);
                } else {
                    XToastUtils.error("请输入正确的目录链接");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        // 自动读取剪贴板
        analysisInput.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                String content = ClipboardUtils.get();
                if (!Objects.equals(content, analysisInput.getQuery().toString()) && UrlUtils.isHttpUrl(content)) {
                    analysisInput.setQuery(content, false);
                }
            }
        });
        WidgetUtils.initRecyclerView(chapterView);
        // 监听拖拽和侧滑删除，更新UI和数据源。
        chapterView.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                return adapter.onMoveItemList(srcHolder, targetHolder);
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
                adapter.onRemoveItem(srcHolder);
            }

        });
        adapter = new ChapterListAdapter();
        adapter.setOnItemClickListener((itemView, item, position) -> readChapter(item.getChapter()));
        adapter.setOnItemLongClickListener((itemView, item, position) -> showItemPopupMenu(itemView, position));
        chapterView.setAdapter(adapter);

        // 长按拖拽，默认关闭。
        chapterView.setLongPressDragEnabled(true);
        // 滑动删除，默认关闭。
        chapterView.setItemViewSwipeEnabled(true);

        // 初始化
        if (novel != null) {
            analysisInput.setQuery(novel.getUrl(), true);
        }
        analysisInput.clearFocus();
    }

    @Override
    protected void initListeners() {
        // 上拉加载
        refreshLayout.setOnLoadMoreListener(layout -> presenter.loadMore(false));
    }

    @Override
    public AnalysisPresenter createPresenter() {
        return new AnalysisPresenter();
    }

    private void showItemPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view.findViewById(R.id.title), Gravity.CENTER);
        popupMenu.inflate(R.menu.menu_chapter_list);
        popupMenu.setOnMenuItemClickListener(item -> {
            final int id = item.getItemId();
            int pos = position;
            switch (id) {
                case R.id.selected_all:
                    adapter.getData().forEach(chapterWrapper -> chapterWrapper.setSelected(true));
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.unselected_all:
                    adapter.getData().forEach(chapterWrapper -> chapterWrapper.setSelected(false));
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.selected_up:
                    do {
                        adapter.getItem(pos--).setSelected(true);
                    } while (pos >= 0 && !adapter.getItem(pos).isSelected());
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.unselected_up:
                    do {
                        adapter.getItem(pos--).setSelected(false);
                    } while (pos >= 0 && adapter.getItem(pos).isSelected());
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.selected_down:
                    do {
                        adapter.getItem(pos++).setSelected(true);
                    } while (pos < adapter.getItemCount() && !adapter.getItem(pos).isSelected());
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.unselected_down:
                    do {
                        adapter.getItem(pos++).setSelected(false);
                    } while (pos < adapter.getItemCount() && adapter.getItem(pos).isSelected());
                    adapter.notifyDataSetChanged();
                    break;
                default:
            }
            return false;
        });
        popupMenu.show();
    }


    @OnClick({R.id.fab_sort, R.id.fab_edit_info, R.id.fab_to_top, R.id.fab_to_bottom, R.id.fab_download, R.id.fab_rename, R.id.fab_config})
    public void handleFabClicked(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.fab_edit_info:
                PageOption.to(BookDetailFragment.class)
                    .setNewActivity(true)
                    .putSerializable(BookDetailFragment.INFO, presenter.getNovel())
                    .putBoolean(BookDetailFragment.SHOW_ACTION, false)
                    .open(this);
                break;
            case R.id.fab_to_bottom:
                chapterView.scrollToPosition(adapter.getItemCount() - 1);
                break;
            case R.id.fab_to_top:
                chapterView.scrollToPosition(0);
                break;
            case R.id.fab_rename:
                renameChapters();
                break;
            case R.id.fab_download:
                presenter.submitDownload();
                break;
            case R.id.fab_sort:
                sortToc();
                break;
            case R.id.fab_config:
                openNewPage(RuleEditorFragment.class, RuleEditorFragment.KEY_RULE, presenter.getRule());
                break;
            default:
        }
        fabMenu.toggle(false);
    }

    private void renameChapters() {
        String defaultTemplate = "第{{章节序号}}章 {{章节名}}";
        new MaterialDialog.Builder(requireContext())
            .title("自定义格式化模板")
            .content("输入章节匹配的正则模板，如：第{{章节序号}}章 {{章节名}}")
            .inputType(InputType.TYPE_CLASS_TEXT)
            .input(
                "请输入章节格式化正则",
                defaultTemplate,
                false,
                ((dialog, input) -> {
                    String template = input.toString();
                    int index = 1;
                    List<Chapter> selectedChapters = getSelectedChapters();
                    for (int i = 0; i < selectedChapters.size(); i++) {
                        Chapter chapter = selectedChapters.get(i);
                        String name = chapter.getName();
                        name = StringUtils.remove(name, "[0-9]", "第.*?章");
                        String newName = template.replace("{{章节序号}}", String.valueOf(index++)).replace("{{章节名}}", name);
                        chapter.setName(newName);
                    }
                    adapter.notifyDataSetChanged();
                }))
            .positiveText("确定")
            .negativeText("取消")
            .cancelable(true)
            .show();
    }

    /**
     * 阅读一章节
     *
     * @param chapter 章节
     */
    private void readChapter(Chapter chapter) {
        openNewPage(ChapterTextFragment.class, "chapter", chapter);
    }

    /**
     * 执行解析
     *
     * @param url 目录地址
     */
    private void doAnalysis(String url) {
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableAutoLoadMore(false);
        // 初始化
        adapter.clear();
        fabMenu.setVisibility(View.GONE);
        stateLayout.showLoading("全力解析中...");
        presenter.doAnalysis(url);
    }

    /**
     * 排序
     */
    private void sortToc() {
        List<ChapterWrapper> sorted = ListUtil.sort(new ArrayList<>(adapter.getData()), new ChapterComparator());
        adapter.refresh(sorted);
    }


    public void addMore(Chapter chapter) {
        if (adapter.isEmpty()) {
            stateLayout.showContent();
            fabMenu.setVisibility(View.VISIBLE);
        }
        adapter.add(new ChapterWrapper(chapter));
        refreshLayout.finishLoadMore();
    }

    public void analysisFinished(boolean hasMore) {
        if (hasMore) {
            refreshLayout.finishLoadMore();
            refreshLayout.setEnableLoadMore(true);
            refreshLayout.setEnableAutoLoadMore(true);
        } else {
            refreshLayout.finishLoadMoreWithNoMoreData();
        }
        if (!presenter.isLoading()) {
            if (adapter.isEmpty()) {
                stateLayout.showEmpty("没有发现章节");
            } else {
                stateLayout.showContent();
            }
        }
        analysisInput.clearFocus();
    }

    public List<Chapter> getSelectedChapters() {
        return adapter.getData().stream().filter(ChapterWrapper::isSelected).map(ChapterWrapper::getChapter).collect(Collectors.toList());
    }

    @Subscribe(tags = {@Tag(KEY_NOVEL_INFO)})
    public void setNovel(Novel novel) {
        presenter.setNovel(novel);
    }
}
