package com.unclezs.novel.app.model;

import com.unclezs.novel.app.db.entity.DownloadRecord;
import com.xuexiang.xormlite.AppDataBaseRepository;
import com.xuexiang.xormlite.db.DBService;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author blog.unclezs.com
 * @date 2021/05/27 0:19
 */
public class DownloadRecordModel {
    private final DBService<DownloadRecord> dbService;

    public DownloadRecordModel() {
        dbService = AppDataBaseRepository.getInstance().getDataBase(DownloadRecord.class);
    }


    public List<DownloadRecord> getAll() {
        try {
            return dbService.queryAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public void save(DownloadRecord downloadRecord) {
        try {
            dbService.insert(downloadRecord);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(DownloadRecord downloadRecord) {
        try {
            dbService.deleteData(downloadRecord);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
