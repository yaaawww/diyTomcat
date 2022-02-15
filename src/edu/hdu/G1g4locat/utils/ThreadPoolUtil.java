package edu.hdu.G1g4locat.utils;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {
    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 100, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10));
    public static void run (Runnable r) {
        threadPool.execute(r);
    }
}
