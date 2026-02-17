package com.rustserver.service;

import com.rustserver.dto.PlayerDTO;
import com.rustserver.entity.PlayerInfo;

import java.util.List;

public interface PlayerService {

    /**
     * Get player by Steam ID
     */
    PlayerDTO getPlayerBySteamId(String steamId);

    /**
     * Get player history
     */
    List<PlayerDTO> getPlayerHistory(String steamId);

    /**
     * Save or update player
     */
    PlayerInfo saveOrUpdatePlayer(String steamId, String name);
}
