package NIO.c1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class TestGatheringWrites {
    /**
     * 集中写入
     * @param args
     */
    public static void main(String[] args) {
        //准备字节缓冲区
        ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b2 = StandardCharsets.UTF_8.encode("world");
        ByteBuffer b3 = StandardCharsets.UTF_8.encode("你好");
        //创建于文件的连接
        try (FileChannel channel = new RandomAccessFile("words.txt", "rw").getChannel()) {
            //向文件写入
            channel.write(new ByteBuffer[]{b1,b2,b3});
        } catch (IOException e) {
        }
    }
}
