package com.unclezs.novel.app;

import com.google.gson.Gson;
import com.unclezs.novel.app.util.ExecUtils;
import com.unclezs.novel.app.util.Logger;
import com.unclezs.novel.app.util.Md5Utils;

import org.gradle.api.DefaultTask;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.Collections;
import java.util.List;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @author blog.unclezs.com
 * @date 2021/05/28 0:53
 */
@Setter
@Getter
public class PublishApk extends DefaultTask {

    /**
     * 版本号
     */
    @Input
    String version;
    /**
     * 版本码
     */
    @Input
    private Integer versionCode;
    /**
     * 更新链接
     */
    @Input
    private String updateUrl;
    /**
     * 下载地址
     */
    @Input
    private String downloadUrl;
    /**
     * 更新Apk
     */
    @InputFile
    private File apk;
    /**
     * 更新内容
     */
    @Input
    private List<String> whatNew;
    @InputDirectory
    private File outDir;
    /**
     * 远程apk的位置
     */
    @Input
    private String apkRemote;
    /**
     * 远程版本信息位置
     */
    @Input
    private String versionRemote;

    public PublishApk() {
        Logger.init(getProject().getLogger());
        setGroup(BasePlugin.BUILD_GROUP);
        setDescription("构建Apk并且部署更新");
        setDependsOn(Collections.singletonList("assembleRelease"));
    }

    @TaskAction
    public void buildApk() {
        File apkFile = FileUtil.copy(apk, FileUtil.file(outDir, String.format("Uncle小说-%s.apk", version)), true);
        UpgradeInfo upgradeInfo = new UpgradeInfo();
        // 计算MD5
        upgradeInfo.setApkMd5(Md5Utils.getFileMD5(apkFile));
        // 计算大小
        upgradeInfo.setApkSize(FileUtil.size(apkFile) / 1024);
        // 默认成功
        upgradeInfo.setCode(0);
        // 总为有更新
        upgradeInfo.setUpdateStatus(1);
        // 版本信息
        upgradeInfo.setVersionCode(versionCode);
        upgradeInfo.setVersionName(version);
        // 下载地址
        upgradeInfo.setDownloadUrl(downloadUrl + apkFile.getName());
        // 更新内容
        StringBuilder whatNews = new StringBuilder();
        for (int i = 0; i < whatNew.size(); i++) {
            whatNews.append(whatNew.get(i)).append("\r\n");
        }
        upgradeInfo.setModifyContent(whatNews.toString());
        // 生成更新JSON
        String json = new Gson().newBuilder().setPrettyPrinting().create().toJson(upgradeInfo);
        // 打印JSON
        Logger.info(json);

        File versionFile = FileUtil.file(outDir, "version.json");
        FileUtil.writeUtf8String(json, versionFile);
        // 上传到服务器
        String local = apkFile.getAbsolutePath();
        if (SystemUtil.getOsInfo().isWindows()) {
            local = String.format("/cygdrive/%s", local.replace(StrUtil.BACKSLASH, StrUtil.SLASH).replace(StrUtil.COLON, CharSequenceUtil.EMPTY));
        }
        ExecUtils.exec("rsync", "-avz", "--chmod", "777", local, apkRemote);
        local = versionFile.getAbsolutePath();
        if (SystemUtil.getOsInfo().isWindows()) {
            local = String.format("/cygdrive/%s", local.replace(StrUtil.BACKSLASH, StrUtil.SLASH).replace(StrUtil.COLON, CharSequenceUtil.EMPTY));
        }
        ExecUtils.exec("rsync", "-avz", "--chmod", "777", local, versionRemote);

    }
}
