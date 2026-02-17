package com.rustserver.controller;

import com.rustserver.common.Result;
import com.rustserver.dto.PlayerDTO;
import com.rustserver.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    /**
     * Get player by Steam ID
     */
    @GetMapping("/{steamId}")
    public Result<PlayerDTO> getPlayer(@PathVariable String steamId) {
        PlayerDTO player = playerService.getPlayerBySteamId(steamId);
        if (player == null) {
            return Result.error(404, "Player not found");
        }
        return Result.success(player);
    }

    /**
     * Get player history (servers played on)
     */
    @GetMapping("/{steamId}/history")
    public Result<List<PlayerDTO>> getPlayerHistory(@PathVariable String steamId) {
        return Result.success(playerService.getPlayerHistory(steamId));
    }
}
