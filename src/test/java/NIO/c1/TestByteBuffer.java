package NIO.c1;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


@Slf4j
public class TestByteBuffer {

    /*public static void main(String[] args) throws Exception {
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            //准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                //从channal中读数据，向缓冲区写入（Buffer）。
                System.out.println("初始：posti:" + buffer.position() + "，limit:" + buffer.limit());
                int leng = channel.read(buffer);
                System.out.println("read后：posti:" + buffer.position() + "，limit:" + buffer.limit());
                System.out.println("字节数：" + leng);
                if (leng == -1) break;
                //buffer切换到读取模式,把标志位置为0
                buffer.flip();
                System.out.println("flip后：posti:" + buffer.position() + "，limit:" + buffer.limit());
                //一个个字节读取buffer中的内容
                while (buffer.hasRemaining()) {//判断是否还有剩余数据
                    byte b = buffer.get();
                    System.out.println("读出结果：" + (char) b + ",poist:" + buffer.position());
                }
                buffer.clear();//切换为写模式，清空buffer的内容,compact也可以，保留未读内存
                System.out.println("clear后：posti:" + buffer.position() + "，limit:" + buffer.limit());
            }
        } catch (IOException e) {

        }
    }*/

    public static void main(String[] args) {
        test1();
    }
    private static void test1() {
        //获取当前时间
        long start = System.currentTimeMillis();
        final Logger logger = (Logger) LoggerFactory.getLogger(TestByteBuffer.class);
        //输入流输出流，通道
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(10);
            //read会返回本次写入的字节数，如果返回-1则表示文件结束
            while (channel.read(buffer)!=-1) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
//                    log.debug("实际字节{}", (char) b);
                    logger.debug("实际字节{}", (char) b);
                }
                buffer.clear();
            }
        } catch (IOException e) {

        }
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start)+"ms");
    }
}
