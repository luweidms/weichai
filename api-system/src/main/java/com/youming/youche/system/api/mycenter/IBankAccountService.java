package com.youming.youche.system.api.mycenter;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.system.domain.mycenter.CmbBankAccountInfo;
import com.youming.youche.system.dto.mycenter.BankAccountListDto;
import com.youming.youche.system.vo.mycenter.AccountBalanceVo;
import com.youming.youche.system.vo.mycenter.CreatePrivateAccountVo;
import com.youming.youche.system.vo.mycenter.CreatePublicAccountVo;
import com.youming.youche.system.vo.mycenter.UpdateBankAccountVo;

import java.util.List;
import java.util.Map;

/**
 * @InterfaceName ICmbBankAccountInfoService
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/25 17:55
 */
public interface IBankAccountService extends IBaseService<CmbBankAccountInfo> {

    /** 获取个人账户列表
     * */
    List<BankAccountListDto> getUserAccList(Long userId, String accType);

    /** 获取车队账户列表
     * */
    List<BankAccountListDto> getTenantAccList(Long tenantId,String accType);

    /** 根据mbrNo获取账户信息
     * */
    CmbBankAccountInfo getByMbrNo(String mbrNo);

    /** 根据mbrNo获取账户信息
     * */
    CmbBankAccountInfo getByMerchNo(String merchNo);

    /** 公户注册
     * */
    void publicAccReg(CreatePublicAccountVo createPublicAccountVo);

    /** 私户注册
     * */
    void privceAccReg(CreatePrivateAccountVo createPrivateAccountVo);

    /** 管理员信息变更
     * */
    void mgrInfoChg(UpdateBankAccountVo updateBankAccountVo);

    /** 获取账户余额
     * */
    String getBalance(String mbrNo);

    /** 获取个人账户余额
     * */
    AccountBalanceVo getUserAccBalance(Long userId);

    /** 获取车队账户余额
     * */
    AccountBalanceVo getTenantAccBalance(Long tenantId);

    /** 同步银行账户注册结果
     * */
    void syncBnkAccRegResult();

    /** 同步银行账户余额
     * */
    void syncBnkAccBalance();

    /** 商户进件回调接口
     * */
    void merchRegCallBack(Map<String, Object> param);

    /**
     * 财务工作台 平台剩余金额
     */
    List<WorkbenchDto> getTableFinancialPlatformSurplusAmount();

    /**
     * 财务工作台 平台已用金额
     */
    List<WorkbenchDto> getTableFinacialPlatformUsedAmount();

    /**
     * 财务工作台 今日充值金额
     */
    List<WorkbenchDto> getTableFinacialRechargeTodayAmount();
}