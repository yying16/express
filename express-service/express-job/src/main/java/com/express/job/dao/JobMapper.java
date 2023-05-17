package com.express.job.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.job.domain.Job;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobMapper extends BaseMapper<Job> {

}
