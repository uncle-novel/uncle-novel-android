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
        String js = "\"var reqParams \\u003d {\\n    url: url,\\n    headers: {\\n        \\\"Referer\\\": url,\\n        \\\"Cookie\\\": params.cookie\\n    },\\n    method: \\\"GET\\\"\\n}\\nvar html \\u003d utils.request(JSON.stringify(reqParams));\\nresult \\u003d utils.match(html, \\\"//p/allText()\\\")\\n\"";
        Params params = new Params();
        params.setCookie("_paabbcc=0ggpf2f2n9p0umhcfubjk68nr5; _ga=GA1.2.1198897455.1624503028; _gid=GA1.2.127817223.1624503028; url=https%3A%2F%2Fwww.po18.tw; authtoken1=cXdlMDU1Mg%3D%3D; authtoken2=NjYyNzc3MDE2ZmQ2NjU2MDcwNzA1NTk2YjlkNTNmZTI%3D; authtoken3=686525769; authtoken4=1649371916; authtoken5=1624503409; authtoken6=1; bgcolor=bg-default; word=select-s; _po18rf-tk001=af6183f91fba48c3e9cac1d655943e5f84b711d8a19a2f2d1be8dbafc4536e85a%3A2%3A%7Bi%3A0%3Bs%3A13%3A%22_po18rf-tk001%22%3Bi%3A1%3Bs%3A32%3A%22rIaN6x0NyLZnLD3aFoq1Zo_7ZwW7At1i%22%3B%7D");
        System.out.println(GsonUtils.PRETTY.toJson(params));
        ScriptContext.put(ScriptContext.SCRIPT_CONTEXT_VAR_RESULT, "");
        ScriptContext.put(ScriptContext.SCRIPT_CONTEXT_VAR_URL, "https://www.po18.tw/books/743225/articles/8793874");
        ScriptContext.put(ScriptContext.SCRIPT_CONTEXT_VAR_SOURCE, "");
        ScriptContext.put(ScriptContext.SCRIPT_CONTEXT_VAR_PARAMS, params);
        ScriptUtils.execute(js, ScriptContext.current());
    }

}
