package com.unclezs.novel.app.exts.webview;

import android.view.KeyEvent;

/**
 * @author blog.unclezs.com
 * @since 2019/1/4 下午11:32
 */
public interface FragmentKeyDown {

  /**
   * fragment按键监听
   *
   * @param keyCode
   * @param event
   * @return
   */
  boolean onFragmentKeyDown(int keyCode, KeyEvent event);
}
