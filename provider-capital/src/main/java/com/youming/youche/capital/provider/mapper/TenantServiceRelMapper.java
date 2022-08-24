package com.youming.youche.capital.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.capital.domain.TenantServiceRel;
import com.youming.youche.capital.vo.TenantServiceVo;
import com.youming.youche.components.workbench.WorkbenchDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TenantServiceRelMapper extends BaseMapper<TenantServiceRel> {
    List<TenantServiceRel> doQueryQuotaAmtListByTenantId(@Param("tenantServiceVo") TenantServiceVo tenantServiceVo);

    IPage<TenantServiceRel> doQueryQuotaAmtListByTenantIdByPage(Page pae, @Param("tenantServiceVo") TenantServiceVo tenantServiceVo);

    /**
     * 财务工作台  油账户  已用金额
     * @return
     */
    List<WorkbenchDto> getTableFinancialOilUsedAmount();

    /**
     * 财务工作台  油账户  累计剩余
     */
    List<WorkbenchDto> getTableFinacialOilSurpleAmount();
}
