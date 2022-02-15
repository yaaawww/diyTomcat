package edu.hdu.G1g4locat.http;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 对Response数据进行封装
 */
public class Response {
    private StringWriter stringWriter;
    private PrintWriter writer;
    private byte[] body;

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    private String contentType;

    public Response() {
        this.stringWriter = new StringWriter();
        this.writer = new PrintWriter(stringWriter);
        this.contentType = "text/html";
    }

    public String getContentType() {
        return contentType;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public byte[] getBody() {
        if (null == body) {
            String content = stringWriter.toString();
            byte[] body = content.getBytes(StandardCharsets.UTF_8);
        }
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
