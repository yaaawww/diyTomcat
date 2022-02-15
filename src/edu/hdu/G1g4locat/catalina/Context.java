package edu.hdu.G1g4locat.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.LogFactory;

public class Context {
    private String path;
    private String docBase;

    public Context(String path, String docBase) {
        TimeInterval timeInterval = DateUtil.timer();
        this.path = path;
        this.docBase = docBase;
        LogFactory.get().info("Deploying web application directory {}", this.docBase);
        LogFactory.get().info("Deploying of web application directory {} has finished in {} ms", this.docBase, timeInterval.intervalMs());
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
}
