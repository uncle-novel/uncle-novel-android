package com.unclezs.novel.app.model;

import com.unclezs.novel.analyzer.spider.Spider;
import com.unclezs.novel.analyzer.spider.pipline.MediaFilePipeline;
import com.unclezs.novel.analyzer.spider.pipline.TxtPipeline;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.app.core.EbookPipeline;
import com.unclezs.novel.app.presenter.DownloadingPresenter;
import com.xuexiang.xutil.XUtil;

import java.io.File;
import java.io.Serializable;
import java.util.function.Consumer;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 爬虫任务包装，监听任务
 *
 * @author blog.unclezs.com
 * @date 2021/4/30 11:18
 */
@Getter
@Setter
public class SpiderWrapper implements Serializable {
    /**
     * 等待下载中
     */
    public static final int WAIT_RUN = 1001;
    /**
     * 下载进度
     */
    private final transient ObservableValue<Double> progress;
    /**
     * 下载进度 文字
     */
    private final transient ObservableValue<String> progressText;
    /**
     * 错误数量
     */
    private final transient ObservableValue<Integer> errorCount;
    /**
     * 当前状态
     */
    private ObservableValue<Integer> state;
    /**
     * p层需要手动注入
     */
    @Setter
    private transient DownloadingPresenter presenter;
    /**
     * 爬虫
     */
    private Spider spider;
    private boolean txt;
    private boolean epub;
    /**
     * 小说名称,也是文件名称
     */
    private String name;
    /**
     * 唯一任务ID
     */
    private String id;
    private transient File tmpFile;
    /**
     * 完成时回调，反序列化后需要手动重新设置
     */
    @Setter
    private transient Consumer<SpiderWrapper> onSucceed;

    /**
     * 无参构造，json反序列化时使用
     */
    public SpiderWrapper() {
        this.progress = new ObservableValue<>();
        this.progressText = new ObservableValue<>();
        this.errorCount = new ObservableValue<>();
        this.state = new ObservableValue<>(WAIT_RUN);
        if (this.id == null) {
            this.id = IdUtil.fastSimpleUUID();
        }
        setId(id);
    }

    /**
     * 首次创建调用
     *
     * @param spider 爬虫
     */
    public SpiderWrapper(Spider spider, Consumer<SpiderWrapper> onSucceed) {
        this();
        this.spider = spider;
        this.onSucceed = onSucceed;
        init();
    }

    /**
     * 初始化事件绑定
     */
    public void init() {
        // 初始数据
        progress.setValue(spider.progress());
        progressText.setValue(spider.progressText());
        errorCount.setValue(spider.errorCount());
        this.name = spider.getNovel().getTitle();
        // 事件监听
        spider.setProgressChangeHandler((numberProgress, textProgress) -> {
            backup();
            XUtil.runOnUiThread(() -> {
                progress.setValue(numberProgress);
                progressText.setValue(textProgress);
                errorCount.setValue(spider.errorCount());
            });
        });
        // 状态切换监听
        spider.setOnStateChange(newState -> XUtil.runOnUiThread(() -> {
            state.setValue(newState);
            switch (newState) {
                case Spider.SUCCESS:
                    // 处理完成后的其他逻辑
                    onSucceed.accept(this);
                    checkRunTask();
                    break;
                case Spider.STOPPED:
                case Spider.PAUSED:
                case Spider.COMPLETE:
                    checkRunTask();
                    break;
                default:
            }
        }));
    }

    /**
     * 初始化爬虫，在初始运行数据装配完成后调用，初始化爬虫的pipeline
     */
    public void initSpider() {
        // 下载格式
        if (isAudio()) {
            spider.addPipeline(new MediaFilePipeline());
        } else {
            if (epub) {
                EbookPipeline pipeline = new EbookPipeline();
                spider.addPipeline(pipeline);
            }
            if (txt) {
                TxtPipeline pipeline = new TxtPipeline();
                // 合并章节
                pipeline.setMerge(true);
                pipeline.setDeleteVolume(true);
                spider.addPipeline(pipeline);
            }
        }
    }

    /**
     * 反序列化之后调用
     *
     * @param onSucceed 完成回调
     */
    public void init(Consumer<SpiderWrapper> onSucceed) {
        this.onSucceed = onSucceed;
        // 初始化事件
        init();
        // 初始化爬虫
        initSpider();
    }

    /**
     * 状态是否一致
     *
     * @param expectStates 状态
     * @return true 一致
     */
    public boolean isState(int... expectStates) {
        if (this.state == null) {
            return false;
        }
        for (int expectState : expectStates) {
            if (expectState == this.state.getValue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 停止任务
     */
    public void stop() {
        this.spider.stop();
    }

    /**
     * 执行任务
     */
    public void run() {
        this.spider.runAsync();
    }

    /**
     * 执行任务,判断是否可以执行，不可执行则提交等待
     */
    public void runTask() {
        if (presenter.canRunTasksNumber() > 0) {
            this.spider.runAsync();
        } else {
            this.waiting();
        }
    }

    /**
     * 重试执行
     */
    public void retry() {
        spider.resetRetryTimes();
        runTask();
    }

    /**
     * 暂停执行
     */
    public void pause() {
        // 已经完成了，不能暂停了
        if (spider.isExceed(Spider.COMPLETE)) {
            return;
        }
        this.state.setValue(Spider.PAUSED);
        this.spider.pause();
    }

    /**
     * 检测是否可以执行新的任务，如果可以则执行
     */
    public void checkRunTask() {
        presenter.runTask();
    }

    /**
     * 等待执行
     */
    public void waiting() {
        if (isState(Spider.RUNNING)) {
            this.spider.pause();
        }
        this.state.setValue(WAIT_RUN);
    }

    /**
     * 忽略错误直接保存，只能是COMPLETE状态
     */
    public void save() {
        this.spider.setIgnoreError(true);
        runTask();
    }

    /**
     * 保存临时文件
     */
    private void backup() {
        FileUtil.writeUtf8String(GsonUtils.toJson(this), tmpFile);
    }

    /**
     * 设置ID
     */
    public void setId(String id) {
        this.id = id;
        this.tmpFile = FileUtil.file(DownloadingPresenter.TMP_DIR, id);
    }

    public boolean isAudio() {
        return Boolean.TRUE.equals(spider.getAnalyzerRule().getAudio());
    }
}
