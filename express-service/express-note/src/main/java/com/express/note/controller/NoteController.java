package com.express.note.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.common.vo.R;
import com.express.note.domain.Note;
import com.express.note.service.NoteCacheService;
import com.express.note.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class NoteController {

    @Autowired
    NoteService noteService;

    @Autowired
    NoteCacheService noteCacheService;

    /**
     * 查询/获取所有帖子
     * */
    @PostMapping("/getNotes/{currentPage}/{pageSize}/{account}")
    public R getNotes(@PathVariable long currentPage, @PathVariable long pageSize, @RequestBody Map<String, String> condition, @PathVariable String account) {
        IPage iPage = noteCacheService.getNotes(currentPage,pageSize,condition,account);
        if(iPage==null)
            return R.failed();
        return R.ok(iPage);
    }

    /**
     * 添加帖子（先添加到redis,再用kafka异步写入数据库）
     * */
    @PostMapping("/addNote")
    public R addNote(@RequestBody Note note){
        if(noteCacheService.addNote(note)){
            return R.ok();
        }
        return R.failed();
    }

    @GetMapping("/getDetail/{id}")
    public R getDetail(@PathVariable String id){
        Note note = noteCacheService.getDetail(id);
        if(note==null){
            return R.failed();
        }
        return R.ok(note);
    }

    @GetMapping("/delete/{noteId}")
    public R delete(@PathVariable String noteId){
        if(noteCacheService.delete(noteId)){
            return R.ok();
        }
        return R.failed();
    }

    @GetMapping("/getMyCollection/{account}")
    public R getMyCollection(@PathVariable String account){
        List<Note> list = noteCacheService.getMyCollection(account);
        if(list==null)
            return R.failed();
        return R.ok(list);
    }
}
