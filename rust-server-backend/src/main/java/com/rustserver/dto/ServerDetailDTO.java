package com.rustserver.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ServerDetailDTO {
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
    private Integer players;
    private Integer maxPlayers;
    private Integer queuePlayers;
    private Integer fps;
    private Integer ping;
    private Integer isOfficial;
    private Integer isModded;
    private BigDecimal gatherRate;
    private List<String> tags;
    private String website;
    private String discord;
    private String description;
    private String bannerUrl;
    private String status;
    private BigDecimal uptime;
    private LocalDateTime lastWipe;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
