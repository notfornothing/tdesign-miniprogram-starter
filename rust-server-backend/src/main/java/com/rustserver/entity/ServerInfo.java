package com.rustserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("rs_server")
public class ServerInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String ip;
    private Integer port;
    private String steamId;
    private String region;
    private String country;
    private String mapName;
    private Integer mapSize;
    private Integer seed;
    private Integer maxPlayers;
    private Integer isOfficial;
    private Integer isModded;
    private BigDecimal gatherRate;
    private String tags;
    private String website;
    private String discord;
    private String description;
    private String bannerUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
