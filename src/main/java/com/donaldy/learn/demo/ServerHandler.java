package com.donaldy.learn.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ByteBuf in = (ByteBuf) msg;

        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));

        ctx.write(in); // 将接收到的消息写给发送者， 而不冲刷出站消息

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {

        // 将未决消息（暂存与 ChannelOutboundBuffer 中的消息）冲刷到远程节点， 并且关闭该 Channel
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        cause.printStackTrace();
        ctx.close();
    }
}
