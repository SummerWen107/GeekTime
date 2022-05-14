package com.summerwen.geektime_001.node2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;

/**
 * 类描述
 *
 * @author wenjunpu
 * @Date 2022/05/13/18:03
 * @Description
 */
@RestController
@RequestMapping("/data")
public class Synchronized02 {



    @GetMapping("wrong1")
    public int wrong1(@RequestParam(value = "count", defaultValue = "1000000") int count) {
        Data1.reset();
        //多线程循环一定次数调用Data类不同实例的wrong方法
        IntStream.rangeClosed(1, count).parallel().forEach(i -> new Data1().wrong());
        return Data1.getCounter();
    }

    @GetMapping("wrong2")
    public int wrong2(@RequestParam(value = "count", defaultValue = "1000000") int count) {
        Data2.reset();
        //多线程循环一定次数调用Data类不同实例的wrong方法
        IntStream.rangeClosed(1, count).parallel().forEach(i -> new Data2().wrong());
        return Data2.getCounter();
    }
}
