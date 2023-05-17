package com.express.activity.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.express.activity.dao.ActivityMapper;
import com.express.activity.dao.ActivityMapper;
import com.express.activity.domain.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ActivityService {

    @Autowired
    ActivityMapper activityMapper;

    public List<Activity> getActivitys(){
        QueryWrapper<Activity> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", false);
        return activityMapper.selectList(wrapper);
    }

    public boolean addActivity(Activity activity){
        try{
            activityMapper.insert(activity);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateActivity(Activity activity){
        try{
            activityMapper.updateById(activity);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
