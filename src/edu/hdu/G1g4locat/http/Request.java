package edu.hdu.G1g4locat.http;

import cn.hutool.core.util.StrUtil;
import edu.hdu.G1g4locat.catalina.Context;
import edu.hdu.G1g4locat.catalina.Engine;
import edu.hdu.G1g4locat.catalina.Service;
import edu.hdu.G1g4locat.utils.MiniBrowser;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * 对Request请求数据进行封装处理
 */
public class Request {
    private String requestString;
    private String uri;
    private Socket socket;
    private Context context;
    private Service service;
    public Request(Socket socket, Service service) throws IOException {
        this.socket = socket;
        this.service = service;
        parseHttpRequest();
        if(StrUtil.isEmpty(requestString))
            return;
        parseUri();
        parseContext();
        if (!"/".equals(context.getPath())) {
            uri = StrUtil.removePrefix(uri, context.getPath());
            if (StrUtil.isEmpty(uri))
                uri = "/";
        }
    }

    private void parseHttpRequest() throws IOException {
        InputStream is = socket.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(is, false);
        requestString = new String(bytes, "utf-8");
    }

    private void parseUri() {
        String temp;
        temp = StrUtil.subBetween(requestString, " ", " ");
        if(!StrUtil.contains(temp, '?')) {
            uri = temp;
            return;
        }
        temp = StrUtil.subBefore(temp, '?', false);
        uri = temp;
    }

    public String getUri() {
        return uri;
    }

    public String getRequestString() {
        return requestString;
    }

    public Context getContext() {
        return context;
    }

    private void parseContext() {
        Engine engine = service.getEngine();
        context = engine.getDefaultHost().getContext(uri);
        if (null != context)
            return;
        String path = StrUtil.subBetween(uri, "/", "/");
        if (null == path)
            path = "/";
        else
            path = "/" + path;
        context = engine.getDefaultHost().getContext(path);
        if (null == context)
            context = engine.getDefaultHost().getContext("/");
    }
}
