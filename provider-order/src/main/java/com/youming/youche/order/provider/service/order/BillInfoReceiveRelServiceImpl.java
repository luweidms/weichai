package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.order.api.order.IBillAccountTenantRelService;
import com.youming.youche.order.api.order.IBillInfoReceiveRelService;
import com.youming.youche.order.domain.order.BillAccountTenantRel;
import com.youming.youche.order.domain.order.BillInfoReceiveRel;
import com.youming.youche.order.provider.mapper.order.BillInfoReceiveRelMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
public class BillInfoReceiveRelServiceImpl extends ServiceImpl<BillInfoReceiveRelMapper, BillInfoReceiveRel> implements IBillInfoReceiveRelService {
    @Autowired
    private IBillAccountTenantRelService billAccountTenantRelService;

    @Resource
    LoginUtils loginUtils;

    @Override
    public BillInfoReceiveRel getDefaultBillInfoByTenantId(Long tenantId) {
        if (null == tenantId) {
            return null;
        }
        BillAccountTenantRel billAccountTenantRel = billAccountTenantRelService.getDefaultBillAccount(tenantId);
        if (null == billAccountTenantRel) {
            return null;
        }
        return billAccountTenantRelService.getBillInfoReceiveRel(billAccountTenantRel);
    }

    @Override
    public BillInfoReceiveRel getBillInfoReceiveRelByBillInfoId(Long billInfoId, Long tenantId) {
        LambdaQueryWrapper<BillInfoReceiveRel> lambda=new QueryWrapper<BillInfoReceiveRel>().lambda();
        lambda.eq(BillInfoReceiveRel::getBillInfoId,billInfoId)
                .eq(BillInfoReceiveRel::getTenantId,tenantId);
        return this.getOne(lambda);
    }

    @Override
    public BillInfoReceiveRel getBillInfoReceiveRelByRelId(Long relId) {
        LambdaQueryWrapper<BillInfoReceiveRel> lambda=new QueryWrapper<BillInfoReceiveRel>().lambda();
        lambda.eq(BillInfoReceiveRel::getRelId,relId);
        return this.getOne(lambda);
    }

    @Override
    public BillInfoReceiveRel getDefaultBillInfo(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        return this.getDefaultBillInfoByTenantId(tenantId);
    }
}
