-- =====================================================
-- Rust Server Query System Database Schema
-- MySQL 8.0+
-- 字段尽量使用字符串类型，便于前后端交互
-- =====================================================

CREATE DATABASE IF NOT EXISTS rust_server DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE rust_server;

-- -----------------------------------------------------
-- 服务器信息表
-- -----------------------------------------------------
DROP TABLE IF EXISTS rs_server_status;
DROP TABLE IF EXISTS rs_wipe_history;
DROP TABLE IF EXISTS rs_player_server;
DROP TABLE IF EXISTS rs_player;
DROP TABLE IF EXISTS rs_search_history;
DROP TABLE IF EXISTS rs_server;

CREATE TABLE rs_server (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(255) NOT NULL COMMENT '服务器名称',
    ip VARCHAR(50) NOT NULL COMMENT 'IP地址',
    port VARCHAR(10) NOT NULL DEFAULT '28015' COMMENT '端口',
    steam_id VARCHAR(50) COMMENT 'Steam ID',
    region VARCHAR(20) COMMENT '地区(cn/us/eu/asia/au)',
    country VARCHAR(10) COMMENT '国家代码',
    map_name VARCHAR(100) COMMENT '地图名称',
    map_size VARCHAR(20) COMMENT '地图大小',
    seed VARCHAR(20) COMMENT '地图种子',
    max_players VARCHAR(10) COMMENT '最大玩家数',
    is_official VARCHAR(5) DEFAULT '0' COMMENT '是否官方服(0/1)',
    is_modded VARCHAR(5) DEFAULT '0' COMMENT '是否模组服(0/1)',
    gather_rate VARCHAR(20) DEFAULT '1' COMMENT '采集倍率',
    tags VARCHAR(500) COMMENT '标签',
    website VARCHAR(255) COMMENT '网站',
    discord VARCHAR(255) COMMENT 'Discord链接',
    description TEXT COMMENT '描述',
    banner_url VARCHAR(255) COMMENT '横幅图片URL',
    status VARCHAR(20) DEFAULT 'unknown' COMMENT '状态(online/offline/unknown)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_ip_port (ip, port),
    INDEX idx_region (region),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务器信息表';

-- -----------------------------------------------------
-- 服务器实时状态表
-- -----------------------------------------------------
CREATE TABLE rs_server_status (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    server_id VARCHAR(32) NOT NULL COMMENT '服务器ID',
    players VARCHAR(10) DEFAULT '0' COMMENT '当前玩家数',
    max_players VARCHAR(10) DEFAULT '0' COMMENT '最大玩家数',
    queue_players VARCHAR(10) DEFAULT '0' COMMENT '排队人数',
    fps VARCHAR(10) COMMENT '服务器FPS',
    ping VARCHAR(10) COMMENT '延迟(ms)',
    uptime VARCHAR(20) COMMENT '在线率(%)',
    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    INDEX idx_server_time (server_id, recorded_at),
    FOREIGN KEY (server_id) REFERENCES rs_server(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务器实时状态表';

-- -----------------------------------------------------
-- Wipe记录表
-- -----------------------------------------------------
CREATE TABLE rs_wipe_history (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    server_id VARCHAR(32) NOT NULL COMMENT '服务器ID',
    wipe_time DATETIME NOT NULL COMMENT 'Wipe时间',
    wipe_type VARCHAR(20) DEFAULT 'map' COMMENT 'Wipe类型(map/blueprint/full)',
    player_count VARCHAR(10) COMMENT 'Wipe时玩家数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_server_time (server_id, wipe_time),
    FOREIGN KEY (server_id) REFERENCES rs_server(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Wipe记录表';

-- -----------------------------------------------------
-- 玩家记录表
-- -----------------------------------------------------
CREATE TABLE rs_player (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    steam_id VARCHAR(50) NOT NULL UNIQUE COMMENT 'Steam ID',
    name VARCHAR(100) COMMENT '玩家名称',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    total_playtime VARCHAR(20) DEFAULT '0' COMMENT '总游戏时长(秒)',
    first_seen DATETIME COMMENT '首次发现时间',
    last_seen DATETIME COMMENT '最后在线时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_steam_id (steam_id),
    INDEX idx_last_seen (last_seen)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家记录表';

-- -----------------------------------------------------
-- 玩家服务器关联表
-- -----------------------------------------------------
CREATE TABLE rs_player_server (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    player_id VARCHAR(32) NOT NULL COMMENT '玩家ID',
    server_id VARCHAR(32) NOT NULL COMMENT '服务器ID',
    score VARCHAR(20) DEFAULT '0' COMMENT '分数',
    playtime VARCHAR(20) DEFAULT '0' COMMENT '在该服游戏时长(秒)',
    first_seen DATETIME COMMENT '首次进入时间',
    last_seen DATETIME COMMENT '最后在线时间',
    UNIQUE KEY uk_player_server (player_id, server_id),
    INDEX idx_server (server_id),
    INDEX idx_player (player_id),
    FOREIGN KEY (player_id) REFERENCES rs_player(id) ON DELETE CASCADE,
    FOREIGN KEY (server_id) REFERENCES rs_server(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家服务器关联表';

-- -----------------------------------------------------
-- 搜索历史表
-- -----------------------------------------------------
CREATE TABLE rs_search_history (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    keyword VARCHAR(100) NOT NULL COMMENT '搜索关键词',
    search_count VARCHAR(20) DEFAULT '1' COMMENT '搜索次数',
    last_searched DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后搜索时间',
    UNIQUE KEY uk_keyword (keyword),
    INDEX idx_search_count (search_count DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索历史表';

-- -----------------------------------------------------
-- 测试数据
-- -----------------------------------------------------
INSERT INTO rs_server (id, name, ip, port, region, map_name, map_size, max_players, is_official, is_modded, gather_rate, status) VALUES
('1001', 'Rusticated.com - Main', '47.115.230.101', '28015', 'asia', 'Procedural Map', '4000', '300', '0', '0', '1', 'online'),
('1002', 'Facepunch Singapore', '103.62.49.103', '28015', 'asia', 'Procedural Map', '4500', '400', '1', '0', '1', 'online'),
('1003', 'Rustoria.co - 3x Solo/Duo/Trio', '45.88.228.91', '28015', 'eu', 'Procedural Map', '3500', '250', '0', '1', '3', 'online'),
('1004', 'GGEZ.RIP - 5x MAX 4', '185.217.59.43', '28015', 'eu', 'Procedural Map', '3000', '200', '0', '1', '5', 'online'),
('1005', 'Facepunch US West', '208.103.5.182', '28015', 'us', 'Procedural Map', '4250', '350', '1', '0', '1', 'online');

-- 状态测试数据
INSERT INTO rs_server_status (id, server_id, players, max_players, queue_players, ping, recorded_at) VALUES
('2001', '1001', '187', '300', '0', '35', NOW()),
('2002', '1002', '245', '400', '12', '28', NOW()),
('2003', '1003', '156', '250', '0', '120', NOW()),
('2004', '1004', '98', '200', '0', '145', NOW()),
('2005', '1005', '312', '350', '5', '89', NOW());

-- Wipe历史测试数据
INSERT INTO rs_wipe_history (id, server_id, wipe_time, wipe_type, player_count, created_at) VALUES
('3001', '1001', DATE_SUB(NOW(), INTERVAL 7 DAY), 'map', '150', NOW()),
('3002', '1002', DATE_SUB(NOW(), INTERVAL 3 DAY), 'map', '280', NOW()),
('3003', '1003', DATE_SUB(NOW(), INTERVAL 5 DAY), 'blueprint', '120', NOW()),
('3004', '1004', DATE_SUB(NOW(), INTERVAL 10 DAY), 'full', '80', NOW()),
('3005', '1005', DATE_SUB(NOW(), INTERVAL 1 DAY), 'map', '300', NOW());

-- 玩家测试数据
INSERT INTO rs_player (id, steam_id, name, avatar_url, total_playtime, first_seen, last_seen) VALUES
('4001', '76561198012345678', 'PlayerOne', 'https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/fe/fef49e7fa7e1997310d705b2a6158ff8dc1cdfeb.jpg', '3600', DATE_SUB(NOW(), INTERVAL 30 DAY), NOW()),
('4002', '76561198087654321', 'RustMaster', 'https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/fe/fef49e7fa7e1997310d705b2a6158ff8dc1cdfeb.jpg', '7200', DATE_SUB(NOW(), INTERVAL 60 DAY), NOW()),
('4003', '76561198111223344', 'SurvivorX', 'https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/fe/fef49e7fa7e1997310d705b2a6158ff8dc1cdfeb.jpg', '1800', DATE_SUB(NOW(), INTERVAL 15 DAY), NOW());

-- 玩家服务器关联测试数据
INSERT INTO rs_player_server (id, player_id, server_id, score, playtime, first_seen, last_seen) VALUES
('5001', '4001', '1001', '1500', '1800', DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
('5002', '4001', '1002', '800', '1200', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
('5003', '4002', '1003', '2500', '3600', DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
('5004', '4003', '1001', '600', '600', DATE_SUB(NOW(), INTERVAL 3 DAY), NOW());
