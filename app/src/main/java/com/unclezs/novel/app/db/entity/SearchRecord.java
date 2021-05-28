package com.unclezs.novel.app.db.entity;

import androidx.annotation.NonNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 搜索记录
 *
 * @author xuexiang
 * @since 2019-12-04 22:57
 */
@DatabaseTable(tableName = "search_record")
public class SearchRecord implements Serializable {

    @DatabaseField(generatedId = true)
    private long id;
    /**
     * 搜索内容
     */
    @DatabaseField
    private String content;

    /**
     * 搜索时间
     */
    @DatabaseField
    private long time;


    public long getId() {
        return id;
    }

    public SearchRecord setId(long id) {
        this.id = id;
        return this;
    }

    public String getContent() {
        return content;
    }

    public SearchRecord setContent(String content) {
        this.content = content;
        return this;
    }

    public long getTime() {
        return time;
    }

    public SearchRecord setTime(long time) {
        this.time = time;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "QueryRecord{" +
            "Id=" + id +
            ", content='" + content + '\'' +
            ", time=" + time +
            '}';
    }
}
