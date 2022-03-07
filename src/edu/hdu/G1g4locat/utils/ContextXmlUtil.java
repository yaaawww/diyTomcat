package edu.hdu.G1g4locat.utils;

import cn.hutool.core.io.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class ContextXmlUtil {
    public static String getWatchedResource() {
        try {
            String xml = FileUtil.readUtf8String(Constant.contextXmlFile);
            Document d = Jsoup.parse(xml);
            Element e = d.selectFirst("WatchedResource");
            return e.text();
        } catch (Exception e) {
            e.printStackTrace();
            return "WEB-INF/web.xml";
        }
    }
}
