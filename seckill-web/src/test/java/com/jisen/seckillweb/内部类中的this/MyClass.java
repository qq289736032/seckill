package com.jisen.seckillweb.内部类中的this;

/**
 * @author jisen
 * @date 2019/7/8 20:42
 */
public class MyClass {

    public void method(){
        System.out.println(this.getClass().getName());
    }

    class InnerClass{
        public void innermethod(){
            System.out.println(this.getClass().getName());
        }
    }
}
