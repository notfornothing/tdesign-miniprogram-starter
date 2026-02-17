package com.rustserver.service.impl;

import com.rustserver.entity.ServerInfo;
import com.rustserver.mapper.ServerMapper;
import com.rustserver.service.MapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {

    private final ServerMapper serverMapper;

    @Override
    public String getMapUrl(Long serverId) {
        ServerInfo server = serverMapper.selectById(serverId);
        if (server == null) {
            return null;
        }

        // Rust maps are typically served at a specific URL pattern
        // This would depend on the server's configuration
        return String.format("https://playrust.io/map/%s:%d", server.getIp(), server.getPort());
    }

    @Override
    public Object getMapData(Long serverId) {
        ServerInfo server = serverMapper.selectById(serverId);
        if (server == null) {
            return null;
        }

        Map<String, Object> mapData = new HashMap<>();
        mapData.put("mapName", server.getMapName());
        mapData.put("mapSize", server.getMapSize());
        mapData.put("seed", server.getSeed());
        mapData.put("mapUrl", getMapUrl(serverId));

        return mapData;
    }
}
