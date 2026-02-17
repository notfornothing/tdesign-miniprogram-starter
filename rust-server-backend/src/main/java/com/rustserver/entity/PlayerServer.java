package com.rustserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rs_player_server")
public class PlayerServer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long playerId;
    private Long serverId;
    private Integer score;
    private Long playtime;
    private LocalDateTime firstSeen;
    private LocalDateTime lastSeen;
}
