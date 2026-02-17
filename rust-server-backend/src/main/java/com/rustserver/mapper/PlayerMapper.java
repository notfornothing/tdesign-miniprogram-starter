package com.rustserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rustserver.entity.PlayerInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PlayerMapper extends BaseMapper<PlayerInfo> {
}
