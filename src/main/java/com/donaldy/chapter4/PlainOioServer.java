package com.donaldy.chapter4;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class PlainOioServer {

    public void serve(int port) throws IOException {

        // 将服务器绑定到指定端口
        final ServerSocket socket = new ServerSocket(port);

        try {

            for (;;) {

                //　接收连接
                final Socket clientSocket = socket.accept();
                System.out.println("Accepted connection from " + clientSocket);

                // 创建一个新线程来出来该连接
                new Thread(new Runnable() {
                    public void run() {
                        OutputStream out;

                        try {
                            out = clientSocket.getOutputStream();
                            out.write("Hi!\r\n".getBytes(
                                    Charset.forName("UTF-8")));

                            out.flush();
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException ex) {

                            }
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
