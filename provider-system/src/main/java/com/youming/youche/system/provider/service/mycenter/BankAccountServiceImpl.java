package com.youming.youche.system.provider.service.mycenter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.cloud.api.cmb.ICmbTransferService;
import com.youming.youche.cloud.constant.CmbIntfConst;
import com.youming.youche.cloud.dto.cmb.CpnInfoReqDto;
import com.youming.youche.cloud.dto.cmb.MbrBalQryRepDto;
import com.youming.youche.cloud.dto.cmb.MbrBalQryReqDto;
import com.youming.youche.cloud.dto.cmb.MerchRegCallBackDto;
import com.youming.youche.cloud.dto.cmb.MerchRegQryRepDto;
import com.youming.youche.cloud.dto.cmb.MerchRegQryReqDto;
import com.youming.youche.cloud.dto.cmb.MerchRegReqDto;
import com.youming.youche.cloud.dto.cmb.NtsResultDto;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.system.api.mycenter.IBankAccountService;
import com.youming.youche.system.api.mycenter.IBankReqRecordService;
import com.youming.youche.system.domain.mycenter.CmbBankAccountInfo;
import com.youming.youche.system.dto.mycenter.AccountBalanceDto;
import com.youming.youche.system.dto.mycenter.BankAccountListDto;
import com.youming.youche.system.provider.mapper.mycenter.BankAccountMapper;
import com.youming.youche.system.vo.mycenter.AccountBalanceVo;
import com.youming.youche.system.vo.mycenter.CreatePrivateAccountVo;
import com.youming.youche.system.vo.mycenter.CreatePublicAccountVo;
import com.youming.youche.system.vo.mycenter.UpdateBankAccountVo;
import com.youming.youche.util.BeanMapUtils;
import com.youming.youche.util.JsonHelper;
import com.youming.youche.util.PKUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * @ClassName CmbBankAccountInfoServiceImpl
 * @Description ????????????
 * @Author zag
 * @Date 2022/1/25 17:57
 */
@DubboService(version = "1.0.0")
public class BankAccountServiceImpl extends BaseServiceImpl<BankAccountMapper, CmbBankAccountInfo> implements IBankAccountService {

    private static final Logger log = LoggerFactory.getLogger(BankAccountServiceImpl.class);

    @DubboReference(version = "1.0.0")
    ICmbTransferService cmbTransferService;

    @Autowired
    IBankReqRecordService bankReqRecordService;

    /** ????????????????????????
     * */
    @Override
    public List<BankAccountListDto> getUserAccList(Long userId, String accType) {
        String certType = "";
        if (!StringUtils.isBlank(accType)) {
            certType = accType.equals("0") ? CmbIntfConst.CertType.P01 : CmbIntfConst.CertType.C35;//0:?????????1:??????
        }
        return baseMapper.selectUserAccList(userId, certType);
    }

    /** ????????????????????????
     * */
    @Override
    public List<BankAccountListDto> getTenantAccList(Long tenantId, String accType) {
        String certType = "";
        if (!StringUtils.isBlank(accType)) {
            certType = accType.equals("0") ? CmbIntfConst.CertType.P01 : CmbIntfConst.CertType.C35;//0:?????????1:??????
        }
        return baseMapper.selectTenantAccList(tenantId, certType);
    }

    /** ??????mbrNo??????????????????
     * */
    @Override
    public CmbBankAccountInfo getByMbrNo(String mbrNo){
        if(StringUtils.isBlank(mbrNo)){
            return null;
        }
        LambdaQueryWrapper<CmbBankAccountInfo> queryWrapper=Wrappers.lambdaQuery();
        queryWrapper.eq(CmbBankAccountInfo::getMbrNo,mbrNo);
        return baseMapper.selectOne(queryWrapper);
    }

    /** ??????mbrNo??????????????????
     * */
    @Override
    public CmbBankAccountInfo getByMerchNo(String merchNo){
        if(StringUtils.isBlank(merchNo)){
            return null;
        }
        LambdaQueryWrapper<CmbBankAccountInfo> queryWrapper=Wrappers.lambdaQuery();
        queryWrapper.eq(CmbBankAccountInfo::getMerchNo, merchNo);
        return baseMapper.selectOne(queryWrapper);
    }

    /** ????????????
     * */
    private void accountReg(CmbBankAccountInfo cmbBankAccountInfo) {
        MerchRegReqDto merchRegReq = convert2MerchRegReqDto(cmbBankAccountInfo);
        NtsResultDto result = null;
        try {
            log.info("[CMB][????????????]????????????????????????????????????" + merchRegReq.getCertName());
            result = cmbTransferService.merchReg(merchRegReq);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMB][????????????]?????????????????????" + e.getMessage());
            throw new BusinessException("??????????????????");
        }
        if (result != null) {
            bankReqRecordService.addRecord(merchRegReq.getReqNo(), CmbIntfConst.TranFunc.MERCHREG, JsonHelper.toJson(merchRegReq),
                    null,
                    result.getCode(),
                    result.getMsg(),
                    JsonHelper.toJson(result));
            if (result.isSuccess()) {
                log.info("[CMB][????????????]????????????????????????");
                cmbBankAccountInfo.setStatus("I");
                cmbBankAccountInfo.setAvaBal("0.00");
                int rows = baseMapper.insert(cmbBankAccountInfo);
                if (rows > 0) {
                    log.info("[CMB][????????????]????????????????????????");
                }else {
                    log.error("[CMB][????????????]????????????????????????");
                }
            } else {
                String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                log.error("[CMB][????????????]?????????????????????" + errorMsg);
                throw new BusinessException(errorMsg);
            }
        }else {
            log.error("[CMB][????????????]??????????????????");
            throw new BusinessException("??????????????????");
        }
    }

    /** ????????????
     * */
    @Override
    public void publicAccReg(CreatePublicAccountVo createPublicAccountVo) {
        log.info("[CMB][????????????][??????]start");
        if(existsCertNo(createPublicAccountVo.getCertNo())){
            throw new BusinessException("???????????????????????????");
        }
        CmbBankAccountInfo cmbBankAccountInfo = new CmbBankAccountInfo();
        BeanUtils.copyProperties(createPublicAccountVo, cmbBankAccountInfo);
        accountReg(cmbBankAccountInfo);
        log.info("[CMB][????????????][??????]end");
    }

    /** ????????????
     * */
    @Override
    public void privceAccReg(CreatePrivateAccountVo createPrivateAccountVo) {
        log.info("[CMB][????????????][??????]sstart");
        if(existsCertNo(createPrivateAccountVo.getCertNo())){
            throw new BusinessException("????????????????????????");
        }
        CmbBankAccountInfo cmbBankAccountInfo = new CmbBankAccountInfo();
        BeanUtils.copyProperties(createPrivateAccountVo, cmbBankAccountInfo);
        accountReg(cmbBankAccountInfo);
        log.info("[CMB][????????????][??????]end");
    }

    /** ??????????????????????????????
     * */
    private boolean existsCertNo(String certNo) {
        LambdaQueryWrapper<CmbBankAccountInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(CmbBankAccountInfo::getCertNo, certNo)
                .ne(CmbBankAccountInfo::getStatus,CmbIntfConst.MerchRegStatus.F);
        int count = baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            return true;
        }
        return false;
    }

    /** ?????????????????????
     * */
    public String getFullPath(String path) {
        String fullUrl = null;
        try {
            FastDFSHelper client = FastDFSHelper.getInstance();
            fullUrl = client.getHttpURL(path);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
        return fullUrl;
    }

    private MerchRegReqDto convert2MerchRegReqDto(CmbBankAccountInfo cmbBankAccountInfo) {
        MerchRegReqDto merchRegReq = new MerchRegReqDto();
        merchRegReq.setReqNo("WC" + PKUtil.getPK());
        merchRegReq.setPlatformNo(cmbTransferService.getPlatformNo());
        cmbBankAccountInfo.setPlatformNo(merchRegReq.getPlatformNo());
        if (cmbBankAccountInfo.getCertType().equals(CmbIntfConst.CertType.C35)) {//??????
            merchRegReq.setCertNo(cmbBankAccountInfo.getCertNo());
            merchRegReq.setCertName(cmbBankAccountInfo.getCertName());
            merchRegReq.setCertType(cmbBankAccountInfo.getCertType());
            merchRegReq.setIndiFlag(cmbBankAccountInfo.getIndiFlag());
         //   merchRegReq.setCertFrontPhoto(getFullPath(cmbBankAccountInfo.getCertFrontPhotoUrl()));
            merchRegReq.setCertFrontPhoto(cmbBankAccountInfo.getCertFrontPhotoUrl());
            merchRegReq.setCertStartDate(cmbBankAccountInfo.getCertStartDate());
            merchRegReq.setCertEndDate(cmbBankAccountInfo.getCertEndDate());
            merchRegReq.setMgrMobile(cmbBankAccountInfo.getMgrMobile());
            merchRegReq.setMgrIdentity(cmbBankAccountInfo.getMgrIdentity());
            CpnInfoReqDto cpnInfoReq = new CpnInfoReqDto();
            cpnInfoReq.setChgrNoType(CmbIntfConst.CertType.P01);
            cmbBankAccountInfo.setChgrNoType(CmbIntfConst.CertType.P01);
            cpnInfoReq.setChgrName(cmbBankAccountInfo.getChgrName());
            cpnInfoReq.setChgrMobile(cmbBankAccountInfo.getChgrMobile());
            cpnInfoReq.setChgrFrontPhoto(cmbBankAccountInfo.getChgrFrontPhotoUrl());
            cpnInfoReq.setChgrBackPhoto(cmbBankAccountInfo.getChgrBackPhotoUrl());
            cpnInfoReq.setChgrNo(cmbBankAccountInfo.getChgrNo());
            cpnInfoReq.setChgrStartDate(cmbBankAccountInfo.getChgrStartDate());
            cpnInfoReq.setChgrEndDate(cmbBankAccountInfo.getChgrEndDate());
            cpnInfoReq.setChgrAddr(cmbBankAccountInfo.getChgrAddr());
            merchRegReq.setCpnInfo(cpnInfoReq);
        } else if (cmbBankAccountInfo.getCertType().equals(CmbIntfConst.CertType.P01)) {//??????
            merchRegReq.setCertNo(cmbBankAccountInfo.getCertNo());
            merchRegReq.setCertName(cmbBankAccountInfo.getCertName());
            merchRegReq.setCertType(cmbBankAccountInfo.getCertType());
            merchRegReq.setCertFrontPhoto(cmbBankAccountInfo.getCertFrontPhotoUrl());
            merchRegReq.setCertBackPhoto(cmbBankAccountInfo.getCertBackPhotoUrl());
            merchRegReq.setCertStartDate(cmbBankAccountInfo.getCertStartDate());
            merchRegReq.setCertEndDate(cmbBankAccountInfo.getCertEndDate());
            merchRegReq.setMgrMobile(cmbBankAccountInfo.getMgrMobile());
            merchRegReq.setMgrIdentity(cmbBankAccountInfo.getMgrIdentity());
        }

        merchRegReq.setOpnMode("F");
        merchRegReq.setFromMbrNo(cmbBankAccountInfo.getCertNo());
        merchRegReq.setFromMbrName(cmbBankAccountInfo.getCertName());
        cmbBankAccountInfo.setOpnMode("F");
        cmbBankAccountInfo.setFromMbrNo(cmbBankAccountInfo.getCertNo());
        cmbBankAccountInfo.setFromMbrName(cmbBankAccountInfo.getCertName());
        return merchRegReq;
    }

    /** ?????????????????????
     * */
    @Override
    public void mgrInfoChg(UpdateBankAccountVo updateBankAccountVo) {

    }

    /** ??????????????????
     * */
    @Override
    public String getBalance(String mbrNo) {
        log.info("[CMB][??????????????????]start");
        MbrBalQryReqDto mbrBalQryReq = new MbrBalQryReqDto();
        mbrBalQryReq.setPlatformNo(cmbTransferService.getPlatformNo());
        mbrBalQryReq.setMbrNo(mbrNo);
        String balance = "0.00";
        NtsResultDto<MbrBalQryRepDto> result = null;
        try {
            log.info("[CMB][??????????????????]?????????????????????????????????:" + mbrNo);
            result = cmbTransferService.mbrBalQry(mbrBalQryReq);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMB][??????????????????]?????????????????????" + e.getMessage());
            throw new BusinessException("???????????????????????????????????????");
        }
        if (result != null) {
            MbrBalQryRepDto mbrBalQryRep = result.getData();
            bankReqRecordService.addRecord(null, CmbIntfConst.TranFunc.MBRBALQRY, JsonHelper.toJson(mbrBalQryReq),
                    mbrBalQryRep == null ? null : mbrBalQryRep.getRespNo(),
                    result.getCode(),
                    result.getMsg(),
                    JsonHelper.toJson(result));
            if (result.isSuccess()) {
                balance = mbrBalQryRep.getAvaBal();
                log.info("[CMB][??????????????????]????????????:" + balance);
                LambdaQueryWrapper<CmbBankAccountInfo> queryWrapper = Wrappers.lambdaQuery();
                queryWrapper.eq(CmbBankAccountInfo::getMbrNo, mbrNo);
                CmbBankAccountInfo bankAccountInfo = baseMapper.selectOne(queryWrapper);
                BigDecimal oldBal = new BigDecimal(bankAccountInfo.getAvaBal());
                BigDecimal newBal = new BigDecimal(balance);
                if (newBal.compareTo(oldBal) != 0) {
                    bankAccountInfo.setAvaBal(balance);
                    baseMapper.updateById(bankAccountInfo);
                    log.info("[CMB][??????????????????]?????????????????????????????????" + oldBal + ",????????????" + newBal);
                } else {
                    log.info("[CMB][??????????????????]????????????????????????????????????????????????" + oldBal + ",????????????" + newBal);
                }
            } else {
                String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                log.error("[CMB][??????????????????]?????????????????????" + errorMsg);
                throw new BusinessException(errorMsg);
            }
        } else {
            log.error("[CMB][??????????????????]??????????????????");
            throw new BusinessException("????????????????????????");
        }
        log.info("[CMB][??????????????????]end");
        return balance;
    }

    /** ????????????????????????
     * */
    @Override
    public AccountBalanceVo getUserAccBalance(Long userId) {
        List<AccountBalanceDto> list = baseMapper.getUserAccBalance(userId);
        return getAccBalance(list);
    }

    /** ????????????????????????
     * */
    @Override
    public AccountBalanceVo getTenantAccBalance(Long tenantId) {
        List<AccountBalanceDto> list = baseMapper.getTenantAccBalance(tenantId);
        return getAccBalance(list);
    }

    /** ?????????????????????
     * */
    private AccountBalanceVo getAccBalance(List<AccountBalanceDto> list) {
        BigDecimal publicAccBalance = new BigDecimal(0.00);
        BigDecimal privateAccBalance = new BigDecimal(0.00);
        BigDecimal totalAccBalance = new BigDecimal(0.00);
        for (AccountBalanceDto item : list) {
            if (item.getCertType().equals("C35")) {
                publicAccBalance = item.getBalance();
            } else if (item.getCertType().equals("P01")) {
                privateAccBalance = item.getBalance();
            }
            totalAccBalance = totalAccBalance.add(item.getBalance());
        }
        AccountBalanceVo accountBalanceVo = new AccountBalanceVo();
        accountBalanceVo.setPublicAccBalance(publicAccBalance.setScale(2,BigDecimal.ROUND_HALF_DOWN).toString());
        accountBalanceVo.setPrivateAccBalance(privateAccBalance.setScale(2,BigDecimal.ROUND_HALF_DOWN).toString());
        accountBalanceVo.setTotalAccBalance(totalAccBalance.setScale(2,BigDecimal.ROUND_HALF_DOWN).toString());
        return accountBalanceVo;
    }

    /**
     * ??????????????????????????????
     * @author zag
     * @date 2022/3/25 15:50
     * @return void
     */
    @Override
    public void syncBnkAccRegResult() {
        log.info("[CMBTask][????????????????????????]start");
        LambdaQueryWrapper<CmbBankAccountInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(CmbBankAccountInfo::getStatus, CmbIntfConst.MerchRegStatus.I);
        List<CmbBankAccountInfo> list = baseMapper.selectList(queryWrapper);

        MerchRegQryReqDto merchRegQryReq = new MerchRegQryReqDto();
        merchRegQryReq.setPlatformNo(cmbTransferService.getPlatformNo());
        for (CmbBankAccountInfo bankAccountInfo : list) {
            merchRegQryReq.setCertNo(bankAccountInfo.getCertNo());
            merchRegQryReq.setCertType(bankAccountInfo.getCertType());
            NtsResultDto<MerchRegQryRepDto> result = null;
            try {
                log.info("[CMBTask][????????????????????????]????????????????????????????????????" + merchRegQryReq.getCertNo());
                result = cmbTransferService.merchRegQry(merchRegQryReq);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[CMBTask][????????????????????????]?????????????????????" + e.getMessage());
                continue;
                //throw new BusinessException("?????????????????????????????????????????????");
            }
            if (result != null) {
                if (result.isSuccess()) {
                    log.info("[CMBTask][????????????????????????]");
                    MerchRegQryRepDto merchRegQryRep = result.getData();
                    if (merchRegQryRep != null && !merchRegQryRep.getStatus().equals("I")) {
                        log.info("[CMBTask][????????????????????????]???????????????" + merchRegQryRep.getStatus());
                        bankReqRecordService.addRecord(null, CmbIntfConst.TranFunc.MERCHREGQRY, JsonHelper.toJson(merchRegQryReq),
                                merchRegQryRep.getRespNo(),
                                result.getCode(),
                                result.getMsg(),
                                JsonHelper.toJson(result));
                        bankAccountInfo.setStatus(merchRegQryRep.getStatus());
                        bankAccountInfo.setResult(merchRegQryRep.getResult());
                        bankAccountInfo.setMerchNo(merchRegQryRep.getMerchNo());
                        bankAccountInfo.setMbrNo(merchRegQryRep.getMbrNo());
                        int rows = baseMapper.updateById(bankAccountInfo);
                        if (rows > 0) {
                            log.info("[CMBTask][????????????????????????]??????????????????????????????");
                        } else {
                            log.error("[CMBTask][????????????????????????]??????????????????????????????");
                        }
                    }
                } else {
                    String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                    log.error("[CMBTask][????????????????????????]?????????????????????" + errorMsg);
                    continue;
                    //throw new BusinessException(errorMsg);
                }
            }else {
                log.error("[CMBTask][????????????????????????]??????????????????");
                throw new BusinessException("?????????????????????????????????????????????");
            }
        }
        log.info("[CMBTask][????????????????????????]end");
    }

    /**
     * ????????????????????????
     * @author zag
     * @date 2022/3/25 16:41
     * @return void
     */
    @Override
    public void syncBnkAccBalance(){
        log.info("[CMBTask][????????????????????????]start");
        LambdaQueryWrapper<CmbBankAccountInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(CmbBankAccountInfo::getStatus, CmbIntfConst.MerchRegStatus.S);
        List<CmbBankAccountInfo> list = baseMapper.selectList(queryWrapper);
        MbrBalQryReqDto mbrBalQryReq=new MbrBalQryReqDto();
        mbrBalQryReq.setPlatformNo(cmbTransferService.getPlatformNo());
        for (CmbBankAccountInfo bankAccountInfo : list) {
            mbrBalQryReq.setMbrNo(bankAccountInfo.getMbrNo());
            NtsResultDto<MbrBalQryRepDto> result=null;
            try {
                log.info("[CMBTask][????????????????????????]?????????????????????????????????????????????" + bankAccountInfo.getMbrNo());
                result = cmbTransferService.mbrBalQry(mbrBalQryReq);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[CMBTask][????????????????????????]?????????????????????" + e.getMessage());
                continue;
                //throw new BusinessException("????????????????????????");
            }
            if(result!=null){
                if(result.isSuccess()){
                    MbrBalQryRepDto mbrBalQryRep= result.getData();
                    BigDecimal oldBal=new BigDecimal(bankAccountInfo.getAvaBal());
                    BigDecimal newBal=new BigDecimal(mbrBalQryRep.getAvaBal());
                    if(newBal.compareTo(oldBal) !=0) {
                        bankReqRecordService.addRecord(null, CmbIntfConst.TranFunc.MBRBALQRY, JsonHelper.toJson(mbrBalQryRep),
                                mbrBalQryRep.getRespNo(),
                                result.getCode(),
                                result.getMsg(),
                                JsonHelper.toJson(result));
                        bankAccountInfo.setAvaBal(mbrBalQryRep.getAvaBal());
                        int rows = baseMapper.updateById(bankAccountInfo);
                        if (rows > 0) {
                            log.info("[CMBTask][????????????????????????]?????????????????????????????????" + oldBal + ",????????????" + newBal);
                        } else {
                            log.error("[CMBTask][????????????????????????]??????????????????");
                        }
                    }
                }else {
                    String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                    log.error("[CMBTask][????????????????????????]?????????????????????" + errorMsg);
                    continue;
                    //throw new BusinessException(errorMsg);
                }
            }else {
                log.error("[CMBTask][????????????????????????]??????????????????");
                throw new BusinessException("?????????????????????????????????????????????");
            }
        }
        log.info("[CMBTask][????????????????????????]end");
    }

    /**
     * ????????????????????????
     * @author zag
     * @date 2022/4/1 16:42
     * @param param 
     * @return void
     */
    @Override
    public void merchRegCallBack(Map<String, Object> param) {
        log.info("[CMBCallBack][??????????????????]start");
        MerchRegCallBackDto merchRegCallBack = null;
        try {
            merchRegCallBack = BeanMapUtils.mapToBean(param, MerchRegCallBackDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMBCallBack][??????????????????]?????????????????????" + e.getMessage());
            throw new BusinessException("??????????????????");
        }
        LambdaQueryWrapper<CmbBankAccountInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(CmbBankAccountInfo::getCertNo, merchRegCallBack.getCertNo())
                .eq(CmbBankAccountInfo::getStatus, CmbIntfConst.MerchRegStatus.I);
        CmbBankAccountInfo bankAccountInfo = baseMapper.selectOne(queryWrapper);
        if (bankAccountInfo != null) {
            log.info("[CMBCallBack][??????????????????]???????????????" + merchRegCallBack.getCertNo());
            bankReqRecordService.addRecord(merchRegCallBack.getOrigReqNo(), CmbIntfConst.TranFunc.MERCHREG_CALLBACK, JsonHelper.toJson(merchRegCallBack),
                    null,
                    "200",
                    "??????????????????",
                    null);
            bankAccountInfo.setStatus(merchRegCallBack.getStatus());
            bankAccountInfo.setResult(merchRegCallBack.getResult());
            bankAccountInfo.setMerchNo(merchRegCallBack.getMerchNo());
            bankAccountInfo.setMbrNo(merchRegCallBack.getMbrNo());
            int rows = baseMapper.updateById(bankAccountInfo);
            if (rows > 0) {
                log.info("[CMBCallBack][??????????????????]???????????????????????????????????????");
            } else {
                log.error("[CMBCallBack][??????????????????]???????????????????????????????????????");
            }
        } else {
            log.info("[CMBCallBack][??????????????????]?????????????????????????????????" + merchRegCallBack.getCertNo());
        }
        log.info("[CMBCallBack][??????????????????]end");
    }

    @Override
    public List<WorkbenchDto> getTableFinancialPlatformSurplusAmount() {
        return baseMapper.getTableFinancialPlatformSurplusAmount();
    }

    @Override
    public List<WorkbenchDto> getTableFinacialPlatformUsedAmount() {
        return baseMapper.getTableFinacialPlatformUsedAmount();
    }

    @Override
    public List<WorkbenchDto> getTableFinacialRechargeTodayAmount() {
        return baseMapper.getTableFinacialRechargeTodayAmount();
    }

}
