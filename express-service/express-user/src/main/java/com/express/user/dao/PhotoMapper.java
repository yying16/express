package com.express.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.user.domain.Photo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PhotoMapper extends BaseMapper<Photo> {
}
