package com.youming.youche.market.provider.service.youka;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.api.facilitator.IServiceProductVerService;
import com.youming.youche.market.api.facilitator.ISysStaticDataMarketService;
import com.youming.youche.market.api.youka.IOilEntityService;
import com.youming.youche.market.api.youka.IRechargeInfoRecordService;
import com.youming.youche.market.api.youka.IVoucherInfoService;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.facilitator.ServiceProductVer;
import com.youming.youche.market.domain.youka.*;
import com.youming.youche.market.dto.youca.RechargeConsumeRecordOut;
import com.youming.youche.market.dto.youca.VoucherInfoDto;
import com.youming.youche.market.provider.mapper.youka.RechargeConsumeRebateMapper;
import com.youming.youche.market.provider.mapper.youka.RechargeConsumeRecordMapper;
import com.youming.youche.market.provider.mapper.youka.VoucherInfoMapper;
import com.youming.youche.system.api.ISysCfgService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * <p>
 * 代金券信息表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-03-08
 */
@DubboService(version = "1.0.0")
@Service
public class VoucherInfoServiceImpl extends BaseServiceImpl<VoucherInfoMapper, VoucherInfo> implements IVoucherInfoService {
    @Resource
    LoginUtils loginUtils;
    @Resource
    RedisUtil redisUtil;
    @Resource
    RechargeConsumeRebateMapper rechargeConsumeRebateMapper;
    @Resource
    OilCardManagementsServiceImpl oilCardManagementsService;

    @Resource
    RechargeConsumeRecordMapper rechargeConsumeRecordMapper;
    @Resource
    IServiceProductService serviceProductService;
    @Resource
    IRechargeInfoRecordService rechargeInfoRecordService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    @Resource
    IOilEntityService oilEntityService;
    @Resource
    IServiceProductVerService serviceProductVerService;
    @DubboReference(version = "1.0.0")
    ISysCfgService sysCfgService;
    @Resource
    ISysStaticDataMarketService sysStaticDataService;
    @Override
    public List<VoucherInfoDto> doQueryRechargeConsumeRecordList(List<String> strList, Long tenantId, Long serviceUserId) {
        return baseMapper.doQueryRechargeConsumeRecord(strList, tenantId, serviceUserId);

    }

    @Override
    public IPage<RechargeConsumeRecordOut> getRechargeConsumeRecords(String orderId, String serviceUserId,
                                                                     String recordStartTime,
                                                                     String recordEndTime,
                                                                     String recordType,
                                                                     Integer cardType,
                                                                     String tenantName, String cardNum, String voucherId,
                                                                     String plateNumber, Integer fromType,
                                                                     String serviceName, Integer rebate,
                                                                     String accessToken, String address, String dealRemark,
                                                                     Integer pageNum,Integer pageSize) {
        LocalDateTime startTime1 = null;
        LocalDateTime endTime1 = null;
        if(recordStartTime != null && StringUtils.isNotEmpty(recordStartTime)){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String applyTimeStr = recordStartTime+" 00:00:00";
            startTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
        }
        if(recordEndTime != null && StringUtils.isNotEmpty(recordEndTime)){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String endApplyTimeStr = recordEndTime+" 23:59:59";
            endTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<RechargeConsumeRecordOut> page = new Page<>(pageNum, pageSize);
        IPage<RechargeConsumeRecordOut> voucherInfoList = baseMapper.getRechargeConsumeRecords(orderId, serviceUserId, startTime1,
                endTime1, recordType, cardType, tenantName, cardNum, voucherId, plateNumber, fromType, serviceName,
                rebate, loginInfo.getTenantId(), address, dealRemark,page);
        List<String> oilCardNums = new ArrayList<String>();
        List<Long> consumeFlowIds = new ArrayList<Long>();
        for (RechargeConsumeRecordOut record : voucherInfoList.getRecords()) {
            oilCardNums.add(record.getCardNum());
            consumeFlowIds.add(record.getId());
        }
        Map<Long, RechargeConsumeRebate> rechargeConsumeRebateMap = this.getRechargeConsumeRebateList(consumeFlowIds);
        Map<String, String> plateNumberMap = oilCardManagementsService.getOilCardVehicleRelList(oilCardNums, StringUtils.isNotBlank(loginInfo.getTenantId().toString()) ? Long.valueOf(loginInfo.getTenantId()) : loginInfo.getTenantId());
//        List<RechargeConsumeRecordOut> results = new ArrayList<>();
        if (voucherInfoList != null && voucherInfoList.getRecords().size() > 0) {
            for (RechargeConsumeRecordOut out : voucherInfoList.getRecords()) {
//                RechargeConsumeRecordOut out = new RechargeConsumeRecordOut();
//                BeanUtil.copyProperties(entity, out);
                RechargeConsumeRebate rechargeConsumeRebate = rechargeConsumeRebateMap.get(out.getId());
                out.setDealRemark(rechargeConsumeRebate != null ? rechargeConsumeRebate.getDealRemark() : "");
                out.setPlateNumber(plateNumberMap.get(out.getCardNum()));
                out.setSourceTypeName(out.getSourceType()==null? "":getSysStaticData("RECHARGE_CONSUME_RECORD_SOURCE_TYPE",String.valueOf(out.getSourceType())).getCodeName());
                out.setCardTypeName(out.getCardType()==null?"":getSysStaticData("RECHARGE_CONSUME_RECORD_CARD_TYPE",String.valueOf(out.getCardType())).getCodeName());
                out.setRecordTypeName(out.getRecordType()==null?"":getSysStaticData("RECHARGE_CONSUME_RECORD_TYPE",String.valueOf(out.getRecordType())).getCodeName());
                out.setVoucherStateName(out.getVoucherState()==null?"":getSysStaticData("RECHARGE_CONSUME_RECORD_VOUCHER_STATE",String.valueOf(out.getVoucherState())).getCodeName());
                out.setMonthBillStateName(out.getMonthBillState()==null?"":getSysStaticData("RECHARGE_CONSUME_RECORD_MONTH_BILL_STATE",String.valueOf(out.getMonthBillState())).getCodeName());
                out.setRecordSourceName(out.getRecordSource()==null?"":getSysStaticData("RECHARGE_CONSUME_RECORD_SOURCE",String.valueOf(out.getRecordSource())).getCodeName());
                out.setUnitPriceDouble(out.getUnitPrice()==null?null:out.getUnitPrice()/100.00);// 单价
                out.setOilRiseDouble(out.getOilRise()==null?null:out.getOilRise()/100.00);// 加油升数
                out.setAmountDouble(out.getAmount()==null?null:out.getAmount()/100.00);//金额
                out.setBalanceDouble(out.getBalance()==null?null:out.getBalance()/100.00);//余额
//                results.add(out);
            }
        }
        return voucherInfoList;
    }

    @Override
    public List sumRechargeConsumeRecords(String orderId, String serviceUserId, String startTime, String endTime, String recordType, Integer cardType, String tenantName, String cardNum, String voucherId, String plateNumber, Integer fromType, String serviceName, Integer rebate, String accessToken, String address, String dealRemark) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<RechargeConsumeRecord> voucherInfoList = baseMapper.sumRechargeConsume(orderId, serviceUserId, startTime, endTime, recordType, cardType, tenantName, cardNum, voucherId, plateNumber, fromType, serviceName, rebate, loginInfo.getTenantId(), address, dealRemark);

        return voucherInfoList;
    }


    public Map<Long, RechargeConsumeRebate> getRechargeConsumeRebateList(List<Long> consumeFlowIds) {

        Map<Long, RechargeConsumeRebate> out = new HashMap<>();
        if (consumeFlowIds == null || consumeFlowIds.size() == 0) {
            return out;
        }
        Map<String, Object> param = new HashMap<String, Object>(1);
        param.put("consumeFlowIds", consumeFlowIds);
        List<RechargeConsumeRebate> list = rechargeConsumeRebateMapper.getRechargeConsumeRebateList(consumeFlowIds);
        if (list != null && list.size() > 0) {
            for (RechargeConsumeRebate rechargeConsumeRebate : list) {
                out.put(rechargeConsumeRebate.getConsumeFlowId(), rechargeConsumeRebate);
            }
        }
        return out;
    }

    private SysUser getSysUser(String accessToken) {
        SysUser sysUser = (SysUser) redisUtil.get("user:" + accessToken);
        return sysUser;
    }

    @Override
    public boolean judgeRechargeIsNeedWithdrawal(String busiCode,LoginInfo loginInfo) {
        if (StringUtils.isBlank(busiCode)) {
            throw new BusinessException("请输入busiCode值");
        }

        Date updateDate = new Date();
        RechargeInfoRecord info = rechargeInfoRecordService.getRechargeInfoRecordByRechargeFlowId(busiCode);
        if (info == null) {
            throw new BusinessException("根据busiCode值：" + busiCode + " 未找到充值记录");
        }
        ServiceProduct serviceProduct = serviceProductService.getServiceProduct(info.getCardNum(), info.getCardType(),DateUtil.formatDate(DateUtil.asDate(info.getCreateDate()), DateUtil.DATETIME_FORMAT));
        if (serviceProduct == null) {
            throw new BusinessException("根据卡号：" + info.getCardNum() + "卡号类型：" + info.getCardType() + " 未找到站点信息");
        }
        //更改母卡余额
        long parentAccountBalance = (serviceProduct.getParentAccountBalance() == null ? 0L : serviceProduct.getParentAccountBalance());
        SysOperLogConst.BusiCode servicePorductCode = SysOperLogConst.BusiCode.servicePorduct;
        sysOperLogService.saveSysOperLog(loginInfo,servicePorductCode,serviceProduct.getId(), SysOperLogConst.OperType.Add, "减少母卡余额,金额：" + (double)info.getPayCash()/100);
        boolean isNeedWithdrawal = false;
        info.setUpdateDate(LocalDateTime.now());
        info.setUpdateOpId(loginInfo.getId());
        info.setState(OrderAccountConst.RECHARGE_OIL_CARD.PAY_STATE2);
        //油卡充值核销记录
        //OilEntity entity = voucherInfoSV.getOilEntity(info.getCardNum(), info.getFlowId());
        OilEntity entity = oilEntityService.get(info.getSoNbr());
        if (entity == null) {
            throw new BusinessException("根据油卡号：" + info.getCardNum() + ",批次号：" + info.getId() + " 未找到油卡核销记录");
        }
        entity.setRechargeState(OrderAccountConst.OIL_ENTITY.RECHARGE_STATE3);
        oilEntityService.saveOrUpdate(entity);
        String threshold = (String) sysCfgService.getCfgVal(OrderAccountConst.RECHARGE_OIL_CARD.RECHARGE_WITHDRAWAL_THRESHOLD, 0, String.class);
        if (StringUtils.isBlank(threshold)) {
            throw new BusinessException("未找到系统配置是否提现阈值参数");
        }
        long thresholdAmount = Long.parseLong(threshold);
        if ((parentAccountBalance - info.getRechargeAmount().longValue()) < thresholdAmount) {
            info.setNoRechargeAmount(info.getRechargeAmount());
            isNeedWithdrawal = true;
        } else {
            info.setNoRechargeAmount(0L);
            isNeedWithdrawal = false;
            serviceProduct.setParentAccountBalance( parentAccountBalance - info.getRechargeAmount().longValue());
            serviceProductService.saveOrUpdate(serviceProduct);
            ServiceProductVer spv = new ServiceProductVer();
            BeanUtil.copyProperties(serviceProduct,spv);
            spv.setUpdateDate(new Date().toString());
            serviceProductVerService.save(spv);

        }
        rechargeInfoRecordService.saveOrUpdate(info);
        com.youming.youche.commons.constant.SysOperLogConst.BusiCode busiCodeLog = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.rechargeInfoRecord;//充值记录
        sysOperLogService.saveSysOperLog(loginInfo,busiCodeLog,info.getId(), SysOperLogConst.OperType.Add, "打款成功");
        return isNeedWithdrawal;
    }

    public SysStaticData getSysStaticData(String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }
}
