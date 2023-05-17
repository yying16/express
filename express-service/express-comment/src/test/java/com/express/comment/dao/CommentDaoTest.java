package com.express.comment.dao;

import com.express.comment.domain.Comment;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.*;

@SpringBootTest
public class CommentDaoTest {

    @Autowired
    CommentDao commentDao;

    @Test
    public void insert() {
        System.out.println(commentDao.insert(new Comment()));
    }

    @Test
    public void searchAll() {
    }
}