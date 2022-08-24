package com.youming.youche.market.api.facilitator;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.market.domain.facilitator.AccountBankRel;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-24
 */
public interface IAccountBankRelService extends IBaseService<AccountBankRel> {
     /**
      * 查询服务商归属下银行
      * @param userId
      * @param bankType
      * @param userType
      * @return
      */
     Boolean getBank(long userId, int bankType, int userType);
}
