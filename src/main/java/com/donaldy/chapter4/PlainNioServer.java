package com.donaldy.chapter4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class PlainNioServer {

    public void server(int port) throws IOException {

        ServerSocketChannel serverChannel = ServerSocketChannel.open();

        serverChannel.configureBlocking(false);

        ServerSocket ssocket = serverChannel.socket();

        InetSocketAddress address = new InetSocketAddress(port);

        ssocket.bind(address);

        // 打开　Selector 来处理　Channel
        Selector selector = Selector.open();

        // 将　ServerSocket 注册到Selector以接受连接
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        final ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes());

        for (;;) {

            try {
                selector.select();
            } catch (IOException ex) {
                 ex.printStackTrace();
                 // handle exception
                break;
            }

            // 获取所有接受事件的SelectionKey实例
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                iterator.remove();

                try {
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();

                        SocketChannel client = server.accept();
                        client.configureBlocking(false);

                        // 接受客户端，并将它注册到选择器
                        client.register(selector, SelectionKey.OP_WRITE |
                                SelectionKey.OP_READ, msg.duplicate());

                        System.out.println("Accepted connection from " + client);

                        // 检查套接字是否已经准备好写数据
                        if (key.isWritable()) {
                            SocketChannel client2 = (SocketChannel) key.channel();

                            ByteBuffer buffer =
                                    (ByteBuffer) key.attachment();

                            while (buffer.hasRemaining()) {
                                if (client2.write(buffer) == 0) {
                                    break;
                                }
                            }
                        }
                        client.close();
                    }
                } catch (IOException ex) {
                    key.cancel();

                    try {
                        key.channel().close();
                    } catch (IOException cex) {
                        //
                    }
                }
            }
        }
    }
}
