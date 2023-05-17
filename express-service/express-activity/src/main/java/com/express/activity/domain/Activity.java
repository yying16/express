package com.express.activity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
    @TableId(type = IdType.ASSIGN_ID)
    String activityId;
    String promulgator;
    String category;
    String title;
    String content;
    String photo;
    String contact;
    int collected;
    boolean deleted;
    boolean anonymous;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String updateTime;
    @TableField(exist = false)
    String[] pictures;
    @TableField(exist = false)
    boolean enshrined = false; // 被当前用户收藏标志
}
