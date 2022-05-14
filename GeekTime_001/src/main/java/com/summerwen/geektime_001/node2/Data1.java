package com.summerwen.geektime_001.node2;

import lombok.Getter;

/**
 * 类描述
 *
 * @author wenjunpu
 * @Date 2022/05/13/17:59
 * @Description
 */

class Data1 {
    @Getter
    private static int counter = 0;

    public static int reset() {
        counter = 0;
        return counter;
    }

    public synchronized void wrong() {
        counter++;
    }
}
