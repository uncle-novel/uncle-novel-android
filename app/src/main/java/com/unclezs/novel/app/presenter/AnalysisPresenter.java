package com.unclezs.novel.app.presenter;

import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import com.hwangjr.rxbus.RxBus;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.spider.TocSpider;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.SerializationUtils;
import com.unclezs.novel.app.base.BasePresenter;
import com.unclezs.novel.app.manager.RuleManager;
import com.unclezs.novel.app.utils.XToastUtils;
import com.unclezs.novel.app.utils.rx.RxUtils;
import com.unclezs.novel.app.views.fragment.analysis.AnalysisFragment;
import com.unclezs.novel.app.views.fragment.download.DownloadingFragment;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xutil.XUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import lombok.Getter;

/**
 * @author blog.unclezs.com
 * @date 2021/05/21 15:40
 */
public class AnalysisPresenter extends BasePresenter<AnalysisFragment> {
    private final AtomicBoolean loading = new AtomicBoolean(false);
    private Disposable analysisDisposable;
    private TocSpider spider;
    @Getter
    private Novel novel;
    private String url;

    public void doAnalysis(String url) {
        this.url = url;
        if (analysisDisposable != null && !analysisDisposable.isDisposed()) {
            analysisDisposable.dispose();
            spider.cancel();
        }
        spider = new TocSpider(getRule());
        spider.setOnNewItemAddHandler(chapter -> XUtil.runOnUiThread(() -> view.addMore(chapter)));
        loading.set(true);
        loadMore(true);
    }

    public void loadMore(boolean first) {
        AnalyzerRule rule = getRule();
        spider.setRule(rule);
        if (analysisDisposable != null && !analysisDisposable.isDisposed()) {
            return;
        }
        // 执行解析
        analysisDisposable = Completable.create(emitter -> {
            if (first) {
                spider.toc(url);
                novel = spider.getNovel();
                if (StringUtils.isBlank(rule.getName())) {
                    rule.setName(novel.getTitle());
                }
            } else {
                spider.loadMore();
            }
            // 自动翻页
            while (Boolean.TRUE.equals(rule.getToc().getAutoNext()) && spider.hasMore() && loading.get()) {
                spider.loadMore();
            }
            loading.set(false);
            emitter.onComplete();
        }).compose(o -> RxUtils.threadTrans(o, view))
            .doOnError(Throwable::printStackTrace)
            .doFinally(() -> view.analysisFinished(spider.hasMore())).subscribe();
    }

    public boolean isLoading() {
        return loading.get();
    }


    /**
     * 提交下载
     */
    public void submitDownload() {
        // 运行设备>=Android 11.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            new MaterialDialog.Builder(view.requireContext())
                .title("下载需要获取文件存储权限")
                .positiveText("确定")
                .negativeText("取消")
                .onPositive((d, w) -> view.startActivityForResult(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION), 101))
                .cancelable(true).show();
        } else {
            if (novel != null) {
                List<Chapter> selectedChapters = view.getSelectedChapters();
                if (CollectionUtils.isEmpty(selectedChapters)) {
                    XToastUtils.error("至少选择一个章节进行下载");
                    return;
                }
                Novel novelCopy = SerializationUtils.deepClone(this.novel);
                novelCopy.setChapters(SerializationUtils.deepClone(selectedChapters));
                RxBus.get().post(DownloadingFragment.BUS_ACTION_ADD_TASK, novelCopy);
            }
        }
    }

    public void setNovel(Novel novel) {
        this.novel = novel;
    }


    public AnalyzerRule getRule() {
        return RuleManager.getOrDefault(url);
    }
}
