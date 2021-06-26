package com.unclezs.novel.app.core;

import android.annotation.SuppressLint;

import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.spider.helper.SpiderHelper;
import com.unclezs.novel.analyzer.spider.pipline.AbstractTextPipeline;
import com.unclezs.novel.analyzer.util.FileUtils;
import com.unclezs.novel.analyzer.util.StringUtils;

import java.io.File;
import java.io.IOException;

import lombok.Setter;
import timber.log.Timber;

/**
 * 保存为文本文件的pipeline
 * 路径为当前目录的下的downloads
 *
 * @author blog.unclezs.com
 * @date 2020/12/23 10:58 下午
 */
@Setter
public class TxtPipeline extends AbstractTextPipeline {
    private static final String DOWNLOAD_FILE_FORMAT = "%s/%d.%s.txt";
    /**
     * 是否合并文件
     */
    private boolean merge;
    /**
     * 删除章节文件
     */
    private boolean deleteVolume;

    @Override
    @SuppressLint("DefaultLocale")
    public void processChapter(Chapter chapter) {
        String filePath = String.format(DOWNLOAD_FILE_FORMAT, getFilePath(), chapter.getOrder(), StringUtils.removeInvalidSymbol(chapter.getName()));
        try {
            // 写入文件
            FileUtils.writeString(filePath, chapter.getContent(), getCharset());
        } catch (IOException e) {
            Timber.d(e, "文件写入失败：%s", filePath);
        }
    }

    @Override
    public void onComplete() {
        if (merge) {
            try {
                SpiderHelper.mergeNovel(new File(getFilePath()), getNovel().getTitle().concat(".txt"), deleteVolume);
            } catch (Exception e) {
                Timber.d(e, "文件合并失败：%s", getFilePath());
            }
        }
    }
}
