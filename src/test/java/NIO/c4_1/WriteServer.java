package NIO.c4_1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class WriteServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8888));
        Selector selector = Selector.open();
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT,null);

        while (true){
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()){
                    SocketChannel socketChannel = ssc.accept();
                    socketChannel.configureBlocking(false);
                    SelectionKey scKey = socketChannel.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);

                    //1向客户端发送数据
                    StringBuffer sb = new StringBuffer();
                    for(int i=0;i<1000000;i++){
                        sb.append("a");
                    }
                    ByteBuffer buff = Charset.defaultCharset().encode(sb.toString());

                    //2返回实际写入的字节数
                    int write = socketChannel.write(buff);
                    System.out.println(write);

                    //3判断是否还有数据未写入
                    if(buff.hasRemaining() ){
                        //4 关注可写事件
                        scKey.interestOps(scKey.interestOps() | SelectionKey.OP_WRITE);
                        //5 把未写入的数据放到附件中
                        scKey.attach(buff);
                    }
                }else if (key.isWritable()){
                    ByteBuffer attachment = (ByteBuffer) key.attachment();
                    SocketChannel channel = (SocketChannel) key.channel();
                    int write = channel.write(attachment);
                    System.out.println(write);
                    //6清理附件
                    if(!attachment.hasRemaining()) {
                        key.attach(null);
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
        }
    }
}
