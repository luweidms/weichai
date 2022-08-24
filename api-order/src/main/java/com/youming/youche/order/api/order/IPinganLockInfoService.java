package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.PinganLockInfo;
import com.youming.youche.order.dto.order.AccountBankRelDto;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface IPinganLockInfoService extends IBaseService<PinganLockInfo> {

    /**
     * 查询账户锁定业务的金额
     * @param userId 用户编号
     */
    Long getAccountLockSum(Long userId);


    /**
     * 查询账户锁定业务的金额明细
     * @param userId 用户编号
     */
     Page<AccountBankRelDto> queryLockBalanceDetails(Long userId,Integer pageNum,
                                                     Integer pageSize);
}
