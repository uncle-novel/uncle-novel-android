package com.unclezs.novel.app.views.adapter;

import androidx.annotation.NonNull;

import com.unclezs.novel.app.R;
import com.unclezs.novel.app.db.entity.SearchRecord;
import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;

/**
 * @author xuexiang
 * @since 2019-12-04 23:21
 */
public class SearchRecordTagAdapter extends BaseRecyclerAdapter<SearchRecord> {

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.adapter_search_record_tag_item;
    }

    @Override
    protected void bindData(@NonNull RecyclerViewHolder holder, int position, SearchRecord item) {
        if (item != null) {
            holder.text(R.id.tv_tag, item.getContent());
        }
    }
}
