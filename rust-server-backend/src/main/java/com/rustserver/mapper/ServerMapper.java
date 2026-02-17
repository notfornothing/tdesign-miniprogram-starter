package com.rustserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rustserver.entity.ServerInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServerMapper extends BaseMapper<ServerInfo> {
}
