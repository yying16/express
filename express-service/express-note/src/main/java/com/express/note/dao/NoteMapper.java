package com.express.note.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.note.domain.Note;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoteMapper extends BaseMapper<Note> {

}
