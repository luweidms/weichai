package com.youming.youche.market.api.facilitator;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.market.domain.facilitator.AccountBankUserTypeRel;
import com.youming.youche.market.vo.maintenanceaudit.AccountBankByTenantIdVO;
import com.youming.youche.market.vo.maintenanceaudit.AccountBankUserTypeRelVo;

import java.util.List;

/**
 * <p>
 * 银行卡跟用户类型的关系表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-24
 */
public interface IAccountBankUserTypeRelService extends IBaseService<AccountBankUserTypeRel> {
    /**
     *  是否绑卡
     * @param userId
     * @param bankType
     * @param userType
     * @return
     */
      Boolean isUserTypeBindCard(Long userId, Integer bankType, Integer userType);

    /**
     * 根据用户编号查询账号信息
     * @param userId 用户编号-必填
     * @return
     */
    List<AccountBankUserTypeRelVo> queryAccountBankRel(Long userId);

    /**
     * 根据租户id查询租户收款账号和收款人
     * @param tenantId
     * @return
     */
    AccountBankByTenantIdVO getAccountBankRel(Long tenantId);

}
