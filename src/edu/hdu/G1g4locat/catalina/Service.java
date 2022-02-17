package edu.hdu.G1g4locat.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.LogFactory;
import edu.hdu.G1g4locat.utils.ServerXMLUtil;

import java.util.List;

public class Service {
    private String name;
    private Engine engine;
    private Server server;
    private List<Connector> connectors;

    public Service(Server server) {
        this.server = server;
        this.name = ServerXMLUtil.getServiceName();
        this.engine = new Engine(this);
        this.connectors = ServerXMLUtil.getConnectors(this);
    }

    public void start() {
        init();
    }

    public Engine getEngine() {
        return engine;
    }

    public Server getServer() {
        return server;
    }

    private void init() {
        TimeInterval timeInterval = DateUtil.timer();
        for(Connector c : connectors)
            c.init();
        LogFactory.get().info("Initialization processed in {} ms", timeInterval.intervalMs());
        for (Connector c : connectors)
            c.start();
    }

}
