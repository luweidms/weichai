package com.youming.youche.system.provider.mapper.mycenter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.system.domain.mycenter.CmbBankAccountInfo;
import com.youming.youche.system.dto.mycenter.AccountBalanceDto;
import com.youming.youche.system.dto.mycenter.BankAccountListDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @InterfaceName CmbBankAccountInfoMapper
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/25 17:54
 */
public interface BankAccountMapper extends BaseMapper<CmbBankAccountInfo> {

    /**
     * 查询个人账户列表
     */
    List<BankAccountListDto> selectUserAccList(@Param("userId") Long userId,@Param("certType") String certType);

    /**
     * 查询平台车队账户列表
     */
    List<BankAccountListDto> selectTenantAccList(@Param("tenantId") Long tenantId,@Param("certType") String certType);

    /**
     * 查询个人账户金额
     */
    List<AccountBalanceDto> getUserAccBalance(@Param("userId") Long userId);

    /**
     * 查询平台车队账户金额
     */
    List<AccountBalanceDto> getTenantAccBalance(@Param("tenantId") Long tenantId);

    /**
     * 查询平台车队账户Id列表
     */
    List<Long> getTenantAccIdList(@Param("tenantId") Long tenantId);

    /**
     * 查询个人账户Id列表
     */
    List<Long> getUserAccIdList(@Param("userId") Long userId,@Param("tenantId") Long tenantId);

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