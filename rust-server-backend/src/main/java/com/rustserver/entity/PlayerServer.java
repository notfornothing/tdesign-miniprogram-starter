package com.rustserver.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rs_player_server")
public class PlayerServer {
    @TableId
    private String id;
    private String playerId;
    private String serverId;
    private String score;
    private String playtime;
    private LocalDateTime firstSeen;
    private LocalDateTime lastSeen;
}
