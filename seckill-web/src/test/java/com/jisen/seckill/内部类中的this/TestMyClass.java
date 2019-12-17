package com.jisen.seckill.内部类中的this;

/**
 * @author jisen
 * @date 2019/7/8 20:43
 */
public class TestMyClass {
    public static void main(String[] args) {
        MyClass myClass = new MyClass();
        myClass.method();
        MyClass.InnerClass innerClass = myClass.new InnerClass();
        innerClass.innermethod();
    }
}
