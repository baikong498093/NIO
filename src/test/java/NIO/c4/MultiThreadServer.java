package NIO.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc=ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss=Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        Worker worker = new Worker("worker-0");
        while (true){
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()){
                SelectionKey key = iter.next();
                iter.remove();
                if(key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    System.out.println("before register——————>"+sc.getRemoteAddress());
                    worker.register(sc);//worker-0
                    System.out.println("after register——————>"+sc.getRemoteAddress());
                }
            }
        }
    }

    static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;
        private volatile Boolean start=false;
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();//队列
        public Worker(String name) {
            this.name = name;
        }
        public void register(SocketChannel sc) throws IOException {
            if(!start) {
                selector = Selector.open();
                thread = new Thread(this, name);
                thread.start();
                start=true;
            }
            //向队列中添加了任务，但这个任务并没有立刻被执行
            queue.add(()->{
                try {
                    sc.register(selector,SelectionKey.OP_READ,null);//Boss
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            });
            selector.wakeup();//唤醒selector方法
            sc.register(selector,SelectionKey.OP_READ,null);
        }
        @Override
        public void run() {
            while (true){
                try {
                    selector.select();
                    Runnable task = queue.poll();
                    if(task!=null){
                        task.run();
                    }
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()){
                        SelectionKey key = iter.next();
                        iter.remove();
                        if(key.isReadable()){
                            ByteBuffer buffer=ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel)key.channel();
                            System.out.println("channel————————>"+channel.getRemoteAddress());
                            channel.read(buffer);
                            buffer.flip();
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
