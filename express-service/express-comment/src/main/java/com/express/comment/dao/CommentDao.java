package com.express.comment.dao;


import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.express.comment.domain.Comment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

@Repository
public class CommentDao {
    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 插入comment
     */
    public List<Comment> insert(Comment comment) {
        comment.setId(IdWorker.getIdStr(Comment.class));
        mongoTemplate.save(comment, "comment");
        return searchAll();
    }

    public List<Comment> searchAll() {
        List<Comment> list = mongoTemplate.find(new Query(new Criteria()), Comment.class, "comment");
        list.sort((c1, c2) -> c2.getId().compareTo(c1.getId()));
        return list;
    }

    public List<Comment> getChildren(String _id){
        Comment comment = mongoTemplate.findById(_id,Comment.class,"comment");
        return comment.getChildren();
    }

    public boolean updateComment(String _id, List<Comment> list) {
        Query query = Query.query(Criteria.where("_id").is(_id));
        Update update = new Update();
        update.set("children", list);
        mongoTemplate.updateFirst(query, update, "comment");
        return true;
    }
}
