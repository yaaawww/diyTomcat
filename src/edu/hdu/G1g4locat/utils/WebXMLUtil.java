package edu.hdu.G1g4locat.utils;

import cn.hutool.core.io.FileUtil;
import edu.hdu.G1g4locat.catalina.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class WebXMLUtil {
    private static Map<String, String> mimeTypeMapping = new HashMap<>();

    public static synchronized String getMimeType(String extName) {
        if (mimeTypeMapping.isEmpty())
            initMimeType();

        String mimeType = mimeTypeMapping.get(extName);
        if (null == mimeType)
            return "text/html";
        else
            return mimeType;
    }

    public static void initMimeType() {
        String xml = FileUtil.readUtf8String(Constant.webXmlFile);
        Document d = Jsoup.parse(xml);

        Elements es = d.select("mime-mapping");
        for (Element e : es) {
            String extName = e.select("extension").first().text();
            String mimType = e.select("mime-type").first().text();
            mimeTypeMapping.put(extName, mimType);
        }
    }

    public static String getWelcomeFile(Context context) {
        String xml = FileUtil.readUtf8String(Constant.webXmlFile);
        Document d = Jsoup.parse(xml);
        Elements es = d.select("welcome-file");
        for (Element e : es) {
            String welcomeFileName = e.text();
            File f = new File(context.getDocBase(), welcomeFileName);
            if (f.exists())
                return f.getName();
        }
        return "index.html";
    }
}
