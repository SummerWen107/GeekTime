package com.summerwen.geektime_001.node2;

import lombok.extern.slf4j.Slf4j;

/**
 * 类描述
 *
 * @author wenjunpu
 * @Date 2022/05/13/17:47
 * @Description
 */

@Slf4j
public class Interesting2 {


    volatile int a = 1;
    volatile int b = 1;

    public synchronized void add() {
        log.info("add start");
        for (int i = 0; i < 10000; i++) {
            a++;
            b++;
        }
        log.info("add done");
    }

    public void compare() {
        log.info("compare start");
        for (int i = 0; i < 10000; i++) {
            //a始终等于b吗？
            if (a < b) {
                log.info("a:{},b:{},{}", a, b, a > b);
                //最后的a>b应该始终是false吗？
            }
        }
        log.info("compare done");
    }
}
