package com.donaldy.chapter8;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class BootstrapClient {

    public static void main(String[] args) {

        EventLoopGroup group = new NioEventLoopGroup();

        // 创建一个Bootstrap类的实例以创建和连接新的客户端Channel
        Bootstrap bootstrap = new Bootstrap();

        // 设置EventLoopGroup，提供用于处理Channel事件的EventLoop
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                        System.out.println("Received data");
                    }
                });
        ChannelFuture future = bootstrap.connect(new InetSocketAddress("www.maning.com", 80));

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {

                    System.out.println("Connection established");
                } else {

                    System.err.println("Connection attempt failed");
                    future.cause().printStackTrace();
                }
            }
        });
    }
}
