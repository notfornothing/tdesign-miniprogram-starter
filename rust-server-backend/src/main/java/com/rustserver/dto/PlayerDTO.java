package com.rustserver.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlayerDTO {
    private Long id;
    private String steamId;
    private String name;
    private String avatarUrl;
    private Long totalPlaytime;
    private LocalDateTime firstSeen;
    private LocalDateTime lastSeen;
    private Integer score;
    private Long playtimeOnServer;
    private String serverName;
}
