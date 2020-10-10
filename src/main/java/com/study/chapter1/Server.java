package com.study.chapter1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Server {

    private ServerSocket serverSocket;

    Server(int port) {
        try {
            // 创建一个新的ServerSocket，用以监听指定端口上的连接请求
            this.serverSocket = new ServerSocket(port);

            System.out.println("服务器启动成功，端口：" + port);
        } catch (IOException e) {
            System.out.println("服务器启动失败");
        }
    }

    // 要管理多个并发客户端，需要为每个新的客户端Socket创建一个新的Thread
    /**
     * 这种情况产生影响
     * 1. 在任何时候都可能有大量的线程处于休眠状态，只是等待输入或者输出数据就绪， 这造成资源浪费。
     * 2. 需要为每个线程的调用栈都分配内存，其默认值大小区间为 64KB 到 1MB，具体取决与操作系统。
     * 3. 即使Java虚拟机（JVM）在物理上可以支持非常大数量的线程，但是远在达到极限之前，上下文切换所带来的开销就会带来麻烦。
     */
    void start() {
        new Thread(this::doStart).start();
    }

    private void doStart() {
        while (true) {
            try {
                // 对accept()方法的调用将被阻塞，直到一个连接建立
                Socket client = serverSocket.accept();
                new ClientHandler(client).start();
            } catch (IOException e) {
                System.out.println("服务器端异常");
            }
        }
    }
}
