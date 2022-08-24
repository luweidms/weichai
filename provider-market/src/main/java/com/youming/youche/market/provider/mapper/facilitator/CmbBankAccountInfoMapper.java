package com.youming.youche.market.provider.mapper.facilitator;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.market.domain.facilitator.CmbBankAccountInfo;
import com.youming.youche.market.dto.user.AccountBankByTenantIdDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
* 招行帐户信息表Mapper接口
* </p>
* @author CaoYaJie
* @since 2022-02-09
*/
    public interface CmbBankAccountInfoMapper extends BaseMapper<CmbBankAccountInfo> {

        List<AccountBankByTenantIdDto> getCmbBankAccountInfo(@Param("tenantId") Long tenantId,
                                                             @Param("accLevel") String accLevel,
                                                             @Param("certType") String certType);

    }
