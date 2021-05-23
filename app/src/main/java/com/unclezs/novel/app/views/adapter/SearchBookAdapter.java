package com.unclezs.novel.app.views.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.R;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.adapter.recyclerview.XRecyclerAdapter;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.imageview.ImageLoader;
import com.xuexiang.xui.widget.imageview.strategy.DiskCacheStrategyEnum;

import java.util.Collection;
import java.util.List;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.NoArgsConstructor;

/**
 * @author blog.unclezs.com
 * @date 2021/05/18 12:21
 */
@NoArgsConstructor
public class SearchBookAdapter extends XRecyclerAdapter<Novel, SearchBookAdapter.Holder> {
    public SearchBookAdapter(Collection<Novel> list) {
        super(list);
    }

    @NonNull
    @Override
    protected Holder getViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflateView(parent, R.layout.item_search_book));
    }

    @Override
    @SuppressLint("SetTextI18n")
    protected void bindData(@NonNull Holder holder, int position, Novel item) {
        String introduce = "简介：";
        String author = "作者：";
        if (StringUtils.isNotBlank(item.getCoverUrl())) {
            ImageLoader.get().loadImage(holder.cover, item.getCoverUrl(), ResUtils.getDrawable(R.drawable.no_cover), DiskCacheStrategyEnum.NONE);
        }
        holder.title.setText(StringUtils.isBlank(item.getTitle()) ? "未知" : item.getTitle());
        holder.author.setText(author + (StringUtils.isBlank(item.getAuthor()) ? "未知" : item.getAuthor()));
        holder.introduce.setText(introduce + (StringUtils.isBlank(item.getIntroduce()) ? "暂无简介" : item.getIntroduce()).trim());
        if (CharSequenceUtil.isAllBlank(item.getCategory(), item.getWordCount(), item.getState())) {
            holder.tags.setVisibility(View.GONE);
        } else {
            holder.tags.setVisibility(View.VISIBLE);
            if (StringUtils.isBlank(item.getCategory())) {
                holder.category.setVisibility(View.GONE);
            } else {
                holder.category.setVisibility(View.VISIBLE);
                holder.category.setText(item.getCategory());
            }
            if (StringUtils.isBlank(item.getWordCount())) {
                holder.wordCount.setVisibility(View.GONE);
            } else {
                holder.wordCount.setVisibility(View.VISIBLE);
                holder.wordCount.setText(item.getWordCount());
            }
            if (StringUtils.isBlank(item.getState())) {
                holder.state.setVisibility(View.GONE);
            } else {
                holder.state.setVisibility(View.VISIBLE);
                holder.state.setText(item.getState());
            }
        }
    }


    public void setItems(List<Novel> items) {
        mData.clear();
        mData.addAll(items);
    }

    static class Holder extends RecyclerViewHolder {
        private final ImageView cover;
        private final TextView title;
        private final TextView state;
        private final TextView wordCount;
        private final TextView category;
        private final TextView introduce;
        private final TextView author;
        private final LinearLayout tags;

        public Holder(View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
            title = itemView.findViewById(R.id.title);
            state = itemView.findViewById(R.id.state);
            wordCount = itemView.findViewById(R.id.word_count);
            category = itemView.findViewById(R.id.category);
            introduce = itemView.findViewById(R.id.introduce);
            author = itemView.findViewById(R.id.author);
            tags = itemView.findViewById(R.id.tags);
        }
    }
}
