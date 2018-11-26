package com.donaldy.chapter4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Netty oio 阻塞版
 */
public class NettyOioServer {

    public void server(int port) throws Exception {

        final ByteBuf buf = Unpooled.unreleasableBuffer(
                Unpooled.copiedBuffer("Hi!\r\n", Charset.forName("UTF-8"))
        );

        EventLoopGroup group = new OioEventLoopGroup();

        try {

            // 创建　ServerBootstrap
            ServerBootstrap b = new ServerBootstrap();

            b.group(group)
                    .channel(OioServerSocketChannel.class) // 使用OioEventLoopGroup以允许阻塞模式(旧的 I/O)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 指定ChannelInitializer，对于每个已接受的连接都调用它

                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    // 添加一个ChannelInboundHandlerAdapter以拦截和处理事件
                                    new ChannelInboundHandlerAdapter() {

                                        public void channelActive (
                                                ChannelHandlerContext ctx) throws Exception {
                                            ctx.writeAndFlush(buf.duplicate())
                                                    // 将消息写到客户端，并添加ChannelFutureListener,　以便消息一被写完就关闭连接
                                                    .addListener(ChannelFutureListener.CLOSE);
                                        }
                                    }
                            );
                        }
                    });
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
