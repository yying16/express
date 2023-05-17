package com.express.activity.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class Jobs {

    @Autowired
    ActivityCacheService activityCacheService;

    //每半个小时刷新一次
    @Scheduled(fixedDelay = 1000*60*30)
    public void fixedDelayJob() {
        log.info("redis更新数据");
        boolean flag = activityCacheService.refresh();
        log.info("刷新结果:" + flag);
    }
}