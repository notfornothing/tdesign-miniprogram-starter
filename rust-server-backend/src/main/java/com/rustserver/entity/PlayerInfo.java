package com.rustserver.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rs_player")
public class PlayerInfo {
    @TableId
    private String id;
    private String steamId;
    private String name;
    private String avatarUrl;
    private String totalPlaytime;
    private LocalDateTime firstSeen;
    private LocalDateTime lastSeen;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
