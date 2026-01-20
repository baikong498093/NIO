package NIO.c2;

import ch.qos.logback.classic.Logger;
import NIO.c1.TestByteBuffer;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class ByteBufferUtil {

    public static void debugRead(ByteBuffer buffer){
        Logger logger = (Logger) LoggerFactory.getLogger(TestByteBuffer.class);
        while (buffer.hasRemaining()) {
            logger.debug("debugRead, 实际字节: {}", (char) buffer.get());
        }
    }

}
