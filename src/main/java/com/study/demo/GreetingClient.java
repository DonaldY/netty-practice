package com.study.demo;

import java.io.*;
import java.net.Socket;

/**
 * @author donald
 * @date 2020/10/10
 */
public class GreetingClient {

    public static void main(String [] args) {
        String host = "192.168.226.36";
        int port = 8000;

        try {

            System.out.println("连接到主机：" + host + " ，端口号：" + port);
            Socket client = new Socket(host, port);
            System.out.println("远程主机地址：" + client.getRemoteSocketAddress());

            OutputStream outToServer = client.getOutputStream();

            DataOutputStream out = new DataOutputStream(outToServer);

            out.writeUTF("Hello from " + client.getLocalSocketAddress());

            InputStream inFromServer = client.getInputStream();

            DataInputStream in = new DataInputStream(inFromServer);

            System.out.println("服务器响应： " + in.readUTF());

            client.close();

        }catch(IOException e) {

            e.printStackTrace();
        }
    }
}
