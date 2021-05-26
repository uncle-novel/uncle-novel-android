package com.unclezs.novel.app.views.adapter;

import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unclezs.novel.app.R;
import com.unclezs.novel.app.model.ChapterWrapper;
import com.unclezs.novel.app.utils.XToastUtils;
import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 拖拽适配器
 *
 * @author xuexiang
 * @since 2019/4/6 下午12:18
 */
public class ChapterListAdapter extends BaseRecyclerAdapter<ChapterWrapper> {

    public ChapterListAdapter() {
        super(new ArrayList<>());
    }

    /**
     * 适配的布局
     *
     * @param viewType
     * @return
     */
    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.adapter_chapter_list_item;
    }

    /**
     * 绑定数据
     *
     * @param holder
     * @param position
     * @param item
     */
    @Override
    public void bindData(@NonNull final RecyclerViewHolder holder, int position, ChapterWrapper item) {
        holder.text(R.id.title, item.getChapter().getName());
        CheckBox checkbox = holder.findViewById(R.id.selected);
        checkbox.setChecked(item.isSelected());
        checkbox.setOnClickListener(v -> item.setSelected(checkbox.isChecked()));
    }

    /**
     * List拖拽移动
     *
     * @param srcHolder
     * @param targetHolder
     */
    public boolean onMoveItemList(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
        // 不同的ViewType不能拖拽换位置。
        if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) {
            return false;
        }
        int fromPosition = srcHolder.getAdapterPosition();
        int toPosition = targetHolder.getAdapterPosition();

        Collections.swap(mData, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        // 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
        return true;
    }


    /**
     * 侧滑删除
     *
     * @param srcHolder
     */
    public int onRemoveItem(RecyclerView.ViewHolder srcHolder) {
        int position = srcHolder.getAdapterPosition();
        delete(position);
        return position;
    }


}
