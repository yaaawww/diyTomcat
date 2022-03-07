package edu.hdu.G1g4locat.test;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import edu.hdu.G1g4locat.utils.MiniBrowser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class catTest {
    private static int port = 18080;
    private static String ip = "127.0.0.1";

    @BeforeClass
    public static void beforeClass() {
        if (NetUtil.isUsableLocalPort(port)) {
            System.err.println("Please start the port: " + port + " of the cat.");
            System.exit(1);
        } else {
            System.out.println("The cat has operated");
        }
    }

    @Test
    public void testHelloTomcat() throws IOException {
        String html = getContentString("/");
        Assert.assertEquals(html, "Hello, this is my server G1g4loCat");
    }

    @Test
    public void testTxt() throws IOException {
        String html = getContentString("/a.txt");
        Assert.assertEquals(html, "GNU/Linux is a spirit.");
    }

    @Test
    public void testHtml() throws IOException {
        String html = getContentString("/a.html");
        Assert.assertEquals(html, "aaaaaaaa");
    }

    @Test
    public void testBIndex() throws IOException {
        String html = getContentString("/b/");
        Assert.assertEquals(html, "Hello G1g4lo from index.html@b.");
    }
    @Test
    public void testTimeConsumeHtml() throws InterruptedException {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 20, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(10));
        TimeInterval timeInterval = DateUtil.timer();

        for (int i = 0; i < 3; i++) {
            threadPool.execute(new Runnable() {
                public void run() {
                    try {
                        getContentString("/timeConsume.html");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.HOURS);

        long duration = timeInterval.intervalMs();

        Assert.assertTrue(duration < 3000);
    }
    @Test
    public void test404() throws IOException {
        String response = getHttpString("/fxxk.html");
        containAssert(response, "HTTP/1.1 404 NOT FOUND");
    }

    @Test
    public void test500() throws IOException {
        String response = getHttpString("/500.html");
        containAssert(response, "HTTP/1.1 500 Internal Server Error");
    }
    private void containAssert(String html, String string) {
        boolean match = StrUtil.containsAny(html, string);
        Assert.assertTrue(match);
    }

    private String getContentString(String uri) throws IOException {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        String content = MiniBrowser.getContentString(url);
        return content;
    }
    private String getHttpString(String uri) throws IOException {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        String http = MiniBrowser.getHttpString(url);
        return http;
    }
    private byte[] getContentByte(String uri) throws IOException {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        byte[] content = MiniBrowser.getContentBytes(url);
        return content;
    }
    @Test
    public void testFunction() {
        System.out.println("\t hfhakhfk ");
    }

    @Test
    public void testPNG() throws IOException {
        byte[] bytes = getContentByte("/test.jpg");
        int pngFileLength = 56637;
        Assert.assertEquals(pngFileLength, bytes.length);
    }
    @Test
    public void testHello() throws IOException {
        String html = getContentString("/hello");
        Assert.assertEquals(html,"Hello, Servlet!");
    }
    @Test
    public void testhello() throws IOException {
        String html = getContentString("/j2ee/hello");
        Assert.assertEquals(html,"Hello, Servlet!");
    }
}