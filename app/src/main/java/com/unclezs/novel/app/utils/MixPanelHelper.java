package com.unclezs.novel.app.utils;

import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.unclezs.novel.analyzer.common.concurrent.ThreadUtils;
import com.unclezs.novel.app.BuildConfig;

import org.json.JSONObject;

import java.util.Map;
import java.util.Map.Entry;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * MixPanel分析工具
 * <p>
 * https://mixpanel.com
 *
 * @author blog.unclezs.com
 * @date 2021/6/19 11:42
 */
@Slf4j
@UtilityClass
public class MixPanelHelper {

    public static final String TOKEN = BuildConfig.MIX_PANEL_TOKEN;
    private static boolean profile = true;

    /**
     * 发送事件
     *
     * @param name          事件名称
     * @param eventPropsMap 事件属性
     */
    public static void event(String name, Map<String, Object> eventPropsMap, Context context) {
        ThreadUtils.execute(() -> sendEvent(name, eventPropsMap, context));
    }

    /**
     * 发送事件
     *
     * @param name 事件名称
     */
    public static void event(String name, Context context) {
        event(name, null, context);
    }


    /**
     * 发送事件
     *
     * @param name          事件名称
     * @param eventPropsMap 事件属性
     */
    public static void sendEvent(String name, Map<String, Object> eventPropsMap, Context context) {
        try {
            MixpanelAPI api = MixpanelAPI.getInstance(context, TOKEN);
            api.setEnableLogging(BuildConfig.DEBUG);
            api.setUseIpAddressForGeolocation(true);
            JSONObject eventProps;
            // 封装事件属性
            if (eventPropsMap != null) {
                eventProps = new JSONObject();
                for (Entry<String, Object> entry : eventPropsMap.entrySet()) {
                    eventProps.put(entry.getKey(), entry.getValue());
                }
                api.track(name, eventProps);
            } else {
                api.track(name);
            }
            if (profile) {
                MixpanelAPI.People people = api.getPeople();
                people.identify(api.getDistinctId());
                people.increment("Update Count", 1L);
                profile = false;
            }
        } catch (Exception e) {
            log.debug("mixPanel异常", e);
        }
    }
}
