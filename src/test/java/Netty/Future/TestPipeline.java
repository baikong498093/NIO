package Netty.Future;

import Netty.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.charset.Charset;

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
                                ByteBuf buf = (ByteBuf) msg;
                                String str = buf.toString(Charset.defaultCharset());
                                Logger.logger.debug("h1 channelRead:{}",str);
                                Student student = new Student(str);
                                //将数据传给下一个入站处理器 handler
                                super.channelRead(ctx, student);
                            }
                        });
                        pipeline.addLast("h2",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                Logger.logger.debug("h2 channelRead:{}",msg,msg.getClass());
                                //因为这一步是将h2处理传给h3，h3不是入站处理器，所以调用该方法没有意义
                                //但如果下一步是入站处理器，那么就需要调用ctx.fireChannelRead(msg)，否则不会执行下一步
//                                super.channelRead(ctx, msg);
//                                nioSocketChannel.fireChannelRead(msg);
                                //3. 没有这一步无法触发出站处理器
                                nioSocketChannel.writeAndFlush(ctx.alloc().buffer().writeBytes("h2 writeAndFlush".getBytes()));
                                //使用ctx的wirte,是上下文关系，会往前传递，nsc是整体，会往后传递
                                //ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("h2 writeAndFlush".getBytes()));
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
                }).bind(8888);
    }
    @Data
    @AllArgsConstructor
    static class Student{
        private String name;
    }
}
