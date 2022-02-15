package edu.hdu.G1g4locat.catalina;

import edu.hdu.G1g4locat.utils.ServerXMLUtil;

public class Service {
    private String name;
    private Engine engine;
    private Server server;


    public Service(Server server) {
        this.server = server;
        this.name = ServerXMLUtil.getServiceName();
        this.engine = new Engine(this);
    }

    public Engine getEngine() {
        return engine;
    }

    public Server getServer() {
        return server;
    }

}
