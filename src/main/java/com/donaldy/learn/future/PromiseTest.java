package com.donaldy.learn.future;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PromiseTest {

    public static void main(String[] args) {
        // main 方法
        PromiseTest test = new PromiseTest();
        NioEventLoopGroup loop = new NioEventLoopGroup();
        Promise<String> promise = test.search(loop, "Netty In Action");

        promise.addListener(new GenericFutureListener<Future<? super String>>() {
            @Override
            public void operationComplete(Future<? super String> future) throws Exception {
                System.out.println("Listener 1, price is " + future.get());
            }

        });

        loop.shutdownGracefully();
    }


    private Promise<String> search(NioEventLoopGroup loop, String prod) {

        DefaultPromise<String> promise = new DefaultPromise<String>(loop.next());

        loop.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println(String.format("	>>search price of %s from internet!",prod));
                promise.setSuccess("$33.33"); // 等待5S后设置future为成功，

            }
        },0, TimeUnit.SECONDS);

        return promise;
    }
}
