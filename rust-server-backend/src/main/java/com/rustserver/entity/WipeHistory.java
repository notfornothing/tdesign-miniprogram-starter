package com.rustserver.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rs_wipe_history")
public class WipeHistory {
    @TableId
    private String id;
    private String serverId;
    private LocalDateTime wipeTime;
    private String wipeType;
    private String playerCount;
    private LocalDateTime createdAt;
}
