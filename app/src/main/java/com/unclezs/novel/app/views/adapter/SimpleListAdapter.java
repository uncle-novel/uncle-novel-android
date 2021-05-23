package com.unclezs.novel.app.views.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.unclezs.novel.app.R;
import com.xuexiang.xui.adapter.listview.BaseListAdapter;
import com.xuexiang.xutil.common.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 主副标题显示适配器
 *
 * @author blog.unclezs.com
 * @since 2018/12/19 上午12:19
 */
public class SimpleListAdapter extends BaseListAdapter<Map<String, String>, SimpleListAdapter.ViewHolder> {

  public static final String KEY_TITLE = "key_title";
  public static final String KEY_SUB_TITLE = "key_sub_title";

  public SimpleListAdapter(Context context, List<Map<String, String>> data) {
    super(context, data);
  }

  @Override
  protected ViewHolder newViewHolder(View convertView) {
    ViewHolder holder = new ViewHolder();
    holder.mTvTitle = convertView.findViewById(R.id.title);
    holder.mTvSubTitle = convertView.findViewById(R.id.tv_sub_title);
    return holder;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.adapter_item_simple_list_2;
  }

  @Override
  protected void convert(ViewHolder holder, Map<String, String> item, int position) {
    holder.mTvTitle.setText(item.get(KEY_TITLE));
    if (!StringUtils.isEmpty(item.get(KEY_SUB_TITLE))) {
      holder.mTvSubTitle.setText(item.get(KEY_SUB_TITLE));
      holder.mTvSubTitle.setVisibility(View.VISIBLE);
    } else {
      holder.mTvSubTitle.setVisibility(View.GONE);
    }
  }

  public static class ViewHolder {
    /**
     * 标题
     */
    public TextView mTvTitle;
    /**
     * 副标题
     */
    public TextView mTvSubTitle;
  }
}
