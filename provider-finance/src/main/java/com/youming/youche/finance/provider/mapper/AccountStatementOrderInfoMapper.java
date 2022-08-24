package com.youming.youche.finance.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.AccountStatementOrderInfo;

/**
* <p>
* Mapper接口
* </p>
* @author zengwen
* @since 2022-06-27
*/
    public interface AccountStatementOrderInfoMapper extends BaseMapper<AccountStatementOrderInfo> {

        void deleteAccountStatementOrder(Long accountStatementId);
    }
