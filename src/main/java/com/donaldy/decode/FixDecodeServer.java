package com.donaldy.decode;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.util.CharsetUtil;

/**
 * @author donald
 * @date 2020/11/12
 */
public class FixDecodeServer {

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
                            ch.pipeline().addLast(new FixedLengthFrameDecoder(3));
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

        new FixDecodeServer().startEchoServer(8088);

    }

}

@ChannelHandler.Sharable
class EchoServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Receive client : [" + ((ByteBuf) msg).toString(CharsetUtil.UTF_8) + "]");
    }

}
