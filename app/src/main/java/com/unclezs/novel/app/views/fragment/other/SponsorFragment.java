package com.unclezs.novel.app.views.fragment.other;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.unclezs.novel.app.R;
import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.base.NullPresenter;
import com.unclezs.novel.app.utils.ClipboardUtils;
import com.unclezs.novel.app.utils.Utils;
import com.unclezs.novel.app.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author blog.unclezs.com
 * @date 2021/05/25 13:24
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "支持作者\n你的支持将是我最大的动力")
public class SponsorFragment extends BaseFragment<NullPresenter> {
    @BindView(R.id.wx_mp)
    SuperTextView wxMp;
    @BindView(R.id.wx)
    SuperTextView wx;
    @BindView(R.id.alipay)
    SuperTextView alipay;
    @BindView(R.id.alipay_envelopes_qrcode)
    SuperTextView alipayQrCode;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sponsor;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initListeners() {
        wxMp.setOnSuperTextViewClickListener(v -> {
            ClipboardUtils.set(getString(R.string.app_mp));
            XToastUtils.success("已复制，去微信搜索关注吧~");
        });
        wx.setOnSuperTextViewClickListener(v -> Utils.goWeb(requireContext(), getString(R.string.url_wx)));
        alipay.setOnSuperTextViewClickListener(v -> Utils.goWeb(requireContext(), getString(R.string.url_alipay)));
        alipayQrCode.setOnSuperTextViewClickListener(v -> Utils.goWeb(requireContext(), getString(R.string.url_alipay_qr)));
    }


    @OnClick(R.id.alipay_pay)
    public void goAlipay() {
        try {
            Intent intent = Intent.parseUri("intent://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F{urlCode}%3F_s%3Dweb-other&_t=1472443966571#Intent;scheme=alipayqr;package=com.eg.android.AlipayGphone;end".replace("{urlCode}", "fkx09459uuksg30f6d6ml02"), 1);
            startActivity(intent);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.alipay_envelopes_code)
    public void goAlipayCode() {
        ClipboardUtils.set(getString(R.string.alipay_search_code));
        XToastUtils.success("已复制红包搜索码，在支付宝进行搜索吧~");
        PackageManager packageManager = requireContext().getApplicationContext().getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage("com.eg.android.AlipayGphone");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
