package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IBillAccountTenantRelService;
import com.youming.youche.order.api.order.IBillAgreementService;
import com.youming.youche.order.api.order.IBillInfoReceiveRelService;
import com.youming.youche.order.api.order.IBillInfoService;
import com.youming.youche.order.api.order.IBillReceiveService;
import com.youming.youche.order.api.order.IBillSettingService;
import com.youming.youche.order.domain.order.BillAccountTenantRel;
import com.youming.youche.order.domain.order.BillAgreement;
import com.youming.youche.order.domain.order.BillInfo;
import com.youming.youche.order.domain.order.BillInfoReceiveRel;
import com.youming.youche.order.domain.order.BillReceive;
import com.youming.youche.order.domain.order.BillSetting;
import com.youming.youche.order.provider.mapper.order.BillInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


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
public class BillInfoServiceImpl extends BaseServiceImpl<BillInfoMapper, BillInfo> implements IBillInfoService {

    @Resource
    IBillAccountTenantRelService billAccountTenantRelService;

    @Resource
    IBillInfoService billInfoService;

    @Resource
    IBillInfoReceiveRelService billInfoReceiveRelService;

    @Resource
    IBillReceiveService billReceiveService;

    @Resource
    IBillSettingService billSettingService;

    @Resource
    IBillAgreementService billAgreementService;

    @Override
    public BillInfo getBillInfoByLookUp(String lookUp) {
        LambdaQueryWrapper<BillInfo> lambda=new QueryWrapper<BillInfo>().lambda();
        lambda.eq(BillInfo::getBillLookUp,lookUp);
        return this.getOne(lambda);
    }

    @Override
    public BillInfoReceiveRel getDefaultBillInfoByTenantId(Long tenantId) {
        BillAccountTenantRel billAccountTenantRel = null;
        QueryWrapper<BillAccountTenantRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id",tenantId);
        queryWrapper.eq("default_flag",true).orderByDesc("id");
        List<BillAccountTenantRel> billAccountTenantRelList = billAccountTenantRelService.list(queryWrapper);
        if(billAccountTenantRelList != null && billAccountTenantRelList.size()>0){
            billAccountTenantRel = billAccountTenantRelList.get(0);
        }
        if(billAccountTenantRel != null) {
          return getBillInfoReceiveRel(billAccountTenantRel);
        }
        return null;
    }

    @Override
    public boolean completeness(BillInfo billInfo) {
        return StringUtils.isNotBlank(billInfo.getTaxpayerNumber())
                && StringUtils.isNotBlank(billInfo.getBankName())
                && StringUtils.isNotBlank(billInfo.getAcctNo())
                && StringUtils.isNotBlank(billInfo.getLinkPhone())
                && StringUtils.isNotBlank(billInfo.getLinkMan())
                && StringUtils.isNotBlank(billInfo.getCompanyAddress())
                && StringUtils.isNotBlank(billInfo.getBusinessLicense())
                && StringUtils.isNotBlank(billInfo.getOperationCertificate());
    }

    private BillInfoReceiveRel getBillInfoReceiveRel(BillAccountTenantRel billAccountTenantRel) {
        BillInfoReceiveRel billInfoReceiveRel = null;
        if (null == billAccountTenantRel.getBillInfoReceiveRel()) {
            billInfoReceiveRel  = createOrSelect(billAccountTenantRel.getAcctName(), billAccountTenantRel.getTenantId());
            billAccountTenantRel.setBillInfoReceiveRel(billInfoReceiveRel.getRelId());
            billAccountTenantRelService.updateById(billAccountTenantRel);
        } else {
            billInfoReceiveRel = billInfoReceiveRelService.getById(billAccountTenantRel.getBillInfoReceiveRel());
        }

        BillInfo billInfo = billInfoService.getById(billInfoReceiveRel.getBillInfoId());
        BillReceive billReceive = billReceiveService.getById(billInfoReceiveRel.getReceiveId());
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
    private BillInfoReceiveRel createOrSelect(String lookUp, Long tenantId) {
        BillInfoReceiveRel billInfoReceiveRel = null;

        //查找是否有同名的开票信息，如果存在，直接关联
        BillInfo billInfo = billInfoService.getBillInfoByLookUp(lookUp);
        if (null == billInfo) {
            billInfo = new BillInfo();
            billInfo.setBillLookUp(lookUp);
            billInfoService.save(billInfo);
        } else {
            //一个车队的同名公户，关联到相同的票据&接收地址
            QueryWrapper<BillInfoReceiveRel> billInfoReceiveRelQueryWrapper = new QueryWrapper<>();
            billInfoReceiveRelQueryWrapper.eq("billInfo_id",billInfo.getId());
            billInfoReceiveRelQueryWrapper.eq("tenant_id",tenantId);
            List<BillInfoReceiveRel> billInfoReceiveRels = billInfoReceiveRelService.list(billInfoReceiveRelQueryWrapper);
            if(billInfoReceiveRels != null && billInfoReceiveRels.size()>0){
                billInfoReceiveRel = billInfoReceiveRels.get(0);
            }
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
