package com.unclezs.novel.app.utils.update;

import com.unclezs.novel.app.utils.XToastUtils;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;

/**
 * 自定义版本更新提示
 *
 * @author blog.unclezs.com
 * @since 2019/4/15 上午12:01
 */
public class CustomUpdateFailureListener implements OnUpdateFailureListener {

  /**
   * 是否需要错误提示
   */
  private final boolean mNeedErrorTip;

  public CustomUpdateFailureListener() {
    this(true);
  }

  public CustomUpdateFailureListener(boolean needErrorTip) {
    mNeedErrorTip = needErrorTip;
  }

  /**
   * 更新失败
   *
   * @param error 错误
   */
  @Override
  public void onFailure(UpdateError error) {
    if (mNeedErrorTip) {
      XToastUtils.error(error);
    }
    if (error.getCode() == UpdateError.ERROR.DOWNLOAD_FAILED) {
      UpdateTipDialog.show("应用下载失败，是否考虑切换" + UpdateTipDialog.DOWNLOAD_TYPE_NAME + "下载？");
    }
  }
}
