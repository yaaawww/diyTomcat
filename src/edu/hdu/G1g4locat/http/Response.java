package edu.hdu.G1g4locat.http;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Locale;

/**
 * 对Response数据进行封装
 */
public class Response extends BaseResponse {
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

    public byte[] getBody() throws UnsupportedEncodingException {
        if (null == body) {
            String content = stringWriter.toString();
            body = content.getBytes("UTF-8");
        }
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
