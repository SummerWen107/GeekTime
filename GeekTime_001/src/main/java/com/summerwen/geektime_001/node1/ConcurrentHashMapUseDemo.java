package com.summerwen.geektime_001.node1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 类描述
 *
 * @author wenjunpu
 * @Date 2022/05/12/21:49
 * @Description
 */
@Slf4j
@RestController
@RequestMapping("/concurrentHashMap")
public class ConcurrentHashMapUseDemo {

    //循环次数
    private static int LOOP_COUNT = 10000000;
    //线程数量
    private static int THREAD_COUNT = 10;
    //元素数量
    private static int ITEM_COUNT = 10;

    /**
     * 普通使用
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/normalUse")
    private Map<String, Long> normalUse() throws InterruptedException {
        ConcurrentHashMap<String, Long> freqs = new ConcurrentHashMap<>(ITEM_COUNT);
        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, LOOP_COUNT).parallel().forEach(i -> {
                    //这里是随机的key，但总和应该是对的
                    //获得一个随机的Key
                    String key = "item" + ThreadLocalRandom.current().nextInt(ITEM_COUNT);
                    synchronized (freqs) {
                        if (freqs.containsKey(key)) {
                            //Key存在则+1
                            freqs.put(key, freqs.get(key) + 1);
                        } else {
                            //Key不存在则初始化为1
                            freqs.put(key, 1L);
                        }
                    }
                }
        ));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        long count = freqs.values().stream().mapToLong(Long::longValue).reduce(0,Long::sum);
        log.info("数量：{}",count);
        //AtomicReference<Long> count = new AtomicReference<>(0L);
        //freqs.values().forEach(e-> count.set(count.get() + e));
        //log.info("normalUse合计值为:{}",count);
        return freqs;
    }

    /**
     * 改进方式
     * computeIfAbsent 不允许key为null
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/computeIfAbsent")
    private Map<String, Long> computeIfAbsent() throws InterruptedException {
        ConcurrentHashMap<String, LongAdder> freqs = new ConcurrentHashMap<>(ITEM_COUNT);
        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, LOOP_COUNT).parallel().forEach(i -> {
                    String key = "item" + ThreadLocalRandom.current().nextInt(ITEM_COUNT);
                    //利用computeIfAbsent()方法来实例化LongAdder，然后利用LongAdder来进行线程安全计数
                    freqs.computeIfAbsent(key, k -> new LongAdder()).increment();
                }
        ));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        //因为我们的Value是LongAdder而不是Long，所以需要做一次转换才能返回
        return freqs.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().longValue())
                );
    }


    /**
     * 检查正确的使用(ComputeIfAbsent)和不正确使用(synchronized)的性能及正确性
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/contrastNormalUseAndComputeIfAbsent")
    public String contrastNormalUseAndComputeIfAbsent() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("normalUse");
        Map<String, Long> normalUse = normalUse();
        stopWatch.stop();
        //校验元素数量
        Assert.isTrue(normalUse.size() == ITEM_COUNT, "normalUse size error");
        //校验累计总数
        Assert.isTrue(normalUse.values().stream().mapToLong(value -> value).reduce(0, Long::sum) == LOOP_COUNT, "normaluse count error");
        stopWatch.start("computeIfAbsent");
        Map<String, Long> goodUse = computeIfAbsent();
        stopWatch.stop();
        Assert.isTrue(goodUse.size() == ITEM_COUNT, "gooduse size error");
        Assert.isTrue(goodUse.values().stream().mapToLong(value -> value).reduce(0, Long::sum) == LOOP_COUNT, "computeIfAbsent count error");
        log.info(stopWatch.prettyPrint());
        return "OK";
    }

    /**
     * 对比 computeIfAbsent 和 putIfAbsent 的性能
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/contrastComputeIfAbsentAndPutIfAbsent")
    private String contrastComputeIfAbsentAndPutIfAbsent() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("computeIfAbsent");
        Map<String, Long> goodUse = computeIfAbsent();
        stopWatch.stop();
        //校验元素数量
        Assert.isTrue(goodUse.size() == ITEM_COUNT, "computeIfAbsent size error");
        //校验累计总数
        Assert.isTrue(goodUse.values().stream().mapToLong(value -> value).reduce(0, Long::sum) == LOOP_COUNT, "computeIfAbsent count error");

        stopWatch.start("putIfAbsent");
        Map<String, Long> putIfAbsent = putIfAbsent();
        stopWatch.stop();
        Assert.isTrue(putIfAbsent.size() == ITEM_COUNT, "gooduse size error");
        Assert.isTrue(putIfAbsent.values().stream().mapToLong(value -> value).reduce(0, Long::sum) == LOOP_COUNT, "putIfAbsent count error");
        log.info(stopWatch.prettyPrint());
        return "OK";
    }


    @GetMapping("/putIfAbsent")
    private Map<String, Long> putIfAbsent() throws InterruptedException {
        ConcurrentHashMap<String, LongAdder> freqs = new ConcurrentHashMap<>(ITEM_COUNT);
        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, LOOP_COUNT).parallel().forEach(i -> {
                    String key = "item" + ThreadLocalRandom.current().nextInt(ITEM_COUNT);
                    //利用computeIfAbsent()方法来实例化LongAdder，然后利用LongAdder来进行线程安全计数
            LongAdder longAdder = freqs.putIfAbsent(key, new LongAdder());
            if (longAdder != null){
                longAdder.increment();
            }else {
                freqs.get(key).increment();
            }
                }
        ));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        Assert.isTrue(freqs.values().stream().mapToLong(LongAdder::longValue).reduce(0,Long::sum) == LOOP_COUNT,"数量不对");
        //因为我们的Value是LongAdder而不是Long，所以需要做一次转换才能返回
        return freqs.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().longValue()));
    }
}
