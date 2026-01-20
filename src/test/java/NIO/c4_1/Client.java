package NIO.c4_1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8888));
        sc. write(ByteBuffer.wrap("111222333444".getBytes()));
        System.in.read();
    }
}
