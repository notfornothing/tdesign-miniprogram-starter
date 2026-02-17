package com.rustserver.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServerListDTO {
    private Long id;
    private String name;
    private String ip;
    private Integer port;
    private String region;
    private String country;
    private String mapName;
    private Integer mapSize;
    private Integer players;
    private Integer maxPlayers;
    private Integer queuePlayers;
    private Integer ping;
    private Integer isOfficial;
    private Integer isModded;
    private BigDecimal gatherRate;
    private String status;
    private String bannerUrl;
}
