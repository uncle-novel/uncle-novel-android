package com.unclezs.novel.app.presenter;

import com.hwangjr.rxbus.RxBus;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.spider.Spider;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.app.base.BasePresenter;
import com.unclezs.novel.app.db.entity.DownloadRecord;
import com.unclezs.novel.app.manager.RuleManager;
import com.unclezs.novel.app.model.SpiderWrapper;
import com.unclezs.novel.app.utils.XToastUtils;
import com.unclezs.novel.app.views.fragment.download.DownloadRecordFragment;
import com.unclezs.novel.app.views.fragment.download.DownloadingFragment;
import com.xuexiang.xutil.file.FileUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.io.FileUtil;

/**
 * @author blog.unclezs.com
 * @date 2021/05/26 11:24
 */
public class DownloadingPresenter extends BasePresenter<DownloadingFragment> {
    public static final String DOWNLOADS_CACHE_CONFIG = "downloads.json";
    public static final String TMP_DIR = FileUtils.getDiskCacheDir("downloads");
    private final DownloadConfigPresenter downloadConfigPresenter = new DownloadConfigPresenter();

    /**
     * 创建爬虫
     *
     * @param novel 小说（带目录）
     */
    public void addTask(Novel novel) {
        String savePath = downloadConfigPresenter.getSavePath();
        if (!FileUtils.createOrExistsDir(savePath)) {
            XToastUtils.error("下载文件夹不存在:" + savePath);
            return;
        }
        Spider spider = new Spider();
        spider.setUrl(novel.getUrl());
        spider.setNovel(novel);
        spider.setAnalyzerRule(RuleManager.getOrDefault(novel.getUrl()));
        spider.setSavePath(savePath);
        spider.setRetryTimes(downloadConfigPresenter.getRetryNum());
        spider.setThreadNum(downloadConfigPresenter.getThreadNum());
        SpiderWrapper wrapper = new SpiderWrapper(spider, this::onCompleted);
        wrapper.setPresenter(this);
        String format = downloadConfigPresenter.getFormat();
        wrapper.setEpub(StringUtils.containsIgnoreCase(format, "EPUB"));
        wrapper.setTxt(StringUtils.containsIgnoreCase(format, "TXT"));
        wrapper.initSpider();
        view.addTask(wrapper);
        // 如果还有任务数量剩余则直接启动
        wrapper.runTask();
    }

    /**
     * 启动任务，限制设置的任务数量
     */
    public void runTask() {
        List<SpiderWrapper> tasks = view.getAdapter().getData();
        // 添加新的任务
        int canRunTasksNumber = canRunTasksNumber();
        if (canRunTasksNumber > 0) {
            // 等待中的任务
            List<SpiderWrapper> waitingTask = tasks.stream()
                .filter(task -> task.isState(SpiderWrapper.WAIT_RUN))
                .collect(Collectors.toList());
            for (int i = 0; i < canRunTasksNumber && i < waitingTask.size(); i++) {
                waitingTask.get(i).run();
            }
            // 任务数量超过了，则让超出的任务等待执行
        } else if (canRunTasksNumber < 0) {
            List<SpiderWrapper> runningTask = tasks.stream()
                .filter(task -> task.getSpider().isState(Spider.RUNNING))
                .collect(Collectors.toList());
            for (int i = canRunTasksNumber; i < 0 && runningTask.size() + i >= 0; i++) {
                runningTask.get(runningTask.size() + i).waiting();
            }
        }
    }

    /**
     * 处理完成后
     */
    public void onCompleted(SpiderWrapper wrapper) {
        removeTask(wrapper);
        // 保存下载历史，不存在下载历史页面则直接入库
        DownloadRecord downloadRecord = new DownloadRecord();
        downloadRecord.setEpub(wrapper.isEpub());
        downloadRecord.setTxt(wrapper.isTxt());
        downloadRecord.setAudio(wrapper.isAudio());
        downloadRecord.setChapterNum(wrapper.getSpider().getTotalCount());
        downloadRecord.setName(wrapper.getName());
        downloadRecord.setPath(wrapper.getSpider().getSavePath());
        // 添加到下载历史列表
        RxBus.get().post(DownloadRecordFragment.BUS_TAG_ADD_RECORD, downloadRecord);
        XToastUtils.success("下载完成：" + wrapper.getName());
    }

    public void removeTask(SpiderWrapper wrapper) {
        // 移除任务
        view.getAdapter().delete(view.getAdapter().getData().indexOf(wrapper));
        // 删除缓存
        FileUtil.del(FileUtil.file(TMP_DIR, wrapper.getId()));
    }

    public void restore() {
        List<String> names = FileUtil.listFileNames(TMP_DIR);
        List<SpiderWrapper> tasks = new ArrayList<>();
        for (String name : names) {
            String json = FileUtil.readUtf8String(FileUtil.file(TMP_DIR, name));
            SpiderWrapper task = GsonUtils.parse(json, SpiderWrapper.class);
            task.setPresenter(this);
            task.setId(name);
            task.init(this::onCompleted);
            task.pause();
            tasks.add(task);
        }
        view.addTask(tasks);
    }

    /**
     * 剩余可以运行的任务数量
     *
     * @return 任务数量
     */
    public int canRunTasksNumber() {
        int maxTaskNum = downloadConfigPresenter.getTaskNum();
        long currentRunning = view.getAdapter().getData().stream().filter(task -> task.isState(Spider.RUNNING)).count();
        return (int) (maxTaskNum - currentRunning);
    }


}
