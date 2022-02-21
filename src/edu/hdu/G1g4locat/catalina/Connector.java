package edu.hdu.G1g4locat.catalina;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import edu.hdu.G1g4locat.http.Request;
import edu.hdu.G1g4locat.http.Response;
import edu.hdu.G1g4locat.utils.Constant;
import edu.hdu.G1g4locat.utils.ThreadPoolUtil;
import edu.hdu.G1g4locat.utils.WebXMLUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Connector implements Runnable {
    int port;

    public void setPort(int port) {
        this.port = port;
    }

    private Service service;

    public Service getService() {
        return service;
    }

    public Connector(Service service) {
        this.service = service;
    }

    public void init() {
        LogFactory.get().info("Initializing ProtocolHandler [http-bio-{}]", port);
    }

    public void start() {
        LogFactory.get().info("Initializing ProtocolHandler [http-bio-{}]", port);
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Cat已启动");
            while (true) {
                Socket s = serverSocket.accept();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Request request = new Request(s, service);
                            Response response = new Response();
                            HttpProcessor processor = new HttpProcessor();
                            processor.execute(s, request, response);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (!s.isClosed())
                                try {
                                    s.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                        }
                    }
                };
                ThreadPoolUtil.run(r);
            }
        } catch (IOException e) {
            LogFactory.get().error(e);
            e.printStackTrace();
        }
    }
}
