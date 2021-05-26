package com.unclezs.novel.app.views.fragment.components;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.hwangjr.rxbus.RxBus;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.util.SerializationUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.R;
import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.presenter.BookDetailPresenter;
import com.unclezs.novel.app.views.fragment.analysis.AnalysisFragment;
import com.unclezs.novel.app.views.fragment.download.DownloadingFragment;
import com.unclezs.novel.app.widget.Tag;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.imageview.ImageLoader;
import com.xuexiang.xui.widget.imageview.strategy.DiskCacheStrategyEnum;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * @author blog.unclezs.com
 * @date 2021/05/21 21:06
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "小说详情")
public class BookDetailFragment extends BaseFragment<BookDetailPresenter> {
    public static final String INFO = "novel";
    public static final String INFO_FOR_EDIT = "edit";
    @BindView(R.id.cover)
    ImageView cover;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.author)
    TextView author;
    @BindView(R.id.introduce)
    TextView introduce;
    @BindView(R.id.tags)
    LinearLayout tags;
    @BindView(R.id.category)
    Tag category;
    @BindView(R.id.word_count)
    Tag wordCount;
    @BindView(R.id.state)
    Tag state;
    @BindView(R.id.action_analysis)
    Button actionAnalysis;
    @BindView(R.id.action_download)
    Button actionDownload;
    @BindView(R.id.edit_title)
    AppCompatImageView editTitle;
    private Novel novel;
    private boolean isEdit;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_book_detail;
    }

    @Override
    protected void initViews() {
        if (getArguments() != null) {
            novel = (Novel) getArguments().getSerializable(INFO);
            isEdit = getArguments().getBoolean(INFO_FOR_EDIT, false);
            if (isEdit) {
                actionAnalysis.setVisibility(View.GONE);
                actionDownload.setVisibility(View.GONE);
                RxBus.get().post(AnalysisFragment.KEY_NOVEL_INFO, novel);
            } else {
                editTitle.setVisibility(View.GONE);
            }
            if (StringUtils.isNotBlank(novel.getCoverUrl())) {
                ImageLoader.get().loadImage(cover, novel.getCoverUrl(), ResUtils.getDrawable(R.drawable.no_cover), DiskCacheStrategyEnum.NONE);
            }
            title.setText(StringUtils.isBlank(novel.getTitle()) ? "未知" : novel.getTitle());
            author.setText(StringUtils.isBlank(novel.getAuthor()) ? "未知" : novel.getAuthor());
            introduce.setText((StringUtils.isBlank(novel.getIntroduce()) ? "暂无简介" : novel.getIntroduce()).trim());
            if (CharSequenceUtil.isAllBlank(novel.getCategory(), novel.getWordCount(), novel.getState())) {
                tags.setVisibility(View.GONE);
            } else {
                tags.setVisibility(View.VISIBLE);
                if (StringUtils.isBlank(novel.getCategory())) {
                    category.setVisibility(View.GONE);
                } else {
                    category.setVisibility(View.VISIBLE);
                    category.setText(novel.getCategory());
                }
                if (StringUtils.isBlank(novel.getWordCount())) {
                    wordCount.setVisibility(View.GONE);
                } else {
                    wordCount.setVisibility(View.VISIBLE);
                    wordCount.setText(novel.getWordCount());
                }
                if (StringUtils.isBlank(novel.getState())) {
                    state.setVisibility(View.GONE);
                } else {
                    state.setVisibility(View.VISIBLE);
                    state.setText(novel.getState());
                }
            }
        }
    }

    @OnClick(R.id.action_analysis)
    public void toAnalysis() {
        PageOption.to(AnalysisFragment.class)
            .setNewActivity(true)
            .putBoolean(AnalysisFragment.KEY_SHOW_TITLE_BAR, true)
            .putSerializable(AnalysisFragment.KEY_NOVEL_INFO, novel)
            .setAnim(CoreAnim.slide)
            .open(this);
    }


    @OnClick(R.id.edit_title)
    public void editTitle() {
        new MaterialDialog.Builder(requireContext())
            .title("编辑")
            .content("修改小说的名称")
            .inputType(InputType.TYPE_CLASS_TEXT)
            .input(
                "请输入小说的名称",
                novel.getTitle(),
                false,
                ((dialog, input) -> {
                    String template = input.toString();
                    novel.setTitle(template);
                    title.setText(template);
                }))
            .positiveText("确定")
            .negativeText("取消")
            .cancelable(true)
            .show();
    }

    @OnClick(R.id.action_download)
    public void toDownload() {
        Novel novelCopy = SerializationUtils.deepClone(this.novel);
        RxBus.get().post(DownloadingFragment.BUS_ACTION_ADD_TASK, novelCopy);
    }
}
