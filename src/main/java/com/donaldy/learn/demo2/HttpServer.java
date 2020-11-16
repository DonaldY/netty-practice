package com.donaldy.learn.demo2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * @author donald
 * @date 2020/11/15
 */
public class HttpServer {
    public void start(int port) throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                      @Override
                                      public void initChannel(SocketChannel ch) {
                                          ch.pipeline()
                                                  .addLast("codec", new HttpServerCodec()) // HTTP 编解码
                                                  .addLast("compressor", new HttpContentCompressor())// HttpContent 压缩
                                                  .addLast("aggregator", new HttpObjectAggregator(65536))// HTTP 消息聚合
                                                  .addLast("handler", new HttpServerHandler());
                                          // 自定义业务逻辑处理器
                                      }
                                  }
                    )
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind().sync();
            System.out.println("Http Server started， Listening on " + port);
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new HttpServer().start(8088);
    }
}

class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        String content = String.format("Receive http request, uri: %s, method: %s, content: %s%n",
                msg.uri(), msg.method(), msg.content().toString(CharsetUtil.UTF_8));

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(content.getBytes()));
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }

}
