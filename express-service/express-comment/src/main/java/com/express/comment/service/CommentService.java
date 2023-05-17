package com.express.comment.service;

import com.express.comment.dao.CommentDao;
import com.express.comment.domain.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {

    /**
     *获取所有评论
     **/
    public List<Comment> getAllComment();


    /**
     * 直接添加新的话题
     * */
    public List<Comment> addComment(Comment comment);

    /**
     * 回复话题
     * */
    public List<Comment> replayComment(String superId,Comment comment);
}
