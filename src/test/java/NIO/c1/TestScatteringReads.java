package NIO.c1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestScatteringReads {
    /**
     * 分散读
     * @param args
     */
    public static void main(String[] args) {
        try (FileChannel channel = new RandomAccessFile("data.txt", "r").getChannel()) {
            ByteBuffer b1=ByteBuffer.allocate(3);
            ByteBuffer b2=ByteBuffer.allocate(3);
            ByteBuffer b3=ByteBuffer.allocate(5);
            channel.read(new ByteBuffer[]{b1,b2,b3});
            b1.flip();
            b2.flip();
            b3.flip();

            System.err.println((char)b1.get());
            System.err.println((char)b2.get());
            System.err.println((char)b3.get());
        } catch (IOException e) {

        }
    }
}
