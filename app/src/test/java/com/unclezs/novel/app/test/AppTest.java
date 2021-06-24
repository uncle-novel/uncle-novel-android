package com.unclezs.novel.app.test;

import com.unclezs.novel.analyzer.core.model.Params;
import com.unclezs.novel.analyzer.script.ScriptContext;
import com.unclezs.novel.analyzer.script.ScriptUtils;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.regex.RegexUtils;

import org.junit.Test;

/**
 * @author blog.unclezs.com
 * @date 2021/05/20 0:59
 */
public class AppTest {
    @Test
    public void testRegex() {
        String param = "keyword";
        String src = "完美时间欸";
        String script = RegexUtils.get("\\{\\{([^{]*?" + param + ".*?)\\}\\}", src, 1);
        System.out.println(script);
    }

    @Test
    public void testScript() {
    }

}
