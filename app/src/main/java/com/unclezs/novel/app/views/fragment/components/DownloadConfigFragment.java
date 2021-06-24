package com.unclezs.novel.app.views.fragment.components;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

import com.unclezs.novel.app.R;
import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.presenter.DownloadConfigPresenter;
import com.unclezs.novel.app.utils.FileUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.IntConsumer;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.NumberUtil;

/**
 * @author blog.unclezs.com
 * @date 2021/05/25 18:59
 */
@Page(name = "下载配置")
@SuppressLint("NonConstantResourceId")
public class DownloadConfigFragment extends BaseFragment<DownloadConfigPresenter> {
    public static final int SAVE_PATH_REQUEST_CODE = 1001;
    private static final List<String> FORMATS = ListUtil.of("TXT", "EPUB");
    @BindView(R.id.save_path)
    SuperTextView savePath;
    @BindView(R.id.select_save_path)
    Button selectSavePath;
    @BindView(R.id.thread_num)
    SuperTextView threadNum;
    @BindView(R.id.task_num)
    SuperTextView taskNum;
    @BindView(R.id.retry_num)
    SuperTextView retryNum;
    @BindView(R.id.format)
    SuperTextView format;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_download_config;
    }

    @Override
    public DownloadConfigPresenter createPresenter() {
        return new DownloadConfigPresenter();
    }

    @Override
    protected void initViews() {
        updateSavePath(presenter.getSavePath());
        selectSavePath.setOnClickListener(v -> showFileChooser());
        updateRetryNum(presenter.getRetryNum());
        updateThreadNum(presenter.getThreadNum());
        updateTaskNum(presenter.getTaskNum());
        updateFormat(presenter.getFormat());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && data != null && data.getData() != null) {
            DocumentFile file = DocumentFile.fromTreeUri(requireContext(), data.getData());
            if (file != null) {
                presenter.setSavePath(FileUtils.getPath(requireContext(), file.getUri()));
            }
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        String initPath = presenter.getSavePath();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && initPath != null) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, initPath);
        }
        startActivityForResult(intent, SAVE_PATH_REQUEST_CODE);
    }

    @OnClick(R.id.thread_num)
    public void changeThreadNum() {
        showEditDialog(
            NumberUtil.appendRange(1, 32, new ArrayList<>()),
            presenter.getThreadNum(),
            "请选择线程数量",
            num -> presenter.setThreadNum(num));
    }

    @OnClick(R.id.retry_num)
    public void changeRetryNum() {
        showEditDialog(
            NumberUtil.appendRange(0, 99, new ArrayList<>()),
            presenter.getRetryNum(),
            "请选失败重试次数",
            num -> presenter.setRetryNum(num));
    }

    @OnClick(R.id.task_num)
    public void changeTaskNum() {
        showEditDialog(NumberUtil.appendRange(1, 5, new ArrayList<>()),
            presenter.getTaskNum(),
            "请选择最大任务数量",
            num -> presenter.setTaskNum(num));
    }


    @OnClick(R.id.format)
    public void changeFormat() {
        String[] split = presenter.getFormat().split(DownloadConfigPresenter.DOWNLOAD_CONFIG_FORMAT_SPLIT);
        Integer[] selected = new Integer[split.length];
        for (int i = 0; i < split.length; i++) {
            selected[i] = FORMATS.indexOf(split[i]);
        }
        new MaterialDialog.Builder(requireContext())
            .title("下载格式选择")
            .items(FORMATS)
            .itemsCallbackMultiChoice(selected, (dialog, which, text) -> {
                presenter.setFormat(text);
                return false;
            })
            .positiveText("确定")
            .negativeText("取消")
            .show();
    }


    @SuppressLint("InflateParams")
    private <T> void showEditDialog(Collection<T> items, int initValue, String title, IntConsumer callback) {
        View view = getLayoutInflater().inflate(R.layout.wigets_spinner, null);
        MaterialSpinner spinner = view.findViewById(R.id.spinner);
        spinner.setItems(new ArrayList<>());
        spinner.getItems().addAll(items);
        spinner.setSelectedIndex(initValue > 0 ? initValue - 1 : initValue);
        new MaterialDialog.Builder(requireContext())
            .customView(view, true)
            .title(title)
            .positiveText("确定")
            .negativeText("取消")
            .onPositive((d, w) -> callback.accept(spinner.getSelectedItem()))
            .cancelable(true).show();
    }

    @Override
    protected void initListeners() {
        savePath.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                presenter.setSavePath(savePath.getCenterEditValue());
            }
        });
    }

    public void updateSavePath(String path) {
        savePath.setCenterEditString(path);
    }

    public void updateThreadNum(Integer num) {
        threadNum.setRightString(String.valueOf(num));
    }

    public void updateTaskNum(Integer num) {
        taskNum.setRightString(String.valueOf(num));
    }

    public void updateRetryNum(Integer num) {
        retryNum.setRightString(String.valueOf(num));
    }

    public void updateFormat(String value) {
        format.setRightString(value);
    }
}
