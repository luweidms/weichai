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
 * @Description 添加描述
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

    /** 获取个人账户列表
     * */
    @Override
    public List<BankAccountListDto> getUserAccList(Long userId, String accType) {
        String certType = "";
        if (!StringUtils.isBlank(accType)) {
            certType = accType.equals("0") ? CmbIntfConst.CertType.P01 : CmbIntfConst.CertType.C35;//0:私户；1:公户
        }
        return baseMapper.selectUserAccList(userId, certType);
    }

    /** 获取车队账户列表
     * */
    @Override
    public List<BankAccountListDto> getTenantAccList(Long tenantId, String accType) {
        String certType = "";
        if (!StringUtils.isBlank(accType)) {
            certType = accType.equals("0") ? CmbIntfConst.CertType.P01 : CmbIntfConst.CertType.C35;//0:私户；1:公户
        }
        return baseMapper.selectTenantAccList(tenantId, certType);
    }

    /** 根据mbrNo获取账户信息
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

    /** 根据mbrNo获取账户信息
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

    /** 账户注册
     * */
    private void accountReg(CmbBankAccountInfo cmbBankAccountInfo) {
        MerchRegReqDto merchRegReq = convert2MerchRegReqDto(cmbBankAccountInfo);
        NtsResultDto result = null;
        try {
            log.info("[CMB][商户进件]发送接口请求，账户名称：" + merchRegReq.getCertName());
            result = cmbTransferService.merchReg(merchRegReq);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMB][商户进件]接口请求异常：" + e.getMessage());
            throw new BusinessException("账户注册失败");
        }
        if (result != null) {
            bankReqRecordService.addRecord(merchRegReq.getReqNo(), CmbIntfConst.TranFunc.MERCHREG, JsonHelper.toJson(merchRegReq),
                    null,
                    result.getCode(),
                    result.getMsg(),
                    JsonHelper.toJson(result));
            if (result.isSuccess()) {
                log.info("[CMB][商户进件]接口请求返回成功");
                cmbBankAccountInfo.setStatus("I");
                cmbBankAccountInfo.setAvaBal("0.00");
                int rows = baseMapper.insert(cmbBankAccountInfo);
                if (rows > 0) {
                    log.info("[CMB][商户进件]商户信息保存成功");
                }else {
                    log.error("[CMB][商户进件]商户信息保存失败");
                }
            } else {
                String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                log.error("[CMB][商户进件]接口返回失败：" + errorMsg);
                throw new BusinessException(errorMsg);
            }
        }else {
            log.error("[CMB][商户进件]接口请求失败");
            throw new BusinessException("账户注册失败");
        }
    }

    /** 公户注册
     * */
    @Override
    public void publicAccReg(CreatePublicAccountVo createPublicAccountVo) {
        log.info("[CMB][商户进件][公户]start");
        if(existsCertNo(createPublicAccountVo.getCertNo())){
            throw new BusinessException("企业证照号码已存在");
        }
        CmbBankAccountInfo cmbBankAccountInfo = new CmbBankAccountInfo();
        BeanUtils.copyProperties(createPublicAccountVo, cmbBankAccountInfo);
        accountReg(cmbBankAccountInfo);
        log.info("[CMB][商户进件][公户]end");
    }

    /** 私户注册
     * */
    @Override
    public void privceAccReg(CreatePrivateAccountVo createPrivateAccountVo) {
        log.info("[CMB][商户进件][私户]sstart");
        if(existsCertNo(createPrivateAccountVo.getCertNo())){
            throw new BusinessException("身份证号码已存在");
        }
        CmbBankAccountInfo cmbBankAccountInfo = new CmbBankAccountInfo();
        BeanUtils.copyProperties(createPrivateAccountVo, cmbBankAccountInfo);
        accountReg(cmbBankAccountInfo);
        log.info("[CMB][商户进件][私户]end");
    }

    /** 判断是否存在证照编号
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

    /** 获取文件全路径
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
        if (cmbBankAccountInfo.getCertType().equals(CmbIntfConst.CertType.C35)) {//公户
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
        } else if (cmbBankAccountInfo.getCertType().equals(CmbIntfConst.CertType.P01)) {//私户
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

    /** 管理员信息变更
     * */
    @Override
    public void mgrInfoChg(UpdateBankAccountVo updateBankAccountVo) {

    }

    /** 获取账户余额
     * */
    @Override
    public String getBalance(String mbrNo) {
        log.info("[CMB][获取账户余额]start");
        MbrBalQryReqDto mbrBalQryReq = new MbrBalQryReqDto();
        mbrBalQryReq.setPlatformNo(cmbTransferService.getPlatformNo());
        mbrBalQryReq.setMbrNo(mbrNo);
        String balance = "0.00";
        NtsResultDto<MbrBalQryRepDto> result = null;
        try {
            log.info("[CMB][获取账户余额]发送接口请求，账户编号:" + mbrNo);
            result = cmbTransferService.mbrBalQry(mbrBalQryReq);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMB][获取账户余额]接口请求异常：" + e.getMessage());
            throw new BusinessException("获取账户余额，接口请求异常");
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
                log.info("[CMB][获取账户余额]账户余额:" + balance);
                LambdaQueryWrapper<CmbBankAccountInfo> queryWrapper = Wrappers.lambdaQuery();
                queryWrapper.eq(CmbBankAccountInfo::getMbrNo, mbrNo);
                CmbBankAccountInfo bankAccountInfo = baseMapper.selectOne(queryWrapper);
                BigDecimal oldBal = new BigDecimal(bankAccountInfo.getAvaBal());
                BigDecimal newBal = new BigDecimal(balance);
                if (newBal.compareTo(oldBal) != 0) {
                    bankAccountInfo.setAvaBal(balance);
                    baseMapper.updateById(bankAccountInfo);
                    log.info("[CMB][获取账户余额]更新账户余额，原余额：" + oldBal + ",新余额：" + newBal);
                } else {
                    log.info("[CMB][获取账户余额]账户余额无变化无需更新，原余额：" + oldBal + ",新余额：" + newBal);
                }
            } else {
                String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                log.error("[CMB][获取账户余额]接口返回失败：" + errorMsg);
                throw new BusinessException(errorMsg);
            }
        } else {
            log.error("[CMB][获取账户余额]接口请求失败");
            throw new BusinessException("获取账户余额失败");
        }
        log.info("[CMB][获取账户余额]end");
        return balance;
    }

    /** 获取个人账户余额
     * */
    @Override
    public AccountBalanceVo getUserAccBalance(Long userId) {
        List<AccountBalanceDto> list = baseMapper.getUserAccBalance(userId);
        return getAccBalance(list);
    }

    /** 获取车队账户余额
     * */
    @Override
    public AccountBalanceVo getTenantAccBalance(Long tenantId) {
        List<AccountBalanceDto> list = baseMapper.getTenantAccBalance(tenantId);
        return getAccBalance(list);
    }

    /** 获取总账户余额
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
     * 同步银行账户注册结果
     * @author zag
     * @date 2022/3/25 15:50
     * @return void
     */
    @Override
    public void syncBnkAccRegResult() {
        log.info("[CMBTask][同步商户进件结果]start");
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
                log.info("[CMBTask][同步商户进件结果]发送接口请求，证件编号：" + merchRegQryReq.getCertNo());
                result = cmbTransferService.merchRegQry(merchRegQryReq);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[CMBTask][同步商户进件结果]接口请求异常：" + e.getMessage());
                continue;
                //throw new BusinessException("同步商户进件结果，接口请求异常");
            }
            if (result != null) {
                if (result.isSuccess()) {
                    log.info("[CMBTask][同步商户进件结果]");
                    MerchRegQryRepDto merchRegQryRep = result.getData();
                    if (merchRegQryRep != null && !merchRegQryRep.getStatus().equals("I")) {
                        log.info("[CMBTask][同步商户进件结果]审批结果：" + merchRegQryRep.getStatus());
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
                            log.info("[CMBTask][同步商户进件结果]更新商户进件结果成功");
                        } else {
                            log.error("[CMBTask][同步商户进件结果]更新商户进件结果失败");
                        }
                    }
                } else {
                    String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                    log.error("[CMBTask][同步商户进件结果]接口返回失败：" + errorMsg);
                    continue;
                    //throw new BusinessException(errorMsg);
                }
            }else {
                log.error("[CMBTask][同步商户进件结果]接口请求失败");
                throw new BusinessException("同步商户进件结果，接口请求失败");
            }
        }
        log.info("[CMBTask][同步商户进件结果]end");
    }

    /**
     * 同步银行账户余额
     * @author zag
     * @date 2022/3/25 16:41
     * @return void
     */
    @Override
    public void syncBnkAccBalance(){
        log.info("[CMBTask][同步银行账户余额]start");
        LambdaQueryWrapper<CmbBankAccountInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(CmbBankAccountInfo::getStatus, CmbIntfConst.MerchRegStatus.S);
        List<CmbBankAccountInfo> list = baseMapper.selectList(queryWrapper);
        MbrBalQryReqDto mbrBalQryReq=new MbrBalQryReqDto();
        mbrBalQryReq.setPlatformNo(cmbTransferService.getPlatformNo());
        for (CmbBankAccountInfo bankAccountInfo : list) {
            mbrBalQryReq.setMbrNo(bankAccountInfo.getMbrNo());
            NtsResultDto<MbrBalQryRepDto> result=null;
            try {
                log.info("[CMBTask][同步银行账户余额]发送接口请求，账户子商帐编号：" + bankAccountInfo.getMbrNo());
                result = cmbTransferService.mbrBalQry(mbrBalQryReq);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[CMBTask][同步银行账户余额]接口请求异常：" + e.getMessage());
                continue;
                //throw new BusinessException("查询账户余额异常");
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
                            log.info("[CMBTask][同步银行账户余额]数据更新成功，原余额：" + oldBal + ",新余额：" + newBal);
                        } else {
                            log.error("[CMBTask][同步银行账户余额]数据更新失败");
                        }
                    }
                }else {
                    String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                    log.error("[CMBTask][同步银行账户余额]接口请求失败：" + errorMsg);
                    continue;
                    //throw new BusinessException(errorMsg);
                }
            }else {
                log.error("[CMBTask][同步银行账户余额]接口请求失败");
                throw new BusinessException("同步银行账户余额，接口请求失败");
            }
        }
        log.info("[CMBTask][同步银行账户余额]end");
    }

    /**
     * 商户进件回调接口
     * @author zag
     * @date 2022/4/1 16:42
     * @param param 
     * @return void
     */
    @Override
    public void merchRegCallBack(Map<String, Object> param) {
        log.info("[CMBCallBack][商户进件回调]start");
        MerchRegCallBackDto merchRegCallBack = null;
        try {
            merchRegCallBack = BeanMapUtils.mapToBean(param, MerchRegCallBackDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMBCallBack][商户进件回调]数据转换异常：" + e.getMessage());
            throw new BusinessException("数据处理异常");
        }
        LambdaQueryWrapper<CmbBankAccountInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(CmbBankAccountInfo::getCertNo, merchRegCallBack.getCertNo())
                .eq(CmbBankAccountInfo::getStatus, CmbIntfConst.MerchRegStatus.I);
        CmbBankAccountInfo bankAccountInfo = baseMapper.selectOne(queryWrapper);
        if (bankAccountInfo != null) {
            log.info("[CMBCallBack][商户进件回调]账户编号：" + merchRegCallBack.getCertNo());
            bankReqRecordService.addRecord(merchRegCallBack.getOrigReqNo(), CmbIntfConst.TranFunc.MERCHREG_CALLBACK, JsonHelper.toJson(merchRegCallBack),
                    null,
                    "200",
                    "接口回调成功",
                    null);
            bankAccountInfo.setStatus(merchRegCallBack.getStatus());
            bankAccountInfo.setResult(merchRegCallBack.getResult());
            bankAccountInfo.setMerchNo(merchRegCallBack.getMerchNo());
            bankAccountInfo.setMbrNo(merchRegCallBack.getMbrNo());
            int rows = baseMapper.updateById(bankAccountInfo);
            if (rows > 0) {
                log.info("[CMBCallBack][商户进件回调]进件审批结果，数据更新成功");
            } else {
                log.error("[CMBCallBack][商户进件回调]进件审批结果，数据更新失败");
            }
        } else {
            log.info("[CMBCallBack][商户进件回调]未找到待审批账户编号：" + merchRegCallBack.getCertNo());
        }
        log.info("[CMBCallBack][商户进件回调]end");
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
