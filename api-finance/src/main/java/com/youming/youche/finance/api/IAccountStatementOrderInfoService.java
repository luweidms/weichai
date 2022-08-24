package com.youming.youche.finance.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.AccountStatementOrderInfo;
import com.youming.youche.finance.vo.QueryAccountStatementOrdersVo;

import java.util.List;

/**
* <p>
    *  服务类
    * </p>
* @author zengwen
* @since 2022-06-27
*/
    public interface IAccountStatementOrderInfoService extends IBaseService<AccountStatementOrderInfo> {

        void deleteAccountStatementOrder(Long accountStatementId);

        List<AccountStatementOrderInfo> getAccountStatementOrder(Long accountStatementId);

        Page<AccountStatementOrderInfo> getAccountStatementOrderPage(Long accountStatementId, QueryAccountStatementOrdersVo vo, Integer pageNum, Integer pageSize);
    }
