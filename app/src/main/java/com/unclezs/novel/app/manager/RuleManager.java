package com.unclezs.novel.app.manager;

import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.App;
import com.xuexiang.xutil.resource.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.experimental.UtilityClass;

/**
 * 规则管理器
 *
 * @author blog.unclezs.com
 * @date 2021/4/24 1:07
 */
@UtilityClass
public class RuleManager {

    public static final String RULES_FILE_NAME = "rules.json";
    private static final List<AnalyzerRule> RULES;

    static {
        File ruleJsonFile = ResourceManager.file(RULES_FILE_NAME);
        if (ruleJsonFile.exists()) {
            String ruleJson = ResourceManager.fileString(RULES_FILE_NAME);
            RuleHelper.loadRules(ruleJson);
        } else {
            saveRule(new ArrayList<>());
        }
        RULES = new ArrayList<>(RuleHelper.rules());
        // 绑定监听
        RuleHelper.setOnRuleChangeListener(rules -> {
            RULES.clear();
            RULES.addAll(rules);
            saveRule();
        });
    }

    public static List<AnalyzerRule> rules() {
        return RULES;
    }

    /**
     * 存在则哥更，不存在添加
     *
     * @param rule 规则
     */
    public static void addRule(AnalyzerRule rule) {
        if (StringUtils.isBlank(rule.getSite())) {
            return;
        }
        AnalyzerRule old = RuleHelper.getRule(rule.getSite());
        if (old != null) {
            BeanUtil.copyProperties(rule, old, CopyOptions.create().setTransientSupport(false));
            saveRule();
        } else {
            RuleHelper.addRule(rule);
        }
    }

    /**
     * 存在则哥更，不存在添加
     *
     * @param rules 规则
     */
    public static void addAllRule(List<AnalyzerRule> rules) {
        for (AnalyzerRule rule : rules) {
            addRule(rule);
        }
    }

    /**
     * 规则是否存在
     *
     * @param rule 规则
     * @return true 存在
     */
    public static boolean exist(AnalyzerRule rule) {
        return RuleHelper.getRule(rule.getSite()) != null;
    }

    /**
     * 获取规则
     *
     * @param url 网站
     * @return 规则
     */
    public static AnalyzerRule get(String url) {
        return RuleHelper.getRule(url);
    }

    /**
     * 获取规则
     *
     * @param url 网站
     * @return 规则不存在则创建
     */
    public static AnalyzerRule getOrDefault(String url) {
        return RuleHelper.getOrDefault(url);
    }

    /**
     * 更新全部规则
     *
     * @param rules 所有规则
     */
    public static void update(List<AnalyzerRule> rules) {
        RuleHelper.setRules(rules);
    }


    /**
     * 文本小说规则
     *
     * @return 规则
     */
    public static List<AnalyzerRule> textRules() {
        return RULES.stream().filter(rule -> Boolean.TRUE.equals(rule.getEnabled()) && rule.isEffective() && Boolean.FALSE.equals(rule.getAudio())).collect(Collectors.toList());
    }

    /**
     * 文本小说搜索规则
     *
     * @return 规则
     */
    public static List<AnalyzerRule> textSearchRules() {
        return textRules().stream().filter(rule -> rule.getSearch() != null && rule.getSearch().isEffective()).collect(Collectors.toList());
    }

    /**
     * 有声小说规则
     *
     * @return 规则
     */
    public static List<AnalyzerRule> audioRules() {
        return RULES.stream().filter(rule -> Boolean.TRUE.equals(rule.getEnabled()) && rule.isEffective() && Boolean.TRUE.equals(rule.getAudio())).collect(Collectors.toList());
    }

    /**
     * 有声小说搜索规则
     *
     * @return 规则
     */
    public static List<AnalyzerRule> audioSearchRules() {
        return audioRules().stream().filter(rule -> rule.getSearch() != null && rule.getSearch().isEffective()).collect(Collectors.toList());
    }

    public static void saveRule() {
        saveRule(RULES);
    }

    private static void saveRule(List<AnalyzerRule> rules) {
        ResourceManager.saveString(GsonUtils.toJson(rules), RULES_FILE_NAME);
    }
}
