package NIO.c4;

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

public class Server {
    static int i =1;
    public static void main(String[] args) throws IOException {
        selector();
    }

    private static  void selector() throws IOException {
        //1.创建Selector
        Selector selector=Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        //2.建立seletor和channel的联系
        SelectionKey ssckey = ssc.register(selector, 0, null);
        //让key只关注accept事件
        ssckey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        while (true){
            //3.select方法，没有事件发生会阻塞，有事件发生则恢复运行
            //在事件发生但未处理时则不会阻塞，事件处理或取消才会继续阻塞
            selector.select();
            //4.处理事件，该selectorKey中包含了所有发生的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()){
                SelectionKey key = iter.next();
                //拿到就可以删掉本次集合中的key了，否则接下来会发生空指针
                //因为处理完成后未删除key，下次循环到这，仍会处理该key，然而实际上上次的连接已经处理完了，所以会发生空指针异常。
                iter.remove();
                System.out.println("key---------->"+key);
                //5.区分事件类型
                if(key.isAcceptable()){
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                }else if(key.isReadable()){
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer)key.attachment();
                        int read = channel.read(buffer);
                        if(read==-1){
                            key.cancel();
                        }else{
                            split(buffer);
                            if(buffer.position()==buffer.capacity()){
                                ByteBuffer newBuffer=ByteBuffer.allocate(buffer.capacity()*2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }
                            System.out.println("读出结果:" + byteBufferToString(buffer));
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                        key.cancel();
                    }
                }
            }
        }
    }

    private static void nioNotObstruct() throws IOException {
        //使用nio来理解非阻塞模式
        ByteBuffer buffer=ByteBuffer.allocate(16);
        //1：创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //服务器设为非阻塞模式
        ssc.configureBlocking(false);
        //2：绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
        //3：连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true){
            //System.out.println("accept建立连接之前");
            //4.accept 建立与客户端连接，SocketChannel用来与客户端之间通信
            //非阻塞，不会停，但是没有连接的情况accept返回的是null
            SocketChannel sc = ssc.accept();
            if(sc!=null){
                System.out.println("accept建立了连接..."+sc);
                //将SocketChannel设置为非阻塞模式,会影响SocketChannel.read()是否阻塞
                sc.configureBlocking(false);
                channels.add(sc);
            }
            for(SocketChannel channel:channels){
                int read = channel.read(buffer);//非阻塞，如果没有读到数据，则会返回0
                if(read>0){
                    buffer.flip();
                    //一个个字节读取buffer中的内容

                    System.out.println("读出结果:"+byteBufferToString(buffer));
                }
            }
        }
    }
    private static void nioObstruct() throws IOException {
        //使用nio来理解阻塞模式
        ByteBuffer buffer=ByteBuffer.allocate(16);
        //1：创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //2：绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
        //3：连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true){
            //4.accept 建立与客户端连接，SocketChannel用来与客户端之间通信
            System.out.println("accept...建立连接");
            SocketChannel sc = ssc.accept();//阻塞方法，线程停止运行
            System.out.println("connected..."+sc);
            channels.add(sc);
            for(SocketChannel channel:channels){
                System.out.println("before read..."+channel);
                channel.read(buffer);//阻塞方法，线程停止运行
                buffer.flip();
                buffer.clear();
                //一个个字节读取buffer中的内容
                while (buffer.hasRemaining()) {//判断是否还有剩余数据
                    byte b = buffer.get();
                    System.out.println("读出结果："+(char)b+",poist:"+ buffer.position());
                }
                System.out.println("after read..."+channel);
            }
        }
    }

    public static String byteBufferToString(ByteBuffer buffer){
        //一个个字节读取buffer中的内容
        String str="";
        while (buffer.hasRemaining()) {//判断是否还有剩余数据
            byte b = buffer.get();
            str+=(char)b;
        }
        buffer.clear();
        return  str;
    }
    private static void split(ByteBuffer source){
        source.flip();
        for(int i = 0; i<source.limit(); i++){
            if(source.get(i)=='\n'){
                int length=i+1-source.position();
                ByteBuffer tar=ByteBuffer.allocate(length);
                for(int j=0;j<length;j++){
                    tar.put(source.get());
                }
                String str="";
                for (int t=0;t<tar.limit();t++){
                    str+=(char)tar.get(t);
                }
                System.out.println(str);
            }
            source.compact();
        }

    }
}
