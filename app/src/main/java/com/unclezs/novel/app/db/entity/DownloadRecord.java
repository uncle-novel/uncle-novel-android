package com.unclezs.novel.app.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import lombok.Data;

/**
 * @author blog.unclezs.com
 * @date 2021/05/26 11:15
 */
@Data
@DatabaseTable(tableName = "download_record")
public class DownloadRecord implements Serializable {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String path;
    @DatabaseField
    private boolean audio;
    @DatabaseField
    private boolean txt;
    @DatabaseField
    private boolean epub;
    @DatabaseField
    private Integer chapterNum;
}
