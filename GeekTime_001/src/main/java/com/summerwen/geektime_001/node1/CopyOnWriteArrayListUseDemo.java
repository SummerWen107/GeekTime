package com.summerwen.geektime_001.node1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * CopyOnWriteArrayList 最好的使用方式是读多写少，合理的使用，不能忙吗的使用
 * 在写多读少的情况下，性能不及普通ArrayList的百分之1
 *
 * 读多写少的情况
 * ---------------------------------------------
 * ns         %     Task name
 * ---------------------------------------------
 * 010570300  004%  Read:copyOnWriteArrayList
 * 243216800  096%  Read:synchronizedList
 *
 *
 * 写多读少的情况
 * ---------------------------------------------
 * ns         %     Task name
 * ---------------------------------------------
 * 2267177500  099%  Write:copyOnWriteArrayList
 * 020425700  001%  Write:synchronizedList
 *
 * @author wenjunpu
 * @Date 2022/05/13/16:19
 * @Description
 */
@RestController
@RequestMapping("/CopyOnWriteArrayList")
@Slf4j
public class CopyOnWriteArrayListUseDemo {

    //测试并发写的性能
    @GetMapping("write")
    public Map testWrite() {
        List<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        List<Integer> synchronizedList = Collections.synchronizedList(new ArrayList<>());
        StopWatch stopWatch = new StopWatch();
        int loopCount = 100000;
        stopWatch.start("Write:copyOnWriteArrayList");
        //循环100000次并发往CopyOnWriteArrayList写入随机元素
        IntStream.rangeClosed(1, loopCount).parallel().forEach(__ -> copyOnWriteArrayList.add(ThreadLocalRandom.current().nextInt(loopCount)));
        stopWatch.stop();
        stopWatch.start("Write:synchronizedList");
        //循环100000次并发往加锁的ArrayList写入随机元素
        IntStream.rangeClosed(1, loopCount).parallel().forEach(__ -> synchronizedList.add(ThreadLocalRandom.current().nextInt(loopCount)));
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        Map result = new HashMap();
        result.put("copyOnWriteArrayList", copyOnWriteArrayList.size());
        result.put("synchronizedList", synchronizedList.size());
        return result;
    }

    //帮助方法用来填充List
    private void addAll(List<Integer> list) {
        list.addAll(IntStream.rangeClosed(1, 1000000).boxed().collect(Collectors.toList()));
    }

    //测试并发读的性能
    @GetMapping("read")
    public Map testRead() {
        //创建两个测试对象
        List<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        List<Integer> synchronizedList = Collections.synchronizedList(new ArrayList<>());
        //填充数据
        addAll(copyOnWriteArrayList);
        addAll(synchronizedList);
        StopWatch stopWatch = new StopWatch();
        int loopCount = 1000000;
        int count = copyOnWriteArrayList.size();
        stopWatch.start("Read:copyOnWriteArrayList");
        //循环1000000次并发从CopyOnWriteArrayList随机查询元素
        IntStream.rangeClosed(1, loopCount).parallel().forEach(__ -> copyOnWriteArrayList.get(ThreadLocalRandom.current().nextInt(count)));
        stopWatch.stop();
        stopWatch.start("Read:synchronizedList");
        //循环1000000次并发从加锁的ArrayList随机查询元素
        IntStream.range(0, loopCount).parallel().forEach(__ -> synchronizedList.get(ThreadLocalRandom.current().nextInt(count)));
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        Map result = new HashMap();
        result.put("copyOnWriteArrayList", copyOnWriteArrayList.size());
        result.put("synchronizedList", synchronizedList.size());
        return result;
    }
}
