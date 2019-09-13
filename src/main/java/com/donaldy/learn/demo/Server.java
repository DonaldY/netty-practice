package com.donaldy.learn.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {

    public static void main(String[] args) {

        int port = 8000;

        //创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // 用来接收客户端的连接
        EventLoopGroup workGroup = new NioEventLoopGroup(); // 用来进行SocketChannel的网络读写

        try {

            // 辅助启动类
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workGroup)  // 注册两个线程组
                    .channel(NioServerSocketChannel.class) // 通道, 指定 NIO 传输 Channel
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            // 调用链
                            ch.pipeline().addLast(new ServerHandler()); // 实现业务逻辑
                        }
                    });

            ChannelFuture f = b.bind(port).sync(); // bind是异步过程, 绑定接口, 阻塞

            f.channel().closeFuture().sync(); // 阻塞, 直到 Channel 关闭

        } catch(Exception e){

            e.printStackTrace();

        } finally {

            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
