package com.express.job.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.express.job.dao.JobMapper;
import com.express.job.domain.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class JobService {

    @Autowired
    JobMapper jobMapper;

    public List<Job> getJobs(){
        QueryWrapper<Job> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", false);
        return jobMapper.selectList(wrapper);
    }

    public boolean addJob(Job job){
        try{
            jobMapper.insert(job);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateJob(Job job){
        try{
            jobMapper.updateById(job);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
