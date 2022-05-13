package com.summerwen.geektime_001.node1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述
 *
 * @author wenjunpu
 * @Date 2022/05/12/19:44
 * @Description
 */
@RestController
@RequestMapping("/ThreadLocal")
public class ThreadLocalErrDemo {

    private static final ThreadLocal<Integer> currentUser = ThreadLocal.withInitial(() -> null);

    //http://localhost:8081/ThreadLocal/wrong?userId=1
    //http://localhost:8081/ThreadLocal/wrong?userId=2
    //正常的before是null，但没有显示的删除缓存，导致缓存错乱
    //{
    //"before": "http-nio-8080-exec-1:1",
    //"after": "http-nio-8080-exec-1:2"
    //}
    @GetMapping("wrong")
    public Map wrong(@RequestParam("userId") Integer userId) {
        //设置用户信息之前先查询一次ThreadLocal中的用户信息
        String before  = Thread.currentThread().getName() + ":" + currentUser.get();
        //设置用户信息到ThreadLocal
        currentUser.set(userId);
        //设置用户信息之后再查询一次ThreadLocal中的用户信息
        String after  = Thread.currentThread().getName() + ":" + currentUser.get();
        //汇总输出两次查询结果
        Map result = new HashMap();
        result.put("before", before);
        result.put("after", after);
        return result;
    }

    /**
     * 正确版本
     * @param userId
     * @return
     */
    @GetMapping("right")
    public Map right(@RequestParam("userId") Integer userId) {
        //设置用户信息之前先查询一次ThreadLocal中的用户信息
        String before  = Thread.currentThread().getName() + ":" + currentUser.get();
        //设置用户信息到ThreadLocal
        currentUser.set(userId);
        try {
            //设置用户信息之后再查询一次ThreadLocal中的用户信息
            String after  = Thread.currentThread().getName() + ":" + currentUser.get();
            //汇总输出两次查询结果
            Map result = new HashMap();
            result.put("before", before);
            result.put("after", after);
            return result;
        }finally {
            currentUser.remove();
        }

    }

}
