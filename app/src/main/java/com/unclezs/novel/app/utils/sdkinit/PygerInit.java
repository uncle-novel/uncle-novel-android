package com.unclezs.novel.app.utils.sdkinit;

import android.app.Application;

import com.pgyer.pgyersdk.PgyerSDKManager;
import com.pgyer.pgyersdk.pgyerenum.FeatureEnum;
import com.unclezs.novel.app.BuildConfig;

import lombok.experimental.UtilityClass;

/**
 * 蒲公英初始化
 *
 * @author blog.unclezs.com
 * @date 2021/05/28 10:26
 */
@UtilityClass
public class PygerInit {
    public static void init(Application application) {
        new PgyerSDKManager.InitSdk()
            .setContext(application)
            .setApiKey(BuildConfig.PGYER_API_KEY)
            .setFrontJSToken(BuildConfig.PGYER_FRONT_JS_KEY)
            .enable(FeatureEnum.ANALYTICE_FUNCTION_SHAKE)
            .build();
    }
}
