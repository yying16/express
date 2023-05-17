package com.express.note.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.express.common.util.IPageUtil;
import com.express.common.util.TimeUtil;
import com.express.note.domain.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class NoteCacheService {


    @Autowired
    public StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private NoteService noteService;
    /**
     * 查询note
     * */
    public IPage getNotes(long currentPage, long pageSize, Map<String, String> condition,String account) {
        try {
            List<Object> list = redisTemplate.opsForHash().values("notes"); // 字符串数组
            List<Object> enshrineList = redisTemplate.opsForHash().values("enshrine"); // 点赞列表
            List<Object> ret = new ArrayList<>();
            String category = "",type = "",content = "";
            if(!condition.isEmpty()){// 条件搜索
                if(condition.getOrDefault("category","").length()>0){
                    category = condition.get("category");
                }
                if(condition.getOrDefault("type","").length()>0){
                    type = condition.get("type");
                }
                if(condition.getOrDefault("content","").length()>0){
                    content = condition.get("content");
                }
            }
            for (int i = 0; i < list.size(); i++) {
                JSONObject json = JSON.parseObject(String.valueOf(list.get(i))); // 每个note
                if(Boolean.parseBoolean(json.get("deleted").toString())){
                    continue;
                }
                if(category.length()>0 && !json.get("category").equals(category))
                    continue;
                if(type.length()>0 && content.length()>0){
                    if(type.equals("标题")){
                        if(!json.get("title").toString().contains(content)){ // 不包含关键字
                            continue;
                        }
                    }else if(type.equals("发布者")){
                        if(!json.get("promulgator").toString().contains(content)){ // 不包含关键字
                            continue;
                        }
                    }else if(type.equals("内容")){
                        if(!json.get("content").toString().contains(content)){ // 不包含关键字
                            continue;
                        }
                    }
                }
                for (int j = 0; j < enshrineList.size(); j++) {
                    JSONObject enshrine = JSONObject.parseObject(String.valueOf(enshrineList.get(j)));
                    if(enshrine.get("type").equals("note")&&enshrine.get("collectionId").equals(json.get("noteId"))&&enshrine.get("collector").equals(account)){ // 当前用户点了赞
                        json.put("enshrined",true);
                    }
                }
                ret.add(json.toString());
            }
            return IPageUtil.listToIPage(ret, currentPage, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 添加note
     */
    public boolean addNote(Note note) {
        try {
            String id = IdWorker.getIdStr(Note.class);
            note.setNoteId(id);
            note.setDeleted(false);
            note.setAnonymous(false);
            note.setCollected(0);
            note.setEnshrined(false);
            note.setCreateTime(TimeUtil.getCurrentTime());
            note.setUpdateTime(TimeUtil.getCurrentTime());
            if(note.getCategory()==null||note.getCategory().length()==0){
                note.setCategory("其他");
            }
            //写入redis
            redisTemplate.opsForHash().put("notes", id, JSONObject.toJSON(note).toString());
            //异步写入数据库
            kafkaTemplate.send("note","addNote" ,JSONObject.toJSON(note).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 查询note详情
     * */
    public Note getDetail(String noteId){
        String note = String.valueOf(redisTemplate.opsForHash().get("notes",noteId));
        Note ret = JSONObject.toJavaObject(JSON.parseObject(note),Note.class);
        String[] list = ret.getPhoto().split("#");
        for (int i = 0; i < list.length; i++) {
            list[i] = String.valueOf(redisTemplate.opsForHash().get("photos",list[i]));
        }
        ret.setPictures(list);
        return ret;
    }

    /**
     * 删除note
     * */
    public boolean delete(String noteId){
        try{
            String note = String.valueOf(redisTemplate.opsForHash().get("notes",noteId));
            Note ret = JSONObject.toJavaObject(JSON.parseObject(note),Note.class);
            ret.setDeleted(true);
            //异步写入数据库
            kafkaTemplate.send("note","updateNote" ,JSONObject.toJSON(ret).toString());
            //修改redis
            redisTemplate.opsForHash().put("notes",String.valueOf(ret.getNoteId()),JSONObject.toJSON(ret).toString());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public List<Note> getMyCollection(String account){
        List<Object> list = redisTemplate.opsForHash().values("enshrine");
        List<Note> ret = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = JSONObject.parseObject((String.valueOf(list.get(i))));
            if(jsonObject.get("type").equals("note")&&jsonObject.get("collector").equals(account)){
                String json = String.valueOf(redisTemplate.opsForHash().get("notes",String.valueOf(jsonObject.get("collectionId"))));
                Note note = JSONObject.parseObject(json,Note.class);
                if(!note.isDeleted())
                    ret.add(note);
            }
        }
        return ret;
    }

    public boolean refresh(){
        List<Object> list = redisTemplate.opsForHash().values("notes"); // 字符串数组
        List<Note> notes = noteService.getNotes();
        for (int i = 0; i < list.size(); i++) { // 清除已删除的
            Note note = JSONObject.toJavaObject(JSON.parseObject(String.valueOf(list.get(i))),Note.class);
            if(note.isDeleted()){
                redisTemplate.boundHashOps("notes").delete(note.getNoteId());
            }
        }
        for (int i = 0; i < notes.size(); i++) { // 插入未缓存的数据
            Note note = notes.get(i);
            if(Boolean.FALSE.equals(redisTemplate.boundHashOps("notes").hasKey(note.getNoteId()))){
                redisTemplate.opsForHash().put("notes",note.getNoteId(),JSONObject.toJSON(note).toString());
            }
        }
        return true;
    }
}