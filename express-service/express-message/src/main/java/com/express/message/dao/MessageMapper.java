package com.express.message.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.message.domain.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
