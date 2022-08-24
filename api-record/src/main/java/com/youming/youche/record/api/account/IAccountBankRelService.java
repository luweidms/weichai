package com.youming.youche.record.api.account;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.record.domain.account.AccountBankRel;
import com.youming.youche.record.dto.PTDto;

import java.util.List;

/**
 * <p>
 * 银行卡表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface IAccountBankRelService extends IBaseService<AccountBankRel> {

    /**
     * 查询用户银行账户编号
     * @param userId   用户编号
     * @param userType 用户类型
     * @return
     */
    public boolean isUserTypeBindCardAll(long userId,Integer userType);


    /**
     * 根据用户编号查询账号信息
     * @param userId 用户编号-必填
     * @param userType 用户类型-选填
     * @param bankType 账户类型-选填
     * @return
     */
    List<AccountBankRel> queryAccountBankRel(long userId, Integer userType, Integer bankType);

    /**
     * 通过车牌号查询用户银行卡信息
     *
     * @param platNumber 车牌号
     * @return
     */
    List<AccountBankRel> getTenantCardListByPlatNumber(String platNumber);


    /**
     * @param bankType 银行类型（0：对私，1：对公）（可选）
     *                 接口出参：
     *                 acctName        账户名
     *                 acctNo          银行卡号
     *                 bankName        开户行
     *                 bankProvCity    开户省市
     *                 branchName      开户支行
     *                 获取充值账户信息
     */
    PTDto getPTAccountAndTailNumber(Integer bankType, String accessToken, Long... userId);

    /**
     * 获取平台账户
     *
     * @param relId 主键
     */
    AccountBankRel getAccountBankRelById(Long relId);

    /**
     * 判断用户是否曾经绑定过 对私  银行卡--不需要修改
     */
    Boolean isUserBindCardEver(Long userId);

    /**
     * 判断用户是否曾经绑定过 某类型 银行卡--不需要修改
     *
     * @param bankType 传null时不区分对公对私
     */
    Boolean isUserBindCardEver(Long userId, Integer bankType);

    /**
     * 通过账户编号查询银行卡信息
     *
     * @param acctNo 账户编号
     * @return
     */
    AccountBankRel getAccountBankRelByAcctNo(String acctNo);

}
