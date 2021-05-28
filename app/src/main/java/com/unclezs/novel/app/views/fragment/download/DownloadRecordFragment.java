package com.unclezs.novel.app.views.fragment.download;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.BuildConfig;
import com.unclezs.novel.app.R;
import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.db.entity.DownloadRecord;
import com.unclezs.novel.app.presenter.DownloadRecordPresenter;
import com.unclezs.novel.app.utils.XToastUtils;
import com.unclezs.novel.app.utils.rx.RxUtils;
import com.unclezs.novel.app.views.adapter.DownloadRecordAdapter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import lombok.Getter;

/**
 * @author blog.unclezs.com
 * @date 2021/05/26 9:44
 */
@Page
@SuppressLint("NonConstantResourceId")
public class DownloadRecordFragment extends BaseFragment<DownloadRecordPresenter> {
    public static final String BUS_TAG_ADD_RECORD = "BUS_TAG_ADD_RECORD";
    @BindView(R.id.record_view)
    RecyclerView recordView;
    @Getter
    private DownloadRecordAdapter adapter;

    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_download_record;
    }

    @Override
    protected void initArgs() {
        super.initArgs();
        RxBus.get().register(this);
    }

    @Override
    public DownloadRecordPresenter createPresenter() {
        return new DownloadRecordPresenter();
    }

    @Override
    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected void initViews() {
        adapter = new DownloadRecordAdapter(presenter);
        adapter.setOnItemClickListener(this::onItemClick);
        recordView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recordView.setAdapter(adapter);
        Single.create((SingleEmitter<List<DownloadRecord>> emitter) -> {
            List<DownloadRecord> records = presenter.queryAll();
            emitter.onSuccess(records);
        }).compose(o -> RxUtils.threadTrans(o, getLifecycleOwner()))
            .subscribe(downloadRecords -> adapter.loadMore(downloadRecords));
    }

    public void remove(int pos) {
        adapter.delete(pos);
    }

    @Subscribe(tags = {@Tag(BUS_TAG_ADD_RECORD)})
    public void addRecord(DownloadRecord downloadRecord) {
        adapter.load(downloadRecord);
        presenter.add(downloadRecord);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

    public void onItemClick(View itemView, DownloadRecord item, int position) {
        if (item.isAudio()) {
            XToastUtils.warning("有声小说不支持直接打开");
            return;
        }
        String filepath = item.getPath() + StringUtils.BACKSLASH + item.getName() + (item.isEpub() ? ".epub" : ".txt");
        File file = new File(filepath);
        if (file.exists()) {
            Uri uri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".fileProvider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setDataAndType(uri, "text/plain");
            this.startActivity(intent);
        } else {
            XToastUtils.error("文件不存在：" + filepath);
        }
    }
}
