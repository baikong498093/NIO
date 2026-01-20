package NIO.c1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadDemo {
    static ExecutorService executor = Executors.newFixedThreadPool(5);
    int num=0;

    public static void main(String[] args) {
        // 创建一个固定大小的线程池
        // 提交任务给线程池
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                System.out.println(Thread.currentThread().getName() + " is executing task.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // 关闭线程池
        executor.shutdown();

        try {
            // 等待所有任务完成
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("All tasks completed.");
    }
    private void runTest(){

    }
}
