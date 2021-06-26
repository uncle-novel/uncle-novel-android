package com.unclezs.novel.app.presenter;

import android.content.Context;
import android.net.Uri;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.base.BasePresenter;
import com.unclezs.novel.app.manager.RuleManager;
import com.unclezs.novel.app.utils.ClipboardUtils;
import com.unclezs.novel.app.utils.FileUtils;
import com.unclezs.novel.app.utils.XToastUtils;
import com.unclezs.novel.app.utils.rx.RxUtils;
import com.unclezs.novel.app.views.fragment.rule.RuleManagerFragment;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;

/**
 * @author blog.unclezs.com
 * @date 2021/05/22 16:16
 */
public class RuleManagerPresenter extends BasePresenter<RuleManagerFragment> {

    /**
     * 导入规则
     *
     * @param ruleJson 规则JSON
     */
    public void importRule(String ruleJson) {
        if (StringUtils.isBlank(ruleJson)) {
            XToastUtils.error("导入失败，格式错误");
            return;
        }
        try {
            JsonElement element = JsonParser.parseString(ruleJson);
            if (element.isJsonArray()) {
                List<AnalyzerRule> rules = RuleHelper.parseRules(ruleJson, AnalyzerRule.class);
                RuleManager.addAllRule(rules);
            } else {
                AnalyzerRule rule = RuleHelper.parseRule(ruleJson, AnalyzerRule.class);
                RuleManager.addRule(rule);
            }
            XToastUtils.success("导入成功");
            view.refreshRules();
        } catch (Exception e) {
            e.printStackTrace();
            XToastUtils.error("导入失败，格式错误");
        }
    }


    /**
     * 从文件导入规则
     *
     * @param uri 文件
     */
    public void importRuleFromFile(Uri uri) {
        Context context = view.requireContext();
        try {
            InputStream stream = context.getContentResolver().openInputStream(uri);
            String ruleJson = IoUtil.readUtf8(stream);
            importRule(ruleJson);
        } catch (IOException e) {
            e.printStackTrace();
            XToastUtils.error("导入失败");
        }
    }

    /**
     * 从网络导入书源
     *
     * @param url 书源链接
     */
    public void importRuleFromUrl(String url) {
        if (!UrlUtils.isHttpUrl(url)) {
            XToastUtils.error("错误的书源链接");
            return;
        }
        Single.create((SingleEmitter<String> emitter) -> {
            RequestParams params = RequestParams.create(url);
            params.setCharset(StandardCharsets.UTF_8.name());
            String source = Http.content(params);
            emitter.onSuccess(source);
        }).compose(o -> RxUtils.threadTrans(o, view.getLifecycleOwner()))
            .doOnSuccess(this::importRule)
            .doOnError(e -> XToastUtils.error("导入失败"))
            .subscribe();
    }


    /**
     * 从文件导入规则
     *
     * @param uri 文件
     */
    public void exportRuleToFile(Uri uri) {
        Context context = view.requireContext();
        try {
            String ruleJson = GsonUtils.PRETTY.toJson(RuleManager.rules());
            String path = FileUtils.getPath(context, uri);
            FileUtil.writeUtf8String(ruleJson, path);
            XToastUtils.success("导出成功到：" + path);
        } catch (Exception e) {
            e.printStackTrace();
            XToastUtils.error("导出失败");
        }
    }

    /**
     * 从剪贴版导入规则
     */
    public void importRuleFromClipboard() {
        String ruleJson = ClipboardUtils.get();
        if (ruleJson != null) {
            importRule(ruleJson);
        }
    }
}
