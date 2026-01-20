package NIO.c1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Demo {
    public static void main(String[] args) {
        forceTest();
    }

    /**
     * 将流设为强制写入磁盘中
     */
    private static void forceTest(){
        try (FileChannel r = new RandomAccessFile("demo1.txt", "rw").getChannel()) {
            Date date=new Date();
            //该参数为true表示直接写入磁盘中，否则则存在与缓存中，直接写入磁盘速度较慢
            r.force(true);
            ByteBuffer b1 = StandardCharsets.UTF_8.encode("你好你好你好你好你好\n");
            ByteBuffer b2 = StandardCharsets.UTF_8.encode("你好你好你好你好你好\n");
            r.write(new ByteBuffer[]{b1,b2});
            Date date2=new Date();
            System.out.println("毫秒："+(date2.getTime()-date.getTime()));
        } catch (IOException e) {
        }
    }
}
