package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.ITenantServiceRelService;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import com.youming.youche.market.domain.facilitator.TenantServiceRelVer;
import com.youming.youche.market.dto.facilitator.ProductSaveDto;
import com.youming.youche.market.provider.mapper.facilitator.TenantServiceRelMapper;
import com.youming.youche.market.provider.utis.CommonUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static com.youming.youche.record.common.SysStaticDataEnum.PT_TENANT_ID;


/**
 * <p>
 * 服务商与租户关系 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
@DubboService(version = "1.0.0")
@Service
public class TenantServiceRelServiceImpl extends BaseServiceImpl<TenantServiceRelMapper, TenantServiceRel> implements ITenantServiceRelService {
    @Resource
    private TenantServiceRelMapper tenantServiceRelMapper;

    /**
     * 保存或修改
     * @param tenantServiceRel
     * @param isUpdate
     * @param user
     */
    @Override
    public void saveOrUpdateTenantServiceRel(TenantServiceRel tenantServiceRel, Boolean isUpdate, LoginInfo user) {
        if(isUpdate){
            tenantServiceRel.setOpId(user.getId());
            tenantServiceRel.setUpdateTime(LocalDateTime.now());
        }else {
            tenantServiceRel.setCreateTime(LocalDateTime.now());
            tenantServiceRel.setCreatorId(user.getId());
            tenantServiceRel.setAuthState(SysStaticDataEnum.CUSTOMER_AUTH_STATE.WAIT_AUTH);
            tenantServiceRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        }
        this.saveOrUpdate(tenantServiceRel);
    }

    @Override
    public TenantServiceRel getTenantServiceRel(Long tenantId, Long serviceUserId) {
        LambdaQueryWrapper<TenantServiceRel> lambd= new QueryWrapper<TenantServiceRel>().lambda();
        if(tenantId!=null && tenantId!=0){
            //问题
            lambd.eq(TenantServiceRel::getTenantId,tenantId);
        }
        if(serviceUserId!=null && serviceUserId!=0){
            lambd.eq(TenantServiceRel::getServiceUserId,serviceUserId);
        }
        lambd.orderByDesc(TenantServiceRel::getId).last(" limit 1");
        List<TenantServiceRel> list = tenantServiceRelMapper.selectList(lambd);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public TenantServiceRel getTenantServiceRel(Long serviceUserId) {
        LambdaQueryWrapper<TenantServiceRel> lambd= new QueryWrapper<TenantServiceRel>().lambda();
        if(serviceUserId!=null && serviceUserId!=0){
            lambd.eq(TenantServiceRel::getServiceUserId,serviceUserId);
        }
        lambd.orderByDesc(TenantServiceRel::getId).last(" limit 1");
        List<TenantServiceRel> list = tenantServiceRelMapper.selectList(lambd);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public void saveOrUpdate(TenantServiceRel tenantServiceRel, Boolean isUpdate,LoginInfo user) {
        if(isUpdate){
            tenantServiceRel.setOpId(user.getId());
            tenantServiceRel.setUpdateTime(LocalDateTime.now());
            this.update(tenantServiceRel);
        }else {
            tenantServiceRel.setCreateTime(LocalDateTime.now());
            tenantServiceRel.setCreatorId(user.getId());
            tenantServiceRel.setAuthState(SysStaticDataEnum.CUSTOMER_AUTH_STATE.WAIT_AUTH);
            tenantServiceRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
            this.save(tenantServiceRel);
        }
    }

    @Override
    public void updateTenantServiceRelState(Long userId, Integer state) {
        UpdateWrapper<TenantServiceRel>  updateWrapper= new UpdateWrapper<>();
        updateWrapper.set("state",state)
                     .eq("service_user_id",userId);
        this.update(updateWrapper);
    }




    @Override
    public Boolean checkTenantService(Long tenantId, ProductSaveDto productSaveIn, Boolean isUpdate) {
        if (productSaveIn.getIsFleet() == SysStaticDataEnum.isFleet.TENANT) {
            LambdaQueryWrapper<TenantServiceRel> lambdaq=new QueryWrapper<TenantServiceRel>().lambda();
            lambdaq.eq(TenantServiceRel::getTenantId,tenantId)
                    .eq(TenantServiceRel::getServiceUserId,productSaveIn.getServiceUserId());
            TenantServiceRel tenantServiceRel = this.getOne(lambdaq);
            if (isUpdate) {
                if ((tenantServiceRel == null || CommonUtil.checkEnumIsNull(tenantServiceRel.getState(), SysStaticDataEnum.SYS_STATE_DESC.STATE_NO))
                        && CommonUtil.checkEnumIsNotNull(productSaveIn.getState(), SysStaticDataEnum.SYS_STATE_DESC.STATE_YES)) {
                    throw new BusinessException("无效服务商站点，不能修改成有效合作站点！");
                }
            } else {
                if (tenantServiceRel == null) {
                    throw new BusinessException("未与该服务商合作，不能新增站点！");
                }
                if (tenantServiceRel.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
                    throw new BusinessException("与该服务商合作关系是无效，不能新增站点！");
                }
                if (tenantServiceRel.getAuthState() != SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS) {
                    throw new BusinessException("未审核通过的服务商不能新增合作站点!");
                }
            }
        }
        return true;
    }
}
