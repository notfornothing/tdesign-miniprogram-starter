-- Rust Server Query System Database Schema
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS rust_server DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE rust_server;

-- 服务器信息表
CREATE TABLE IF NOT EXISTS rs_server (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL COMMENT '服务器名称',
    ip VARCHAR(50) NOT NULL COMMENT 'IP地址',
    port INT NOT NULL DEFAULT 28015 COMMENT '端口',
    steam_id VARCHAR(50) COMMENT 'Steam ID',
    region VARCHAR(20) COMMENT '地区(cn/us/eu/asia/au)',
    country VARCHAR(10) COMMENT '国家代码',
    map_name VARCHAR(100) COMMENT '地图名称',
    map_size INT COMMENT '地图大小',
    seed INT COMMENT '地图种子',
    max_players INT COMMENT '最大玩家数',
    is_official TINYINT DEFAULT 0 COMMENT '是否官方服',
    is_modded TINYINT DEFAULT 0 COMMENT '是否模组服',
    gather_rate DECIMAL(5,2) DEFAULT 1.0 COMMENT '采集倍率',
    tags VARCHAR(500) COMMENT '标签JSON数组',
    website VARCHAR(255) COMMENT '网站',
    discord VARCHAR(255) COMMENT 'Discord链接',
    description TEXT COMMENT '描述',
    banner_url VARCHAR(255) COMMENT '横幅图片URL',
    status VARCHAR(20) DEFAULT 'unknown' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_ip_port (ip, port),
    INDEX idx_region (region),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务器信息表';

-- 服务器实时状态表
CREATE TABLE IF NOT EXISTS rs_server_status (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    server_id BIGINT NOT NULL,
    players INT DEFAULT 0 COMMENT '当前玩家数',
    max_players INT DEFAULT 0,
    queue_players INT DEFAULT 0 COMMENT '排队人数',
    fps INT COMMENT '服务器FPS',
    ping INT COMMENT '延迟',
    uptime DECIMAL(5,2) COMMENT '在线率',
    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_server_time (server_id, recorded_at),
    FOREIGN KEY (server_id) REFERENCES rs_server(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务器实时状态表';

-- Wipe记录表
CREATE TABLE IF NOT EXISTS rs_wipe_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    server_id BIGINT NOT NULL,
    wipe_time DATETIME NOT NULL COMMENT 'Wipe时间',
    wipe_type VARCHAR(20) DEFAULT 'map' COMMENT 'Wipe类型',
    player_count INT COMMENT 'Wipe时玩家数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_server_time (server_id, wipe_time),
    FOREIGN KEY (server_id) REFERENCES rs_server(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Wipe记录表';

-- 玩家记录表
CREATE TABLE IF NOT EXISTS rs_player (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    steam_id VARCHAR(50) NOT NULL UNIQUE COMMENT 'Steam ID',
    name VARCHAR(100) COMMENT '玩家名称',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    total_playtime BIGINT DEFAULT 0 COMMENT '总游戏时长(秒)',
    first_seen DATETIME COMMENT '首次发现时间',
    last_seen DATETIME COMMENT '最后在线时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_steam_id (steam_id),
    INDEX idx_last_seen (last_seen)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家记录表';

-- 玩家服务器关联表
CREATE TABLE IF NOT EXISTS rs_player_server (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    player_id BIGINT NOT NULL,
    server_id BIGINT NOT NULL,
    score INT DEFAULT 0 COMMENT '分数',
    playtime BIGINT DEFAULT 0 COMMENT '在该服游戏时长(秒)',
    first_seen DATETIME COMMENT '首次进入时间',
    last_seen DATETIME COMMENT '最后在线时间',
    UNIQUE KEY uk_player_server (player_id, server_id),
    INDEX idx_server (server_id),
    INDEX idx_player (player_id),
    FOREIGN KEY (player_id) REFERENCES rs_player(id) ON DELETE CASCADE,
    FOREIGN KEY (server_id) REFERENCES rs_server(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家服务器关联表';

-- 搜索历史表
CREATE TABLE IF NOT EXISTS rs_search_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    keyword VARCHAR(100) NOT NULL,
    search_count INT DEFAULT 1,
    last_searched DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_keyword (keyword),
    INDEX idx_search_count (search_count DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索历史表';

-- 插入测试数据
INSERT INTO rs_server (name, ip, port, region, map_name, max_players, is_official, is_modded, gather_rate, status) VALUES
('Rusticated.com - Main', '47.115.230.101', 28015, 'asia', 'Procedural Map', 300, 0, 0, 1.0, 'online'),
('Facepunch Singapore', '103.62.49.103', 28015, 'asia', 'Procedural Map', 400, 1, 0, 1.0, 'online'),
('Rustoria.co - 3x Solo/Duo/Trio', '45.88.228.91', 28015, 'eu', 'Procedural Map', 250, 0, 1, 3.0, 'online'),
('GGEZ.RIP - 5x MAX 4', '185.217.59.43', 28015, 'eu', 'Procedural Map', 200, 0, 1, 5.0, 'online');

-- 插入状态测试数据
INSERT INTO rs_server_status (server_id, players, max_players, queue_players, ping, recorded_at) VALUES
(1, 187, 300, 0, 35, NOW()),
(2, 245, 400, 12, 28, NOW()),
(3, 156, 250, 0, 120, NOW()),
(4, 98, 200, 0, 145, NOW());
