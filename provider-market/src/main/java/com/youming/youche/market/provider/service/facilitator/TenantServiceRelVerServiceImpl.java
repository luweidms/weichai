package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.market.api.facilitator.ITenantServiceRelService;
import com.youming.youche.market.api.facilitator.ITenantServiceRelVerService;
import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import com.youming.youche.market.domain.facilitator.TenantServiceRelVer;
import com.youming.youche.market.dto.facilitator.ServiceProductDto;
import com.youming.youche.market.dto.facilitator.TenantServiceDto;
import com.youming.youche.market.provider.mapper.facilitator.TenantServiceRelVerMapper;
import com.youming.youche.market.provider.transfer.ServiceInfoTransfer;
import com.youming.youche.market.provider.transfer.TenantServiceTransfer;
import com.youming.youche.market.provider.utis.ReadisUtil;
import com.youming.youche.market.vo.facilitator.ServiceInfoVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.youming.youche.conts.EnumConsts.SysStaticData.CUSTOMER_AUTH_STATE;
import static com.youming.youche.conts.EnumConsts.SysStaticData.SERVICE_BUSI_TYPE;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_STATE_DESC;


/**
 * <p>
 * 服务商与租户关系 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-25
 */
@DubboService(version = "1.0.0")
@Service
public class TenantServiceRelVerServiceImpl extends BaseServiceImpl<TenantServiceRelVerMapper, TenantServiceRelVer> implements ITenantServiceRelVerService {
    @Resource
    private TenantServiceRelVerMapper tenantServiceRelVerMapper;
    @Resource
    private LoginUtils loginUtils;
    @Resource
    private TenantServiceTransfer tenantServiceTransfer;
    @Autowired
    private ITenantServiceRelService tenantServiceRelService;
    @Resource
    private ServiceInfoTransfer serviceInfoTransfer;
    @Resource
    private ReadisUtil readisUtil;


    @Override
    public TenantServiceRelVer getTenantServiceRelVer(Long relId) {
        LambdaQueryWrapper<TenantServiceRelVer> lambda = new QueryWrapper<TenantServiceRelVer>().lambda();
        lambda.eq(TenantServiceRelVer::getRelId, relId)
                .orderByDesc(TenantServiceRelVer::getId)
                .last("limit 1");
        return this.getOne(lambda);
    }

    @Override
    public TenantServiceRelVer getTenantServiceRelVerByInvitationId(Long relId, Long invitationId) {
        LambdaQueryWrapper<TenantServiceRelVer> lambda = new QueryWrapper<TenantServiceRelVer>().lambda();
        lambda.eq(TenantServiceRelVer::getRelId, relId);
        if (invitationId != null){
            lambda.eq(TenantServiceRelVer::getInvitationId,invitationId);
        }
        lambda.orderByDesc(TenantServiceRelVer::getId)
                    .last("limit 1");
        return this.getOne(lambda);
    }

    @Override
    public TenantServiceRelVer getTenantServiceRelVer(Long tenantId, Long serviceUserId) {
        LambdaQueryWrapper<TenantServiceRelVer> lambda = new QueryWrapper<TenantServiceRelVer>().lambda();
        lambda.eq(TenantServiceRelVer::getTenantId, tenantId)
                .eq(TenantServiceRelVer::getServiceUserId, serviceUserId)
                .orderByDesc(TenantServiceRelVer::getId)
                .last("limit 1");
        return this.getOne(lambda);
    }

    @Override
    public void saveTenantServiceRelHis(TenantServiceRelVer tenantServiceRelVer, boolean isUpdate, LoginInfo baseUser) {
        tenantServiceRelVer.setUpdateTime(LocalDateTime.now());
        tenantServiceRelVer.setUpdateOpId(baseUser.getId());
        this.saveOrUpdate(tenantServiceRelVer);
    }

    @Override
    public boolean saveTenantServiceRelVer(TenantServiceRelVer tenantServiceRelVer) {
        boolean b = this.saveOrUpdate(tenantServiceRelVer);
        return b;
    }

    @Override
    public IPage<TenantServiceDto> queryTenantServiceHis(Integer pageNum, Integer pageSize, String loginAcct,
                                                         String serviceName, String linkman, Integer serviceType,
                                                         String logInfoUser) throws Exception {
        LoginInfo user = loginUtils.get(logInfoUser);
        Page<TenantServiceDto> page = new Page(pageNum, pageSize);

        IPage<TenantServiceDto> tenantServiceDtoIPage = tenantServiceRelVerMapper.queryTenantServiceHis(page, EnumConsts.SERVICE_IS_DEL.YES,
                user.getTenantId(), loginAcct, serviceName,
                linkman, serviceType);
        List<TenantServiceDto> list = tenantServiceDtoIPage.getRecords();
        List<TenantServiceDto> vo = tenantServiceTransfer.getToTenantServiceVo(list);
        tenantServiceDtoIPage.setRecords(vo);

        return tenantServiceDtoIPage;
    }

    @Override
    public IPage<ServiceProductDto> queryProductHis(Integer pageNum, Integer pageSize, String productName, String serviceCall, String address, String serviceName, String logInfoUser) throws Exception {

        LoginInfo user = loginUtils.get(logInfoUser);
        Page<ServiceProductDto> page = new Page(pageNum, pageSize);
        IPage<ServiceProductDto> tenantServiceDtoIPage = tenantServiceRelVerMapper.queryProductHis(page, productName, serviceCall, address, serviceName, user.getTenantId(), EnumConsts.SERVICE_IS_DEL.YES);
        return tenantServiceDtoIPage;
    }

    @Override
    public ServiceInfoVo seeServiceInfoHis(Long serviceUserId, Long relId, String logInfoUser) {
        ServiceInfoVo serviceInfoVo = serviceInfoTransfer.acquireServiceInfoVo(serviceUserId);
        LoginInfo baseUser = loginUtils.get(logInfoUser);
        TenantServiceRelVer tenantServiceRelVer = null;
        if (relId == null || relId < 0) {
            TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(baseUser.getTenantId(), serviceUserId);
            if (tenantServiceRel != null) {
                BeanUtils.copyProperties(tenantServiceRel, serviceInfoVo);
                tenantServiceRelVer = getTenantServiceRelVer(tenantServiceRel.getId());
                serviceInfoVo.setTenantServiceRelVer(tenantServiceRelVer);
            } else {
                tenantServiceRelVer = getTenantServiceRelVer(baseUser.getTenantId(), serviceUserId);
                BeanUtils.copyProperties(tenantServiceRelVer, serviceInfoVo);
            }
        } else {
            tenantServiceRelVer = getTenantServiceRelVer(relId);
            BeanUtils.copyProperties(tenantServiceRelVer, serviceInfoVo);
        }

        if (serviceInfoVo.getAuthState() != null) {
            String authStateName = readisUtil.getSysStaticData(CUSTOMER_AUTH_STATE, serviceInfoVo.getAuthState().toString()).getCodeName();
            serviceInfoVo.setAuthStateName(authStateName);
        }
        if (serviceInfoVo.getBalanceType() != null) {
            Integer balanceType = serviceInfoVo.getBalanceType();
            if (balanceType != null && balanceType == 1) {
                serviceInfoVo.setBalanceTypeName("账期");
            } else if (balanceType != null && balanceType == 2) {
                serviceInfoVo.setBalanceTypeName("月结");
            } else if (balanceType != null && balanceType == 3) {
                serviceInfoVo.setBalanceTypeName("无账期");
            }
        }
        if (serviceInfoVo.getIsBillAbility() != null) {
            if (serviceInfoVo.getIsBillAbility() == 1) {
                serviceInfoVo.setIsBillAbilityName("支持");
            } else {
                serviceInfoVo.setIsBillAbilityName("不支持");
            }
        }
        if (serviceInfoVo.getServiceType() != null) {
            String serviceTypeName = readisUtil.getSysStaticData(SERVICE_BUSI_TYPE, serviceInfoVo.getServiceType().toString()).getCodeName();
            serviceInfoVo.setServiceTypeName(serviceTypeName);
        }
        if (serviceInfoVo.getState() != null) {
            String codeName = readisUtil.getSysStaticData(SYS_STATE_DESC, serviceInfoVo.getState().toString()).getCodeName();
            serviceInfoVo.setStateName(codeName);
        }
        if (serviceInfoVo.getIsBill() != null) {
            Integer isBill = serviceInfoVo.getIsBill();
            if (isBill >= 0) {
                if (isBill == 1) {
                    serviceInfoVo.setIsBillName("是");
                } else {
                    serviceInfoVo.setIsBillName("否");
                }
            }
        }
        return serviceInfoVo;
    }

    /**
     * 获取当前用户工具
     * @param accessToken
     * @return
     */


}
