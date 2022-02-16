package edu.hdu.G1g4locat.utils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MiniBrowser {
    public static void main(String[] args) throws IOException {
        String testUrl = "http://79bfcf55-56ea-471a-9c80-472372d3fe04.challenge.ctf.show/v/c?r=Li4vLi4vLi4vV0VCLUlORi9jbGFzc2VzL2NvbS9jdGZzaG93L2NvbnRyb2xsZXIvSW5kZXguY2xhc3M=";
        byte[] res = getContentBytes(testUrl, false);
        FileOutputStream fileOutputStream = new FileOutputStream("file/index.class");
        BufferedOutputStream bufferedInputStream = new BufferedOutputStream(fileOutputStream);
        bufferedInputStream.write(res);
        bufferedInputStream.close();
    }
    public static String getContentString(String url) throws IOException {
        return getContentString(url, false);
    }
    public static String getContentString(String url, boolean gzip) throws IOException {
        byte[] result = getContentBytes(url, gzip);
        if(null == result)
            return null;
        return new String(result, "utf-8").trim();
    }
    public static byte[] getContentBytes(String url) throws IOException {
        return getContentBytes(url, false);
    }
    /**
     * A method is used to get rid of the headers
     * @param url
     * @param gzip
     * @return
     * @throws IOException
     */
    public static byte[] getContentBytes(String url, boolean gzip) throws IOException {
        byte[] response = getHttpBytes(url, gzip);
        byte[] doubleReturn = "\r\n\r\n".getBytes();//There is whiteLine after http header end, so there are two \r\n\r\n

        int pos = -1;
        for (int i = 0; i < response.length - doubleReturn.length; i++) {
            byte[] temp = Arrays.copyOfRange(response, i, i + doubleReturn.length);

            if(Arrays.equals(temp, doubleReturn)) {
                pos = i;
                break;
            }
        }
        if(-1 == pos)
            return null;
        pos += doubleReturn.length;

        byte[] result = Arrays.copyOfRange(response, pos, response.length);
        return result;
    }
    public static String getHttpString(String url) throws IOException {
        return getHttpString(url, false);
    }
    public static String getHttpString(String url, boolean gzip) throws IOException {
        byte[] bytes = getHttpBytes(url, gzip);
        return new String(bytes).trim();
    }
    public static byte[] getHttpBytes(String url) throws IOException {
        return getHttpBytes(url, false);
    }

    /**
     * Get the byte of response of the server.
     * @param url
     * @param gzip
     * @return
     * @throws IOException
     */
    public static byte[] getHttpBytes(String url, boolean gzip) throws IOException {
        byte[] result = null;
        URL u = new URL(url);
        //Client conncects the server.
//        String proxyIP="127.0.0.1";//代理服务器地址
//        int proxyPort=8080;//代理服务器端口
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIP, proxyPort));
        Socket client = new Socket();
        int port = u.getPort();
        if (-1 == port)
            port = 80;
        InetSocketAddress inetSocketAddress = new InetSocketAddress(u.getHost(), port);
        client.connect(inetSocketAddress, 1000);
        //write the request headers into our map;
        Map<String, String> requestHeaders = new HashMap<>();

        requestHeaders.put("Host", u.getHost() + ":" + port);
        requestHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
        requestHeaders.put("Connection", "close");
        requestHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:96.0) Gecko/20100101 Firefox/96.0");

        if (gzip) {
            requestHeaders.put("Accept-Encoding", "gzip");
        }
        String path = u.getPath();
        if (path.length() == 0)
            path = "/";

        String firstLine = "GET " + u.getFile() + " HTTP/1.1\r\n";

        StringBuffer httpRequestString = new StringBuffer();
        httpRequestString.append(firstLine);
        Set<String> headers = requestHeaders.keySet();
        for (String header : headers) {
            String headLine = header + ":" + requestHeaders.get(header) + "\r\n";
            httpRequestString.append(headLine);
        }
        //send our request
        PrintWriter pWriter = new PrintWriter(client.getOutputStream(), true);
        pWriter.println(httpRequestString);

        //receive response
        InputStream is = client.getInputStream();

        result = readBytes(is, true);
        client.close();

        return result;
    }
    public static byte[] readBytes(InputStream is, boolean fully) throws IOException {
        int buffer_size = 1024;
        byte buffer[] = new byte[buffer_size];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while(true) {
            int length = is.read(buffer);
            if(-1==length)
                break;
            baos.write(buffer, 0, length);
            if(!fully && length!=buffer_size)
                break;
        }
        byte[] result =baos.toByteArray();
        return result;
    }
}
