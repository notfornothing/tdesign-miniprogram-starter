package com.rustserver.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rustserver.a2s.A2SClient;
import com.rustserver.a2s.model.A2SInfo;
import com.rustserver.a2s.model.A2SPlayer;
import com.rustserver.common.ErrorCode;
import com.rustserver.common.PageResult;
import com.rustserver.dto.*;
import com.rustserver.entity.ServerInfo;
import com.rustserver.entity.ServerStatus;
import com.rustserver.entity.WipeHistory;
import com.rustserver.mapper.ServerMapper;
import com.rustserver.mapper.ServerStatusMapper;
import com.rustserver.mapper.WipeHistoryMapper;
import com.rustserver.service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService {

    private final ServerMapper serverMapper;
    private final ServerStatusMapper serverStatusMapper;
    private final WipeHistoryMapper wipeHistoryMapper;
    private final A2SClient a2SClient;

    @Override
    public PageResult<ServerListDTO> getServerList(ServerFilterDTO filter) {
        Page<ServerInfo> page = new Page<>(filter.getPageNum(), filter.getPageSize());

        LambdaQueryWrapper<ServerInfo> wrapper = new LambdaQueryWrapper<>();

        // Keyword search
        if (StringUtils.hasText(filter.getKeyword())) {
            wrapper.and(w -> w.like(ServerInfo::getName, filter.getKeyword())
                    .or().like(ServerInfo::getIp, filter.getKeyword()));
        }

        // Region filter
        if (StringUtils.hasText(filter.getRegion())) {
            wrapper.eq(ServerInfo::getRegion, filter.getRegion());
        }

        // Official filter
        if (filter.getIsOfficial() != null) {
            wrapper.eq(ServerInfo::getIsOfficial, filter.getIsOfficial());
        }

        // Modded filter
        if (filter.getIsModded() != null) {
            wrapper.eq(ServerInfo::getIsModded, filter.getIsModded());
        }

        // Sorting
        String sortField = StringUtils.hasText(filter.getSortField()) ? filter.getSortField() : "id";
        boolean isAsc = "asc".equalsIgnoreCase(filter.getSortOrder());

        switch (sortField) {
            case "players":
                // Would need a join for real player count sorting
                wrapper.orderBy(true, isAsc, ServerInfo::getId);
                break;
            case "name":
                wrapper.orderBy(true, isAsc, ServerInfo::getName);
                break;
            default:
                wrapper.orderBy(true, isAsc, ServerInfo::getId);
        }

        Page<ServerInfo> result = serverMapper.selectPage(page, wrapper);

        List<ServerListDTO> dtoList = result.getRecords().stream()
                .map(this::convertToListDTO)
                .collect(Collectors.toList());

        return PageResult.of(dtoList, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    @Cacheable(value = "serverDetail", key = "#id")
    public ServerDetailDTO getServerDetail(Long id) {
        ServerInfo server = serverMapper.selectById(id);
        if (server == null) {
            return null;
        }

        ServerDetailDTO dto = convertToDetailDTO(server);

        // Get latest status
        LambdaQueryWrapper<ServerStatus> statusWrapper = new LambdaQueryWrapper<>();
        statusWrapper.eq(ServerStatus::getServerId, id)
                .orderByDesc(ServerStatus::getRecordedAt)
                .last("LIMIT 1");
        ServerStatus status = serverStatusMapper.selectOne(statusWrapper);

        if (status != null) {
            dto.setPlayers(status.getPlayers());
            dto.setMaxPlayers(status.getMaxPlayers());
            dto.setQueuePlayers(status.getQueuePlayers());
            dto.setFps(status.getFps());
            dto.setPing(status.getPing());
            dto.setUptime(status.getUptime());
        }

        // Get last wipe
        LambdaQueryWrapper<WipeHistory> wipeWrapper = new LambdaQueryWrapper<>();
        wipeWrapper.eq(WipeHistory::getServerId, id)
                .orderByDesc(WipeHistory::getWipeTime)
                .last("LIMIT 1");
        WipeHistory lastWipe = wipeHistoryMapper.selectOne(wipeWrapper);
        if (lastWipe != null) {
            dto.setLastWipe(lastWipe.getWipeTime());
        }

        return dto;
    }

    @Override
    public A2SInfo queryServer(String ip, Integer port) {
        try {
            return a2SClient.queryInfo(ip, port);
        } catch (IOException e) {
            log.error("Failed to query server {}:{}", ip, port, e);
            throw new RuntimeException(ErrorCode.QUERY_FAILED.getMessage(), e);
        }
    }

    @Override
    public List<A2SPlayer> getOnlinePlayers(Long serverId) {
        ServerInfo server = serverMapper.selectById(serverId);
        if (server == null) {
            return new ArrayList<>();
        }
        return queryPlayers(server.getIp(), server.getPort());
    }

    @Override
    public List<A2SPlayer> queryPlayers(String ip, Integer port) {
        try {
            return a2SClient.queryPlayers(ip, port);
        } catch (IOException e) {
            log.error("Failed to query players for {}:{}", ip, port, e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "serverList", allEntries = true)
    public ServerInfo addServer(ServerQueryDTO dto) {
        // Check if already exists
        LambdaQueryWrapper<ServerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServerInfo::getIp, dto.getIp())
                .eq(ServerInfo::getPort, dto.getPort());
        ServerInfo existing = serverMapper.selectOne(wrapper);
        if (existing != null) {
            return existing;
        }

        // Query server info via A2S
        A2SInfo a2SInfo = queryServer(dto.getIp(), dto.getPort());

        ServerInfo server = new ServerInfo();
        server.setName(a2SInfo.getName());
        server.setIp(dto.getIp());
        server.setPort(dto.getPort());
        server.setMapName(a2SInfo.getMap());
        server.setMaxPlayers(a2SInfo.getMaxPlayers());
        server.setStatus("online");
        server.setIsOfficial(0);
        server.setIsModded(0);
        server.setGatherRate(BigDecimal.ONE);

        // Parse keywords for extra info
        if (StringUtils.hasText(a2SInfo.getKeywords())) {
            server.setTags(a2SInfo.getKeywords());
            parseKeywords(server, a2SInfo.getKeywords());
        }

        serverMapper.insert(server);

        // Record initial status
        recordServerStatus(server.getId(), a2SInfo);

        return server;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"serverList", "serverDetail"}, allEntries = true)
    public void deleteServer(Long id) {
        serverMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void refreshServerStatus(Long serverId) {
        ServerInfo server = serverMapper.selectById(serverId);
        if (server == null) {
            return;
        }

        try {
            A2SInfo info = a2SClient.queryInfo(server.getIp(), server.getPort());
            server.setName(info.getName());
            server.setMapName(info.getMap());
            server.setMaxPlayers(info.getMaxPlayers());
            server.setStatus("online");
            serverMapper.updateById(server);

            recordServerStatus(serverId, info);
        } catch (IOException e) {
            log.error("Failed to refresh server {}: {}", serverId, e.getMessage());
            server.setStatus("offline");
            serverMapper.updateById(server);
        }
    }

    @Override
    public FilterOptionsDTO getFilterOptions() {
        FilterOptionsDTO dto = new FilterOptionsDTO();

        dto.setRegions(Arrays.asList("cn", "us", "eu", "asia", "au", "sa"));

        List<FilterOptionsDTO.GatherRateOption> gatherRates = new ArrayList<>();
        FilterOptionsDTO.GatherRateOption low = new FilterOptionsDTO.GatherRateOption();
        low.setLabel("1x-2x");
        low.setMin(1.0);
        low.setMax(2.0);
        gatherRates.add(low);

        FilterOptionsDTO.GatherRateOption medium = new FilterOptionsDTO.GatherRateOption();
        medium.setLabel("2x-5x");
        medium.setMin(2.0);
        medium.setMax(5.0);
        gatherRates.add(medium);

        FilterOptionsDTO.GatherRateOption high = new FilterOptionsDTO.GatherRateOption();
        high.setLabel("5x+");
        high.setMin(5.0);
        high.setMax(null);
        gatherRates.add(high);

        dto.setGatherRates(gatherRates);
        dto.setTotalServers(Math.toIntExact(serverMapper.selectCount(null)));

        return dto;
    }

    private void recordServerStatus(Long serverId, A2SInfo info) {
        ServerStatus status = new ServerStatus();
        status.setServerId(serverId);
        status.setPlayers(info.getPlayers());
        status.setMaxPlayers(info.getMaxPlayers());
        status.setQueuePlayers(0);
        status.setPing(info.getPing().intValue());
        status.setRecordedAt(LocalDateTime.now());
        serverStatusMapper.insert(status);
    }

    private void parseKeywords(ServerInfo server, String keywords) {
        String[] parts = keywords.split(",");
        for (String part : parts) {
            part = part.trim();
            if (part.startsWith("gather_")) {
                try {
                    String rateStr = part.replace("gather_", "");
                    BigDecimal rate = new BigDecimal(rateStr);
                    server.setGatherRate(rate);
                } catch (NumberFormatException ignored) {
                }
            } else if (part.equals("official")) {
                server.setIsOfficial(1);
            } else if (part.equals("modded")) {
                server.setIsModded(1);
            } else if (part.startsWith("region_")) {
                server.setRegion(part.replace("region_", ""));
            }
        }
    }

    private ServerListDTO convertToListDTO(ServerInfo server) {
        ServerListDTO dto = new ServerListDTO();
        dto.setId(server.getId());
        dto.setName(server.getName());
        dto.setIp(server.getIp());
        dto.setPort(server.getPort());
        dto.setRegion(server.getRegion());
        dto.setCountry(server.getCountry());
        dto.setMapName(server.getMapName());
        dto.setMapSize(server.getMapSize());
        dto.setIsOfficial(server.getIsOfficial());
        dto.setIsModded(server.getIsModded());
        dto.setGatherRate(server.getGatherRate());
        dto.setStatus(server.getStatus());
        dto.setBannerUrl(server.getBannerUrl());
        return dto;
    }

    private ServerDetailDTO convertToDetailDTO(ServerInfo server) {
        ServerDetailDTO dto = new ServerDetailDTO();
        dto.setId(server.getId());
        dto.setName(server.getName());
        dto.setIp(server.getIp());
        dto.setPort(server.getPort());
        dto.setSteamId(server.getSteamId());
        dto.setRegion(server.getRegion());
        dto.setCountry(server.getCountry());
        dto.setMapName(server.getMapName());
        dto.setMapSize(server.getMapSize());
        dto.setSeed(server.getSeed());
        dto.setIsOfficial(server.getIsOfficial());
        dto.setIsModded(server.getIsModded());
        dto.setGatherRate(server.getGatherRate());
        dto.setWebsite(server.getWebsite());
        dto.setDiscord(server.getDiscord());
        dto.setDescription(server.getDescription());
        dto.setBannerUrl(server.getBannerUrl());
        dto.setStatus(server.getStatus());
        dto.setCreatedAt(server.getCreatedAt());
        dto.setUpdatedAt(server.getUpdatedAt());

        if (StringUtils.hasText(server.getTags())) {
            dto.setTags(Arrays.asList(server.getTags().split(",")));
        } else {
            dto.setTags(new ArrayList<>());
        }

        return dto;
    }
}
