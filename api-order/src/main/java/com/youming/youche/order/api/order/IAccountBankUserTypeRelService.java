package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.AccountBankUserTypeRel;

/**
 * <p>
 * 银行卡跟用户类型的关系表 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
public interface IAccountBankUserTypeRelService extends IBaseService<AccountBankUserTypeRel> {

    /**
     * 判断用户是否已绑定指定类型的银行卡
     */
    Boolean isUserTypeBindCard(long userId, int bankType, int userType);

    /**
     * 判断用户是否已绑定银行卡
     * @param userId 用户编号
     * @return true-已绑定，false-未绑定
     */
    Boolean isUserTypeBindCard(long userId, int userType);

}
