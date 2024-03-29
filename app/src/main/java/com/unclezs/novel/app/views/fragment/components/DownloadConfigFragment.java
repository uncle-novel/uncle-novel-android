package com.unclezs.novel.app.views.fragment.components;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private ActivityResultLauncher<Uri> selectDocTree;

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
        selectDocTree = requireActivity().registerForActivityResult(new ActivityResultContracts.OpenDocumentTree(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                requireActivity().getContentResolver().takePersistableUriPermission(result, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                onChangeSavePath(result);
            }
        });
    }

    /**
     * 更改保存文件夹
     *
     * @param result 文件夹路径
     */
    public void onChangeSavePath(Uri result) {
        DocumentFile file = DocumentFile.fromTreeUri(requireContext(), result);
        if (file != null) {
            presenter.setSavePath(FileUtils.getPath(requireContext(), file.getUri()));
        }
    }


    private void showFileChooser() {
        selectDocTree.launch(null);
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
