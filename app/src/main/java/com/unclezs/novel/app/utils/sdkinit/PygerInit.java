package com.unclezs.novel.app.utils.sdkinit;

import android.app.Application;

import com.pgyer.pgyersdk.PgyerSDKManager;
import com.pgyer.pgyersdk.pgyerenum.FeatureEnum;

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
            .enable(FeatureEnum.ANALYTICE_FUNCTION_SHAKE)
            .build();
    }
}
