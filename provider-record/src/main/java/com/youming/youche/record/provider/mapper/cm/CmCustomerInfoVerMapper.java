package com.youming.youche.record.provider.mapper.cm;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.cm.CmCustomerInfoVer;
import com.youming.youche.record.vo.cm.CmCustomerInfoVo;
import org.apache.ibatis.annotations.Param;

public interface CmCustomerInfoVerMapper extends BaseMapper<CmCustomerInfoVer> {
    CmCustomerInfoVer selectCustomerVerById(@Param("customerId")Long customerId, @Param("tenantId")Long tenantId);
    Integer insertCustomerInfoVer(@Param("customerInfo") CmCustomerInfoVo customerInfo) throws Exception;
    Integer updateCustomerInfoVer(@Param("customerInfo") CmCustomerInfoVo customerInfo,
                               @Param("tenantId") Long tenantId);
}
