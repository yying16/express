package com.express.note;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.express.common.util.IPageUtil;
import com.express.common.util.TimeUtil;
import com.express.note.domain.Note;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;

@SpringBootTest
public class NoteApplicationTest {

    @Autowired
    public StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() {
//        Map<String, String> map = new HashMap<>();
//        map.put("1", JSONObject.toJSONString(new Note()));
//        map.put("2", JSONObject.toJSONString(new Note()));
//        map.put("3", JSONObject.toJSONString(new Note()));
//        List<Note> list = new ArrayList<>();
//        list.add(new Note(1,"1","1","1","1","1",false,false, TimeUtil.getCurrentTime(),TimeUtil.getCurrentTime()));
//        redisTemplate.opsForHash().put("test","1",JSONObject.toJSON(IPageUtil.listToIPage(list,1,5)).toString());
    }

    @Test
    void redisTest(){
        System.out.println(redisTemplate.opsForValue().get("noteIndex"));
    }

}
