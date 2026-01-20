package Netty.Future;

import Netty.Logger;

import java.util.concurrent.*;

public class TestJdkFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();

        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Logger.logger.debug("执行异步任务");
                Thread.sleep(3000);
                return 50;
            }
        });

        Logger.logger.debug("主线程继续执行");
        // get方法是阻塞方法，会让主线程阻塞，等待异步任务完成
        Logger.logger.debug("结果是{}", future.get());
    }
}
