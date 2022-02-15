package edu.hdu.G1g4locat;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;
import edu.hdu.G1g4locat.catalina.*;
import edu.hdu.G1g4locat.http.Request;
import edu.hdu.G1g4locat.http.Response;
import edu.hdu.G1g4locat.utils.Constant;
import edu.hdu.G1g4locat.utils.ThreadPoolUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Bootstrap {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}