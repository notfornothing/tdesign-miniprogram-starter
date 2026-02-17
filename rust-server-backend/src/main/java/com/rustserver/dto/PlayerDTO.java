package com.rustserver.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlayerDTO {
    private String id;
    private String steamId;
    private String name;
    private String avatarUrl;
    private String totalPlaytime;
    private LocalDateTime firstSeen;
    private LocalDateTime lastSeen;
    private String score;
    private String playtimeOnServer;
    private String serverName;
}
