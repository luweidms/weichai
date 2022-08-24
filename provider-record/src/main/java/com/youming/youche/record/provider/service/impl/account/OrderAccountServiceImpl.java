package com.youming.youche.record.provider.service.impl.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.record.api.account.IOrderAccountService;
import com.youming.youche.record.api.user.IUserDataInfoRecordService;
import com.youming.youche.record.domain.account.OrderAccount;
import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.provider.mapper.account.OrderAccountMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 订单账户表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-24
 */
@DubboService(version = "1.0.0")
public class OrderAccountServiceImpl extends BaseServiceImpl<OrderAccountMapper, OrderAccount> implements IOrderAccountService {

    @Resource
    IUserDataInfoRecordService iUserDataInfoRecordService;

    @Override
    public List<OrderAccount> getOrderAccount(Long userId, Long tenantId, Integer userType) {
        if (userId == null) {
            throw new BusinessException("用户id不能为空");
        } else if (tenantId == null) {
            throw new BusinessException("车队id不能为空");
        }
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq(tenantId, "tenant_id");
        if (userType > 0) {
            queryWrapper.eq("user_type", userType);
        }
        List<OrderAccount> list = baseMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public OrderAccount queryOrderAccount(long userId, String vehicleAffiliation, long tenantId, long sourceTenantId, String oilAffiliation, Integer userType) throws BusinessException {
        if (userId <= 0L) {
            throw new BusinessException("用户id有误");
        }
        if (sourceTenantId <= 0L) {
            throw new BusinessException("请输入资金来源租户id");
        }
        if (StringUtils.isBlank(vehicleAffiliation)) {
            throw new BusinessException("请输入资金渠道");
        }
        if (StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("请输入油资金渠道");
        }
        if (userType == null || userType <= 0) {
            throw new BusinessException("请输入用户类型");
        }
        // 通过userid获取用户信息
        UserDataInfo user = iUserDataInfoRecordService.getUserDataInfo(userId);
        if (user == null) {
            throw new BusinessException("没有找用户信息!");
        }
        QueryWrapper<OrderAccount> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("user_type",userType);
        queryWrapper.eq("vehicle_Affiliation",vehicleAffiliation);
        queryWrapper.eq("source_Tenant_Id",sourceTenantId);
        queryWrapper.eq("oil_Affiliation",oilAffiliation);
        OrderAccount orderAccount =baseMapper.selectOne(queryWrapper);
        if (orderAccount == null) {
            OrderAccount newOrderAccount = new OrderAccount();
            newOrderAccount.setUserId(userId);
            //会员体系改造开始
            newOrderAccount.setUserType(userType);
            //会员体系改造结束
            newOrderAccount.setVehicleAffiliation(vehicleAffiliation);
            newOrderAccount.setAccState(1);//有效
            newOrderAccount.setCreateDate(LocalDateTime.now());
            newOrderAccount.setCreateTime(LocalDateTime.now());
            newOrderAccount.setAccLevel(1);
            newOrderAccount.setTenantId(user.getTenantId());
            newOrderAccount.setSourceTenantId(sourceTenantId);
            newOrderAccount.setOilAffiliation(oilAffiliation);
            this.saveOrUpdate(newOrderAccount);
            return newOrderAccount;
        } else {
            return orderAccount;
        }
    }






}
