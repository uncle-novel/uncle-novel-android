package com.unclezs.novel.app.utils.sdkinit;

import android.app.Application;

import com.unclezs.novel.app.App;
import com.unclezs.novel.app.base.BaseActivity;
import com.unclezs.novel.app.db.InternalDataBase;
import com.xuexiang.xormlite.AppDataBaseRepository;
import com.xuexiang.xormlite.logs.DBLog;
import com.xuexiang.xpage.PageConfig;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.XUI;
import com.xuexiang.xutil.XUtil;

import lombok.experimental.UtilityClass;

/**
 * X系列基础库初始化
 *
 * @author blog.unclezs.com
 * @since 2019-06-30 23:54
 */
@UtilityClass
public final class XBasicLibInit {


    /**
     * 初始化基础库SDK
     */
    public static void init(Application application) {
        // 工具类
        initXUtil(application);
        // 页面框架
        initXPage(application);
        // UI框架
        initXUI(application);
        // 路由框架
        initRouter(application);
        // 数据库
        initDB(application);
    }

    /**
     * 初始化XUtil工具类
     */
    private static void initXUtil(Application application) {
        XUtil.init(application);
        XUtil.debug(App.isDebug());
    }

    /**
     * 初始化XPage页面框架
     */
    private static void initXPage(Application application) {
        PageConfig.getInstance()
            .debug(App.isDebug() ? "PageLog" : null)
            .setContainActivityClazz(BaseActivity.class)
            .init(application);
    }

    /**
     * 初始化XUI框架
     */
    private static void initXUI(Application application) {
        XUI.init(application);
        XUI.debug(App.isDebug());
    }

    /**
     * 初始化路由框架
     */
    private static void initRouter(Application application) {
        // 这两行必须写在init之前，否则这些配置在init过程中将无效
        if (App.isDebug()) {
            XRouter.openLog();     // 打印日志
            XRouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        XRouter.init(application);
    }

    /**
     * 初始化数据库框架
     *
     * @param application /
     */
    private static void initDB(Application application) {
        AppDataBaseRepository.getInstance()
            //设置内部存储的数据库实现接口
            .setIDatabase(new InternalDataBase())
            .init(application);
        DBLog.debug(App.isDebug());
    }

}
