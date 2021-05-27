package com.unclezs.novel.app.utils;


/**
 * SharedPreferences管理工具基类
 *
 * @author blog.unclezs.com
 * @since 2018/11/27 下午5:16
 */
public final class SettingUtils {

    private static final String IS_FIRST_OPEN_KEY = "is_first_open_key";
    private static final String SEARCH_BOOK_SEARCH_TYPE = "search_book_search_type";

    private SettingUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 是否是第一次启动
     */
    public static boolean isFirstOpen() {
        return MMKVUtils.getBoolean(IS_FIRST_OPEN_KEY, true);
    }

    /**
     * 设置是否是第一次启动
     */
    public static void setIsFirstOpen(boolean isFirstOpen) {
        MMKVUtils.put(IS_FIRST_OPEN_KEY, isFirstOpen);
    }

    /**
     * 获取是否搜索有声小说
     *
     * @return 是否搜索有声小说
     */
    public static boolean isSearchAudio() {
        return MMKVUtils.getBoolean(SEARCH_BOOK_SEARCH_TYPE, false);
    }

    /**
     * 设置是否搜索有声小说
     *
     * @param isSearchAudio 是否搜索有声小说
     */
    public static void setSearchAudio(boolean isSearchAudio) {
        MMKVUtils.put(SEARCH_BOOK_SEARCH_TYPE, isSearchAudio);
    }


}
