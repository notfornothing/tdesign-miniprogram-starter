package com.rustserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rs_player")
public class PlayerInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String steamId;
    private String name;
    private String avatarUrl;
    private Long totalPlaytime;
    private LocalDateTime firstSeen;
    private LocalDateTime lastSeen;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
