package com.unclezs.novel.app.model;

import com.unclezs.novel.analyzer.model.Chapter;

import lombok.Data;

/**
 * @author blog.unclezs.com
 * @date 2021/05/21 1:35
 */
@Data
public class ChapterWrapper {
    private boolean selected;
    private Chapter chapter;

    public ChapterWrapper(Chapter chapter) {
        this.chapter = chapter;
        this.selected = true;
    }
}
