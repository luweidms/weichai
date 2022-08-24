package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.ITenantProductRelVerService;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import com.youming.youche.market.domain.facilitator.TenantProductRelVer;
import com.youming.youche.market.dto.facilitator.ProductSaveDto;
import com.youming.youche.market.provider.mapper.facilitator.TenantProductRelVerMapper;
import com.youming.youche.market.provider.utis.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.youming.youche.conts.SysStaticDataEnum.PT_TENANT_ID;


/**
 * <p>
 * 租户与站点关系表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-29
 */
@DubboService(version = "1.0.0")
@Service
public class TenantProductRelVerServiceImpl extends BaseServiceImpl<TenantProductRelVerMapper, TenantProductRelVer> implements ITenantProductRelVerService {


    @Override
    public void saveProductVer(TenantProductRel tenantProductRel, ProductSaveDto productSaveIn, Boolean isUpdate, LoginInfo baseUser) {
        //保存历史记录
        TenantProductRelVer tenantProductRelVer = new TenantProductRelVer();
        BeanUtils.copyProperties( tenantProductRel,tenantProductRelVer);

        if (StringUtils.isNotBlank(productSaveIn.getFixedBalance())) {
            tenantProductRelVer.setFixedBalance(CommonUtil.getLongByString(productSaveIn.getFixedBalance()));
        }else{
            tenantProductRelVer.setFixedBalance(null);
        }
        if (StringUtils.isNotBlank(productSaveIn.getFixedBalanceBill())) {
            tenantProductRelVer.setFixedBalanceBill(CommonUtil.getLongByString(productSaveIn.getFixedBalanceBill()));
        }else{
            tenantProductRelVer.setFixedBalanceBill(null);
        }
        tenantProductRelVer.setFloatBalance(productSaveIn.getFloatBalance());
        tenantProductRelVer.setLocaleBalanceState(productSaveIn.getLocaleBalanceState());
        tenantProductRelVer.setFloatBalanceBill(productSaveIn.getFloatBalanceBill());
        if (isUpdate) {
            tenantProductRelVer.setState(productSaveIn.getState());
        }
        /*if(StringUtils.isNotBlank(productSaveIn.getFixedBalance()) && StringUtils.isNotBlank(productSaveIn.getFloatBalance())){
            throw new BusinessException("数据异常:不开票浮动价和固定价都检测到有值");
        }*/
        if(StringUtils.isNotBlank(productSaveIn.getFixedBalanceBill()) && StringUtils.isNotBlank(productSaveIn.getFloatBalanceBill())){
            throw new BusinessException("数据异常:开票浮动价和固定价都检测到有值");
        }
        tenantProductRelVer.setUpdateTime(LocalDateTime.now());
        tenantProductRelVer.setUpdateOpId(baseUser.getId());
        tenantProductRelVer.setRelId(tenantProductRel.getId());
        tenantProductRelVer.setId(null);
        this.saveOrUpdate(tenantProductRelVer);
    }

    @Override
    public void saveProductRelVelShare(ProductSaveDto productSaveIn, TenantProductRel tenantProductRel,LoginInfo baseUser) {
        TenantProductRelVer tenantProductRelVer = new TenantProductRelVer();
        BeanUtils.copyProperties(tenantProductRel,tenantProductRelVer);
        BeanUtils.copyProperties(productSaveIn,tenantProductRelVer);
        if(StringUtils.isNotBlank(productSaveIn.getFixedBalance())){
            tenantProductRelVer.setFixedBalance(CommonUtil.getLongByString(productSaveIn.getFixedBalance()));
        }else{
            tenantProductRelVer.setFixedBalance(null);
        }
        if(StringUtils.isNotBlank(productSaveIn.getFixedBalanceBill())){
            tenantProductRelVer.setFixedBalanceBill(CommonUtil.getLongByString(productSaveIn.getFixedBalanceBill()));
        }else{
            tenantProductRelVer.setFixedBalanceBill(null);
        }
        tenantProductRelVer.setTenantId(PT_TENANT_ID);
        if (productSaveIn.getProductState() != null && productSaveIn.getProductState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
            tenantProductRelVer.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);
        } else {
            tenantProductRelVer.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        }
        tenantProductRelVer.setUpdateTime(LocalDateTime.now());
        tenantProductRelVer.setUpdateOpId(baseUser.getId());
        tenantProductRelVer.setRelId(tenantProductRel.getId());
        tenantProductRelVer.setId(null);
        this.saveOrUpdate(tenantProductRelVer);
    }

    @Override
    public void saveProductHis(TenantProductRelVer tenantProductRelVer,LoginInfo baseUser) {
        tenantProductRelVer.setUpdateTime(LocalDateTime.now());
        tenantProductRelVer.setUpdateOpId(baseUser.getId());
        this.saveOrUpdate(tenantProductRelVer);
    }

    @Override
    public TenantProductRelVer getTenantProductRelVer(Long relId) {
        LambdaQueryWrapper<TenantProductRelVer> lambd=new QueryWrapper<TenantProductRelVer>().lambda();
        lambd.eq(TenantProductRelVer::getRelId,relId)
                .orderByDesc(TenantProductRelVer::getId)
                .last("limit 1");
        return this.getOne(lambd);
    }


}
