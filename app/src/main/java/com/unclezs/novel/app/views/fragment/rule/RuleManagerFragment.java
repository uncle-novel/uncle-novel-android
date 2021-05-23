package com.unclezs.novel.app.views.fragment.rule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.app.R;
import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.manager.RuleManager;
import com.unclezs.novel.app.presenter.RuleManagerPresenter;
import com.unclezs.novel.app.utils.XToastUtils;
import com.unclezs.novel.app.views.adapter.RuleListAdapter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import butterknife.BindView;

/**
 * @author blog.unclezs.com
 * @date 2021/05/22 16:14
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "规则管理")
public class RuleManagerFragment extends BaseFragment<RuleManagerPresenter> implements PopupMenu.OnMenuItemClickListener {

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
                break;
            case R.id.add:
                PageOption.to(RuleEditorFragment.class)
                    .putBoolean(RuleEditorFragment.KEY_RULE_ADD, true)
                    .setRequestCode(RuleEditorFragment.RESULT_CODE)
                    .open(this);
                break;
            case R.id.export:
                break;
            case R.id.clipboard_import:
                break;
            default:
        }
        return false;
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
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
}
