package com.express.message.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enshrine {
    @TableId(type = IdType.ASSIGN_ID)
    String enshrineId;
    String type;
    String collectionId;
    String collector;
}
