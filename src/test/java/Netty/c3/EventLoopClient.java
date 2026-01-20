package Netty.c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        //1、启动器
        Channel channel = new Bootstrap()
                //2、添加EventLoop
                .group(new NioEventLoopGroup())
                //3、选择客户端的Channel实现类，NioSocketChannel是基于NIO的SocketChannel实现
                .channel(NioSocketChannel.class)
                //4、添加处理器handler，负责处理读写事件
                .handler(
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override//在建立连接后被调用
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new StringEncoder());//将字符串转为ByteBuf
                            }
                        }
                )
                //5、连接服务器
                .connect(new InetSocketAddress("localhost", 8888))
                .sync()
                .channel();
        System.out.println(channel);
        //6、发送数据
        channel.writeAndFlush("hello world");
        System.out.println("");
    }
}
