package com.unclezs.novel.app;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.script.ScriptContext;
import com.unclezs.novel.analyzer.script.ScriptUtils;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.app.utils.MMKVUtils;
import com.unclezs.novel.app.utils.sdkinit.ANRWatchDogInit;
import com.unclezs.novel.app.utils.sdkinit.PygerInit;
import com.unclezs.novel.app.utils.sdkinit.XBasicLibInit;
import com.unclezs.novel.app.utils.sdkinit.XUpdateInit;
import com.xuexiang.xormlite.annotation.DataBase;
import com.xuexiang.xormlite.enums.DataBaseType;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

/**
 * @author blog.unclezs.com
 * @since 2018/11/7 下午1:12
 */
@DataBase(name = "app", type = DataBaseType.INTERNAL)
public class App extends Application {
    public static final String BUS_TAG_EXIT = "BUS_TAG_EXIT";

    /**
     * @return 当前app是否是调试开发模式
     */
    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 解决4.x运行崩溃的问题
        MultiDex.install(this);
        PygerInit.init(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLibs();
        initRxErrorHandler();
    }

    /**
     * 初始化rx的错误处理
     */
    private void initRxErrorHandler() {
        RxJavaPlugins.setErrorHandler(Throwable::printStackTrace);
    }

    /**
     * 初始化基础库
     */
    private void initLibs() {
        // X系列基础库初始化
        XBasicLibInit.init(this);
        // mmkv
        MMKVUtils.init(this);
        // 版本更新初始化
        XUpdateInit.init(this);
        // ANR监控
        ANRWatchDogInit.init();
    }

}
