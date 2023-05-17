package com.express.note.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_note")
public class Note implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    String noteId;
    String promulgator;
    String category;
    String title;
    String contact;
    String content;
    String photo;
    boolean deleted;
    boolean anonymous;
    int collected;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String updateTime;
    @TableField(exist = false)
    String[] pictures;
    @TableField(exist = false)
    boolean enshrined = false; // 被当前用户收藏标志
}