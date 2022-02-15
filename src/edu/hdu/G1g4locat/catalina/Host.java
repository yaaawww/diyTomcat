package edu.hdu.G1g4locat.catalina;

import edu.hdu.G1g4locat.utils.Constant;
import edu.hdu.G1g4locat.utils.ServerXMLUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Host {
    private String name;
    private Map<String, Context> contextMap;
    private Engine engine;

    public Host(String name, Engine engine) {
        this.contextMap = new HashMap<>();
        this.name = name;
        this.engine = engine;
        scanContextsOnWebAppFolder();
        scanContextsInServerXML();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void scanContextsInServerXML() {
        List<Context> contexts = ServerXMLUtil.getContexts();
        for (Context context : contexts) {
            contextMap.put(context.getPath(), context);
        }
    }

    private void scanContextsOnWebAppFolder() {
        File[] folders = Constant.webappsFolder.listFiles();
        for (File folder : folders) {
            if (!folder.exists())
                return;
            loadContext(folder);
        }
    }

    private void loadContext(File folder) {
        String path = folder.getName();
        if ("ROOT".equals(path))
            path = "/";
        else
            path = "/" + path;
        String docBase = folder.getAbsolutePath();
        Context context = new Context(path, docBase);
        contextMap.put(context.getPath(), context);
    }

    public Context getContext(String path) {
        return contextMap.get(path);
    }
}
