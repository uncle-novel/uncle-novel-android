package com.unclezs.novel.app.utils.update;

import androidx.annotation.NonNull;

import com.unclezs.novel.app.utils.XToastUtils;
import com.xuexiang.xupdate.proxy.IUpdateHttpService;

import java.util.Map;

/**
 * XHttp2实现的请求更新
 *
 * @author blog.unclezs.com
 * @since 2018/8/12 上午11:46
 */
public class XHttpUpdateHttpServiceImpl implements IUpdateHttpService {

    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, Object> params, @NonNull final IUpdateHttpService.Callback callBack) {
//    XHttp.get(url)
//            .params(params)
//            .keepJson(true)
//            .execute(new SimpleCallBack<String>() {
//              @Override
//              public void onSuccess(String response) throws Throwable {
//                callBack.onSuccess(response);
//              }
//
//              @Override
//              public void onError(ApiException e) {
//                callBack.onError(e);
//              }
//            });
    }

    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, Object> params, @NonNull final IUpdateHttpService.Callback callBack) {
//    XHttp.post(url)
//            .upJson(JsonUtil.toJson(params))
//            .keepJson(true)
//            .execute(new SimpleCallBack<String>() {
//              @Override
//              public void onSuccess(String response) throws Throwable {
//                callBack.onSuccess(response);
//              }
//
//              @Override
//              public void onError(ApiException e) {
//                callBack.onError(e);
//              }
//            });
    }

    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull final IUpdateHttpService.DownloadCallback callback) {
//    XHttpSDK.addRequest(url, XHttp.downLoad(url)
//            .savePath(path)
//            .saveName(fileName)
//            .isUseBaseUrl(false)
//            .execute(new DownloadProgressCallBack<String>() {
//              @Override
//              public void onStart() {
//                callback.onStart();
//              }
//
//              @Override
//              public void onError(ApiException e) {
//                callback.onError(e);
//              }
//
//              @Override
//              public void update(long downLoadSize, long totalSize, boolean done) {
//                callback.onProgress(downLoadSize / (float) totalSize, totalSize);
//              }
//
//              @Override
//              public void onComplete(String path) {
//                callback.onSuccess(FileUtils.getFileByPath(path));
//              }
//            }));
    }

    @Override
    public void cancelDownload(@NonNull String url) {
        XToastUtils.info("已取消更新");
//    XHttpSDK.cancelRequest(url);
    }
}
