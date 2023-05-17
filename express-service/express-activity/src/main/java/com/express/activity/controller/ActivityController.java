package com.express.activity.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.common.vo.R;
import com.express.activity.domain.Activity;
import com.express.activity.service.ActivityCacheService;
import com.express.activity.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ActivityController {

    @Autowired
    ActivityService activityService;

    @Autowired
    ActivityCacheService activityCacheService;

    /**
     * 查询/获取所有帖子
     * */
    @PostMapping("/getActivitys/{currentPage}/{pageSize}/{account}")
    public R getActivitys(@PathVariable long currentPage, @PathVariable long pageSize, @RequestBody Map<String, String> condition, @PathVariable String account) {
        IPage iPage = activityCacheService.getActivitys(currentPage,pageSize,condition,account);
        if(iPage==null)
            return R.failed();
        return R.ok(iPage);
    }

    /**
     * 添加帖子（先添加到redis,再用kafka异步写入数据库）
     * */
    @PostMapping("/addActivity")
    public R addActivity(@RequestBody Activity activity){
        if(activityCacheService.addActivity(activity)){
            return R.ok();
        }
        return R.failed();
    }

    @GetMapping("/getDetail/{id}")
    public R getDetail(@PathVariable String id){
        Activity activity = activityCacheService.getDetail(id);
        if(activity==null){
            return R.failed();
        }
        return R.ok(activity);
    }

    @GetMapping("/delete/{activityId}")
    public R delete(@PathVariable String activityId){
        if(activityCacheService.delete(activityId)){
            return R.ok();
        }
        return R.failed();
    }

    @GetMapping("/getMyCollection/{account}")
    public R getMyCollection(@PathVariable String account){
        List<Activity> list = activityCacheService.getMyCollection(account);
        if(list==null)
            return R.failed();
        return R.ok(list);
    }
}
