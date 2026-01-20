package Netty.Future;

import Netty.Logger;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class TestNettyFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1. 创建一个EventLoop
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        EventLoop eventLoop = eventLoopGroup.next();
        // 2. 提交一个任务
        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Logger.logger.debug("执行任务");
                Thread.sleep(2000);
                return 50;
            }
        });

//        Logger.logger.debug("主线程继续执行");
//        //同步阻塞方法，会让主线程阻塞，等待异步任务完成
//        Logger.logger.debug("结果是{}", future.get());

        // 3. 注册一个监听器，异步任务完成时，会调用该监听器，主线程不会阻塞
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                //非阻塞方法，立刻去获取异步任务的结果
                Logger.logger.debug("异步任务完成，结果是{}", future.getNow());
            }
        });

        Logger.logger.debug("主线程结束");
    }
}
