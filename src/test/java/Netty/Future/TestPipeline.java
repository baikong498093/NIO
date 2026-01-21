package Netty.Future;

import Netty.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TestPipeline {
    public static void main(String[] args) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //1.通过channel获取pipeline
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();

                        //2.添加入站处理器 head -> h1 -> h2 -> h3 -> h4 ->tail
                        pipeline.addLast("h1",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                Logger.logger.debug("h1 channelRead");
                                super.channelRead(ctx, msg);
                            }
                        });
                        pipeline.addLast("h2",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                Logger.logger.debug("h2 channelRead");
                                super.channelRead(ctx, msg);
                                //3. 没有这一步无法触发出站处理器
                                ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("h2 writeAndFlush".getBytes()));
                            }
                        });
                        //3.添加出站处理器
                        pipeline.addLast("h3",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                Logger.logger.debug("h3 write");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("h4",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                Logger.logger.debug("h4 write");
                                super.write(ctx, msg, promise);
                            }
                        });
                        //执行顺序: h1 -> h2 -> h3 -> h4，出站处理器是从后往前执行的
                    }
                });
    }
}
