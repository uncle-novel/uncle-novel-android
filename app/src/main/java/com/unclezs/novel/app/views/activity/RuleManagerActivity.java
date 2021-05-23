package com.unclezs.novel.app.views.activity;

import android.os.Bundle;

import com.unclezs.novel.app.base.BaseActivity;
import com.unclezs.novel.app.base.BasePresenter;
import com.unclezs.novel.app.views.fragment.rule.RuleManagerFragment;

/**
 * @author blog.unclezs.com
 * @date 2021/05/23 17:11
 */
public class RuleManagerActivity extends BaseActivity<BasePresenter<RuleManagerActivity>> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openPage(RuleManagerFragment.class, getIntent().getExtras());
    }
}
