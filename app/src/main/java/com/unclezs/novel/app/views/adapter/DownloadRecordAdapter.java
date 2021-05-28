package com.unclezs.novel.app.views.adapter;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;

import com.unclezs.novel.app.R;
import com.unclezs.novel.app.db.entity.DownloadRecord;
import com.unclezs.novel.app.presenter.DownloadRecordPresenter;
import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;

/**
 * @author blog.unclezs.com
 * @date 2021/05/27 0:47
 */
public class DownloadRecordAdapter extends BaseRecyclerAdapter<DownloadRecord> {
    private final DownloadRecordPresenter presenter;

    public DownloadRecordAdapter(DownloadRecordPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.adapter_download_record;
    }

    @Override
    @SuppressLint("DefaultLocale")
    protected void bindData(@NonNull RecyclerViewHolder holder, int position, DownloadRecord item) {
        holder.text(R.id.title, item.getName());
        holder.text(R.id.chapter_num, String.format("共%d章", item.getChapterNum()));
        if (item.isAudio()) {
            holder.visible(R.id.audio, View.VISIBLE);
            holder.visible(R.id.epub, View.GONE);
            holder.visible(R.id.txt, View.GONE);
        } else {
            holder.visible(R.id.audio, View.GONE);
            holder.visible(R.id.epub, item.isEpub() ? View.VISIBLE : View.GONE);
            holder.visible(R.id.txt, item.isTxt() ? View.VISIBLE : View.GONE);
        }

        holder.viewClick(R.id.delete, (view, downloadRecord, pos) -> {
            presenter.deleteRecord(item, mData.indexOf(item));
        }, item, position);
    }
}
