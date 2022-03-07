package edu.hdu.G1g4locat.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import edu.hdu.G1g4locat.exception.WebConfigDuplicatedException;
import edu.hdu.G1g4locat.utils.ContextXmlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.*;

public class Context {
    private String path;
    private String docBase;
    private File contextWebXmlFile;

    private Map<String, String> url_servletClassName;
    private Map<String, String> url_servletName;
    private Map<String, String> servletName_className;
    private Map<String, String> className_servletName;

    public Context(String path, String docBase) {
        TimeInterval timeInterval = DateUtil.timer();
        this.path = path;
        this.docBase = docBase;
        LogFactory.get().info("Deploying web application directory {}", this.docBase);
        LogFactory.get().info("Deploying of web application directory {} has finished in {} ms", this.docBase, timeInterval.intervalMs());
        this.contextWebXmlFile = new File(docBase, ContextXmlUtil.getWatchedResource());
        this.url_servletClassName = new HashMap<>();
        this.url_servletName = new HashMap<>();
        this.servletName_className = new HashMap<>();
        this.className_servletName = new HashMap<>();
        deploy();
    }

    public String getPath() {
        return path;
    }

    public String getDocBase() {
        return docBase;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }

    public String getServletClassName (String uri) {
        return url_servletClassName.get(uri);
    }

    private void parseServletMapping(Document d) {
        //url_servletName
        Elements mappingurlElements = d.select("servlet-mapping url-pattern");
        for (Element mappingurlElement : mappingurlElements) {
            String urlPattern = mappingurlElement.text();
            String servletName = mappingurlElement.parents().select("servlet-name").first().text();
            url_servletName.put(urlPattern, servletName);
        }
        //servletName_className className_servletName
        Elements servletNameElements = d.select("servlet servlet-name");
        for (Element servletNameElement: servletNameElements) {
            String nativeServletName = servletNameElement.text();
            String servletClass = servletNameElement.parent().select("servlet-class").first().text();
            servletName_className.put(nativeServletName, servletClass);
            className_servletName.put(servletClass, nativeServletName);
        }
        //url_servletClass
        Set<String> urls = url_servletName.keySet();
        for (String url : urls) {
            String servletName = url_servletName.get(url);
            String servletClass = servletName_className.get(servletName);
            url_servletClassName.put(url, servletClass);
        }
    }

    private void checkDuplicated(Document d, String mapping, String desc) throws WebConfigDuplicatedException{
        Elements elements = d.select(mapping);
        List<String> contents = new ArrayList<>();
        for (Element e : elements) {
            contents.add(e.text());
        }
        Collections.sort(contents);
        String temp = contents.get(0);
        for (int i = 1; i < contents.size(); i++) {
            if (temp.equals(contents.get(i))) {
                throw new WebConfigDuplicatedException(StrUtil.format(desc, temp));
            } else {
                temp = contents.get(i);
            }
        }
    }

    private void checkDuplicated() throws WebConfigDuplicatedException {
        String xml = FileUtil.readUtf8String(contextWebXmlFile);
        Document d = Jsoup.parse(xml);
        checkDuplicated(d, "servlet-mapping url-pattern", "servlet url 重复,请保持其唯一性:{} ");
        checkDuplicated(d, "servlet servlet-name", "servlet 名称重复,请保持其唯一性:{} ");
        checkDuplicated(d, "servlet servlet-class", "servlet 类名重复,请保持其唯一性:{} ");
    }

    private void init () {
        if (!contextWebXmlFile.exists()) {
            return;
        }
        try {
            checkDuplicated();
        } catch (WebConfigDuplicatedException e) {
            e.printStackTrace();
            return;
        }
        String xml = FileUtil.readUtf8String(contextWebXmlFile);
        Document d = Jsoup.parse(xml);
        parseServletMapping(d);
    }

    private void deploy () {
        TimeInterval timeInterval = DateUtil.timer();
        LogFactory.get().info("Deploying web application directory {}", this.docBase);
        init();
        LogFactory.get().info("Deployment of web application directory {} has finished in {} ms",this.getDocBase(),timeInterval.intervalMs());
    }

}
