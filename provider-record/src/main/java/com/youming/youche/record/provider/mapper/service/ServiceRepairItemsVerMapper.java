package com.youming.youche.record.provider.mapper.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.service.ServiceRepairItemsVer;
import org.apache.ibatis.annotations.Param;

/**
* <p>
* 服务商维修想版本Mapper接口
* </p>
* @author zengwen
* @since 2022-05-23
*/
    public interface ServiceRepairItemsVerMapper extends BaseMapper<ServiceRepairItemsVer> {

    ServiceRepairItemsVer getServiceRepairItemsVerById(@Param("id") Long id);
    }
