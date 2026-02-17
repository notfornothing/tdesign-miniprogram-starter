package com.rustserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rs_wipe_history")
public class WipeHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long serverId;
    private LocalDateTime wipeTime;
    private String wipeType;
    private Integer playerCount;
    private LocalDateTime createdAt;
}
