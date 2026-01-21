package Netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

public class TestByteBuf {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        System.out.println(buf);
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<256;i++){
            sb.append('a');
        }
        buf.writeBytes(sb.toString().getBytes());
        System.out.println(buf);
    }
}
