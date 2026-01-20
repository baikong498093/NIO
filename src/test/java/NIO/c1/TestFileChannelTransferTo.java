package NIO.c1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 文件传输
 */
public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        moreTransfer();
    }

    /**
     * 单次文件传输，数据大小受限
     */
    private static void onceTransfer(){
        try (FileChannel inp = new FileInputStream("demo1.txt").getChannel();
             FileChannel out = new FileOutputStream("demo2.txt").getChannel();
        ){
            //效率高，底层会利用操作系统的零拷贝进行优化，上限最多一次传输2G
            inp.transferTo(0,inp.size(),out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 多次文件传输
     */
    private static void moreTransfer(){
        try (FileChannel inp = new FileInputStream("demo1.txt").getChannel();
             FileChannel out = new FileOutputStream("demo2.txt").getChannel();
        ) {
            long size=inp.size();
            for(long len=size;len>0;){
                System.out.println("size:"+size+"len:"+len);
                len-= inp.transferTo((size-len),len,out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
