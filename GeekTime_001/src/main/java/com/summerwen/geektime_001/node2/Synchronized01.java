package com.summerwen.geektime_001.node2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类描述
 *
 * @author wenjunpu
 * @Date 2022/05/13/17:49
 * @Description
 */
@RestController
@RequestMapping("/interesting")
@Slf4j
public class Synchronized01 {

    @GetMapping("/unlock")
    public String unlock() throws InterruptedException {
        Interesting1 interesting = new Interesting1();
        Thread t1 = new Thread(interesting::add);
        Thread t2 = new Thread(interesting::compare);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        return "ok";
    }

    @GetMapping("/lockAdd")
    public String lockAdd() throws InterruptedException {
        Interesting2 interesting = new Interesting2();
        Thread t1 = new Thread(interesting::add);
        Thread t2 = new Thread(interesting::compare);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        return "ok";
    }

    @GetMapping("/lockAll")
    public String lockAll() throws InterruptedException {
        Interesting3 interesting = new Interesting3();
        Thread t1 = new Thread(interesting::add);
        Thread t2 = new Thread(interesting::compare);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        return "ok";
    }



}
