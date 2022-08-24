package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.*;
import com.youming.youche.order.domain.order.*;
import com.youming.youche.order.provider.mapper.order.BillAccountTenantRelMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务实现类O
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
public class BillAccountTenantRelServiceImpl extends BaseServiceImpl<BillAccountTenantRelMapper, BillAccountTenantRel> implements IBillAccountTenantRelService {
    @Autowired
    private IBillInfoService billInfoService;
    @Autowired
    private IBillReceiveService billReceiveService;
    @Autowired
    private IBillInfoReceiveRelService billInfoReceiveRelService;
    @Autowired
    private IBillSettingService billSettingService;
    @Autowired
    private IBillAgreementService billAgreementService;




    @Override
    public BillInfoReceiveRel getDefaultBillInfoByTenantId(Long tenantId) {
        if (null == tenantId) {
            return null;
        }
        BillAccountTenantRel billAccountTenantRel = getDefaultBillAccount(tenantId);
        if (null == billAccountTenantRel) {
            return null;
        }
        return getBillInfoReceiveRel(billAccountTenantRel);
    }

    @Override
    public BillAccountTenantRel getDefaultBilltAccountByTenantId(Long tenantId) {
        if (null == tenantId) {
            return null;
        }
        return getDefaultBillAccount(tenantId);
    }

    @Override
    public BillAccountTenantRel getBillAccountTenantRelByRelId(Long relId) {
        LambdaQueryWrapper<BillAccountTenantRel> lambda=new QueryWrapper<BillAccountTenantRel>().lambda();
        lambda.eq(BillAccountTenantRel::getId,relId);
        return this.getOne(lambda);
    }

    @Override
    public BillAccountTenantRel getDefaultBillAccount(Long tenantId) {
        LambdaQueryWrapper<BillAccountTenantRel> lambda = new QueryWrapper<BillAccountTenantRel>().lambda();
        lambda.eq(BillAccountTenantRel::getTenantId, tenantId)
                .eq(BillAccountTenantRel::getDefaultFlag, true);
        return this.getOne(lambda);
    }
    @Override
    public BillInfoReceiveRel getBillInfoReceiveRel(BillAccountTenantRel billAccountTenantRel) {
        BillInfoReceiveRel billInfoReceiveRel = null;
        if (null == billAccountTenantRel.getBillInfoReceiveRel()) {
            billInfoReceiveRel = createOrSelect(billAccountTenantRel.getAcctName(), billAccountTenantRel.getTenantId());
            billAccountTenantRel.setBillInfoReceiveRel(billInfoReceiveRel.getRelId());
            this.updateById(billAccountTenantRel);
        } else {
            billInfoReceiveRel = billInfoReceiveRelService.getBillInfoReceiveRelByRelId(billAccountTenantRel.getBillInfoReceiveRel());
        }

        BillInfo billInfo = billInfoService.getById(billInfoReceiveRel.getBillInfoId());
        BillReceive billReceive =billReceiveService.getBillReceiveById(billInfoReceiveRel.getReceiveId());
        BillSetting billSetting = billSettingService.getBillSettingByTenantId(billAccountTenantRel.getTenantId());
        BillAgreement billAgreement = billAgreementService.createOrSelectBillAgreement(billSetting.getBillMethod(), billInfo.getId());

        billInfo.setBillAgreement(billAgreement);
        billInfoReceiveRel.setBillInfo(billInfo);
        billInfoReceiveRel.setBillReceive(billReceive);

        return billInfoReceiveRel;
    }


    /**
     * 为一个开票信息获取一条关联关系
     */
    public BillInfoReceiveRel createOrSelect(String lookUp, Long tenantId) {
        BillInfoReceiveRel billInfoReceiveRel = null;

        //查找是否有同名的开票信息，如果存在，直接关联
        BillInfo billInfo = billInfoService.getBillInfoByLookUp(lookUp);
        if (null == billInfo) {
            billInfo = new BillInfo();
            billInfo.setBillLookUp(lookUp);
            billInfoService.save(billInfo);
        } else {
            //一个车队的同名公户，关联到相同的票据&接收地址
            billInfoReceiveRel = billInfoReceiveRelService.getBillInfoReceiveRelByBillInfoId(billInfo.getId(), tenantId);
        }

        //此开票信息在当前租户没有 票据&接收地址 关系
        if (null == billInfoReceiveRel) {
            BillReceive billReceive = new BillReceive();
            billReceive.setTenantId(tenantId);
            billReceiveService.save(billReceive);

            billInfoReceiveRel = new BillInfoReceiveRel();
            billInfoReceiveRel.setBillInfoId(billInfo.getId());
            billInfoReceiveRel.setTenantId(tenantId);
            billInfoReceiveRel.setReceiveId(billReceive.getId());
            billInfoReceiveRel.setDeduction(false);
            billInfoReceiveRelService.save(billInfoReceiveRel);
        }

        billInfoReceiveRel.setBillInfo(billInfo);
        return billInfoReceiveRel;
    }


}
