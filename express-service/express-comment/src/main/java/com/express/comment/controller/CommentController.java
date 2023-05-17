package com.express.comment.controller;

import com.express.comment.dao.CommentDao;
import com.express.comment.domain.Comment;
import com.express.comment.service.impl.CommentServiceImpl;
import com.express.common.vo.R;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class CommentController {

    @Autowired
    CommentDao commentDao;

    @Autowired
    CommentServiceImpl commentService;


    @GetMapping("/getAllComment")
    public R getAllComment(){
        List<Comment> list = commentService.getAllComment();
        return R.ok(list);
    }

    @PostMapping("/addComment")
    public R addComment(@RequestBody Map map){
        Comment comment = new Comment();
        comment.setLabel(String.valueOf(map.get("comment")));
        comment.setChildren(new ArrayList<>());
        List<Comment> list = commentService.addComment(comment);
        return R.ok(list);
    }

    @PostMapping("/replayComment")
    public R replayComment(@Param("superId") String superId, @RequestBody Map map){
        Comment comment = new Comment();
        comment.setLabel(String.valueOf(map.get("comment")));
        comment.setChildren(new ArrayList<>());
        List<Comment> list = commentService.replayComment(superId,comment);
        return R.ok(list);
    }
}
