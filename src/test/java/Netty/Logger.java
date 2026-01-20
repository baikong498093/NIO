package Netty;

import NIO.c1.TestByteBuffer;
import org.slf4j.LoggerFactory;

public class Logger {
    public static ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(TestByteBuffer.class);
}
