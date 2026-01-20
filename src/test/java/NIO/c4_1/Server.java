package NIO.c4_1;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;
import NIO.c1.TestByteBuffer;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static NIO.c2.ByteBufferUtil.debugRead;

@Slf4j
public class Server {
    static Logger logger = (Logger) LoggerFactory.getLogger(TestByteBuffer.class);

    public static void main(String[] args) throws IOException {
        nioSelector();
    }
    //使用selector来处理多个连接
    private static void nioSelector() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        SelectionKey sscKey = ssc.register(selector, 0, null);
        logger.debug("对象sscKey，{}", sscKey);
        sscKey.interestOps(SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8888));
        while (true) {
            //select在事件未处理时，它不会阻塞，接收到事件后，要么处理，要么取消，不能置之不理
            selector.select();
            //处理事件selectedkeys内部包含了所有发生的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                //处理完事件后，必须要从selectedKeys中移除，否则会一直存在,否则下次循环还是会处理到这个key，会出现空指针
                iter.remove();
                logger.debug("对象key，{}", key);
                //区分事件类型
                if(key.isAcceptable()){
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    //处理这个连接
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    SelectionKey sckey = sc.register(selector, 0, buffer);
                    sckey.interestOps(SelectionKey.OP_READ);
                    logger.debug("accept之后，发生了连接{}", sc);
                }else if(key.isReadable()){
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);
                        if(read == -1){
                            key.cancel();
                            //continue;
                        }else{
                            split(buffer);
                            if(buffer.position() == buffer.limit()){
                               ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity()*2);
                               buffer.flip();
                               newBuffer.put(buffer);
                               key.attach(newBuffer);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();
                    }
                }
//                key.cancel();
            }
        }
    }

    //使用nio来理解非阻塞模式，单线程处理多个连接
    //问题：非阻塞时cpu代码一直在空转，cpu占用率高
    private static void nioNotBlock() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();

        ssc.configureBlocking(false);

        ssc.bind(new InetSocketAddress(8888));

        ByteBuffer buffer = ByteBuffer.allocate(10);

        List<SocketChannel> channels = new ArrayList<>();

        while (true) {
            //System.out.println("accept之前，等待连接中");
            SocketChannel sc = ssc.accept();
            if (sc != null) {
                System.out.println("accept之后，发生了连接");
                sc.configureBlocking(false);
                channels.add(sc);
            }            //遍历集合中的链接，如果有链接，并且不为空，则读取数据
            for (SocketChannel ch : channels) {
                int len = ch.read(buffer);
                if(len > 0) {
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    logger.debug("after read....{}", ch);
                }
            }
        }
    }


    //使用nio来理解阻塞模式，单线程
    //单线程处理nio的问题：accept和read都是阻塞的，必须等一个完成，才能处理下一个，
    //所以只有第一次连接发送数据时是正常的，后面的连接发送数据时，会阻塞在read方法上
    private static void nioBlock() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();

        ssc.bind(new InetSocketAddress(8888));

        ByteBuffer buffer = ByteBuffer.allocate(10);

        List<SocketChannel> channels = new ArrayList<>();

        while (true) {
            System.out.println("accept之前，等待连接中");
            SocketChannel channel = ssc.accept();
            System.out.println("accept之后，连接成功");

            channels.add(channel);
            for (SocketChannel ch : channels) {
                ch.read(buffer);
                buffer.flip();
                debugRead(buffer);
                buffer.clear();
                logger.debug("after read....{}",ch);
            }
        }
    }

    private static void split(ByteBuffer buffer){
        buffer.flip();
        for(int i = 0; i < buffer.limit(); i++){
            if(buffer.get(i) == '\n'){
                int len = i - buffer.position() + 1;
                ByteBuffer target = ByteBuffer.allocate(len);
                for(int j = 0; j < len; j++){
                    target.put(buffer.get());
                }
                target.flip();
                debugRead(target);
            }
        }
        buffer.compact();
    }
}
