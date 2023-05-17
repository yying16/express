package com.express.message.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @TableId(type = IdType.ASSIGN_ID)
    String messageId;
    String sender;
    String receiver;
    String content;
    String photo;
    String sendTime;
    String receiveTime;
    String status;
    boolean deleted;
    @TableField(exist = false)
    String[] pictures;
}
