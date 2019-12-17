package com.jisen.seckill;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jisen
 * @date 2019/7/7 17:08
 */
public class TestService {

    private static Runnable getThread(final int i){
        return new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    System.out.println("线程"+Thread.currentThread().getName()+"执行Task"+i);
                }catch (Exception e){

                }
            }
        };
    }


    public static void main(String[] args) {

        new MyThread().start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("do something");
            }
        }).start();



        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int i =0; i<=10; i++){
            executorService.execute(getThread(i));
            if(i==10)
                System.out.println("任务添加完毕");
        }
        executorService.shutdown();
        System.out.println("the end");
    }
}
