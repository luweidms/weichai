package com.youming.youche.record.provider.mapper.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.service.ServiceInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @date 2022/1/7 15:52
 */
public interface ServiceInfoMapper extends BaseMapper<ServiceInfo> {

    /**
     * 根据服务商类型查询
     */
    List<Map> getServiceInfoList(@Param("serviceType") int serviceType, @Param("tenantId") Long tenantId);

    /**
     * @param serviceUserId 用户编号
     * @return 服务商名称
     */
    ServiceInfo getObjectById(@Param("serviceUserId") Long serviceUserId);

}
