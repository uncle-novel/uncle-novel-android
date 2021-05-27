package com.unclezs.novel.app.presenter;

import com.unclezs.novel.app.base.BasePresenter;
import com.unclezs.novel.app.db.entity.DownloadRecord;
import com.unclezs.novel.app.model.DownloadRecordModel;
import com.unclezs.novel.app.views.fragment.download.DownloadRecordFragment;

import java.util.List;

import cn.hutool.core.io.FileUtil;

/**
 * @author blog.unclezs.com
 * @date 2021/05/27 0:38
 */
public class DownloadRecordPresenter extends BasePresenter<DownloadRecordFragment> {
    private final DownloadRecordModel model = new DownloadRecordModel();

    public List<DownloadRecord> queryAll() {
        return model.getAll();
    }

    public void deleteRecord(DownloadRecord downloadRecord, int pos) {
        model.delete(downloadRecord);
        view.remove(pos);
    }

    public void add(DownloadRecord downloadRecord) {
        model.save(downloadRecord);
    }
}
