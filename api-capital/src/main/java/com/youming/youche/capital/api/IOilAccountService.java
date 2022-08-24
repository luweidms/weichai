package com.youming.youche.capital.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.capital.domain.TenantServiceRel;
import com.youming.youche.capital.vo.AccountOilFixedDetailVo;
import com.youming.youche.capital.vo.OilAccountFixedInfoVo;
import com.youming.youche.capital.vo.TenantServiceVo;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.components.workbench.WorkbenchDto;

import java.util.List;

public interface IOilAccountService extends IBaseService<TenantServiceRel> {

    /**
     * 车队定点油账户统计，包括总的可用额度，限额的统计、不限额的统计
     * */
    OilAccountFixedInfoVo queryFixedInfo(String accessToken);

    /**
     * 车队定点油账户详情
     * */
    IPage<TenantServiceRel> queryFixedDetail(String accessToken, AccountOilFixedDetailVo accountOilFixedDetailVo) throws Exception;

    /**
     * 财务工作台  油账户  已用金额
     * @return
     */
    List<WorkbenchDto> getTableFinancialOilUsedAmount();

    /**
     * 财务工作台  油账户  累计剩余
     */
    List<WorkbenchDto> getTableFinacialOilSurpleAmount();


    /***
     * quotaAmt 授信额度 分
     * useQuotaAmt 已用额度 分
     * @param tenantId
     * @return
     * @throws Exception
     */
    List<TenantServiceRel> doQueryQuotaAmtListByTenantId(TenantServiceVo tenantServiceVo);
}
