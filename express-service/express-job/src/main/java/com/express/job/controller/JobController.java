package com.express.job.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.common.vo.R;
import com.express.job.domain.Job;
import com.express.job.service.JobCacheService;
import com.express.job.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class JobController {

    @Autowired
    JobService jobService;

    @Autowired
    JobCacheService jobCacheService;

    /**
     * 查询/获取所有帖子
     * */
    @PostMapping("/getJobs/{currentPage}/{pageSize}/{account}")
    public R getJobs(@PathVariable long currentPage, @PathVariable long pageSize, @RequestBody Map<String, String> condition, @PathVariable String account) {
        IPage iPage = jobCacheService.getJobs(currentPage,pageSize,condition,account);
        if(iPage==null)
            return R.failed();
        return R.ok(iPage);
    }

    /**
     * 添加帖子（先添加到redis,再用kafka异步写入数据库）
     * */
    @PostMapping("/addJob")
    public R addJob(@RequestBody Job job){
        if(jobCacheService.addJob(job)){
            return R.ok();
        }
        return R.failed();
    }

    @GetMapping("/getDetail/{id}")
    public R getDetail(@PathVariable String id){
        Job job = jobCacheService.getDetail(id);
        if(job==null){
            return R.failed();
        }
        return R.ok(job);
    }

    @GetMapping("/delete/{jobId}")
    public R delete(@PathVariable String jobId){
        if(jobCacheService.delete(jobId)){
            return R.ok();
        }
        return R.failed();
    }

    @GetMapping("/getMyCollection/{account}")
    public R getMyCollection(@PathVariable String account){
        List<Job> list = jobCacheService.getMyCollection(account);
        if(list==null)
            return R.failed();
        return R.ok(list);
    }
}
