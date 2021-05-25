package com.unclezs.novel.app.exts.webview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.unclezs.novel.app.R;
import com.unclezs.novel.app.utils.XToastUtils;
import com.xuexiang.xrouter.facade.Postcard;
import com.xuexiang.xrouter.facade.callback.NavCallback;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.slideback.SlideBack;

/**
 * 壳浏览器
 *
 * @author blog.unclezs.com
 * @since 2019/1/5 上午12:15
 */
public class AgentWebActivity extends AppCompatActivity {

  private AgentWebFragment mAgentWebFragment;

  /**
   * 请求浏览器
   *
   * @param url
   */
  public static void goWeb(Context context, final String url) {
    Intent intent = new Intent(context, AgentWebActivity.class);
    intent.putExtra(AgentWebFragment.KEY_URL, url);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_agent_web);

    SlideBack.with(this)
            .haveScroll(true)
            .callBack(this::finish)
            .register();

    Uri uri = getIntent().getData();
    if (uri != null) {
      XRouter.getInstance().build(uri).navigation(this, new NavCallback() {
        @Override
        public void onArrival(Postcard postcard) {
          finish();
        }

        @Override
        public void onLost(Postcard postcard) {
          loadUrl(uri.toString());
        }
      });
    } else {
      String url = getIntent().getStringExtra(AgentWebFragment.KEY_URL);
      loadUrl(url);
    }
  }

  private void loadUrl(String url) {
    if (url != null) {
      openFragment(url);
    } else {
      XToastUtils.error("数据出错！");
      finish();
    }
  }

  private void openFragment(String url) {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.add(R.id.container_frame_layout, mAgentWebFragment = AgentWebFragment.getInstance(url));
    ft.commit();

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    AgentWebFragment agentWebFragment = mAgentWebFragment;
    if (agentWebFragment != null) {
      if (((FragmentKeyDown) agentWebFragment).onFragmentKeyDown(keyCode, event)) {
        return true;
      } else {
        return super.onKeyDown(keyCode, event);
      }
    }
    return super.onKeyDown(keyCode, event);
  }


  @Override
  protected void onDestroy() {
    SlideBack.unregister(this);
    super.onDestroy();
  }
}
