package com.unclezs.novel.app.views.fragment.rule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.R;
import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.manager.RuleManager;
import com.unclezs.novel.app.presenter.RuleEditorPresenter;
import com.unclezs.novel.app.utils.Utils;
import com.unclezs.novel.app.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import butterknife.BindView;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;

/**
 * @author blog.unclezs.com
 * @date 2021/05/22 16:56
 */
@Page(name = "编辑书源")
@SuppressLint("NonConstantResourceId")
public class RuleEditorFragment extends BaseFragment<RuleEditorPresenter> {
    public static final String KEY_RULE = "rule";
    public static final String KEY_RULE_ADD = "isAdd";
    public static final int RESULT_CODE = 1000;
    @BindView(R.id.enabled)
    SuperTextView enabled;
    @BindView(R.id.dynamic)
    SuperTextView dynamic;
    @BindView(R.id.is_audio)
    SuperTextView audio;
    @BindView(R.id.source)
    EditText source;
    @BindView(R.id.name)
    SuperTextView name;
    @BindView(R.id.site)
    SuperTextView site;
    @BindView(R.id.user_agent)
    SuperTextView userAgent;
    @BindView(R.id.cookies)
    SuperTextView cookies;
    private AnalyzerRule rule;
    private boolean isAdd;

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.addAction(new TitleBar.TextAction("保存") {
            @Override
            public void performAction(View view) {
                saveRule();
                if (UrlUtils.isHttpUrl(rule.getSite())) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KEY_RULE, rule);
                    bundle.putBoolean(KEY_RULE_ADD, isAdd);
                    intent.putExtras(bundle);
                    setFragmentResult(RESULT_CODE, intent);
                    // 存在则哥更，不存在添加
                    RuleManager.addRule(rule);
                    popToBack();
                } else {
                    XToastUtils.error("请先填写正确的书源站点");
                }
            }
        });
        return titleBar;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_editor;
    }

    @Override
    protected void initViews() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            rule = (AnalyzerRule) arguments.getSerializable(KEY_RULE);
        }
        if (rule == null) {
            rule = new AnalyzerRule();
            isAdd = true;
        }
        enabled.setSwitchIsChecked(Boolean.TRUE.equals(rule.getEnabled()));
        audio.setSwitchIsChecked(Boolean.TRUE.equals(rule.getAudio()));
        dynamic.setSwitchIsChecked(Boolean.TRUE.equals(rule.getParams().getDynamic()));
        source.setText(GsonUtils.PRETTY.toJson(rule));
        name.setCenterEditString(rule.getName());
        site.setCenterEditString(rule.getSite());
        userAgent.setCenterEditString(rule.getParams().getUserAgent());
        cookies.setCenterEditString(rule.getParams().getCookie());
        cookies.getRightTextView().setOnClickListener(v -> {
            if (UrlUtils.isHttpUrl(rule.getSite())) {
                Utils.goWeb(requireContext(), rule.getSite());
            } else {
                XToastUtils.error("请先填写正确的书源站点");
            }
        });
    }

    @Override
    public RuleEditorPresenter createPresenter() {
        return new RuleEditorPresenter();
    }


    private void saveRule() {
        BeanUtil.copyProperties(RuleHelper.parseRule(source.getText().toString(), AnalyzerRule.class), rule, CopyOptions.create().setTransientSupport(false));
        this.rule.setEnabled(enabled.getSwitchIsChecked());
        this.rule.setAudio(audio.getSwitchIsChecked());
        this.rule.getParams().setDynamic(dynamic.getSwitchIsChecked());
        this.rule.getParams().setCookie(cookies.getCenterEditValue());
        this.rule.getParams().setUserAgent(userAgent.getCenterEditValue());
        this.rule.setName(name.getCenterEditValue());
        this.rule.setSite(site.getCenterEditValue());
    }
}
