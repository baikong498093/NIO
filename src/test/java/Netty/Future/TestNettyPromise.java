package Netty.Future;

import Netty.Logger;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;

import java.util.concurrent.ExecutionException;

public class TestNettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1. 创建一个EventLoop
        EventLoop eventLoop=new NioEventLoopGroup().next();
        //2. 创建一个结果容器,并关联到EventLoop
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);
        new Thread(()->{
            Logger.logger.debug("子线程开始计算");
            try{
                //3. 子线程计算,并设置到结果容器中
                int i=1/0;
                Thread.sleep(1000);
                promise.setSuccess(100);
            }catch (InterruptedException e){
                e.printStackTrace();
                promise.setFailure(e);
            }
        }).start();
        //4. 主线程阻塞等待结果
        Logger.logger.debug("主线程等待结果");
        Logger.logger.debug("promise result:{}",promise.get());

    }
}
