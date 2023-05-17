package com.express.activity.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.activity.domain.Activity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

}
