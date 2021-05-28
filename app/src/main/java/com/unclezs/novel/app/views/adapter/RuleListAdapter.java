package com.unclezs.novel.app.views.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.app.R;
import com.unclezs.novel.app.manager.RuleManager;
import com.unclezs.novel.app.utils.ClipboardUtils;
import com.unclezs.novel.app.utils.XToastUtils;
import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;

import java.util.Collection;
import java.util.List;

import lombok.NoArgsConstructor;

/**
 * @author blog.unclezs.com
 * @date 2021/05/22 17:01
 */
@NoArgsConstructor
@SuppressWarnings("rawtypes")
public class RuleListAdapter extends BaseRecyclerAdapter<AnalyzerRule> {
    public RuleListAdapter(Collection<AnalyzerRule> list) {
        super(list);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.adapter_rule_item;
    }

    @Override
    protected void bindData(@NonNull RecyclerViewHolder holder, int position, AnalyzerRule item) {
        holder.text(R.id.name, item.getName());
        CheckBox enabled = holder.findViewById(R.id.enabled);
        enabled.setChecked(Boolean.TRUE.equals(item.getEnabled()));
        enabled.setOnClickListener(v -> item.setEnabled(enabled.isChecked()));
        holder.visible(R.id.is_audio, Boolean.TRUE.equals(item.getAudio()) ? View.VISIBLE : View.GONE);
        View menu = holder.findViewById(R.id.menu);
        menu.setOnClickListener(v -> showMenu(menu, holder, item));
    }

    public void showMenu(View menu, RecyclerViewHolder holder, AnalyzerRule rule) {
        PopupMenu popup = new PopupMenu(holder.getContext(), menu);
        popup.inflate(R.menu.menu_rule);
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.delete) {
                List<AnalyzerRule> rules = RuleManager.rules();
                rules.remove(rule);
                mData.remove(rule);
                notifyDataSetChanged();
                RuleHelper.setRules(rules);
                XToastUtils.success("删除成功");
            } else if (item.getItemId() == R.id.copy_to_clipboard) {
                ClipboardUtils.set(GsonUtils.PRETTY.toJson(rule));
                XToastUtils.success("已复制");
            }
            return false;
        });
        popup.show();
    }
}
