package com.express.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_photo")
public class Photo {
    @TableId(type = IdType.ASSIGN_ID)
    private String photoId; // 图片Id
    private String photoName; // 图片名称
    private String photoSource; // 图片源（错题id_subject/answer/respond/summary)
    private String photoType; // 图片类型（后缀）
}
