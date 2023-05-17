package com.express.note.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.express.note.dao.NoteMapper;
import com.express.note.domain.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NoteService {

    @Autowired
    NoteMapper noteMapper;


    /**
     * 用于定时器刷新redis缓存
     * */
    public List<Note> getNotes(){
        QueryWrapper<Note> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", false);
        return noteMapper.selectList(wrapper);
    }

    public boolean addNote(Note note){
        try{
            noteMapper.insert(note);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateNote(Note note){
        try{
            noteMapper.updateById(note);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
