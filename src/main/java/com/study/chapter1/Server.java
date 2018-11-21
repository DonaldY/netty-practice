package com.study.chapter1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * test
 */
public class Server {

    private ServerSocket serverSocket;

    public Server(int port) {
        try {
            // 创建一个新的ServerSocket，用以监听指定端口上的连接请求
            this.serverSocket = new ServerSocket(port);

            System.out.println("服务器启动成功，端口：" + port);
        } catch (IOException e) {
            System.out.println("服务器启动失败");
        }
    }

    // 要管理多个并发客户端，需要为每个新的客户端Socket创建一个新的Thread
    public void start() {
        new Thread(new Runnable() {
            public void run() {
                doStart();
            }
        }).start();
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
