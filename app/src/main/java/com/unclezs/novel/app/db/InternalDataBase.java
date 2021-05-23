package com.unclezs.novel.app.db;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.xuexiang.xormlite.AppDataBaseTable;
import com.xuexiang.xormlite.db.DataBaseUtils;
import com.xuexiang.xormlite.db.IDatabase;
import com.xuexiang.xormlite.logs.DBLog;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author xuexiang
 * @since 2019-07-09 17:02
 */
public class InternalDataBase implements IDatabase {
    /**
     * 数据库创建
     *
     * @param database         SQLite数据库
     * @param connectionSource 数据库连接
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            DataBaseUtils.createTablesByClassNames(connectionSource, AppDataBaseTable.getTables());
        } catch (SQLException e) {
            DBLog.e(e);
        }
    }

    /**
     * 数据库升级和降级操作
     *
     * @param database         SQLite数据库
     * @param connectionSource 数据库连接
     * @param oldVersion       旧版本
     * @param newVersion       新版本
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        DBLog.i("数据库旧版本:" + oldVersion);
        DBLog.i("数据库新版本:" + newVersion);
        try {
            //简单暴力，直接删除后重新创建
            dropTablesByClassNames(connectionSource, AppDataBaseTable.getTables());
            DataBaseUtils.createTablesByClassNames(connectionSource, AppDataBaseTable.getTables());
        } catch (SQLException e) {
            DBLog.e(e);
        }
    }

    /**
     * 通过数据库表类名集合创建表
     *
     * @param connectionSource
     * @param tableClassNames 数据库表的类名集合
     * @throws java.sql.SQLException
     */
    private static void dropTablesByClassNames(ConnectionSource connectionSource, List<String> tableClassNames) throws SQLException {
        if (tableClassNames != null && tableClassNames.size() > 0) {
            for (String tableClassName : tableClassNames) {
                try {
                    TableUtils.dropTable(connectionSource, Class.forName(tableClassName), false);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
