package NIO.c4_1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import static NIO.c4_1.Server.logger;

public class MultiThreadServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Thread.currentThread().setName("Boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8888));
        Selector boss = Selector.open();
        ssc.register(boss, SelectionKey.OP_ACCEPT, null);
        //1，创建固定数量的worker线程
        Worker worker = new Worker("Worker-0");
        while (true) {
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    logger.debug("connected {}", sc.getRemoteAddress());
                    logger.debug("before register {}", sc.getRemoteAddress());
                    //2.将sc关联到workerSelector中
                    worker.register(sc);
                    logger.debug("after register {}", sc.getRemoteAddress());
                }
            }

        }
    }

    static class Worker implements Runnable {
        private Thread thread;
        private String name;
        private Selector workerSelector;

        private volatile boolean running = false;

        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }

        public void register(SocketChannel sc) throws IOException {
            if (!running) {
                running = true;
                thread = new Thread(this, name);
                workerSelector = Selector.open();
                thread.start();
            }
            //方法1，在这里向队列添加任务，然后唤醒workerSelector，让sc注册到workerSelector中
            queue.add(()->{
                try {
                    sc.register(workerSelector, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            });
            workerSelector.wakeup();
        }
        public void register2(SocketChannel sc) throws IOException {
            if (!running) {
                running = true;
                thread = new Thread(this, name);
                workerSelector = Selector.open();
                thread.start();
            }
            //方法2，在这里唤醒workerSelector，
            //防止worker的run方法中select阻塞，导致死锁，然后再注册sc到workerSelector中
            workerSelector.wakeup();
            sc.register(workerSelector, SelectionKey.OP_READ, null);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    workerSelector.select();
                    //从队列中取出任务并执行
                    Runnable task = queue.poll();
                    if (task != null) {
                        task.run();
                    }
                    Iterator<SelectionKey> iter = workerSelector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                       SelectionKey key = iter.next();
                       iter.remove();
                       if (key.isReadable()) {
                           ByteBuffer buffer = ByteBuffer.allocate(16);
                           SocketChannel sc = (SocketChannel) key.channel();
                           logger.debug("read {}", sc.getRemoteAddress());
                           sc.read(buffer);
                           buffer.flip();
                           logger.debug("{} 读取到了 {}", sc, buffer);
                       }
                        key.cancel();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
