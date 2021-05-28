package com.unclezs.novel.app.views.fragment.components;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.R;
import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.presenter.ChapterTextPresenter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.progress.loading.ARCLoadingView;

import butterknife.BindView;

/**
 * @author blog.unclezs.com
 * @date 2021/05/22 12:52
 */
@Page
@SuppressLint("NonConstantResourceId")
public class ChapterTextFragment extends BaseFragment<ChapterTextPresenter> {

    public static final String KEY_CHAPTER = "chapter";
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.loading)
    ARCLoadingView loading;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chapter_text;
    }

    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    public ChapterTextPresenter createPresenter() {
        return new ChapterTextPresenter();
    }

    @Override
    protected void initViews() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            Chapter chapter = (Chapter) bundle.getSerializable(KEY_CHAPTER);
            title.setText(chapter.getName());
            if (StringUtils.isNotBlank(chapter.getContent())) {
                content.setText(chapter.getContent());
            } else {
                presenter.loadContent(chapter);
            }
        }
    }

    public void showLoading(boolean show) {
        loading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void onContentLoaded(boolean success, Chapter chapter) {
        if (success) {
            content.setText(chapter.getContent());
        } else {
            content.setText("章节内容获取失败");
        }
    }
}
