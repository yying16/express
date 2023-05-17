package com.express.comment.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.express.comment.dao.CommentDao;
import com.express.comment.domain.Comment;
import com.express.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentDao commentDao;

    @Override
    public List<Comment> getAllComment() {
        return commentDao.searchAll();
    }

    @Override
    public List<Comment> addComment(Comment comment) {
        return commentDao.insert(comment);
    }

    @Override
    public List<Comment> replayComment(String superId, Comment comment) {
        String[] ids = superId.split("#");
        StringBuilder commentId = new StringBuilder(ids[0]);
        List<Comment> List = commentDao.getChildren(ids[0]);
        List<Comment> list = List;
        for (int i = 1; i < ids.length; i++) {
            commentId.append("#").append(ids[i]);
            Comment com = null;
            for (int j = 0; j < list.size(); j++) {
                if(list.get(j).getId().equals(commentId.toString())){
                    com = list.get(j);
                    break;
                }
            }
            if(com.getChildren()==null)
                com.setChildren(new ArrayList<>());
            list = com.getChildren();
        }
        comment.setId(superId+"#"+IdWorker.getIdStr(Comment.class));
        list.add(comment);
        commentDao.updateComment(ids[0],List);
        return getAllComment();
    }
}
