package com.unclezs.novel.app.views.fragment.analysis;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionMenu;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.R;
import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.model.ChapterWrapper;
import com.unclezs.novel.app.presenter.AnalysisPresenter;
import com.unclezs.novel.app.utils.XToastUtils;
import com.unclezs.novel.app.views.adapter.ChapterListAdapter;
import com.unclezs.novel.app.views.fragment.components.ChapterTextFragment;
import com.unclezs.novel.app.views.fragment.rule.RuleEditorFragment;
import com.unclezs.novel.app.views.fragment.rule.RuleManagerFragment;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.statelayout.StatefulLayout;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.OnClick;

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
    private Novel novel;
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
        XRouter.getInstance().inject(this);
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

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.showTitleBar = bundle.getBoolean(KEY_SHOW_TITLE_BAR);
            this.novel = (Novel) bundle.getSerializable(KEY_NOVEL_INFO);
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
        // 初始化事件
        analysisInput.setQuery("https://www.biquyue.com/book_1076/", false);
        analysisInput.clearFocus();


        WidgetUtils.initRecyclerView(chapterView);
        // 监听拖拽和侧滑删除，更新UI和数据源。
        chapterView.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                return adapter.onMoveItemList(srcHolder, targetHolder);
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
                int position = adapter.onRemoveItem(srcHolder);
                XToastUtils.toast("现在的第" + (position + 1) + "被删除。");
            }

        });
        adapter = new ChapterListAdapter();
        adapter.setOnItemClickListener((itemView, item, position) -> readChapter(item.getChapter()));
        chapterView.setAdapter(adapter);

        // 长按拖拽，默认关闭。
        chapterView.setLongPressDragEnabled(true);
        // 滑动删除，默认关闭。
        chapterView.setItemViewSwipeEnabled(true);

        // 初始化
        if (novel != null) {
            analysisInput.setQuery(novel.getUrl(), true);
        }
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

    /**
     * 执行解析
     *
     * @param url 目录地址
     */
    private void doAnalysis(String url) {
        // 初始化
        adapter.clear();
        fabMenu.setVisibility(View.GONE);
        stateLayout.showLoading("全力解析中...");
        presenter.doAnalysis(url);
    }

    @SingleClick
    @OnClick({R.id.fab_un_checked_all, R.id.fab_checked_all, R.id.fab_download, R.id.fab_rename, R.id.fab_config})
    public void handleFabClicked(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.fab_checked_all:
                adapter.getData().forEach(chapterWrapper -> chapterWrapper.setSelected(true));
                adapter.notifyDataSetChanged();
                break;
            case R.id.fab_un_checked_all:
                adapter.getData().forEach(chapterWrapper -> chapterWrapper.setSelected(false));
                adapter.notifyDataSetChanged();
                break;
            case R.id.fab_rename:
                renameChapters();
                break;
            case R.id.fab_download:
                openNewPage(RuleManagerFragment.class);
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
                    List<ChapterWrapper> selectedChapters = adapter.getData().stream().filter(ChapterWrapper::isSelected).collect(Collectors.toList());
                    for (int i = 0; i < selectedChapters.size(); i++) {
                        ChapterWrapper chapter = selectedChapters.get(i);
                        if (chapter.isSelected()) {
                            String name = chapter.getChapter().getName();
                            name = StringUtils.remove(name, "[0-9]", "第.*?章");
                            String newName = template.replace("{{章节序号}}", String.valueOf(index++)).replace("{{章节名}}", name);
                            chapter.getChapter().setName(newName);
                        }
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


    public void addMore(Chapter chapter) {
        if (adapter.isEmpty()) {
            stateLayout.showContent();
        }
        adapter.add(new ChapterWrapper(chapter));
        refreshLayout.finishLoadMore();
    }

    public void analysisFinished(boolean hasMore) {
        if (hasMore) {
            refreshLayout.finishLoadMore();
        } else {
            refreshLayout.finishLoadMoreWithNoMoreData();
        }
        if(!presenter.isLoading()){
            if (adapter.isEmpty()) {
                stateLayout.showEmpty("没有发现章节");
            } else {
                stateLayout.showContent();
            }
        }
        fabMenu.setVisibility(View.VISIBLE);
        analysisInput.clearFocus();

    }

}
