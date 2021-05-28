package com.unclezs.novel.app;

import lombok.Data;

@Data
public class UpgradeInfo {
    private Integer Code;
    private String Msg;
    private Integer UpdateStatus;
    private Integer VersionCode;
    private String VersionName;
    private String ModifyContent;
    private String DownloadUrl;
    private Long ApkSize;
    private String ApkMd5;
}
