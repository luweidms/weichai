package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.market.api.facilitator.*;
import com.youming.youche.market.domain.facilitator.AccountBankRel;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.facilitator.ServiceProductEtc;
import com.youming.youche.market.domain.facilitator.ServiceProductEtcVer;
import com.youming.youche.market.dto.facilitator.EtcProductDetailDto;
import com.youming.youche.market.provider.mapper.facilitator.ServiceProductEtcMapper;
import com.youming.youche.market.provider.utis.ReadisUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-17
 */
@DubboService(version = "1.0.0")
@Service
public class ServiceProductEtcServiceImpl extends ServiceImpl<ServiceProductEtcMapper, ServiceProductEtc> implements IServiceProductEtcService {
    @Autowired
    private IServiceProductEtcVerService serviceProductEtcVerService;
    @Autowired
    private IServiceInfoService serviceInfoService;
    @Autowired
    private IAccountBankRelService accountBankRelService;
    @Autowired
    private ReadisUtil readisUtil;

    @Override
    public EtcProductDetailDto getEtcCardProductInfo(Long productId, Boolean isVer) {
        EtcProductDetailDto etcProductDetailOut=new EtcProductDetailDto();
        if(isVer) {
            ServiceProductEtcVer etcInfoVer=serviceProductEtcVerService.getServiceProductEtcVer(productId);
            if(etcInfoVer!=null) {
                BeanUtils.copyProperties(etcInfoVer,etcProductDetailOut);
            }
        }else {
            ServiceProductEtc etcInfo=this.getById(productId);
            if(etcInfo!=null) {
                BeanUtils.copyProperties(etcInfo,etcProductDetailOut);
            }
        }
        AccountBankRel accountBankRel=null;
        ServiceInfo serviceInfo=null;
        if(etcProductDetailOut.getNrServiceUserId()!=null&&etcProductDetailOut.getNrServiceUserId()>0) {
            serviceInfo=serviceInfoService.getServiceInfoByServiceUserId(etcProductDetailOut.getNrServiceUserId());
            etcProductDetailOut.setNrServiceName(serviceInfo.getServiceName());
        }
        if(etcProductDetailOut.getOrServiceUserId()!=null&&etcProductDetailOut.getOrServiceUserId()>0) {
            if(!etcProductDetailOut.getNrServiceUserId().equals(etcProductDetailOut.getOrServiceUserId())) {
                serviceInfo=serviceInfoService.getServiceInfoByServiceUserId(etcProductDetailOut.getOrServiceUserId());
            }
            etcProductDetailOut.setOrServiceName(serviceInfo.getServiceName());
        }

        if(etcProductDetailOut.getNrAccId()!=null&&etcProductDetailOut.getNrAccId()>0) {
            accountBankRel=accountBankRelService.getById(etcProductDetailOut.getNrAccId());
            if(accountBankRel!=null) {
                etcProductDetailOut.setNrAccName(accountBankRel.getAcctName());
                etcProductDetailOut.setNrAccNo(accountBankRel.getAcctNo());
                etcProductDetailOut.setNrAcctTypeName(accountBankRel.getBankType()!=null?
                        (EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1==accountBankRel.getBankType()?"对公账户":"对私账户"):null);
                etcProductDetailOut.setNrBankName(accountBankRel.getBankName());
                etcProductDetailOut.setNrBranchName(accountBankRel.getBranchName());
            }
        }

        if(etcProductDetailOut.getOrAccId()!=null&&etcProductDetailOut.getOrAccId()>0) {
            if(!etcProductDetailOut.getOrAccId().equals(etcProductDetailOut.getNrAccId())) {
                accountBankRel=accountBankRelService.getById(etcProductDetailOut.getOrAccId());
            }
            if(accountBankRel!=null) {
                etcProductDetailOut.setOrAccName(accountBankRel.getAcctName());
                etcProductDetailOut.setOrAccNo(accountBankRel.getAcctNo());
                etcProductDetailOut.setOrAcctTypeName(accountBankRel.getBankType()!=null?
                        (EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1==accountBankRel.getBankType()?"对公账户":"对私账户"):null);
                etcProductDetailOut.setOrBankName(accountBankRel.getBankName());
                etcProductDetailOut.setOrBranchName(accountBankRel.getBranchName());
            }
        }

        if(etcProductDetailOut.getPltSerFeeType()!=null&&etcProductDetailOut.getPltSerFeeType()>=0) {
            String codeName = readisUtil.getSysStaticData("ETC_PLATE_SERV_FEE_TYPE", String.valueOf(etcProductDetailOut.getPltSerFeeType())).getCodeName();
            etcProductDetailOut.setPltSerFeeTypeName(codeName);
        }

        return etcProductDetailOut;
    }
}
