package Netty.c3;

import NIO.c1.TestByteBuffer;
import ch.qos.logback.classic.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import org.slf4j.LoggerFactory;

public class EventLoopServer {
    static Logger logger = (Logger) LoggerFactory.getLogger(TestByteBuffer.class);

    public static void main(String[] args) {
        //细分2：创建一个独立的EventLoopGroup，用于处理耗时较长的任务
        EventLoopGroup group=new DefaultEventLoop();
        new ServerBootstrap()
                //boss和worker线程组
                //细分1：boss只负责处理 ServerSocketChannel上 的accept事件，
                //worker只负责处理 socketChannel 上的 read事件
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast("handler1",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                logger.debug("handler1：{}",byteBuf.toString(CharsetUtil.UTF_8));
                                //将msg传递给下一个handler
                                ctx.fireChannelRead(msg);
                            }
                        }).addLast(group,"handler2",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                logger.debug("handler2：{}",byteBuf.toString(CharsetUtil.UTF_8));
                            }
                        });
                    }
                }).bind(8888);
    }
}
