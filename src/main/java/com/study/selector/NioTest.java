package com.study.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioTest {

    public static void main(String[] args) throws IOException {

        // 开启一个server channel来监听
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 开启非阻塞模式
        ssc.configureBlocking(false);
        ServerSocket socket = ssc.socket();
        socket.bind(new InetSocketAddress("localhost",65535));//绑定相应IP及port
        Selector selector = Selector.open();//开启一个selector
        ssc.register(selector, SelectionKey.OP_ACCEPT);//绑定注册事件

        while(true){
            selector.select();//阻塞,只有当至少一个注册的事件发生的时候才会继续.
            Set<SelectionKey> selectKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectKeys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                // 处理事件. 可以用多线程来处理.
                //this.dispatch(key);
            }
        }
    }
}
