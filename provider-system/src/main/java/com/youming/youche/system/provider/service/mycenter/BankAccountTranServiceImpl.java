package com.youming.youche.system.provider.service.mycenter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.api.cmb.ICmbTransferService;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.cloud.constant.CmbIntfConst;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.cloud.dto.cmb.ChargeFundInfoRepDto;
import com.youming.youche.cloud.dto.cmb.ChargeFundListQryRepDto;
import com.youming.youche.cloud.dto.cmb.ChargeFundListQryReqDto;
import com.youming.youche.cloud.dto.cmb.FileDownloadRepDto;
import com.youming.youche.cloud.dto.cmb.ItaFundCallBackDto;
import com.youming.youche.cloud.dto.cmb.ItaTranQryRepDto;
import com.youming.youche.cloud.dto.cmb.ItaTranQryReqDto;
import com.youming.youche.cloud.dto.cmb.MbrChargeFundCallBackDto;
import com.youming.youche.cloud.dto.cmb.NtsResultDto;
import com.youming.youche.cloud.dto.cmb.OrderTransferRepDto;
import com.youming.youche.cloud.dto.cmb.OrderTransferReqDto;
import com.youming.youche.cloud.dto.cmb.ReceiptFileDownloadReqDto;
import com.youming.youche.cloud.dto.cmb.RefundInfoRepDto;
import com.youming.youche.cloud.dto.cmb.RefundListQryRepDto;
import com.youming.youche.cloud.dto.cmb.RefundListQryReqDto;
import com.youming.youche.cloud.dto.cmb.TranInfoRepDto;
import com.youming.youche.cloud.dto.cmb.TranListQryRepDto;
import com.youming.youche.cloud.dto.cmb.TranListQryReqDto;
import com.youming.youche.cloud.dto.cmb.WithdrawCallBackDto;
import com.youming.youche.cloud.dto.cmb.WithdrawRepDto;
import com.youming.youche.cloud.dto.cmb.WithdrawReqDto;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.AesEncryptUtil;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.encrypt.K;
import com.youming.youche.record.api.account.IAccountBankRelService;
import com.youming.youche.system.api.ISysCfgService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.api.mycenter.IBankAccountService;
import com.youming.youche.system.api.mycenter.IBankAccountTranService;
import com.youming.youche.system.api.mycenter.IBankReqRecordService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.mycenter.AccountBankRel;
import com.youming.youche.system.domain.mycenter.CheckPasswordErr;
import com.youming.youche.system.domain.mycenter.CmbAccountTransactionRecord;
import com.youming.youche.system.domain.mycenter.CmbAccountTransactionRecordHis;
import com.youming.youche.system.domain.mycenter.CmbBankAccountInfo;
import com.youming.youche.system.dto.mycenter.BankFlowDetailsAppDto;
import com.youming.youche.system.dto.mycenter.BankFlowDetailsAppOutDto;
import com.youming.youche.system.provider.mapper.mycenter.BankAccountMapper;
import com.youming.youche.system.provider.mapper.mycenter.BankAccountTranHisMapper;
import com.youming.youche.system.provider.mapper.mycenter.BankAccountTranMapper;
import com.youming.youche.system.provider.mapper.mycenter.BankCardMapper;
import com.youming.youche.system.provider.mapper.mycenter.CheckPasswordErrMapper;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.system.vo.mycenter.AccountFlowListVo;
import com.youming.youche.system.vo.mycenter.AccountFlowQueryVo;
import com.youming.youche.system.vo.mycenter.AccountTransferVo;
import com.youming.youche.system.vo.mycenter.AccountWithdrawVo;
import com.youming.youche.system.vo.mycenter.BankFlowDetailsAppVo;
import com.youming.youche.system.vo.mycenter.BankFlowDownVo;
import com.youming.youche.system.vo.mycenter.BankFlowDownloadUrlVo;
import com.youming.youche.system.vo.mycenter.BankReceiptVo;
import com.youming.youche.system.vo.mycenter.RechargeAccountVo;
import com.youming.youche.system.vo.mycenter.SetPayPwdVo;
import com.youming.youche.system.vo.mycenter.TenantAccountFlowQueryVo;
import com.youming.youche.system.vo.mycenter.TenantAccountTranFlowQueryVo;
import com.youming.youche.util.BeanMapUtils;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.JsonHelper;
import com.youming.youche.util.PKUtil;
import com.youming.youche.util.SysMagUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.youming.youche.conts.EnumConsts.SmsTemplate.PAY_CODE;

/**
 * @ClassName BankAccountTranServiceImpl
 * @Description ????????????
 * @Author zag
 * @Date 2022/2/19 20:16
 */
@DubboService(version = "1.0.0")
public class BankAccountTranServiceImpl extends BaseServiceImpl<BankAccountTranMapper, CmbAccountTransactionRecord> implements IBankAccountTranService {

    private static final Logger log = LoggerFactory.getLogger(BankAccountTranServiceImpl.class);

    /**
     * ??????????????????Id
     */
    final Long rechargeAccId = 1000000000L;

    /**
     * redis????????????
     */
    final String REDIS_VERIFY_CODE_PACKET="verify_code:";

    @DubboReference(version = "1.0.0")
    ICmbTransferService cmbTransferService;

    @DubboReference(version = "1.0.0")
    ISysSmsSendService sysSmsSendService;

    @Autowired
    IBankReqRecordService bankReqRecordService;

    @Autowired
    ImportOrExportRecordsService importOrExportRecordsService;

    @Autowired
    ISysUserService sysUserService;

    @Autowired
    IBankAccountService bankAccountService;

    @Autowired
    BankAccountMapper bankAccountMapper;

    @Autowired
    BankCardMapper bankCardMapper;

    @Autowired
    BankAccountTranHisMapper bankAccountTranHisMapper;

    @Resource
    RedisUtil redisUtil;

    @Resource
    public BCryptPasswordEncoder passwordEncoder;

    @Resource
    ISysTenantDefService sysTenantDefService;

    @Resource
    LoginUtils loginUtils;

    @Resource
    BankAccountTranMapper bankAccountTranMapper;

    @DubboReference(version = "1.0.0")
    IAccountBankRelService accountBankRelService;

    @Resource
    ISysCfgService sysCfgService;

    @Resource
    CheckPasswordErrMapper checkPasswordErrMapper;

    /** ????????????????????????
     * */
    @Override
    public RechargeAccountVo getRechargeAccount() {
        AccountBankRel accountBankRel = bankCardMapper.selectById(rechargeAccId);
        RechargeAccountVo rechargeAccountVo = new RechargeAccountVo();
        BeanUtils.copyProperties(accountBankRel, rechargeAccountVo);
        return rechargeAccountVo;
    }

    /** ?????????????????????
     * */
    @Override
    public void transfer(AccountTransferVo accountTransferVo) {
        log.info("[CMB][??????????????????]start");
        //checkPayPwd(accountTransferVo.getUserId(), accountTransferVo.getPayPwd());
        NtsResultDto<OrderTransferRepDto> result = null;
        OrderTransferReqDto orderTransferReq = new OrderTransferReqDto();
        BeanUtils.copyProperties(accountTransferVo, orderTransferReq);
        orderTransferReq.setPlatformNo(cmbTransferService.getPlatformNo());
        orderTransferReq.setReqNo("WC" + PKUtil.getPK());
        orderTransferReq.setOrderNo(PKUtil.getPK() + "");
        orderTransferReq.setWdwTyp("1");//???????????????1????????????
        try {
            log.info("[CMB][??????????????????]??????????????????");
            result = cmbTransferService.orderTransfer(orderTransferReq);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMB][??????????????????]????????????????????????" + e.getMessage());
            throw new BusinessException("????????????????????????");
        }
        if (result != null) {
            OrderTransferRepDto orderTransferRep = result.getData();
            bankReqRecordService.addRecord(orderTransferReq.getReqNo(), CmbIntfConst.TranFunc.ORDERTRANSFER, JsonHelper.toJson(orderTransferReq),
                    orderTransferRep == null ? null : orderTransferRep.getRespNo(),
                    result.getCode(),
                    result.getMsg(),
                    JsonHelper.toJson(result));
            if (result.isSuccess()) {
                log.info("[CMB][??????????????????]??????????????????");
                CmbBankAccountInfo payBankAccount = bankAccountMapper.selectById(accountTransferVo.getPayAccountId());
                CmbBankAccountInfo recvBankAccount = bankAccountMapper.selectById(accountTransferVo.getRecvAccountId());
                CmbAccountTransactionRecord accountTransactionRecord = new CmbAccountTransactionRecord();
                accountTransactionRecord.setPayUserId(payBankAccount.getUserId());
                accountTransactionRecord.setRecvUserId(recvBankAccount.getUserId());
                accountTransactionRecord.setReqNo(orderTransferRep.getReqNo());
                accountTransactionRecord.setRespNo(orderTransferRep.getRespNo());
                accountTransactionRecord.setPayAccountId(payBankAccount.getId());
                accountTransactionRecord.setRecvAccountId(recvBankAccount.getId());
                accountTransactionRecord.setPayMbrNo(payBankAccount.getMbrNo());
                accountTransactionRecord.setPayMbrName(payBankAccount.getCertName());
                accountTransactionRecord.setRecvMbrNo(recvBankAccount.getMbrNo());
                accountTransactionRecord.setRecvMbrName(recvBankAccount.getCertName());
                accountTransactionRecord.setTranTime(getNewTranTimeStr(orderTransferRep.getTranDate(), orderTransferRep.getTranTime()));
                accountTransactionRecord.setTranAmt(orderTransferRep.getTranAmt());
                accountTransactionRecord.setTranType(CmbIntfConst.TranType.BP);
                accountTransactionRecord.setTranStatus(CmbIntfConst.TranStatus.Y);
                accountTransactionRecord.setPayoutId(accountTransferVo.getPayoutId());
                int rows = baseMapper.insert(accountTransactionRecord);
                if (rows > 0) {
                    log.info("[CMB][??????????????????]????????????????????????,???????????????" + payBankAccount.getMbrNo() + ",???????????????" + recvBankAccount.getMbrNo());
                } else {
                    log.error("[CMB][??????????????????]????????????????????????,???????????????" + payBankAccount.getMbrNo() + ",???????????????" + recvBankAccount.getMbrNo());
                }
                //??????????????????
                try {
                    String balance = bankAccountService.getBalance(payBankAccount.getMbrNo());
                    log.info("[CMB][??????????????????]??????????????????:" + payBankAccount.getMbrNo() + "??????:" + balance);
                    balance = bankAccountService.getBalance(recvBankAccount.getMbrNo());
                    log.info("[CMB][??????????????????]??????????????????:" + recvBankAccount.getMbrNo() + "??????:" + balance);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("[CMB][??????????????????]????????????????????????:" + e.getMessage());
                }
            } else {
                String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                log.error("[CMB][??????????????????]???????????????" + errorMsg);
                throw new BusinessException(errorMsg);
            }
        }
        log.info("[CMB][??????????????????]end");
    }

    /** ????????????
     * */
    @Override
    public void withdraw(AccountWithdrawVo accountWithdrawVo) {
        log.info("[CMB][??????????????????]start");
        String pwd = AesEncryptUtil.desEncrypt(accountWithdrawVo.getPayPwd());
        checkPayPwd(accountWithdrawVo.getUserId(), pwd);
        WithdrawReqDto withdrawReq = new WithdrawReqDto();
        BeanUtils.copyProperties(accountWithdrawVo, withdrawReq);
        withdrawReq.setPlatformNo(cmbTransferService.getPlatformNo());
        withdrawReq.setReqNo("WC" + PKUtil.getPK());
        withdrawReq.setWdwTyp("1");//???????????????1????????????
        NtsResultDto<WithdrawRepDto> result = null;
        try {
            log.info("[CMB][??????????????????]??????????????????");
            result = cmbTransferService.withdraw(withdrawReq);
        }catch (TimeoutException ex){

            throw new BusinessException("????????????????????????");
        }catch (Exception e) {
            e.printStackTrace();
            log.info("[CMB][??????????????????]???????????????????????????" + e.getMessage());
            throw new BusinessException("??????????????????");
        }
        if (result != null) {
            WithdrawRepDto withdrawRep = result.getData();
            bankReqRecordService.addRecord(withdrawReq.getReqNo(), CmbIntfConst.TranFunc.WITHDRAW, JsonHelper.toJson(withdrawReq),
                    withdrawRep == null ? null : withdrawRep.getRespNo(),
                    result.getCode(),
                    result.getMsg(),
                    JsonHelper.toJson(result));
            if (result.isSuccess()) {
                log.info("[CMB][??????????????????]??????????????????");
                CmbBankAccountInfo recvBankAccount = bankAccountMapper.selectById(accountWithdrawVo.getAccountId());
                CmbAccountTransactionRecord accountTransactionRecord = new CmbAccountTransactionRecord();
                accountTransactionRecord.setPayUserId(recvBankAccount.getUserId());
                accountTransactionRecord.setRecvUserId(recvBankAccount.getUserId());
                accountTransactionRecord.setReqNo(withdrawRep.getReqNo());
                accountTransactionRecord.setRespNo(withdrawRep.getRespNo());
                accountTransactionRecord.setPayAccountId(recvBankAccount.getId());
                accountTransactionRecord.setRecvAccountId(recvBankAccount.getId());
                accountTransactionRecord.setPayMbrNo(recvBankAccount.getMbrNo());
                accountTransactionRecord.setPayMbrName(recvBankAccount.getCertName());
                accountTransactionRecord.setRecvMbrNo(recvBankAccount.getMbrNo());
                accountTransactionRecord.setRecvMbrName(recvBankAccount.getCertName());
                accountTransactionRecord.setRecvAccNo(accountWithdrawVo.getAccNo());
                accountTransactionRecord.setRecvAccName(accountWithdrawVo.getAccName());
                accountTransactionRecord.setRecvEab(accountWithdrawVo.getEab());
                accountTransactionRecord.setTranAmt(withdrawRep.getTranAmt());
                accountTransactionRecord.setTranType(CmbIntfConst.TranType.WD);
                //?????????code:10000
                if(result.getCode().equals(CmbIntfConst.ResponseCode.Code_10000)){
                    accountTransactionRecord.setTranStatus(CmbIntfConst.TranStatus.Y);
                    accountTransactionRecord.setTranTime(getNewTranTimeStr(withdrawRep.getTranDate(), withdrawRep.getTranTime()));
                }else if(result.getCode().equals(CmbIntfConst.ResponseCode.Code_10001)) {    //?????????code:10001???????????????
                    accountTransactionRecord.setTranStatus(CmbIntfConst.TranStatus.R);//???????????????
                }
                int rows = baseMapper.insert(accountTransactionRecord);
                if (rows > 0) {
                    log.info("[CMB][??????????????????]????????????????????????");
                } else {
                    log.info("[CMB][??????????????????]????????????????????????");
                }
                //??????????????????
                try {
                    String balance = bankAccountService.getBalance(recvBankAccount.getMbrNo());
                    log.info("[CMB][??????????????????]????????????:" + recvBankAccount.getMbrNo() + "??????:" + balance);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("[CMB][??????????????????]????????????????????????:" + e.getMessage());
                }
            } else {
                //???????????????????????????????????????????????????
                if(result.getCode().equals(CmbIntfConst.ResponseCode.Code_20001)){
                    log.info("[CMB][??????????????????]????????????????????????");
                    CmbBankAccountInfo recvBankAccount = bankAccountMapper.selectById(accountWithdrawVo.getAccountId());
                    CmbAccountTransactionRecord accountTransactionRecord = new CmbAccountTransactionRecord();
                    accountTransactionRecord.setPayUserId(recvBankAccount.getUserId());
                    accountTransactionRecord.setRecvUserId(recvBankAccount.getUserId());
                    accountTransactionRecord.setReqNo(withdrawReq.getReqNo());
                    accountTransactionRecord.setPayAccountId(recvBankAccount.getId());
                    accountTransactionRecord.setRecvAccountId(recvBankAccount.getId());
                    accountTransactionRecord.setPayMbrNo(recvBankAccount.getMbrNo());
                    accountTransactionRecord.setPayMbrName(recvBankAccount.getCertName());
                    accountTransactionRecord.setRecvMbrNo(recvBankAccount.getMbrNo());
                    accountTransactionRecord.setRecvMbrName(recvBankAccount.getCertName());
                    accountTransactionRecord.setRecvAccNo(accountWithdrawVo.getAccNo());
                    accountTransactionRecord.setRecvAccName(accountWithdrawVo.getAccName());
                    accountTransactionRecord.setRecvEab(accountWithdrawVo.getEab());
                    accountTransactionRecord.setTranAmt(withdrawRep.getTranAmt());
                    accountTransactionRecord.setTranType(CmbIntfConst.TranType.WD);
                    accountTransactionRecord.setTranStatus(CmbIntfConst.TranStatus.O);//????????????
                    int rows = baseMapper.insert(accountTransactionRecord);
                    if (rows > 0) {
                        log.info("[CMB][??????????????????]??????????????????????????????");
                    } else {
                        log.info("[CMB][??????????????????]??????????????????????????????");
                    }
                }
                String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                log.error("[CMB][??????????????????]???????????????" + errorMsg);
                throw new BusinessException(errorMsg);
            }
        }
        log.info("[CMB][??????????????????]end");
    }

    /** ???????????????????????????
     * */
    @Override
    public void sendPayVerifyCode(String phone) {
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysUser::getLoginAcct, phone);
        SysUser sysUser = sysUserService.getOne(queryWrapper);
        if (sysUser== null) {
            throw new BusinessException("????????????????????????");
        }
        //????????????
        String key = REDIS_VERIFY_CODE_PACKET + PAY_CODE + "_" + phone;
        String codeKey = REDIS_VERIFY_CODE_PACKET + PAY_CODE + "_" + phone + "_CODE";

        int count = 0;

        if (redisUtil.hasKey(key)) {
            count = Integer.valueOf(redisUtil.get(key).toString());
        }

        if (count >= 5) {
            throw new BusinessException("24?????????????????????5????????????????????????");
        }

        //?????????????????????????????????
        String randomCode = SysMagUtil.getRandomNumber(6);
        boolean redisResult = redisUtil.setex(codeKey, randomCode, 120);
        if (redisResult) {
            log.info("??????????????????redis??????????????????????????????key:" + codeKey + ";value:" + randomCode + ";time:120");
        }

        Map<String, String> paraMap = new HashMap<String, String>();
        paraMap.put("code", randomCode);
        SysSmsSend sysSmsSend = new SysSmsSend();
        sysSmsSend.setBillId(phone);
        sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.MESSAGE_AUTHENTICATION);
        sysSmsSend.setTemplateId(PAY_CODE);
        sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.NOTIFY));
        sysSmsSend.setParamMap(paraMap);
        sysSmsSendService.sendSms(sysSmsSend);

        //???????????????????????????????????????1
        count++;
        redisResult = redisUtil.setex(key, String.valueOf(count), 24 * 60 * 60);
        if (redisResult) {
            log.info("??????????????????redis????????????????????????????????????key:" + key + ";value:" + count + ";time:24 * 60 * 60");
        }
    }

    /** ???????????????????????????
     * */
    @Override
    public boolean checkPayVerifyCode(String phone, String verifyCode) {
        String codeKey = REDIS_VERIFY_CODE_PACKET +PAY_CODE + "_" + phone + "_CODE";
        if (redisUtil.hasKey(codeKey)) {
            String randomCode = redisUtil.get(codeKey).toString();
            if (null != randomCode && randomCode.equals(verifyCode)) {
                return true;
            }
        }
        return false;
    }

    /** ??????????????????
     * */
    private void checkPayPwd(Long userId,String pwd) {
        String payPwd = baseMapper.selectPayPwd(userId);
        if (StringUtils.isBlank(payPwd)) {
            throw new BusinessException("????????????????????????????????????????????????????????????");
        }
        if (!passwordEncoder.matches(pwd, payPwd)) {
            throw new BusinessException("??????????????????");
        }
    }

    /** ??????????????????
     * */
    @Override
    public boolean setPayPwd(SetPayPwdVo setPayPwdVo) {
        String password = passwordEncoder.encode(setPayPwdVo.getPwd());
        boolean flag = baseMapper.updatePayPwd(password, setPayPwdVo.getUserId()) > 0 ? true : false;

        // ??????????????? ??????????????????????????????????????????
        if (flag) {
            LambdaQueryWrapper<CheckPasswordErr> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CheckPasswordErr::getUserId, setPayPwdVo.getUserId());
            queryWrapper.between(CheckPasswordErr::getCheckDate, DateUtil.getCurrDateTimeBegin(), DateUtil.getCurrDateTimeEnd());
            CheckPasswordErr checkPasswordErr = checkPasswordErrMapper.selectOne(queryWrapper);

            if (checkPasswordErr != null) {
                checkPasswordErr.setStatus(SysStaticDataEnum.PWD_STATUS.PWD_STATUS2);
                checkPasswordErrMapper.updateById(checkPasswordErr);
            }
        }

        return flag;
    }

    /** ??????????????????????????????
     * */
    @Override
    public boolean existsPayPwd(Long userId) {
        return StringUtils.isBlank(baseMapper.selectPayPwd(userId)) ? false : true;
    }

    /** ????????????????????????????????????
     * */
    @Override
    public IPage<AccountFlowListVo> getUserAccountFlow(Integer pageNum, Integer pageSize, AccountFlowQueryVo accountFlowQueryVo) {
        return getAccountFlowListPage(pageNum,pageSize,userQueryWrapper(accountFlowQueryVo));
    }

    /** ????????????????????????????????????
     * */
    @Override
    public IPage<AccountFlowListVo> getTenantAccountFlow(Integer pageNum, Integer pageSize, TenantAccountFlowQueryVo tenantAccountFlowQueryVo) {
        return getAccountFlowListPage(pageNum,pageSize,tenantQueryWrapper(tenantAccountFlowQueryVo));
    }

    /** ??????????????????????????????????????????????????????
     * */
    @Override
    public IPage<AccountFlowListVo> getTenantAccountTranFlow(Integer pageNum, Integer pageSize, TenantAccountTranFlowQueryVo tenantAccountTranFlowQueryVo) {
        List<Long> ids = bankAccountMapper.getTenantAccIdList(tenantAccountTranFlowQueryVo.getTenantId());
        if(ids.size()==0){
            return new Page<>();
        }
        return getAccountFlowListPage(pageNum, pageSize, ids, tenantTranQueryWrapper(tenantAccountTranFlowQueryVo));
    }

    /** ?????????????????????????????????Wx???
     * */
    @Override
    public List<AccountFlowListVo> getUserAccountFlowByWx(Long userId,String yearAndMonth,String businessType){
        //???????????????yyyy-mm ???????????????
        String year=yearAndMonth.split("-")[0];
        String month=yearAndMonth.split("-")[1];
        LocalDate firstDayOfCurrentDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate lastDayOfCurrentDate = firstDayOfCurrentDate.with(TemporalAdjusters.lastDayOfMonth());
        String firstDay=firstDayOfCurrentDate.toString()+" 00:00:00";
        String lastDay=lastDayOfCurrentDate.toString()+" 23:59:59";

        LambdaQueryWrapper<CmbAccountTransactionRecord> queryWrapper = Wrappers.lambdaQuery();
        List<String> tranTypes=new ArrayList<>();
        tranTypes.add(CmbIntfConst.TranType.BP);
        //businessType ???????????? 0:?????? 1:??????
        if(businessType.equals("0")){
            queryWrapper.eq(CmbAccountTransactionRecord::getRecvUserId, userId);
            tranTypes.add(CmbIntfConst.TranType.AC);
            queryWrapper.in(CmbAccountTransactionRecord::getTranType, tranTypes);
        }else if(businessType.equals("1")) {
            queryWrapper.eq(CmbAccountTransactionRecord::getPayUserId, userId);
            tranTypes.add(CmbIntfConst.TranType.WD);
            queryWrapper.in(CmbAccountTransactionRecord::getTranType, tranTypes);
        }else {
            queryWrapper.and(wrapper -> {
                wrapper.eq(CmbAccountTransactionRecord::getPayUserId, userId)
                        .or().eq(CmbAccountTransactionRecord::getRecvUserId, userId);
            });
        }
        queryWrapper.ge(CmbAccountTransactionRecord::getCreateTime, firstDay);
        queryWrapper.le(CmbAccountTransactionRecord::getCreateTime, lastDay);
        queryWrapper.orderByDesc(CmbAccountTransactionRecord::getCreateTime);

        List<CmbAccountTransactionRecord> accountTransactionRecords=baseMapper.selectList(queryWrapper);
        List<AccountFlowListVo> accountFlowListVoList = new ArrayList<>();
        if(accountTransactionRecords!=null){
            for (CmbAccountTransactionRecord item : accountTransactionRecords){
                AccountFlowListVo accountFlowListVo = new AccountFlowListVo();
                BeanUtils.copyProperties(item, accountFlowListVo);
                if(userId.equals(item.getPayUserId())){
                    accountFlowListVo.setBusinessType("1");
                    accountFlowListVo.setBusinessTypeName("??????");
                }else {
                    accountFlowListVo.setBusinessType("0");
                    accountFlowListVo.setBusinessTypeName("??????");
                }
                accountFlowListVo.setTranAmt(new BigDecimal(accountFlowListVo.getTranAmt()).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
                accountFlowListVoList.add(accountFlowListVo);
            }
        }
        return accountFlowListVoList;
    }

    /** ?????????????????????????????????Wx???
     * */
    @Override
    public List<AccountFlowListVo> getTenantAccountFlowByWx(Long tenantId,String yearAndMonth,String businessType){
        List<Long> ids = bankAccountMapper.getTenantAccIdList(tenantId);
        if(ids.size()==0){
            return new ArrayList<>();
        }
        //???????????????yyyy-mm ???????????????
        String year=yearAndMonth.split("-")[0];
        String month=yearAndMonth.split("-")[1];
        LocalDate firstDayOfCurrentDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate lastDayOfCurrentDate = firstDayOfCurrentDate.with(TemporalAdjusters.lastDayOfMonth());
        String firstDay=firstDayOfCurrentDate.toString()+" 00:00:00";
        String lastDay=lastDayOfCurrentDate.toString()+" 23:59:59";

        //businessType ???????????? 0:?????? 1:??????
        LambdaQueryWrapper<CmbAccountTransactionRecord> queryWrapper = Wrappers.lambdaQuery();
        if(businessType.equals("0")){
            queryWrapper.in(CmbAccountTransactionRecord::getRecvAccountId, ids);
        }else if(businessType.equals("1")) {
            queryWrapper.in(CmbAccountTransactionRecord::getPayAccountId, ids);
        }else {
            queryWrapper.and(wrapper -> {
                wrapper.in(CmbAccountTransactionRecord::getPayAccountId, ids)
                        .or()
                        .in(CmbAccountTransactionRecord::getRecvAccountId, ids);
            });
        }
        queryWrapper.eq(CmbAccountTransactionRecord::getTranType,CmbIntfConst.TranType.BP);
        queryWrapper.ge(CmbAccountTransactionRecord::getCreateTime, firstDay);
        queryWrapper.le(CmbAccountTransactionRecord::getCreateTime, lastDay);
        queryWrapper.orderByDesc(CmbAccountTransactionRecord::getCreateTime);

        List<CmbAccountTransactionRecord> accountTransactionRecords=baseMapper.selectList(queryWrapper);
        List<AccountFlowListVo> accountFlowListVoList = new ArrayList<>();
        if(accountTransactionRecords!=null){
            for (CmbAccountTransactionRecord item : accountTransactionRecords){
                AccountFlowListVo accountFlowListVo = new AccountFlowListVo();
                BeanUtils.copyProperties(item, accountFlowListVo);
                if(ids.contains(item.getPayAccountId())){
                    accountFlowListVo.setBusinessType("1");
                    accountFlowListVo.setBusinessTypeName("??????");
                }else {
                    accountFlowListVo.setBusinessType("0");
                    accountFlowListVo.setBusinessTypeName("??????");
                }
                accountFlowListVo.setTranAmt(new BigDecimal(accountFlowListVo.getTranAmt()).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
                accountFlowListVoList.add(accountFlowListVo);
            }
        }
        return accountFlowListVoList;
    }

    /** ??????????????????????????????????????????
     * */
    private IPage<AccountFlowListVo> getAccountFlowListPage(Integer pageNum, Integer pageSize, List<Long> ids, LambdaQueryWrapper<CmbAccountTransactionRecord> queryWrapper){
        Page<CmbAccountTransactionRecord> accountTransactionRecordPage=baseMapper.selectPage(new Page<>(pageNum,pageSize),queryWrapper);
        IPage<AccountFlowListVo> accountFlowListVoIPage=new Page<>();
        BeanUtils.copyProperties(accountTransactionRecordPage,accountFlowListVoIPage);
        List<AccountFlowListVo> accountFlowListVoList = new ArrayList<>();
        if(accountTransactionRecordPage!=null){
            List<CmbAccountTransactionRecord> records=accountTransactionRecordPage.getRecords();
            for (CmbAccountTransactionRecord item : records){
                AccountFlowListVo accountFlowListVo = new AccountFlowListVo();
                BeanUtils.copyProperties(item, accountFlowListVo);
                if(ids.contains(item.getPayAccountId())){
                    accountFlowListVo.setBusinessType("0");
                    accountFlowListVo.setBusinessTypeName("??????");
                }else {
                    accountFlowListVo.setBusinessType("1");
                    accountFlowListVo.setBusinessTypeName("??????");
                }
                accountFlowListVo.setTranAmt(new BigDecimal(accountFlowListVo.getTranAmt()).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
                accountFlowListVoList.add(accountFlowListVo);
            }
        }
        accountFlowListVoIPage.setRecords(accountFlowListVoList);
        return accountFlowListVoIPage;
    }

    /** ????????????????????????????????????
     * */
    private IPage<AccountFlowListVo> getAccountFlowListPage(Integer pageNum, Integer pageSize, LambdaQueryWrapper<CmbAccountTransactionRecord> queryWrapper){
        Page<CmbAccountTransactionRecord> accountTransactionRecordPage=baseMapper.selectPage(new Page<>(pageNum,pageSize),queryWrapper);
        IPage<AccountFlowListVo> accountFlowListVoIPage=new Page<>();
        BeanUtils.copyProperties(accountTransactionRecordPage,accountFlowListVoIPage);
        List<AccountFlowListVo> accountFlowListVoList = new ArrayList<>();
        if(accountTransactionRecordPage!=null){
            List<CmbAccountTransactionRecord> records=accountTransactionRecordPage.getRecords();
            for (CmbAccountTransactionRecord item : records){
                AccountFlowListVo accountFlowListVo = new AccountFlowListVo();
                BeanUtils.copyProperties(item, accountFlowListVo);
                accountFlowListVo.setTranAmt(new BigDecimal(accountFlowListVo.getTranAmt()).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
                accountFlowListVoList.add(accountFlowListVo);
            }
        }
        accountFlowListVoIPage.setRecords(accountFlowListVoList);
        return accountFlowListVoIPage;
    }

    /** ??????????????????????????????
     * */
    @Override
    public BankReceiptVo getElectronicReceipt(String respNo, String tranType) {
        log.info("[CMB][????????????????????????]-----start");
        BankReceiptVo bankReceiptVo = new BankReceiptVo();
        ReceiptFileDownloadReqDto receiptFileDownloadReq = new ReceiptFileDownloadReqDto();
        receiptFileDownloadReq.setPlatformNo(cmbTransferService.getPlatformNo());
        receiptFileDownloadReq.setOrigRespNo(respNo);
        receiptFileDownloadReq.setOrigTranType(tranType);
        NtsResultDto<FileDownloadRepDto> result = null;
        try {
            log.info("[CMB][??????????????????????????????]respNo???" + respNo + ",tranType" + tranType);
            result = cmbTransferService.receiptFileDownload(receiptFileDownloadReq);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[CMB][????????????????????????????????????]" + e.getMessage());
            throw new BusinessException("??????????????????????????????");
        }
        if (result != null) {
            FileDownloadRepDto fileDownloadRep = result.getData();
            bankReqRecordService.addRecord(null, CmbIntfConst.TranFunc.RECEIPTFILEDOWNLOAD, JsonHelper.toJson(receiptFileDownloadReq),
                    receiptFileDownloadReq.getOrigRespNo(),
                    result.getCode(),
                    result.getMsg(),
                    JsonHelper.toJson(result));
            if (result.isSuccess()) {
                log.info("[CMB][????????????????????????????????????]");
                BeanUtils.copyProperties(fileDownloadRep, bankReceiptVo);
            } else {
                String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                log.info("[CMB][????????????????????????]???????????????" + errorMsg);
                throw new BusinessException(errorMsg);
            }
        }
        log.info("[CMB][????????????????????????]-----end");
        return bankReceiptVo;
    }

    /** ??????????????????????????????
     * */
    @Override
    public void userAccFlowExport(AccountFlowQueryVo accountFlowQueryVo, ImportOrExportRecords record) {
        List<CmbAccountTransactionRecord> list = baseMapper.selectList(userQueryWrapper(accountFlowQueryVo));
        List<AccountFlowListVo> accountFlowListVoList = new ArrayList<>();
        for (CmbAccountTransactionRecord item : list) {
            AccountFlowListVo accountFlowListVo = new AccountFlowListVo();
            BeanUtils.copyProperties(item, accountFlowListVo);
            accountFlowListVo.setTranAmt(new BigDecimal(accountFlowListVo.getTranAmt()).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
            accountFlowListVoList.add(accountFlowListVo);
        }
        accFlowExport(accountFlowListVoList, record);
    }

    /** ??????????????????????????????
     * */
    @Override
    public void tenantAccFlowExport(TenantAccountFlowQueryVo tenantAccountFlowQueryVo, ImportOrExportRecords record) {
        List<CmbAccountTransactionRecord> list = baseMapper.selectList(tenantQueryWrapper(tenantAccountFlowQueryVo));
        List<AccountFlowListVo> accountFlowListVoList = new ArrayList<>();
        for (CmbAccountTransactionRecord item : list) {
            AccountFlowListVo accountFlowListVo = new AccountFlowListVo();
            BeanUtils.copyProperties(item, accountFlowListVo);
            accountFlowListVo.setTranAmt(new BigDecimal(accountFlowListVo.getTranAmt()).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
            accountFlowListVoList.add(accountFlowListVo);
        }
        accFlowExport(accountFlowListVoList, record);
    }

    /** ??????????????????????????????
     * */
    @Override
    public void tenantAccTranFlowExport(TenantAccountTranFlowQueryVo tenantAccountTranFlowQueryVo, ImportOrExportRecords record) {
        List<Long> ids = bankAccountMapper.getTenantAccIdList(tenantAccountTranFlowQueryVo.getTenantId());
        if (ids.size() == 0) {
            return;
        }
        LambdaQueryWrapper<CmbAccountTransactionRecord> queryWrapper = tenantTranQueryWrapper(tenantAccountTranFlowQueryVo);
        List<CmbAccountTransactionRecord> list = baseMapper.selectList(queryWrapper);
        List<AccountFlowListVo> accountFlowListVoList = new ArrayList<>();
        for (CmbAccountTransactionRecord item : list) {
            AccountFlowListVo accountFlowListVo = new AccountFlowListVo();
            BeanUtils.copyProperties(item, accountFlowListVo);
            if (ids.contains(item.getPayAccountId())) {
                accountFlowListVo.setBusinessType("0");
                accountFlowListVo.setBusinessTypeName("??????");
            } else {
                accountFlowListVo.setBusinessType("1");
                accountFlowListVo.setBusinessTypeName("??????");
            }
            accountFlowListVo.setTranAmt(new BigDecimal(accountFlowListVo.getTranAmt()).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
            accountFlowListVoList.add(accountFlowListVo);
        }
        tenantAccTranFlowExport(accountFlowListVoList, record);
    }

    /** ??????????????????wrapper
     * */
    private LambdaQueryWrapper<CmbAccountTransactionRecord> userQueryWrapper(AccountFlowQueryVo accountFlowQueryVo) {
        LambdaQueryWrapper<CmbAccountTransactionRecord> queryWrapper = Wrappers.lambdaQuery();
        List<Long> ids = bankAccountMapper.getUserAccIdList(accountFlowQueryVo.getUserId(),accountFlowQueryVo.getTenantId());
        if(ids.size()==0){
            return queryWrapper;
        }
        queryWrapper.and(wrapper -> {
            wrapper.in(CmbAccountTransactionRecord::getPayAccountId, ids)
                    .or()
                    .in(CmbAccountTransactionRecord::getRecvAccountId, ids);
        });
        if (!StringUtils.isBlank(accountFlowQueryVo.getPayMbrName())) {
            queryWrapper.like(CmbAccountTransactionRecord::getPayMbrName, accountFlowQueryVo.getPayMbrName());
        }
        if (!StringUtils.isBlank(accountFlowQueryVo.getRecvMbrName())) {
            queryWrapper.like(CmbAccountTransactionRecord::getRecvMbrName, accountFlowQueryVo.getRecvMbrName());
        }
        if (!StringUtils.isBlank(accountFlowQueryVo.getSerialNumber())) {
            queryWrapper.like(CmbAccountTransactionRecord::getRespNo, accountFlowQueryVo.getSerialNumber());
        }
        if (!StringUtils.isBlank(accountFlowQueryVo.getTranType())) {
            queryWrapper.eq(CmbAccountTransactionRecord::getTranType, accountFlowQueryVo.getTranType());
        }
        if (!StringUtils.isBlank(accountFlowQueryVo.getStartTime())) {
            queryWrapper.ge(CmbAccountTransactionRecord::getCreateTime, accountFlowQueryVo.getStartTime());
        }
        if (!StringUtils.isBlank(accountFlowQueryVo.getEndTime())) {
            queryWrapper.le(CmbAccountTransactionRecord::getCreateTime, accountFlowQueryVo.getEndTime());
        }
        queryWrapper.orderByDesc(CmbAccountTransactionRecord::getCreateTime);
        return queryWrapper;
    }

    /** ??????????????????wrapper
     * */
    private LambdaQueryWrapper<CmbAccountTransactionRecord> tenantQueryWrapper(TenantAccountFlowQueryVo tenantAccountFlowQueryVo){
        LambdaQueryWrapper<CmbAccountTransactionRecord> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.and(wrapper->{
            wrapper.eq(CmbAccountTransactionRecord::getPayAccountId, tenantAccountFlowQueryVo.getAccountId())
                    .or().eq(CmbAccountTransactionRecord::getRecvAccountId, tenantAccountFlowQueryVo.getAccountId());
        });
        if(!StringUtils.isBlank(tenantAccountFlowQueryVo.getSerialNumber())){
            queryWrapper.like(CmbAccountTransactionRecord::getRespNo, tenantAccountFlowQueryVo.getSerialNumber());
        }
        if(!StringUtils.isBlank(tenantAccountFlowQueryVo.getTranType())){
            queryWrapper.eq(CmbAccountTransactionRecord::getTranType, tenantAccountFlowQueryVo.getTranType());
        }
        if(!StringUtils.isBlank(tenantAccountFlowQueryVo.getStartTime())){
            queryWrapper.ge(CmbAccountTransactionRecord::getCreateTime,tenantAccountFlowQueryVo.getStartTime());
        }
        if(!StringUtils.isBlank(tenantAccountFlowQueryVo.getEndTime())){
            queryWrapper.le(CmbAccountTransactionRecord::getCreateTime,tenantAccountFlowQueryVo.getEndTime());
        }
        queryWrapper.orderByDesc(CmbAccountTransactionRecord::getCreateTime);
        return queryWrapper;
    }

    /** ??????????????????????????????wrapper
     * */
    private LambdaQueryWrapper<CmbAccountTransactionRecord> tenantTranQueryWrapper(TenantAccountTranFlowQueryVo tenantAccountTranFlowQueryVo){
        LambdaQueryWrapper<CmbAccountTransactionRecord> queryWrapper = Wrappers.lambdaQuery();
        List<Long> ids = bankAccountMapper.getTenantAccIdList(tenantAccountTranFlowQueryVo.getTenantId());
        if(ids.size()==0){
            return queryWrapper;
        }
        if (tenantAccountTranFlowQueryVo.getBusinessType().equals("0")) { //??????
            queryWrapper.in(CmbAccountTransactionRecord::getRecvAccountId, ids);
        } else if (tenantAccountTranFlowQueryVo.getBusinessType().equals("1")) {   //??????
            queryWrapper.in(CmbAccountTransactionRecord::getPayAccountId, ids);
        } else {
            queryWrapper.and(wrapper -> {
                wrapper.in(CmbAccountTransactionRecord::getPayAccountId, ids)
                        .or()
                        .in(CmbAccountTransactionRecord::getRecvAccountId, ids);
            });
        }
        if (!StringUtils.isBlank(tenantAccountTranFlowQueryVo.getPayMbrNo())) {
            queryWrapper.like(CmbAccountTransactionRecord::getPayMbrNo, tenantAccountTranFlowQueryVo.getPayMbrNo());
        }
        if (!StringUtils.isBlank(tenantAccountTranFlowQueryVo.getPayMbrName())) {
            queryWrapper.like(CmbAccountTransactionRecord::getPayMbrName, tenantAccountTranFlowQueryVo.getPayMbrName());
        }
        if (!StringUtils.isBlank(tenantAccountTranFlowQueryVo.getRecvMbrNo())) {
            queryWrapper.like(CmbAccountTransactionRecord::getRecvMbrNo, tenantAccountTranFlowQueryVo.getRecvMbrNo());
        }
        if (!StringUtils.isBlank(tenantAccountTranFlowQueryVo.getRecvMbrName())) {
            queryWrapper.like(CmbAccountTransactionRecord::getRecvMbrName, tenantAccountTranFlowQueryVo.getRecvMbrName());
        }
        if (!StringUtils.isBlank(tenantAccountTranFlowQueryVo.getBankNo())) {
            queryWrapper.like(CmbAccountTransactionRecord::getRespNo, tenantAccountTranFlowQueryVo.getBankNo());
        }
        if (!StringUtils.isBlank(tenantAccountTranFlowQueryVo.getPayNo())) {
            queryWrapper.like(CmbAccountTransactionRecord::getPayoutId, tenantAccountTranFlowQueryVo.getPayNo());
        }
        if (!StringUtils.isBlank(tenantAccountTranFlowQueryVo.getStartTime())) {
            queryWrapper.ge(CmbAccountTransactionRecord::getCreateTime, tenantAccountTranFlowQueryVo.getStartTime());
        }
        if (!StringUtils.isBlank(tenantAccountTranFlowQueryVo.getEndTime())) {
            queryWrapper.le(CmbAccountTransactionRecord::getCreateTime, tenantAccountTranFlowQueryVo.getEndTime());
        }
        queryWrapper.eq(CmbAccountTransactionRecord::getTranType,CmbIntfConst.TranType.BP);
        queryWrapper.orderByDesc(CmbAccountTransactionRecord::getCreateTime);
        return queryWrapper;
    }
    /** ????????????????????????
     * */
    private void accFlowExport(List<AccountFlowListVo> list,ImportOrExportRecords record){
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{"????????????", "???????????????", "????????????", "???????????????", "???????????????", "??????", "??????"};
            resourceFild = new String[]{"getStrCreateTime", "getRespNo", "getTranTypeName", "getPayMbrName", "getRecvMbrName", "getTranAmt","getTranStatusName"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, AccountFlowListVo.class,null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //????????????
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "????????????.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            record.setMediaUrl(path);
            record.setState(2);
            importOrExportRecordsService.update(record);
        } catch (Exception e) {
            record.setState(3);
            importOrExportRecordsService.update(record);
            e.printStackTrace();
            throw new BusinessException("????????????????????????");
        }
    }


    /** ????????????????????????????????????
     * */
    private void tenantAccTranFlowExport(List<AccountFlowListVo> list,ImportOrExportRecords record) {
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{"????????????", "???????????????", "????????????", "???????????????", "????????????", "???????????????", "????????????", "??????", "??????", "???????????????"};
            resourceFild = new String[]{"getStrCreateTime", "getRespNo", "getBusinessTypeName", "getPayMbrName","getPayMbrNo", "getRecvMbrName","getRecvMbrNo", "getTranAmt", "getTranStatusName","getPayoutId"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, AccountFlowListVo.class, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //????????????
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "????????????.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            record.setMediaUrl(path);
            record.setState(2);
            importOrExportRecordsService.update(record);
        } catch (Exception e) {
            record.setState(3);
            importOrExportRecordsService.update(record);
            e.printStackTrace();
            throw new BusinessException("????????????????????????");
        }
    }

    /** ????????????????????????
     *  1.????????????01??????????????????????????????
     *  2.???????????????????????????????????????????????????????????????????????????
     * */
    @Override
    public void syncRefundRecord(String clearDate) {
        log.info("[CMBTask][????????????????????????]start");
        if (StringUtils.isBlank(clearDate)) {
            clearDate = getYesterDayStr();
        }
        RefundListQryReqDto refundListQryReq = new RefundListQryReqDto();
        refundListQryReq.setPlatformNo(cmbTransferService.getPlatformNo());
        refundListQryReq.setClearDate(clearDate);

        NtsResultDto<RefundListQryRepDto> result = null;
        try {
            log.info("[CMBTask][????????????????????????]????????????????????????????????????" + clearDate);
            result = cmbTransferService.refundListQry(refundListQryReq);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMBTask][????????????????????????]??????????????????" + e.getMessage());
            throw new BusinessException("??????????????????????????????");
        }
        if (result != null) {
            RefundListQryRepDto refundListQryRep = result.getData();
            List<RefundInfoRepDto> refundList = refundListQryRep.getRefundList();
            bankReqRecordService.addRecord(null, CmbIntfConst.TranFunc.REFUNDLISTQRY, JsonHelper.toJson(refundListQryReq),
                    refundListQryRep == null ? null : refundListQryRep.getRespNo(),
                    result.getCode(),
                    result.getMsg(),
                    JsonHelper.toJson(result));
            if (result.isSuccess()) {
                for (RefundInfoRepDto refundInfoRep : refundList) {
                    LambdaQueryWrapper<CmbAccountTransactionRecord> queryWrapper = Wrappers.lambdaQuery();
                    queryWrapper.eq(CmbAccountTransactionRecord::getRespNo, refundInfoRep.getOrigRespNo());
                    CmbAccountTransactionRecord accountTransactionRecord = this.getOne(queryWrapper);
                    if (accountTransactionRecord != null) {
                        accountTransactionRecord.setTranStatus(CmbIntfConst.TranStatus.F);//??????????????????????????????
                        accountTransactionRecord.setClearDate(clearDate);
                        accountTransactionRecord.setRefundNote(refundInfoRep.getRefundNote());
                        accountTransactionRecord.setRefundAmt(refundInfoRep.getRefundAmt());
                        log.info("[CMBTask][????????????????????????]??????????????????,???????????????:" + refundInfoRep.getOrigRespNo() + " ???????????????" + refundInfoRep.getRefundNote() + " ???????????????" + refundInfoRep.getRefundAmt());
                        if (this.updateById(accountTransactionRecord)) {
                            log.info("[CMBTask][????????????????????????]???????????????????????????????????????");
                        } else {
                            log.error("[CMBTask][????????????????????????]???????????????????????????????????????");
                            throw new BusinessException("?????????????????????????????????????????????");
                        }
                    } else {
                        log.error("[CMBTask][????????????????????????]??????????????????????????????" + refundInfoRep.getOrigRespNo() + "???????????????");
                        throw new BusinessException("???????????????????????????????????????????????????");
                    }
                }
            } else {
                String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                log.error("[CMBTask][????????????????????????]?????????????????????" + errorMsg);
                throw new BusinessException("??????????????????????????????????????????");
            }
        } else {
            log.error("[CMBTask][????????????????????????]??????????????????");
            throw new BusinessException("??????????????????????????????");
        }
        log.info("[CMBTask][????????????????????????]end");
    }

    /** ?????????????????????????????????????????????????????????????????????
     *  1.????????????01??????????????????????????????
     *  2.???????????????????????????????????????????????????????????????????????????
     * */
    @Override
    public void syncRechargeRecord(String tranDate) {
        log.info("[CMBTask][????????????????????????]start");
        if (StringUtils.isBlank(tranDate)) {
            tranDate = getYesterDayStr();
        }
        ChargeFundListQryReqDto chargeFundListQryReq = new ChargeFundListQryReqDto();
        chargeFundListQryReq.setPlatformNo(cmbTransferService.getPlatformNo());
        chargeFundListQryReq.setStartDate(tranDate);
        chargeFundListQryReq.setEndDate(tranDate);
        chargeFundListQryReq.setPageNum("1");
        chargeFundListQryReq.setPageSize("20");

        NtsResultDto<ChargeFundListQryRepDto> result = null;
        try {
            log.info("[CMBTask][????????????????????????]????????????????????????????????????" + tranDate);
            result = cmbTransferService.chargeFundListQry(chargeFundListQryReq);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMBTask][????????????????????????]??????????????????" + e.getMessage());
            throw new BusinessException("????????????????????????,??????????????????");
        }
        if (result != null) {
            ChargeFundListQryRepDto chargeFundListQryRep = result.getData();
            bankReqRecordService.addRecord(null, CmbIntfConst.TranFunc.CHARGEFUNDLISTQRY, JsonHelper.toJson(chargeFundListQryReq),
                    chargeFundListQryRep == null ? null : chargeFundListQryRep.getRespNo(),
                    result.getCode(),
                    result.getMsg(),
                    JsonHelper.toJson(result));
            if (result.isSuccess()) {
                log.info("[CMBTask][????????????????????????]????????????????????????");
                Long totalNum = Long.parseLong(chargeFundListQryRep.getTotalNum());
                List<ChargeFundInfoRepDto> chargeFundInfoRepList = chargeFundListQryRep.getItem();
                if (totalNum > 0) {
                    saveChargeFund2DB(chargeFundInfoRepList, tranDate);//???????????????
                    int beginNum = 2;//??????2?????????
                    long pageSize = 20;//??????20?????????
                    long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;//?????????
                    log.info("[CMBTask][????????????????????????]??????????????????????????????" + totalNum + "??????" + totalPage + "???");
                    //???????????????????????????
                    for (int i = beginNum; i <= totalPage; i++) {
                        beginNum = i;
                        chargeFundListQryReq.setPageNum(beginNum + "");
                        try {
                            log.info("[CMBTask][????????????????????????]?????????" + beginNum + "?????????");
                            result = cmbTransferService.chargeFundListQry(chargeFundListQryReq);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("[CMBTask][????????????????????????????????????]" + e.getMessage());
                            throw new BusinessException("????????????????????????????????????");
                        }
                        if (result.isSuccess()) {
                            chargeFundInfoRepList = chargeFundListQryRep.getItem();
                            saveChargeFund2DB(chargeFundInfoRepList, tranDate);//???????????????
                        }
                    }
                }
            } else {
                String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                log.error("[CMBTask][??????????????????????????????]???????????????" + errorMsg);
                throw new BusinessException(errorMsg);
            }
        } else {
            log.error("[CMBTask]????????????????????????");
            throw new BusinessException("????????????????????????");
        }
        log.info("[CMBTask][????????????????????????]end");
    }

    /** ??????????????????
     * */
    private void saveChargeFund2DB(List<ChargeFundInfoRepDto> list,String tranDate) {
        log.info("[CMBTask][????????????????????????]??????????????????start");
        Map<String, CmbBankAccountInfo> bankAccountInfoMap = getBankAccountMap();
        Map<String, CmbAccountTransactionRecord> accountTransactionRecordMap = getAccountTransactionRecordMap(CmbIntfConst.TranType.AC, tranDate);
        for (ChargeFundInfoRepDto chargeFundInfo : list) {
            CmbBankAccountInfo bankAccountInfo = null;
            if (!bankAccountInfoMap.containsKey(chargeFundInfo.getMbrNo())) {
                log.error("[CMBTask][????????????????????????]?????????????????????");
                continue;
            }
            bankAccountInfo = bankAccountInfoMap.get(chargeFundInfo.getMbrNo());
            String key = chargeFundInfo.getOrigReqNo() + "," + chargeFundInfo.getBizSeq();
            //?????????????????????????????????
            if (accountTransactionRecordMap.containsKey(key)) {
                continue;
            }
            CmbAccountTransactionRecord accountTransactionRecord = new CmbAccountTransactionRecord();
            accountTransactionRecord.setPayUserId(bankAccountInfo.getUserId());
            accountTransactionRecord.setRecvAccountId(bankAccountInfo.getUserId());
            accountTransactionRecord.setReqNo(chargeFundInfo.getOrigReqNo());
            accountTransactionRecord.setRespNo(chargeFundInfo.getBizSeq());
            accountTransactionRecord.setRecvAccountId(bankAccountInfo.getId());
            accountTransactionRecord.setPayAccountId(bankAccountInfo.getId());
            accountTransactionRecord.setPayAccNo(chargeFundInfo.getOppAccNo());
            accountTransactionRecord.setPayAccName(chargeFundInfo.getOppAccName());
            accountTransactionRecord.setPayEab(chargeFundInfo.getOppEab());
            accountTransactionRecord.setRecvMbrNo(chargeFundInfo.getMbrNo());
            accountTransactionRecord.setRecvMbrName(bankAccountInfo.getCertName());
            accountTransactionRecord.setTranAmt(chargeFundInfo.getTranAmt());
            accountTransactionRecord.setTranTime(getNewTranTimeStr(chargeFundInfo.getTranDate(), chargeFundInfo.getTranTime()));
            accountTransactionRecord.setTranType(CmbIntfConst.TranType.AC);
            accountTransactionRecord.setTranStatus(CmbIntfConst.TranStatus.Y);
            if (chargeFundInfo.getOrigReqNo().indexOf("WC") != -1) {//??????????????????????????????WC????????????????????????????????????
                accountTransactionRecord.setInjectionMethod(CmbIntfConst.InjectionMethod.MANUAL);//????????????
            } else {
                accountTransactionRecord.setInjectionMethod(CmbIntfConst.InjectionMethod.AUTOMATIC);//????????????
            }
            accountTransactionRecord.setRemark(chargeFundInfo.getRemark());
            int rows = baseMapper.insert(accountTransactionRecord);
            if (rows > 0) {
                log.info("[CMBTask][????????????????????????]???????????????????????????key:"+key);
            } else {
                log.error("[CMBTask][????????????????????????]???????????????????????????key:"+key);
            }
        }
        log.info("[CMBTask][????????????????????????]??????????????????end");
    }

    /** ?????????????????????????????????????????????????????????????????????
     *  1.????????????01??????????????????????????????
     *  2.???????????????????????????????????????????????????????????????????????????
     * */
    @Override
    public void syncTranRecord(String tranDate) {
        log.info("[CMBTask][????????????????????????]start");
        if (StringUtils.isBlank(tranDate)) {
            tranDate = getYesterDayStr();
        }
        TranListQryReqDto tranListQryReq = new TranListQryReqDto();
        tranListQryReq.setPlatformNo(cmbTransferService.getPlatformNo());
        tranListQryReq.setStartDate(tranDate);
        tranListQryReq.setEndDate(tranDate);
        tranListQryReq.setPageNum("1");
        tranListQryReq.setPageSize("20");

        NtsResultDto<TranListQryRepDto> result = null;
        try {
            log.info("[CMBTask][????????????????????????]????????????????????????????????????" + tranDate);
            result = cmbTransferService.tranListQry(tranListQryReq);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMBTask][????????????????????????]??????????????????" + e.getMessage());
            throw new BusinessException("?????????????????????????????????????????????");
        }
        if (result != null) {
            TranListQryRepDto tranListQryRep = result.getData();
            bankReqRecordService.addRecord(null, CmbIntfConst.TranFunc.TRANLISTQRY, JsonHelper.toJson(tranListQryReq),
                    tranListQryRep == null ? null : tranListQryRep.getRespNo(),
                    result.getCode(),
                    result.getMsg(),
                    JsonHelper.toJson(result));
            if (result.isSuccess()) {
                log.info("[CMBTask][????????????????????????]????????????????????????");
                Long totalNum = Long.parseLong(tranListQryRep.getTotalNum());
                List<TranInfoRepDto> tranInfoList = tranListQryRep.getItem();
                if (totalNum > 0) {
                    saveTranList2DB(tranInfoList, tranDate);//???????????????
                    int beginNum = 2;//??????2?????????
                    long pageSize = 20;//??????20?????????
                    long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;//?????????
                    log.info("[CMBTask][????????????????????????]??????????????????????????????" + totalNum + "??????" + totalPage + "???");
                    //???????????????????????????
                    for (int i = beginNum; i <= totalPage; i++) {
                        beginNum = i;
                        tranListQryReq.setPageNum(beginNum + "");
                        try {
                            result = cmbTransferService.tranListQry(tranListQryReq);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("[CMBTask][????????????????????????]?????????????????????" + e.getMessage());
                            throw new BusinessException("?????????????????????????????????????????????");
                        }
                        if (result.isSuccess()) {
                            tranInfoList = tranListQryRep.getItem();
                            saveTranList2DB(tranInfoList, tranDate);//???????????????
                        }
                    }
                }
            } else {
                String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                log.error("[CMBTask][??????????????????]?????????????????????" + errorMsg);
                throw new BusinessException(errorMsg);
            }
        } else {
            log.error("[CMBTask][??????????????????]??????????????????");
            throw new BusinessException("???????????????????????????????????????");
        }
        log.info("[CMBTask][????????????????????????]end");
    }

    /** ??????????????????
     * */
    private void saveTranList2DB(List<TranInfoRepDto> list,String tranDate) {
        log.info("[CMBTask][????????????????????????]??????????????????start");
        Map<String, CmbAccountTransactionRecordHis> accountTransactionRecordHisMap = getAccountTransactionRecordHisMap(tranDate);
        for (TranInfoRepDto tranInfoRep : list) {
            CmbAccountTransactionRecordHis accountTransactionRecordHis = new CmbAccountTransactionRecordHis();
            String key = tranInfoRep.getOrigReqNo() + "," + tranInfoRep.getOrigRespNo();
            //????????????????????????
            if (accountTransactionRecordHisMap.containsKey(key)) {
                continue;
            }
            BeanUtils.copyProperties(tranInfoRep, accountTransactionRecordHis);
            int rows = bankAccountTranHisMapper.insert(accountTransactionRecordHis);
            if (rows > 0) {
                log.info("[CMBTask][????????????????????????]?????????????????????????????????key:" + key);
            } else {
                log.error("[CMBTask][????????????????????????]?????????????????????????????????key:" + key);
            }
        }
        log.info("[CMBTask][????????????????????????]??????????????????end");
    }

    /** ?????????????????????????????????
     * */
    private String getYesterDayStr(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -1);
        return formatter.format(now.getTime());//?????????????????????
    }

    /** ?????????????????????:yyyy-MM-dd HH:mm:ss
     * */
    private String getNewTranTimeStr(String tranDate,String tranTime){
        //?????????????????????:yyyy-MM-dd HH:mm:ss
        String newTranTime="";
        StringBuffer sbTranDate = new StringBuffer(tranDate);
        if (!StringUtils.isBlank(tranDate)) {
            sbTranDate.insert(4, "-");//yyyy-
            sbTranDate.insert(7, "-");//yyyy-MM-dd
            newTranTime = sbTranDate + "";
        }

        StringBuffer sbTranTime = new StringBuffer(tranTime);
        if (!StringUtils.isBlank(tranTime)){
            sbTranTime.insert(2, ":");//HH:
            sbTranTime.insert(5, ":");//HH:mm:ss
            newTranTime = sbTranDate + " " + sbTranTime;
        }
        return newTranTime;
    }

    /** ??????????????????Map
     * */
    private Map<String,CmbAccountTransactionRecord> getAccountTransactionRecordMap(String tranType,String tranDate) {
        Map<String, CmbAccountTransactionRecord> accountTransactionRecordMap = new HashMap<>();
        LambdaQueryWrapper<CmbAccountTransactionRecord> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(CmbAccountTransactionRecord::getTranType, tranType);
        queryWrapper.ge(CmbAccountTransactionRecord::getTranTime, tranDate);
        List<CmbAccountTransactionRecord> list = baseMapper.selectList(queryWrapper);
        for (CmbAccountTransactionRecord accountTransactionRecord : list) {
            String key = accountTransactionRecord.getReqNo() + "," + accountTransactionRecord.getRespNo();
            accountTransactionRecordMap.put(key, accountTransactionRecord);
        }
        return accountTransactionRecordMap;
    }

    /** ????????????????????????Map
     * */
    private Map<String,CmbAccountTransactionRecordHis> getAccountTransactionRecordHisMap(String tranDate) {
        Map<String, CmbAccountTransactionRecordHis> accountTransactionRecordHisMap = new HashMap<>();
        LambdaQueryWrapper<CmbAccountTransactionRecordHis> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(CmbAccountTransactionRecordHis::getOrigTranDate, tranDate);
        List<CmbAccountTransactionRecordHis> list = bankAccountTranHisMapper.selectList(queryWrapper);
        for (CmbAccountTransactionRecordHis accountTransactionRecordHis : list) {
            String key = accountTransactionRecordHis.getOrigReqNo() + "," + accountTransactionRecordHis.getOrigRespNo();
            accountTransactionRecordHisMap.put(key, accountTransactionRecordHis);
        }
        return accountTransactionRecordHisMap;
    }

    /** ????????????????????????Map
     * */
    private Map<String,CmbBankAccountInfo> getBankAccountMap() {
        Map<String,CmbBankAccountInfo> bankAccountInfoMap=new HashMap<>();
        LambdaQueryWrapper<CmbBankAccountInfo> bankAccountInfoQueryWrapper = Wrappers.lambdaQuery();
        bankAccountInfoQueryWrapper.eq(CmbBankAccountInfo::getStatus, CmbIntfConst.MerchRegStatus.S);
        List<CmbBankAccountInfo> list= bankAccountMapper.selectList(bankAccountInfoQueryWrapper);
        for (CmbBankAccountInfo bankAccountInfo:list){
            bankAccountInfoMap.put(bankAccountInfo.getMbrNo(),bankAccountInfo);
        }
        return bankAccountInfoMap;
    }

    /**
     * ??????????????????????????????
     * @author zag
     * @date 2022/5/16 17:49
     * @return void
     */
    @Override
    public void syncWithdrawTranStatus(){
        log.info("[CMBTask][????????????????????????]start");
        //???????????????????????????????????????
        LambdaQueryWrapper<CmbAccountTransactionRecord> queryWrapper = Wrappers.lambdaQuery();
        List<String> statusList=new ArrayList<>();
        statusList.add(CmbIntfConst.TranStatus.R);
        statusList.add(CmbIntfConst.TranStatus.O);
        queryWrapper.in(CmbAccountTransactionRecord::getTranStatus,statusList)
                .eq(CmbAccountTransactionRecord::getTranType,CmbIntfConst.TranType.WD);
        List<CmbAccountTransactionRecord> list=this.list(queryWrapper);
        for (CmbAccountTransactionRecord info:list){
            ItaTranQryReqDto itaTranQryReq=new ItaTranQryReqDto();
            itaTranQryReq.setPlatformNo(cmbTransferService.getPlatformNo());
            itaTranQryReq.setOrigReqType(CmbIntfConst.TranType.WD);
            itaTranQryReq.setOrigReqNo(info.getReqNo());
            if(info.getTranStatus().equals(CmbIntfConst.TranStatus.R)){
                itaTranQryReq.setReturnCode(CmbIntfConst.ResponseCode.Code_10001);

            }else if(info.getTranStatus().equals(CmbIntfConst.TranStatus.O)){
                itaTranQryReq.setReturnCode(CmbIntfConst.ResponseCode.Code_10000);
            }
            NtsResultDto<ItaTranQryRepDto> result = null;
            try {
                log.info("[CMBTask][????????????????????????]??????????????????");
                result = cmbTransferService.itaTranQry(itaTranQryReq);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[CMBTask][????????????????????????]??????????????????" + e.getMessage());
                throw new BusinessException("?????????????????????????????????????????????");
            }
            if (result != null) {

                if(result.isSuccess()){
                    ItaTranQryRepDto itaTranQryRep=  result.getData();
                    info.setTranStatus(itaTranQryRep.getOrigTranStatus());
                    info.setTranTime(getNewTranTimeStr(itaTranQryRep.getOrigTranDate(), itaTranQryRep.getOrigTranTime()));
                    boolean updateResult =  this.update(info);
                    if(updateResult){
                        log.info("[CMBTask][????????????????????????]??????????????????????????????");
                    }else {
                        log.error("[CMBTask][????????????????????????]??????????????????????????????");
                    }
                }else {
                    if(result.getCode().equals("40004") &&
                            (result.getSubCode().equals("TRN.VIRTUAL_TRANSFER_FAIL")//??????????????????????????????????????????????????????????????????
                            || result.getSubCode().equals("TRN.VIRTUAL_TRANSFER_REQ_NO_NOT_EXIST"))){//???????????????????????????????????????????????????
                        info.setTranStatus(CmbIntfConst.TranStatus.F);//?????????????????????
                        boolean updateResult =  this.update(info);
                        if(updateResult){
                            log.info("[CMBTask][????????????????????????]?????????????????????????????????????????????");
                        }else {
                            log.error("[CMBTask][????????????????????????]?????????????????????????????????????????????");
                        }
                    }
                }
            }
        }
        log.info("[CMBTask][??????????????????????????????]start");
    }

    /**
     * ???????????????????????????
     * @author zag
     * @date 2022/4/1 16:49
     * @param param 
     * @return void
     */
    @Override
    public void mbrChargeFundCallBack(Map<String, Object> param) {
        log.info("[CMBCallBack][?????????????????????]start");
        MbrChargeFundCallBackDto mbrChargeFundCallBack = null;
        try {
            mbrChargeFundCallBack = BeanMapUtils.mapToBean(param, MbrChargeFundCallBackDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMBCallBack][?????????????????????]?????????????????????" + e.getMessage());
            throw new BusinessException("??????????????????");
        }
        if (mbrChargeFundCallBack != null) {
            LambdaQueryWrapper<CmbBankAccountInfo> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(CmbBankAccountInfo::getMbrNo, mbrChargeFundCallBack.getMbrNo());
            CmbBankAccountInfo bankAccountInfo = bankAccountMapper.selectOne(queryWrapper);
            if (bankAccountInfo != null) {
                bankReqRecordService.addRecord(null, CmbIntfConst.TranFunc.MBRCHARGEFUND_CALLBACK, JsonHelper.toJson(mbrChargeFundCallBack),
                        mbrChargeFundCallBack.getBizSeq(),
                        "200",
                        "??????????????????",
                        null);
                CmbAccountTransactionRecord accountTransactionRecord = new CmbAccountTransactionRecord();
                accountTransactionRecord.setPayUserId(bankAccountInfo.getUserId());
                accountTransactionRecord.setRecvAccountId(bankAccountInfo.getUserId());
                accountTransactionRecord.setRespNo(mbrChargeFundCallBack.getBizSeq());
                accountTransactionRecord.setRecvAccountId(bankAccountInfo.getId());
                accountTransactionRecord.setPayAccountId(bankAccountInfo.getId());
                accountTransactionRecord.setPayAccNo(mbrChargeFundCallBack.getOppAccNo());
                accountTransactionRecord.setPayAccName(mbrChargeFundCallBack.getOppAccName());
                accountTransactionRecord.setRecvMbrNo(mbrChargeFundCallBack.getMbrNo());
                accountTransactionRecord.setRecvMbrName(mbrChargeFundCallBack.getMbrName());
                accountTransactionRecord.setTranAmt(mbrChargeFundCallBack.getChargeAmt());
                accountTransactionRecord.setTranTime(mbrChargeFundCallBack.getChargeTime());
                accountTransactionRecord.setTranType(CmbIntfConst.TranType.AC);
                accountTransactionRecord.setTranStatus(CmbIntfConst.TranStatus.Y);
                accountTransactionRecord.setInjectionMethod(CmbIntfConst.InjectionMethod.AUTOMATIC);//????????????
                int rows = baseMapper.insert(accountTransactionRecord);
                if (rows > 0) {
                    log.info("[CMBCallBack][?????????????????????]????????????????????????????????????");
                } else {
                    log.error("[CMBCallBack][?????????????????????]????????????????????????????????????");
                }
                //??????????????????
                try {
                    String balance = bankAccountService.getBalance(mbrChargeFundCallBack.getMbrNo());
                    log.info("[CMBCallBack][?????????????????????]????????????:" + mbrChargeFundCallBack.getMbrNo() + "??????:" + balance);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("[CMBCallBack][?????????????????????]????????????????????????:" + e.getMessage());
                }
            }
        }
        log.info("[CMBCallBack][?????????????????????]end");
    }

    /**
     * ????????????????????????
     * @author zag
     * @date 2022/4/1 16:49
     * @param param 
     * @return void
     */
    @Override
    public void itaFundCallBack(Map<String, Object> param) {
        log.info("[CMBCallBack][??????????????????]start");
        ItaFundCallBackDto itaFundCallBack = null;
        try {
            itaFundCallBack = BeanMapUtils.mapToBean(param, ItaFundCallBackDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMBCallBack][??????????????????]?????????????????????"+e.getMessage());
            throw new BusinessException("??????????????????");
        }
        if(itaFundCallBack!=null){
            bankReqRecordService.addRecord(null, CmbIntfConst.TranFunc.ITAFUND_CALLBACK, JsonHelper.toJson(itaFundCallBack),
                    itaFundCallBack.getBizSeq(),
                    "200",
                    "??????????????????",
                    null);
            CmbAccountTransactionRecord accountTransactionRecord = new CmbAccountTransactionRecord();
            accountTransactionRecord.setRespNo(itaFundCallBack.getBizSeq());
            accountTransactionRecord.setPayAccNo(itaFundCallBack.getOppAccNo());
            accountTransactionRecord.setPayAccName(itaFundCallBack.getOppAccName());
            accountTransactionRecord.setPayEab(itaFundCallBack.getOppEab());
            accountTransactionRecord.setTranAmt(itaFundCallBack.getChargeAmt());
            accountTransactionRecord.setTranTime(itaFundCallBack.getChargeTime());
            accountTransactionRecord.setTranType(CmbIntfConst.TranType.AC);
            accountTransactionRecord.setTranStatus(CmbIntfConst.TranStatus.Y);
            accountTransactionRecord.setInjectionMethod(CmbIntfConst.InjectionMethod.ANONYMITY);//????????????
            accountTransactionRecord.setRemark(itaFundCallBack.getRemark());
            int rows = baseMapper.insert(accountTransactionRecord);
            if (rows > 0) {
                log.info("[CMBCallBack][??????????????????]????????????????????????????????????");
            } else {
                log.info("[CMBCallBack][??????????????????]????????????????????????????????????");
            }
        }
        log.info("[CMBCallBack][??????????????????]end");
    }

    /**
     * ??????????????????
     * @author zag
     * @date 2022/5/11 15:32
     * @param param 
     * @return void
     */
    @Override
    public void withdrawCallBack(Map<String, Object> param){
        log.info("[CMBCallBack][????????????]start");
        WithdrawCallBackDto withdrawCallBack=null;
        try {
            withdrawCallBack = BeanMapUtils.mapToBean(param, WithdrawCallBackDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMBCallBack][????????????]?????????????????????"+e.getMessage());
            throw new BusinessException("??????????????????");
        }
        if(withdrawCallBack!=null) {
            bankReqRecordService.addRecord(withdrawCallBack.getOrigReqNo(), CmbIntfConst.TranFunc.WITHDRAW_CALLBACK, JsonHelper.toJson(withdrawCallBack),
                    withdrawCallBack.getOrigRespNo(),
                    "200",
                    "??????????????????",
                    null);
            LambdaQueryWrapper<CmbAccountTransactionRecord> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(CmbAccountTransactionRecord::getReqNo,withdrawCallBack.getOrigReqNo());
            CmbAccountTransactionRecord accountTransactionRecord=this.getOne(queryWrapper);
            if(accountTransactionRecord!=null){
                //??????????????????
                accountTransactionRecord.setTranStatus(withdrawCallBack.getOrigTranStatus());
                //??????????????????
                accountTransactionRecord.setTranTime(getNewTranTimeStr(withdrawCallBack.getOrigTranDate(), withdrawCallBack.getOrigTranTime()));
                boolean result=  this.update(accountTransactionRecord);
                if(result){
                    log.info("[CMBCallBack][????????????]??????????????????????????????????????????????????????"+withdrawCallBack.getOrigReqNo()+",???????????????"+withdrawCallBack.getOrigTranStatus());
                }else {
                    log.error("[CMBCallBack][????????????]??????????????????????????????");
                }
            }
        }
        log.info("[CMBCallBack][????????????]end");
    }


    @Override
    public BankFlowDetailsAppDto getBankDetailsToApp(BankFlowDetailsAppVo bankFlowDetailsAppVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        if(bankFlowDetailsAppVo.getUserType() != SysStaticDataEnum.USER_TYPE.DRIVER_USER && loginInfo != null && tenantId != null){
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
            if (sysTenantDef != null) {
                bankFlowDetailsAppVo.setUserId(sysTenantDef.getAdminUser());
            }
        }
        if (bankFlowDetailsAppVo.getUserId()==null || bankFlowDetailsAppVo.getUserId() <= 0) {
            throw new BusinessException("???????????????id");
        }
        if (StringUtils.isBlank(bankFlowDetailsAppVo.getMonth())) {
            throw new BusinessException("?????????????????????");
        }
        if (Long.valueOf(bankFlowDetailsAppVo.getMonth()).compareTo(EnumConsts.APP_ACCOUNT_DETAILS_OUT.LAST_MONTH) < 0) {
//            throw new BusinessException("?????????????????????????????????!");
        }
//        if (StringUtils.isNotBlank(bankFlowDetailsAppVo.getMonth())) {
//            bankFlowDetailsAppVo.setMonth("'" + bankFlowDetailsAppVo.getMonth() + "'");
//        }
        if (StringUtils.isNotEmpty(bankFlowDetailsAppVo.getQueryType())) {
            if (bankFlowDetailsAppVo.getQueryType().equals(EnumConsts.QUERY_TYPE.PAY)) {//????????????????????????
                bankFlowDetailsAppVo.setFlowType(EnumConsts.FILE_BUSINESS_TYPE.TX);
            }
            if (bankFlowDetailsAppVo.getQueryType().equals(EnumConsts.QUERY_TYPE.INCOME)) {//????????????????????????
                bankFlowDetailsAppVo.setFlowType(EnumConsts.FILE_BUSINESS_TYPE.CZ);
            }
        }
        List<com.youming.youche.record.domain.account.AccountBankRel> accountBankRelList = accountBankRelService.queryAccountBankRel(bankFlowDetailsAppVo.getUserId(), bankFlowDetailsAppVo.getUserType(), -1);
        List<String> pinganAccIdList = new ArrayList<>();
        if(accountBankRelList!=null&&accountBankRelList.size()>0){
            for(com.youming.youche.record.domain.account.AccountBankRel accountBankRel:accountBankRelList){
                pinganAccIdList.add(accountBankRel.getPinganCollectAcctId());
                pinganAccIdList.add(accountBankRel.getPinganPayAcctId());
            }
        }
        bankFlowDetailsAppVo.setPinganAccIdList(pinganAccIdList);
        List<BankFlowDetailsAppOutDto> list =null;
        try {
            list = bankAccountTranMapper.getBankFlowDetailsToAppAndWx(bankFlowDetailsAppVo);
        }catch (Exception e){
            e.printStackTrace();
        }

        List<BankFlowDetailsAppOutDto> outList = new ArrayList<>();
        FastDFSHelper client=null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (BankFlowDetailsAppOutDto dto : list){
            BankFlowDetailsAppOutDto detailsOut = new BankFlowDetailsAppOutDto();
            BeanUtils.copyProperties(dto, detailsOut);
            detailsOut.setTranDate(dto.getTranDate());
            detailsOut.setUserId(bankFlowDetailsAppVo.getUserId());
            if (StringUtils.isNotBlank(detailsOut.getReceiptUrl())) {
                try {
                    detailsOut.setReceiptUrl(client.getHttpURL(detailsOut.getReceiptUrl()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Integer flowType = detailsOut.getFlowType();
            Long outUserId = detailsOut.getUserIdOut();
            Long inUserId = detailsOut.getUserIdIn();
            String acctNameIn = detailsOut.getAcctNameIn();
            String cardNoIn = hideCardNo(detailsOut.getAcctNoIn());
            String acctNameInType = detailsOut.getAcctNameInType();
            String acctNameOut = detailsOut.getAcctNameOut();
            String cardNoOut = hideCardNo(detailsOut.getAcctNoOut());
            String acctNameOutType = detailsOut.getAcctNameOutType();
            if (StringUtils.isBlank(bankFlowDetailsAppVo.getQueryType())) {
                if (String.valueOf(flowType).equals(EnumConsts.FILE_BUSINESS_TYPE.CZ)) {//??????
                    detailsOut.setBusinessType(EnumConsts.FILE_BUSINESS_TYPE.CZ);
                    detailsOut.setBusinessName("??????");
                    detailsOut.setAcctNoName(acctNameIn + "(" + cardNoIn + ")");
                }
                if (String.valueOf(flowType).equals(EnumConsts.FILE_BUSINESS_TYPE.TX)) {//??????
                    detailsOut.setBusinessType(EnumConsts.FILE_BUSINESS_TYPE.TX);
                    detailsOut.setBusinessName("??????");
                    detailsOut.setAcctNoName(acctNameIn + "(" + cardNoIn + ")");
                }
                if (String.valueOf(flowType).equals(EnumConsts.FILE_BUSINESS_TYPE.JY)) {//??????
                    if (String.valueOf(outUserId).equals(String.valueOf(inUserId))) {//??????
                        detailsOut.setBusinessType(EnumConsts.FILE_BUSINESS_TYPE.TURN);
                        detailsOut.setBusinessName("??????");
                        detailsOut.setAcctNoName(acctNameOutType + " -> " + acctNameInType);
                    } else {
                        if (String.valueOf(outUserId).equals(String.valueOf(bankFlowDetailsAppVo.getUserId()))) {//??????
                            detailsOut.setBusinessType(EnumConsts.FILE_BUSINESS_TYPE.TURN_OUT);
                            detailsOut.setBusinessName("??????");
                            detailsOut.setAcctNoName(acctNameIn + "(" + cardNoIn + ")");//????????????
                        }
                        if (String.valueOf(inUserId).equals(String.valueOf(bankFlowDetailsAppVo.getUserId()))) {//??????
                            detailsOut.setBusinessType(EnumConsts.FILE_BUSINESS_TYPE.TURN_IN);
                            detailsOut.setBusinessName("??????");
                            detailsOut.setAcctNoName(acctNameOut + "(" + cardNoOut + ")");//????????????
                        }
                    }
                }
            } else {
                if (bankFlowDetailsAppVo.getQueryType().equals(EnumConsts.QUERY_TYPE.INCOME)) {//??????????????????????????????
                    if (String.valueOf(flowType).equals(EnumConsts.FILE_BUSINESS_TYPE.CZ)) {//??????
                        detailsOut.setBusinessType(EnumConsts.FILE_BUSINESS_TYPE.CZ);
                        detailsOut.setBusinessName("??????");
                        detailsOut.setAcctNoName(acctNameIn + "(" + cardNoIn + ")");
                    } else {
                        detailsOut.setBusinessType(EnumConsts.FILE_BUSINESS_TYPE.TURN_IN); //??????
                        detailsOut.setBusinessName("??????");
                        detailsOut.setAcctNoName(acctNameOut + "(" + cardNoOut + ")");//????????????
                    }
                }
                if (bankFlowDetailsAppVo.getQueryType().equals(EnumConsts.QUERY_TYPE.PAY)) {//??????????????????????????????
                    if (String.valueOf(flowType).equals(EnumConsts.FILE_BUSINESS_TYPE.TX)) {//??????
                        detailsOut.setBusinessType(EnumConsts.FILE_BUSINESS_TYPE.TX);
                        detailsOut.setBusinessName("??????");
                        detailsOut.setAcctNoName(acctNameIn + "(" + cardNoIn + ")");
                    } else {
                        detailsOut.setBusinessType(EnumConsts.FILE_BUSINESS_TYPE.TURN_OUT);//??????
                        detailsOut.setBusinessName("??????");
                        detailsOut.setAcctNoName(acctNameIn + "(" + cardNoIn + ")");
                    }
                }
            }
            outList.add(detailsOut);
        }
        BankFlowDetailsAppDto result=new BankFlowDetailsAppDto();
        result.setMonth(bankFlowDetailsAppVo.getMonth());
        result.setOut(outList);
        return result;
    }

    @Override
    public BankFlowDownloadUrlVo getBankFlowDownloadUrl(BankFlowDownVo bankFlowDownVo) {
        String downloadUrl =String.valueOf(sysCfgService.getCfgVal("BANK_FLOW_DOWNLOAD_URL", 0, String.class));
        if (bankFlowDownVo.getUserId() <= 0) {
            throw new BusinessException("??????id???????????????");
        }
        if (StringUtils.isNotBlank(bankFlowDownVo.getBeginDate())) {
            try {
                bankFlowDownVo.setBeginDate(DateUtil.formatDate(DateUtil.formatStringToDate(bankFlowDownVo.getBeginDate(), DateUtil.DATE_FORMAT), DateUtil.DATE_FORMAT));
            } catch (Exception e) {
                throw new BusinessException("????????????????????????!");
            }
        }
        if (StringUtils.isNotBlank(bankFlowDownVo.getEndDate())) {
            try {
                bankFlowDownVo.setEndDate(DateUtil.formatDate(DateUtil.formatStringToDate(bankFlowDownVo.getEndDate(), DateUtil.DATE_FORMAT), DateUtil.DATE_FORMAT));
            } catch (Exception e) {
                throw new BusinessException("????????????????????????!");
            }
        }
        BankFlowDownloadUrlVo bankFlowDownloadUrlVo=new BankFlowDownloadUrlVo();
        try {
            bankFlowDownloadUrlVo.setDownloadUrl(downloadUrl+"&param="+K.j_s(bankFlowDownVo.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bankFlowDownloadUrlVo;
    }


    /**
     * ???????????? ??????????????????
     */
    public static String hideCardNo(String cardNo) {
        if (cardNo == null) {
            return cardNo;
        }
        if (cardNo.length() > 8) {
            //????????????
            StringBuilder stringBuilder = new StringBuilder();
            return stringBuilder.append(cardNo.substring(0, 4)).append(" **** **** ")
                    .append(cardNo.substring(cardNo.length() - 4)).toString();
        } else {
            return cardNo;
        }
    }
}
