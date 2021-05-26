package com.unclezs.novel.app.views.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.spider.Spider;
import com.unclezs.novel.app.R;
import com.unclezs.novel.app.model.SpiderWrapper;
import com.unclezs.novel.app.presenter.DownloadingPresenter;
import com.unclezs.novel.app.widget.Tag;
import com.xuexiang.xui.adapter.recyclerview.XRecyclerAdapter;

/**
 * @author blog.unclezs.com
 * @date 2021/05/26 10:34
 */
public class DownloadingTaskAdapter extends XRecyclerAdapter<SpiderWrapper, DownloadingTaskAdapter.SpiderHolder> {
    private final DownloadingPresenter presenter;

    public DownloadingTaskAdapter(DownloadingPresenter presenter) {
        this.presenter = presenter;
    }

    @NonNull
    @Override
    protected SpiderHolder getViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SpiderHolder(inflateView(parent, R.layout.adapter_downloading));
    }

    @Override
    @SuppressLint("DefaultLocale")
    protected void bindData(@NonNull SpiderHolder holder, int position, SpiderWrapper wrapper) {
        Spider spider = wrapper.getSpider();
        Novel novel = spider.getNovel();
        // 初值
        holder.title.setText(novel.getTitle());
        holder.progressText.setText(wrapper.getProgressText().getValue());
        holder.updateProgress(wrapper.getProgress().getValue());
        holder.setState(wrapper.getState().getValue());
        holder.updateError(wrapper.getErrorCount().getValue());
        if (wrapper.isAudio()) {
            holder.audio.setVisibility(View.VISIBLE);
            holder.epub.setVisibility(View.GONE);
            holder.txt.setVisibility(View.GONE);
        } else {
            holder.audio.setVisibility(View.GONE);
            holder.epub.setVisibility(wrapper.isEpub() ? View.VISIBLE : View.GONE);
            holder.txt.setVisibility(wrapper.isTxt() ? View.VISIBLE : View.GONE);
        }
        // 事件与状态监听
        holder.start.setOnClickListener(v -> wrapper.runTask());
        holder.pause.setOnClickListener(v -> wrapper.pause());
        holder.retry.setOnClickListener(v -> wrapper.retry());
        holder.stop.setOnClickListener(v -> {
            wrapper.stop();
            presenter.removeTask(wrapper);
        });
        wrapper.getProgress().setListener(holder::updateProgress);
        wrapper.getProgressText().setListener(holder.progressText::setText);
        wrapper.getState().setListener(holder::setState);
        wrapper.getErrorCount().setListener(holder::updateError);
    }

    static class SpiderHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView state;
        private final TextView error;
        private final ProgressBar progressBar;
        private final TextView progressText;
        private final Tag txt;
        private final Tag epub;
        private final Tag audio;
        private final AppCompatImageView start;
        private final AppCompatImageView pause;
        private final AppCompatImageView retry;
        private final AppCompatImageView save;
        private final AppCompatImageView stop;

        public SpiderHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.title);
            state = view.findViewById(R.id.state);
            error = view.findViewById(R.id.error);
            progressBar = view.findViewById(R.id.progressBar);
            progressText = view.findViewById(R.id.progress_text);
            start = view.findViewById(R.id.start);
            stop = view.findViewById(R.id.stop);
            pause = view.findViewById(R.id.pause);
            retry = view.findViewById(R.id.retry);
            save = view.findViewById(R.id.save);
            txt = view.findViewById(R.id.txt);
            epub = view.findViewById(R.id.epub);
            audio = view.findViewById(R.id.audio);
        }

        private void setState(int spiderState) {
            switch (spiderState) {
                case Spider.RUNNING:
                    state.setText("下载中...");
                    setActions(false, true, false, false);
                    break;
                case Spider.PAUSED:
                    state.setText("暂停中...");
                    setActions(true, false, false, false);
                    break;
                case Spider.COMPLETE:
                    state.setText("等待手动重试...");
                    setActions(false, false, true, true);
                    break;
                case SpiderWrapper.WAIT_RUN:
                    state.setText("等待中...");
                    setActions(false, true, false, false);
                    break;
                case Spider.PIPELINE:
                    state.setText("转码中...");
                    setActions(false, false, false, false);
                    break;
                default:
            }
        }

        @SuppressLint("DefaultLocale")
        public void updateError(Integer errorCount) {
            if (errorCount > 0) {
                error.setText(String.format("%d失败", errorCount));
                error.setVisibility(View.VISIBLE);
            } else {
                error.setVisibility(View.GONE);
            }
        }

        public void updateProgress(double progress) {
            progressBar.setProgress((int) (progress * 100));
        }

        public void setActions(boolean showStart, boolean showPause, boolean showRetry, boolean showSave) {
            start.setVisibility(showStart ? View.VISIBLE : View.GONE);
            pause.setVisibility(showPause ? View.VISIBLE : View.GONE);
            retry.setVisibility(showRetry ? View.VISIBLE : View.GONE);
            save.setVisibility(showSave ? View.VISIBLE : View.GONE);
        }

    }
}
