package com.unclezs.novel.app.core;

import android.content.Context;

import com.xuexiang.xrouter.annotation.Router;
import com.xuexiang.xrouter.facade.service.SerializationService;
import com.xuexiang.xutil.net.JsonUtil;

import java.lang.reflect.Type;

@Router(path = "/service/json")
public class JsonSerializationService implements SerializationService {
    @Override
    public void init(Context context) {

    }

    @Override
    public String object2Json(Object instance) {
        return JsonUtil.toJson(instance);
    }

    @Override
    public <T> T parseObject(String input, Type clazz) {
        return JsonUtil.fromJson(input, clazz);
    }
}
