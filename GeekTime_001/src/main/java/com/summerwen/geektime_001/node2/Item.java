package com.summerwen.geektime_001.node2;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 类描述
 *
 * @author wenjunpu
 * @Date 2022/05/14/6:55
 * @Description
 */

@Data
@RequiredArgsConstructor
class Item {
    final String name; //商品名
    int remaining = 1000; //库存剩余
    @ToString.Exclude //ToString不包含这个字段
    ReentrantLock lock = new ReentrantLock();
}
