package Netty.c3;

import NIO.c1.TestByteBuffer;
import ch.qos.logback.classic.Logger;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestEventLoop {
    static Logger logger = (Logger) LoggerFactory.getLogger(TestByteBuffer.class);

    public static void main(String[] args) {
        //1、创建EventLoopGroup事件循环组
        EventLoopGroup group = new NioEventLoopGroup(2);
        //2获取下一个事件循环对象;
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());

        //3，执行普通任务，或者使用submit提交任务
        group.next().execute(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            logger.debug("任务执行了");
        });
        System.out.println("主进程结束");
        //4，执行定时任务
        group.next().scheduleAtFixedRate(()->{
            logger.debug("定时任务执行了");
        },0,1, TimeUnit.SECONDS);
    }
}
