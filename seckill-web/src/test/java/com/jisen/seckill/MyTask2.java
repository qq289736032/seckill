package com.jisen.seckill;

/**
 * @author jisen
 * @date 2019/7/7 18:00
 */
public class MyTask2 implements Runnable {
    @Override
    public void run() {
        for (int i = 0;i<=100;i++){
            try {
                System.out.println("MyTask2:"+Thread.currentThread().getName()+":"+i);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
