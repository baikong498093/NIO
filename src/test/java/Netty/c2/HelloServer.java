package Netty.c2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HelloServer {
    public static void main(String[] args) {
        //1、启动器，负责组装netty组件，启动服务器
        new ServerBootstrap()
                //2、BossEventLoop负责接收客户端的连接，将连接转交给workerEventLoop处理,group组，包含了线程和选择器
                .group(new NioEventLoopGroup())
                //3、选择ServerSocketChannel实现类，NioServerSocketChannel是基于NIO的ServerSocketChannel实现
                .channel(NioServerSocketChannel.class)
                //4、boss 负责处理连接worker（child）负责处理读写 ，决定了worker（child）能执行哪些操作（handler）
                .childHandler(
                        //5、channel代表和客户端进行的数据读写通道Initializer初始化，负责添加别的handler
                        new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            //6、添加具体的handler
                            ch.pipeline().addLast(new StringDecoder());//将ByteBuf转为字符串
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {//自定义handler
                                @Override //处理读事件
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    //打印上一步转换后的字符串
                                    System.out.println(msg);
                                }
                            });
                        }
                }).bind(8080);

    }
}
