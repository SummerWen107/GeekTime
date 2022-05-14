package com.summerwen.geektime_001.node2;

import lombok.Getter;

/**
 * 类描述
 *
 * @author wenjunpu
 * @Date 2022/05/13/17:59
 * @Description
 */

class Data2 {
    @Getter
    private static int counter = 0;

    private static final Object lock = new Object();

    public static int reset() {
        counter = 0;
        return counter;
    }

    /**
     * 在非静态的 wrong 方法上加锁，
     * 只能确保多个线程无法执行同一个实例的 wrong 方法，
     * 却不能保证不会执行不同实例的 wrong 方法。
     * 而静态的 counter 在多个实例中共享，所以必然会出现线程安全问题。
     */
    public void wrong() {
        synchronized (lock) {
            counter++;
        }

    }
    //为什么不直接写成静态方法呢？
    //1、改成静态方法会改变代码结构
//    2、静态代码锁住的是this，如果其他方法也是被synchronized修饰，那么就会产生影响
//    3、使用object可以使用多个锁，减少锁的影响
}
