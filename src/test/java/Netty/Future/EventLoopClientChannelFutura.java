package Netty.Future;

import Netty.Logger;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class EventLoopClientChannelFutura {
    public static void main(String[] args) throws InterruptedException {
        //2.带有FUture.Promise的类型都是和异步方法配套使用的，用来处理异步操作的结果
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override//在建立连接后被调用
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new StringEncoder());//将字符串转为ByteBuf
                            }
                        }
                )
                //1.连接到服务器
                //异步非阻塞，main发起了调用，真正执行connect方法的是NioEventLoopGroup中的线程
                .connect(new InetSocketAddress("localhost", 8888));
        //2.1 使用sync方法阻塞，让主线程等待连接完成（当前阻塞的是main线程）
//        channelFuture.sync();//阻塞等待连接完成，如果没有这行代码，则会因为没有等待EventLoop线程连接完成而向下执行，会出问题
//        //获取channel
//        Channel channel = channelFuture.channel();
//        Logger.logger.debug("channel:{}",channel);
//        channel.writeAndFlush("hello world");

        //2.2 使用addListener(回调对象)方法添加一个监听器，当连接完成时，会调用监听器的operationComplete方法
        channelFuture.addListener(new ChannelFutureListener() {
            //连接成功后，会由nio线程去运行operationComplete方法
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                Logger.logger.debug("channel:{}",channel);
                channel.writeAndFlush("hello world");
            }
        });

    }
}
