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
                            Request request = null;
                            request = new Request(s, service);
                            String requestString = request.getRequestString();
                            String uri = request.getUri();
                            System.out.println("浏览器输入信息:\r\n" + requestString);
                            System.out.println("uri: " + uri);

                            Response response = new Response();

                            if (null == uri)
                                return;
                            System.out.println(uri);

                            Context context = request.getContext();
                            if ("/500.html".equals(uri)) {
                                throw new Exception("this is a test500.");
                            }

                            if ("/".equals(uri))
                                uri = WebXMLUtil.getWelcomeFile(request.getContext());

                            String filename = StrUtil.removePrefix(uri, "/");
                            File file = FileUtil.file(context.getDocBase(), filename);
                            if (file.exists()) {
                                String extName = FileUtil.extName(file);
                                String mimeType = WebXMLUtil.getMimeType(extName);
                                response.setContentType(mimeType);
                                byte[] body = FileUtil.readBytes(file);
                                response.setBody(body);

                                if (filename.equals("timeConsume.html")) {
                                    ThreadUtil.sleep(1000);
                                }
                            } else {
                                handle404(s, uri);
                                return;
                            }
                            handle200(s, response);
                        } catch (Exception e) {
                            LogFactory.get().error(e);
                            try {
                                handle500(s, e);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        } finally {
                            try {
                                if (!s.isClosed())
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

    private static void handle200(Socket s, Response response) throws IOException {
        String contentType = response.getContentType();
        String headText = Constant.response_head_202;
        headText = StrUtil.format(headText, contentType);
        byte[] head = headText.getBytes("UTF-8");
        byte[] body = response.getBody();

        byte[] responseBytes = new byte[head.length + body.length];
        ArrayUtil.copy(head, 0, responseBytes, 0, head.length);
        ArrayUtil.copy(body, 0, responseBytes, head.length, body.length);

        OutputStream os = s.getOutputStream();
        os.write(responseBytes);
        s.close();
    }

    protected void handle404(Socket s, String uri) throws IOException {
        OutputStream os = s.getOutputStream();
        String responseText = StrUtil.format(Constant.testFormat_404, uri, uri);
        responseText = Constant.response_head_404 + responseText;
        byte[] responseByte = responseText.getBytes(StandardCharsets.UTF_8);
        os.write(responseByte);
    }

    protected void handle500(Socket s, Exception e) throws IOException {
        OutputStream os = s.getOutputStream();
        StackTraceElement stes[] = e.getStackTrace();
        StringBuffer sb = new StringBuffer();
        sb.append(e.toString());
        sb.append("\r\n");
        for (StackTraceElement ste : stes) {
            sb.append("\t");
            sb.append(ste.toString());
            sb.append("\r\n");
        }

        String msg = e.getMessage();

        if (null != msg && msg.length() > 20)
            msg = msg.substring(0, 19);

        String text = StrUtil.format(Constant.textFormat_500, msg, e.toString(), sb.toString());
        text = Constant.response_head_500 + text;
        byte[] responseBytes = text.getBytes("utf-8");
        os.write(responseBytes);
    }
}
