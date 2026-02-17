package com.rustserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rustserver.dto.PlayerDTO;
import com.rustserver.entity.PlayerInfo;
import com.rustserver.entity.PlayerServer;
import com.rustserver.entity.ServerInfo;
import com.rustserver.mapper.PlayerMapper;
import com.rustserver.mapper.PlayerServerMapper;
import com.rustserver.mapper.ServerMapper;
import com.rustserver.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerMapper playerMapper;
    private final PlayerServerMapper playerServerMapper;
    private final ServerMapper serverMapper;

    @Override
    @Cacheable(value = "player", key = "#steamId")
    public PlayerDTO getPlayerBySteamId(String steamId) {
        LambdaQueryWrapper<PlayerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlayerInfo::getSteamId, steamId);
        PlayerInfo player = playerMapper.selectOne(wrapper);

        if (player == null) {
            return null;
        }

        return convertToDTO(player);
    }

    @Override
    public List<PlayerDTO> getPlayerHistory(String steamId) {
        LambdaQueryWrapper<PlayerInfo> playerWrapper = new LambdaQueryWrapper<>();
        playerWrapper.eq(PlayerInfo::getSteamId, steamId);
        PlayerInfo player = playerMapper.selectOne(playerWrapper);

        if (player == null) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<PlayerServer> psWrapper = new LambdaQueryWrapper<>();
        psWrapper.eq(PlayerServer::getPlayerId, player.getId())
                .orderByDesc(PlayerServer::getLastSeen);
        List<PlayerServer> playerServers = playerServerMapper.selectList(psWrapper);

        return playerServers.stream()
                .map(ps -> {
                    PlayerDTO dto = new PlayerDTO();
                    dto.setId(player.getId());
                    dto.setSteamId(player.getSteamId());
                    dto.setName(player.getName());
                    dto.setAvatarUrl(player.getAvatarUrl());
                    dto.setScore(ps.getScore());
                    dto.setPlaytimeOnServer(ps.getPlaytime());
                    dto.setFirstSeen(ps.getFirstSeen());
                    dto.setLastSeen(ps.getLastSeen());

                    ServerInfo server = serverMapper.selectById(ps.getServerId());
                    if (server != null) {
                        dto.setServerName(server.getName());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlayerInfo saveOrUpdatePlayer(String steamId, String name) {
        LambdaQueryWrapper<PlayerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlayerInfo::getSteamId, steamId);
        PlayerInfo player = playerMapper.selectOne(wrapper);

        LocalDateTime now = LocalDateTime.now();

        if (player == null) {
            player = new PlayerInfo();
            player.setSteamId(steamId);
            player.setName(name);
            player.setFirstSeen(now);
            player.setLastSeen(now);
            player.setTotalPlaytime(0L);
            playerMapper.insert(player);
        } else {
            player.setName(name);
            player.setLastSeen(now);
            playerMapper.updateById(player);
        }

        return player;
    }

    private PlayerDTO convertToDTO(PlayerInfo player) {
        PlayerDTO dto = new PlayerDTO();
        dto.setId(player.getId());
        dto.setSteamId(player.getSteamId());
        dto.setName(player.getName());
        dto.setAvatarUrl(player.getAvatarUrl());
        dto.setTotalPlaytime(player.getTotalPlaytime());
        dto.setFirstSeen(player.getFirstSeen());
        dto.setLastSeen(player.getLastSeen());
        return dto;
    }
}
