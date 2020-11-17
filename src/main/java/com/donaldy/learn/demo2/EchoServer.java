package com.donaldy.learn.demo2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

import java.util.Objects;

/**
 * @author donald
 * @date 2020/11/16
 */

public class EchoServer {

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

                            ch.pipeline().addLast(new FixedLengthFrameDecoder(10));
                            ch.pipeline().addLast(new ResponseSampleEncoder());
                            ch.pipeline().addLast(new RequestSampleHandler());
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

        new EchoServer().startEchoServer(8088);
    }
}

// 用于将服务端的处理结果进行编码
class ResponseSampleEncoder extends MessageToByteEncoder<ResponseSample> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseSample msg, ByteBuf out) {

        if (msg != null) {

            out.writeBytes(msg.getCode().getBytes());
            out.writeBytes(msg.getData().getBytes());
            out.writeLong(msg.getTimestamp());
        }
    }
}

// 主要负责客户端的数据处理，并通过调用 ctx.channel().writeAndFlush 向客户端返回 ResponseSample 对象，其中包含返回码、响应数据以及时间戳。
class RequestSampleHandler extends ChannelInboundHandlerAdapter {

    @Override

    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        String data = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);

        ResponseSample response = new ResponseSample("OK", data, System.currentTimeMillis());

        ctx.channel().writeAndFlush(response);
    }
}

class ResponseSample {

    private String code;
    private String data;
    private String msg;
    private Long timestamp;

    public String getCode() {
        return code;
    }

    public String getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public ResponseSample(String msg, String data, Long timestamp) {
        this.code = "0";
        this.data = data;
        this.msg = msg;
        this.timestamp = timestamp;
    }
}