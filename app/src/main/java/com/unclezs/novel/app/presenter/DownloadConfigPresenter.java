package com.unclezs.novel.app.presenter;

import com.hwangjr.rxbus.RxBus;
import com.unclezs.novel.app.base.BasePresenter;
import com.unclezs.novel.app.utils.MMKVUtils;
import com.unclezs.novel.app.views.fragment.download.DownloadingFragment;
import com.unclezs.novel.app.views.fragment.components.DownloadConfigFragment;
import com.xuexiang.xutil.file.FileUtils;

import org.apache.commons.lang3.StringUtils;

import cn.hutool.core.text.CharSequenceUtil;

/**
 * @author blog.unclezs.com
 * @date 2021/05/25 20:05
 */
public class DownloadConfigPresenter extends BasePresenter<DownloadConfigFragment> {

    public static final String DOWNLOAD_CONFIG_SAVE_PATH = "download_config_save_path";
    public static final String DOWNLOAD_CONFIG_THREAD_NUM = "download_config_thread_num";
    public static final String DOWNLOAD_CONFIG_TASK_NUM = "download_config_task_num";
    public static final String DOWNLOAD_CONFIG_RETRY_NUM = "download_config_retry_num";
    public static final String DOWNLOAD_CONFIG_FORMAT = "DOWNLOAD_CONFIG_FORMAT";
    public static final String DOWNLOAD_CONFIG_FORMAT_SPLIT = "、";

    public String getSavePath() {
        String defaultPath = FileUtils.getDiskDir("Uncle小说");
        String value = MMKVUtils.getString(DOWNLOAD_CONFIG_SAVE_PATH, defaultPath);
        return StringUtils.isBlank(value) ? defaultPath : value;
    }

    public void setSavePath(String path) {
        MMKVUtils.put(DOWNLOAD_CONFIG_SAVE_PATH, path);
        view.updateSavePath(path);
    }

    public int getThreadNum() {
        return MMKVUtils.getInt(DOWNLOAD_CONFIG_THREAD_NUM, 1);
    }

    public void setThreadNum(Integer threadNum) {
        MMKVUtils.put(DOWNLOAD_CONFIG_THREAD_NUM, threadNum);
        view.updateThreadNum(threadNum);
    }

    public int getTaskNum() {
        return MMKVUtils.getInt(DOWNLOAD_CONFIG_TASK_NUM, 1);
    }

    public void setTaskNum(Integer num) {
        MMKVUtils.put(DOWNLOAD_CONFIG_TASK_NUM, num);
        view.updateTaskNum(num);
        RxBus.get().post(DownloadingFragment.BUS_ACTION_MAX_TASK_NUM_CHANGE, num);
    }

    public int getRetryNum() {
        return MMKVUtils.getInt(DOWNLOAD_CONFIG_RETRY_NUM, 3);
    }

    public void setRetryNum(Integer num) {
        MMKVUtils.put(DOWNLOAD_CONFIG_RETRY_NUM, num);
        view.updateRetryNum(num);
    }

    public String getFormat() {
        return MMKVUtils.getString(DOWNLOAD_CONFIG_FORMAT, "TXT");
    }

    public void setFormat(CharSequence[] formats) {
        String result = CharSequenceUtil.join(DOWNLOAD_CONFIG_FORMAT_SPLIT, (Object[]) formats);
        if (result.isEmpty()) {
            result += "TXT";
        }
        MMKVUtils.put(DOWNLOAD_CONFIG_FORMAT, result);
        view.updateFormat(result);
    }

}
