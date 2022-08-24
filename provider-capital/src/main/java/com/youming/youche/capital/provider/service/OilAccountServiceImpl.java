package com.youming.youche.capital.provider.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.capital.api.IOilAccountService;
import com.youming.youche.capital.domain.TenantServiceRel;
import com.youming.youche.capital.provider.mapper.TenantServiceRelMapper;
import com.youming.youche.capital.vo.AccountOilFixedDetailVo;
import com.youming.youche.capital.vo.OilAccountFixedInfoVo;
import com.youming.youche.capital.vo.TenantServiceVo;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.conts.ServiceConsts;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

@DubboService(version = "1.0.0")
public class OilAccountServiceImpl extends BaseServiceImpl<TenantServiceRelMapper, TenantServiceRel> implements IOilAccountService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private TenantServiceRelMapper tenantServiceRelMapper;
    /**
     * 车队定点油账户统计，包括总的可用额度，限额的统计、不限额的统计
     * */
    @Override
    public OilAccountFixedInfoVo queryFixedInfo(String accessToken) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        TenantServiceVo tenantServiceVo = new TenantServiceVo();
        tenantServiceVo.setTenantId(loginInfo.getTenantId());
        tenantServiceVo.setAgentServiceType(ServiceConsts.AGENT_SERVICE_TYPE.OIL);
        List<TenantServiceRel> list = tenantServiceRelMapper.doQueryQuotaAmtListByTenantId(tenantServiceVo);
        Long allQuotaAmt=0l;//总的授信额度
        TenantServiceRel quota =new TenantServiceRel();
        TenantServiceRel notQuota =new TenantServiceRel();
        notQuota.setQuotaAmt(0l);
        notQuota.setNotUseQuotaAmt(0l);
        notQuota.setUseQuotaAmt(0l);
        for (TenantServiceRel tenantService: list) {
            if(tenantService.getQuotaAmt()==0){
                //不限额
                notQuota.setUseQuotaAmt(notQuota.getUseQuotaAmt()+tenantService.getUseQuotaAmt());
            }else{
                quota.setQuotaAmt(tenantService.getQuotaAmt() + quota.getQuotaAmt());
                quota.setUseQuotaAmt(quota.getUseQuotaAmt()+tenantService.getUseQuotaAmt());
            }
        }
        if(quota.getQuotaAmt()==0){
            quota.setNotUseQuotaAmt(0l);
            allQuotaAmt=0l;
        }else{
            quota.setNotUseQuotaAmt(quota.getQuotaAmt()-quota.getUseQuotaAmt());
            allQuotaAmt=quota.getNotUseQuotaAmt();
        }
        return new OilAccountFixedInfoVo()
                .setAllQuotaAmt(allQuotaAmt)
                .setQuota(quota).setNotQuota(notQuota);
    }
    /**
     * 车队定点油账户详情
     * */
    @Override
    public  IPage<TenantServiceRel> queryFixedDetail(String accessToken, AccountOilFixedDetailVo accountOilFixedDetailVo) throws Exception{
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        TenantServiceVo tenantServiceVo = new TenantServiceVo();
        tenantServiceVo.setServiceName(accountOilFixedDetailVo.getServiceName());
        tenantServiceVo.setTenantId(loginInfo.getTenantId());
        tenantServiceVo.setAgentServiceType(ServiceConsts.AGENT_SERVICE_TYPE.OIL);
        tenantServiceVo.setQuotaAmtType(accountOilFixedDetailVo.getQuotaAmtType());
        Page<TenantServiceVo> page =new Page<>(accountOilFixedDetailVo.getPageNum(),accountOilFixedDetailVo.getPageSize());
        IPage<TenantServiceRel> list = tenantServiceRelMapper.doQueryQuotaAmtListByTenantIdByPage(page,tenantServiceVo);
        return list;
    }

    @Override
    public List<WorkbenchDto> getTableFinancialOilUsedAmount() {
        return baseMapper.getTableFinancialOilUsedAmount();
    }

    @Override
    public List<WorkbenchDto> getTableFinacialOilSurpleAmount() {
        return baseMapper.getTableFinacialOilSurpleAmount();
    }

    /***
     * quotaAmt 授信额度 分
     * useQuotaAmt 已用额度 分
     * @param tenantId
     * @return
     * @throws Exception
     */
    @Override
    public List<TenantServiceRel> doQueryQuotaAmtListByTenantId(TenantServiceVo tenantServiceVo) {
        List<TenantServiceRel> tenantServiceRels = baseMapper.doQueryQuotaAmtListByTenantId(tenantServiceVo);
        return tenantServiceRels;
    }


}
