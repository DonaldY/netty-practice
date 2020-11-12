package com.donaldy.decode;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

/**
 * @author donald
 * @date 2020/11/12
 */
public class DelimiterDecodeServer {

    public void startEchoServer(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        public void initChannel(SocketChannel ch) {
                            ByteBuf delimiter = Unpooled.copiedBuffer("&".getBytes());
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(10, true, true, delimiter));
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    });

            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        } finally {

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {

        new DelimiterDecodeServer().startEchoServer(8088);

    }
}
