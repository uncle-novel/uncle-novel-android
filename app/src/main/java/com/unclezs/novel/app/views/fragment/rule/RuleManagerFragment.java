package com.unclezs.novel.app.views.fragment.rule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.app.BuildConfig;
import com.unclezs.novel.app.R;
import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.manager.ResourceManager;
import com.unclezs.novel.app.manager.RuleManager;
import com.unclezs.novel.app.presenter.RuleManagerPresenter;
import com.unclezs.novel.app.utils.XToastUtils;
import com.unclezs.novel.app.views.adapter.RuleListAdapter;
import com.xuexiang.constant.PermissionConstants;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.system.PermissionUtils;

import java.io.File;

import butterknife.BindView;

/**
 * @author blog.unclezs.com
 * @date 2021/05/22 16:14
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "规则管理")
public class RuleManagerFragment extends BaseFragment<RuleManagerPresenter> implements PopupMenu.OnMenuItemClickListener {

    public static final int EXPORT_REQUEST_CODE = 1002;
    public static final int IMPORT_REQUEST_CODE = 1001;
    @BindView(R.id.rulesView)
    RecyclerView rulesView;

    private PopupMenu popupMenu;
    private RuleListAdapter adapter;

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_web_more) {
            @Override
            public void performAction(View view) {
                showPoPup(view);
            }
        });
        return titleBar;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rule_manager;
    }

    @Override
    protected void initViews() {
        adapter = new RuleListAdapter(RuleManager.rules());
        rulesView.setLayoutManager(new LinearLayoutManager(requireContext()));
        rulesView.setAdapter(adapter);
        adapter.setOnItemClickListener((itemView, item, position) -> {
            PageOption.to(RuleEditorFragment.class)
                .putSerializable(RuleEditorFragment.KEY_RULE, item)
                .putBoolean(RuleEditorFragment.KEY_RULE_ADD, false)
                .setRequestCode(RuleEditorFragment.RESULT_CODE)
                .open(this);
        });
    }

    @Override
    public RuleManagerPresenter createPresenter() {
        return new RuleManagerPresenter();
    }


    /**
     * 显示更多菜单
     *
     * @param view 菜单依附在该View下面
     */
    private void showPoPup(View view) {
        if (popupMenu == null) {
            popupMenu = new PopupMenu(requireContext(), view);
            popupMenu.inflate(R.menu.menu_rule_manager);
            popupMenu.setOnMenuItemClickListener(this);
        }
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.import_rule:
                importFromFile();
                break;
            case R.id.add:
                PageOption.to(RuleEditorFragment.class)
                    .putBoolean(RuleEditorFragment.KEY_RULE_ADD, true)
                    .setRequestCode(RuleEditorFragment.RESULT_CODE)
                    .open(this);
                break;
            case R.id.export:
                exportToFile();
                break;
            case R.id.clipboard_import:
                presenter.importRuleFromClipboard();
                break;
            case R.id.share:
                shareRules();
                break;
            default:
        }
        return false;
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RuleEditorFragment.RESULT_CODE && data != null) {
            Bundle bundle = data.getExtras();
            AnalyzerRule rule = (AnalyzerRule) bundle.getSerializable(RuleEditorFragment.KEY_RULE);
            boolean isAdd = bundle.getBoolean(RuleEditorFragment.KEY_RULE_ADD);
            // 添加规则
            if (isAdd) {
                adapter.add(rule);
                // insert
                XToastUtils.success("添加成功");
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && data != null && data.getData() != null) {
            if (requestCode == IMPORT_REQUEST_CODE) {
                presenter.importRuleFromFile(data.getData());
            }
            if (requestCode == EXPORT_REQUEST_CODE) {
                presenter.exportRuleToFile(data.getData());
            }
        }
    }

    private void importFromFile() {
        PermissionUtils.permission(PermissionConstants.STORAGE)
            .callback(new PermissionUtils.SimpleCallback() {
                @Override
                public void onGranted() {
                    showFileChooser();
                }

                @Override
                public void onDenied() {
                    XToastUtils.error("请先授予应用的文件读写权限");
                }
            })
            .request();
    }


    /**
     * 分享书源
     */
    private void shareRules() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        File file = ResourceManager.file(RuleManager.RULES_FILE_NAME);
        Uri uri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".fileProvider", file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("text/*");
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }


    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"text/*", "application/json"});
        intent.setType("*/*");
        startActivityForResult(intent, IMPORT_REQUEST_CODE);
    }

    private void exportToFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_TITLE, "rules.json");
        intent.setType("application/json");
        intent.setType("*/*");
        startActivityForResult(intent, EXPORT_REQUEST_CODE);
    }

    public void refreshRules() {
        adapter.clear();
        adapter.getData().addAll(RuleManager.rules());
        adapter.notifyDataSetChanged();
    }

}
