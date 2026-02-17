package com.rustserver.controller;

import com.rustserver.a2s.model.A2SInfo;
import com.rustserver.a2s.model.A2SPlayer;
import com.rustserver.common.PageResult;
import com.rustserver.common.Result;
import com.rustserver.dto.*;
import com.rustserver.entity.ServerInfo;
import com.rustserver.service.ServerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;

    /**
     * Get server list with pagination and filters
     */
    @GetMapping
    public Result<PageResult<ServerListDTO>> getServerList(ServerFilterDTO filter) {
        return Result.success(serverService.getServerList(filter));
    }

    /**
     * Get server detail by ID
     */
    @GetMapping("/{id}")
    public Result<ServerDetailDTO> getServerDetail(@PathVariable Long id) {
        ServerDetailDTO detail = serverService.getServerDetail(id);
        if (detail == null) {
            return Result.error(404, "Server not found");
        }
        return Result.success(detail);
    }

    /**
     * Get online players of a server
     */
    @GetMapping("/{id}/players")
    public Result<List<A2SPlayer>> getServerPlayers(@PathVariable Long id) {
        return Result.success(serverService.getOnlinePlayers(id));
    }

    /**
     * Get server statistics
     */
    @GetMapping("/{id}/stats")
    public Result<ServerDetailDTO> getServerStats(@PathVariable Long id) {
        ServerDetailDTO detail = serverService.getServerDetail(id);
        if (detail == null) {
            return Result.error(404, "Server not found");
        }
        return Result.success(detail);
    }

    /**
     * Add a new server
     */
    @PostMapping
    public Result<ServerInfo> addServer(@Valid @RequestBody ServerQueryDTO dto) {
        try {
            ServerInfo server = serverService.addServer(dto);
            return Result.success(server);
        } catch (Exception e) {
            return Result.error(500, "Failed to add server: " + e.getMessage());
        }
    }

    /**
     * Delete a server
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteServer(@PathVariable Long id) {
        serverService.deleteServer(id);
        return Result.success();
    }

    /**
     * Real-time query server using A2S protocol
     */
    @PostMapping("/query")
    public Result<A2SInfo> queryServer(@Valid @RequestBody ServerQueryDTO dto) {
        try {
            A2SInfo info = serverService.queryServer(dto.getIp(), dto.getPort());
            return Result.success(info);
        } catch (Exception e) {
            return Result.error(503, "Failed to query server: " + e.getMessage());
        }
    }

    /**
     * Query online players directly
     */
    @PostMapping("/query/players")
    public Result<List<A2SPlayer>> queryPlayers(@Valid @RequestBody ServerQueryDTO dto) {
        try {
            List<A2SPlayer> players = serverService.queryPlayers(dto.getIp(), dto.getPort());
            return Result.success(players);
        } catch (Exception e) {
            return Result.error(503, "Failed to query players: " + e.getMessage());
        }
    }

    /**
     * Refresh server status
     */
    @PostMapping("/{id}/refresh")
    public Result<Void> refreshServer(@PathVariable Long id) {
        serverService.refreshServerStatus(id);
        return Result.success();
    }

    /**
     * Get filter options
     */
    @GetMapping("/filter-options")
    public Result<FilterOptionsDTO> getFilterOptions() {
        return Result.success(serverService.getFilterOptions());
    }
}
