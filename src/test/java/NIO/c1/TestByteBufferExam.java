package NIO.c1;

import java.nio.ByteBuffer;

public class TestByteBufferExam {
    public static void main(String[] args) {
        ByteBuffer source=ByteBuffer.allocate(32);
        source.put("Hello\nWorld\nHo".getBytes());
        split(source);
        source.put("w are you\n".getBytes());
        split(source);
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
