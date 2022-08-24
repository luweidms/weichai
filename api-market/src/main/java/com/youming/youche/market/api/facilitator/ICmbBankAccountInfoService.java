package com.youming.youche.market.api.facilitator;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.market.domain.facilitator.CmbBankAccountInfo;
import com.youming.youche.market.dto.user.AccountBankByTenantIdDto;

import java.util.List;

/**
 * <p>
 * 招行帐户信息表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-09
 */
public interface ICmbBankAccountInfoService extends IBaseService<CmbBankAccountInfo> {

    /**
     * 查询银行流水
     * @param BankAccountTranFlowInDto
     * @return
     * @throws Exception
     */
//    IPage<> queryBankAccountTranFlowList(BankAccountTranFlowInDto bankAccountTranFlowInDto);
    /**
     * @description 根据用户Id获取银行帐户列表
     * @author zag
     * @date 2021/11/24 21:30
     * @param userId
     * @return java.util.List<com.business.cmb.vo.BankAccountInfo>
     */
     List<CmbBankAccountInfo> queryBankAccountList(Long userId, String accLevel);

    /**
     * 录入订单页面  通过承运车队的tenantId，查找车队的收款账号和收款人
     * @param tenantId
     * @return
     */
    AccountBankByTenantIdDto getCmbBankAccountInfo(Long tenantId);
}
