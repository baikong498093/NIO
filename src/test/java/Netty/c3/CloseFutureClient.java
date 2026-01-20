package Netty.c3;

import Netty.Logger;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class CloseFutureClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8888));
        Channel channel = channelFuture.sync().channel();
        new Thread(()->{
            Scanner sc = new Scanner(System.in);
            while (true){
                String line = sc.nextLine();
                if("q".equals(line)){
                    channel.close();//close是异步操作，不会立即执行关闭，在下面写关闭之后的操作会有问题
//                    System.out.println("close channel");
                    break;
                }
                channel.writeAndFlush(line);
            }
        },"input-thread").start();

        ChannelFuture closeFuture = channel.closeFuture();
/*        System.out.println("waiting close");
        closeFuture.sync();//等待关闭完成
        Logger.logger.debug("关闭完成");*/
        //channel关闭后，会触发关闭完成事件
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Logger.logger.debug("关闭完成");
                //优雅的关闭事件循环组，停止接受新的连接，等待所有连接关闭完成
                group.shutdownGracefully();
            }
        });
        //或者使用lambda表达式,更为简洁
        closeFuture.addListener(future ->{
            Logger.logger.debug("关闭完成");
            group.shutdownGracefully();
        });

    }
}
