package com.youming.youche.system.provider.service.mycenter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.cloud.api.cmb.ICmbTransferService;
import com.youming.youche.cloud.constant.CmbIntfConst;
import com.youming.youche.cloud.dto.cmb.AccAuthReqRepDto;
import com.youming.youche.cloud.dto.cmb.AccAuthReqReqDto;
import com.youming.youche.cloud.dto.cmb.BnkAccBindReqDto;
import com.youming.youche.cloud.dto.cmb.BnkAccCnlReqDto;
import com.youming.youche.cloud.dto.cmb.NtsResultDto;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.system.api.mycenter.IBankAccountService;
import com.youming.youche.system.api.mycenter.IBankCardService;
import com.youming.youche.system.api.mycenter.IBankReqRecordService;
import com.youming.youche.system.domain.mycenter.AccountBankRel;
import com.youming.youche.system.domain.mycenter.CmbBankAccountInfo;
import com.youming.youche.system.provider.mapper.mycenter.BankCardMapper;
import com.youming.youche.system.vo.mycenter.BankCardBindVo;
import com.youming.youche.util.JsonHelper;
import com.youming.youche.util.PKUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @ClassName BankCardServiceImpl
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/19 19:45
 */
@DubboService(version = "1.0.0")
public class BankCardServiceImpl extends BaseServiceImpl<BankCardMapper, AccountBankRel> implements IBankCardService {

    private static final Logger log = LoggerFactory.getLogger(BankCardServiceImpl.class);

    @DubboReference(version = "1.0.0")
    ICmbTransferService cmbTransferService;

    @Autowired
    IBankReqRecordService bankReqRecordService;

    @Autowired
    IBankAccountService bankAccountService;

    @Resource
    RedisUtil redisUtil;

//    @Value("${spring.profiles.active}")
//    private String env;

    /**
     * @description 根据AccountId获取账户银行卡列表
     * @author zag
     * @date 2022/2/19 20:05
     * @param accountId
     * @return java.util.List<com.youming.youche.system.domain.mycenter.AccountBankRel>
     */
    @Override
    public List<AccountBankRel> getListByAccountId(Long accountId) {
        LambdaQueryWrapper<AccountBankRel> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountBankRel::getAcctId,accountId);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据UserId获取用户银行卡列表
     * @author zag
     * @date 2022/5/21 14:23
     * @param userId
     * @return java.util.List<com.youming.youche.system.domain.mycenter.AccountBankRel>
     */
    @Override
    public List<AccountBankRel> getListByUserId(Long userId){
        LambdaQueryWrapper<AccountBankRel> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountBankRel::getUserId,userId);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * @description 账户银行卡绑定
     * @author zag
     * @date 2022/2/23 10:09
     * @param bankCardBindVo
     * @return void
     */
    @Override
    public void bind(BankCardBindVo bankCardBindVo) {
        log.info("[CMB][账户银行卡绑定]start");
        if (existsAcctNo(bankCardBindVo.getAcctNo())) {
            throw new BusinessException("该银行卡号已存在");
        }
        BnkAccBindReqDto bnkAccBindReq = convert2BnkAccBindReq(bankCardBindVo);
        NtsResultDto result = null;
        try {
            log.info("[CMB][账户银行卡绑定]发送接口请求，银行帐号：" + bankCardBindVo.getAcctNo() + ",银行户名：" + bnkAccBindReq.getAccName());
            result = cmbTransferService.bnkAccBind(bnkAccBindReq);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[CMB][账户银行卡绑定]接口请求异常：" + e.getMessage());
            throw new BusinessException("账户银行卡绑定失败");
        }
        if (result != null) {
            bankReqRecordService.addRecord(bnkAccBindReq.getReqNo(), CmbIntfConst.TranFunc.BNKACCBIND, JsonHelper.toJson(bnkAccBindReq),
                    null,
                    result.getCode(),
                    result.getMsg(),
                    JsonHelper.toJson(result));
            if (result.isSuccess()) {
                log.info("[CMB][账户银行卡绑定]接口返回成功");
                AccountBankRel accountBankRel = new AccountBankRel();
                BeanUtils.copyProperties(bankCardBindVo, accountBankRel);
                accountBankRel.setProvinceid(bankCardBindVo.getProvinceId());
                accountBankRel.setCityid(bankCardBindVo.getCityId());
                accountBankRel.setDistrictid(bankCardBindVo.getDistrictId());
                accountBankRel.setBillid(bankCardBindVo.getBillId());
                CmbBankAccountInfo bankAccountInfo = bankAccountService.getById(bankCardBindVo.getAcctId());
                accountBankRel.setBankType(bankAccountInfo.getCertType().equals(CmbIntfConst.CertType.P01) ? 0: 1);
                accountBankRel.setPinganCollectAcctId(bankAccountInfo.getMerchNo());//商户编号
                accountBankRel.setPinganPayAcctId(bankAccountInfo.getMbrNo());//商户子编号
                accountBankRel.setPinganNoutId(bankAccountInfo.getCertNo());//账户编号
                int rows = baseMapper.insert(accountBankRel);
                if (rows > 0) {
                    log.info("[CMB][账户银行卡绑定]数据保存成功");
                } else {
                    log.info("[CMB][账户银行卡绑定]数据保存失败");
                }

                String authKey = bnkAccBindReq.getMerchNo();
                if (redisUtil.hasKey(authKey)) {
                    redisUtil.del(authKey);
                    log.info("[CMB][账户银行卡绑定]账户绑卡成功后清除redis鉴权key：" + authKey);
                }
            } else {
                String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                log.info("[CMB][账户银行卡绑定]接口返回失败：" + errorMsg);
                throw new BusinessException(errorMsg);
            }
        } else {
            log.error("[CMB][账户银行卡绑定]接口请求失败");
            throw new BusinessException("账户银行卡解绑，接口请求失败");
        }
        log.info("[CMB][账户银行卡绑定]end");
    }

    private boolean existsAcctNo(String acctNo) {
        LambdaQueryWrapper<AccountBankRel> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(AccountBankRel::getAcctNo, acctNo);
        int count = baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * @description 账户银行卡解绑
     * @author zag
     * @date 2022/2/23 10:10
     * @param id
     * @return void
     */
    @Override
    public void unbind(Long id) {
        log.info("[CMB][账户银行卡解绑]start");
        BnkAccCnlReqDto bnkAccCnlReq = new BnkAccCnlReqDto();
        AccountBankRel accountBankRel = baseMapper.selectById(id);
        CmbBankAccountInfo bankAccountInfo = bankAccountService.get(accountBankRel.getAcctId());
        bnkAccCnlReq.setPlatformNo(cmbTransferService.getPlatformNo());
        bnkAccCnlReq.setAccNo(accountBankRel.getAcctNo());
        bnkAccCnlReq.setMerchNo(bankAccountInfo.getMerchNo());
        NtsResultDto result = null;
        try {
            log.info("[CMB][账户银行卡解绑]发送接口请求，商户编号：" + bnkAccCnlReq.getMerchNo() + ",银行帐号" + bnkAccCnlReq.getAccNo());
            result = cmbTransferService.bnkAccCnl(bnkAccCnlReq);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMB][账户银行卡解绑]接口请求失败：" + e.getMessage());
            throw new BusinessException("账户银行卡解绑失败");
        }
        if (result != null) {
            bankReqRecordService.addRecord(null, CmbIntfConst.TranFunc.BNKACCCNL, JsonHelper.toJson(bnkAccCnlReq),
                    null,
                    result.getCode(),
                    result.getMsg(),
                    JsonHelper.toJson(result));
            if (result.isSuccess()) {
                log.info("[CMB][账户银行卡解绑]接口返回成功");
                int rows = baseMapper.deleteById(id);
                if (rows > 0) {
                    log.info("[CMB][账户银行卡解绑]数据删除成功");
                } else {
                    log.error("[CMB][账户银行卡解绑]数据删除失败");
                }
            } else {
                String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                log.error("[CMB][账户银行卡解绑]接口返回失败：" + errorMsg);
                throw new BusinessException(errorMsg);
            }
        } else {
            log.error("[CMB][账户银行卡解绑]接口请求失败");
            throw new BusinessException("账户银行卡解绑，接口请求失败");
        }
        log.info("[CMB][账户银行卡解绑]end");
    }

    /**
     * @description 银行卡小额鉴权申请
     * @author zag
     * @date 2022/2/23 10:11
     * @param bankCardBindVo
     * @return java.lang.String
     */
    @Override
    public String authenticationApply(BankCardBindVo bankCardBindVo) {
        log.info("[CMB][小额鉴权申请]start");
        if(existsAcctNo(bankCardBindVo.getAcctNo())) {
            throw new BusinessException("该银行卡号已存在");
        }
        CmbBankAccountInfo bankAccountInfo = bankAccountService.get(bankCardBindVo.getAcctId());
        try {
            String authKey = bankAccountInfo.getMerchNo();
            Object authValue = redisUtil.get(authKey);
            //如果已申请过，则直接获取
            if (authValue != null) {
                log.info("[CMB][小额鉴权申请]redis获取账户：" + authKey + "，鉴权申请信息：" + authValue);
                return authValue.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMB][小额鉴权申请]获取redis鉴权信息失败");
        }
        AccAuthReqReqDto accAuthReqReq = new AccAuthReqReqDto();
        accAuthReqReq.setReqNo("WC" + PKUtil.getPK());
        accAuthReqReq.setPlatformNo(cmbTransferService.getPlatformNo());
        accAuthReqReq.setAccNo(bankCardBindVo.getAcctNo());
        accAuthReqReq.setAccName(bankAccountInfo.getCertName());
        accAuthReqReq.setMerchNo(bankAccountInfo.getMerchNo());
        accAuthReqReq.setBnkName(bankCardBindVo.getBankName());
        accAuthReqReq.setBnkAddress(bankCardBindVo.getProvinceName() + bankCardBindVo.getCityName() + bankCardBindVo.getDistrictName());
        String respNo = null;
        NtsResultDto<AccAuthReqRepDto> result = null;
        try {
            log.info("[CMB][小额鉴权申请]发送接口请求，银行帐号：" + bankCardBindVo.getAcctNo() + ",银行户名：" + accAuthReqReq.getAccName());
            result = cmbTransferService.accAuthReq(accAuthReqReq);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMB][小额鉴权申请]接口请求异常：" + e.getMessage());
            throw new BusinessException("银行卡小额鉴权申请失败");
        }
        if (result != null) {
            AccAuthReqRepDto accAuthReqRep = result.getData();
            bankReqRecordService.addRecord(accAuthReqReq.getReqNo(), CmbIntfConst.TranFunc.ACCAUTHREQ, JsonHelper.toJson(accAuthReqReq),
                    accAuthReqRep == null ? null : accAuthReqRep.getRespNo(),
                    result.getCode(),
                    result.getMsg(),
                    JsonHelper.toJson(result));
            if (result.isSuccess()) {
                respNo = accAuthReqRep.getRespNo();
                String expireTime = accAuthReqRep.getExpireTime() + "000000";
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                try {
                    Date lastDate = formatter.parse(expireTime);
                    int time = (int) (System.currentTimeMillis() - lastDate.getTime()) / 1000;
                    String key = accAuthReqRep.getMerchNo();
                    String value = respNo;
                    redisUtil.setex(key, value, time);
                    log.info("[CMB][小额鉴权申请]鉴权流水号:" + respNo + ",redis保存成功,有效时间：" + time);
                } catch (ParseException e) {
                    e.printStackTrace();
                    log.error("[CMB][小额鉴权申请]鉴权流水号redis保存异常" + e.getMessage());
                }
            } else {
                String errorMsg = result.getMsg() + ":" + result.getSubMsg();
                log.error("[CMB][小额鉴权申请]接口返回失败：" + errorMsg);
                throw new BusinessException(errorMsg);
            }
        }
        log.info("[CMB][小额鉴权申请]end");
        return respNo;
    }

    private BnkAccBindReqDto convert2BnkAccBindReq(BankCardBindVo bankCardBindVo){
        CmbBankAccountInfo bankAccountInfo=bankAccountService.get(bankCardBindVo.getAcctId());
        BnkAccBindReqDto bnkAccBindReq = new BnkAccBindReqDto();
        BeanUtils.copyProperties(bankCardBindVo,bnkAccBindReq);
        bnkAccBindReq.setReqNo("WC"+ PKUtil.getPK());
        bnkAccBindReq.setPlatformNo(cmbTransferService.getPlatformNo());
        bnkAccBindReq.setMerchNo(bankAccountInfo.getMerchNo());
        bnkAccBindReq.setAccNo(bankCardBindVo.getAcctNo());
        bnkAccBindReq.setAccName(bankCardBindVo.getAcctName());
        bnkAccBindReq.setBnkName(bankCardBindVo.getBankName());
        bnkAccBindReq.setBnkAddress(bankCardBindVo.getProvinceName()+bankCardBindVo.getCityName()+bankCardBindVo.getDistrictName());
        bnkAccBindReq.setAccMobile(bankCardBindVo.getBillId());
        if(bankCardBindVo.getBankName().equals("招商银行")){
            bnkAccBindReq.setIsCmbAcc("Y");
        }else {
            bnkAccBindReq.setIsCmbAcc("N");
            //招行测试环境绑卡特殊处理逻辑: 非招行卡绑卡时加入银联号：102100099996 ，否则，提现时状态审理中无法自动转变为交易成功
            //此处，假设非生产即为测试
//            if(!"pro".equals(env)){
//                bnkAccBindReq.setBnkNo("102100099996");
//            }
            bnkAccBindReq.setBnkNo(cmbTransferService.getBnkNo());
        }
        if(bankAccountInfo.getCertType().equals(CmbIntfConst.CertType.P01)){
            bnkAccBindReq.setCertType(bankAccountInfo.getCertType());
            bnkAccBindReq.setCertNo(bankCardBindVo.getIdentification());
            bnkAccBindReq.setAccType(CmbIntfConst.AccType.P);
            if(StringUtils.isBlank(bankCardBindVo.getIdentification())){
                throw new BusinessException("证件号码不能为空");
            }
        }else {
            bnkAccBindReq.setAccType(CmbIntfConst.AccType.C);
        }
        return bnkAccBindReq;
    }


}
