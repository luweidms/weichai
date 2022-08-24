package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IOilRechargeAccountDetailsService;
import com.youming.youche.order.api.order.IOilRechargeAccountFlowService;
import com.youming.youche.order.api.order.IOilSourceRecordService;
import com.youming.youche.order.domain.order.OilRechargeAccountDetails;
import com.youming.youche.order.domain.order.OilRechargeAccountFlow;
import com.youming.youche.order.domain.order.OilSourceRecord;
import com.youming.youche.order.provider.mapper.order.OilSourceRecordMapper;
import com.youming.youche.order.provider.utils.MatchAmountUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@DubboService(version = "1.0.0")
@Service
public class OilSourceRecordServiceImpl extends BaseServiceImpl<OilSourceRecordMapper, OilSourceRecord> implements IOilSourceRecordService {
    @Autowired
    private IOilRechargeAccountDetailsService oilRechargeAccountDetailsService;
    @Resource
    private MatchAmountUtil matchAmountUtil;
    @Autowired
    private IOilRechargeAccountFlowService oilRechargeAccountFlowService;

    @Override
    public List<OilSourceRecord> getOilSourceRecordNoPayBalance(String orderId, Long tenantId) {
        LambdaQueryWrapper<OilSourceRecord> lambda= Wrappers.lambdaQuery();
        lambda.eq(OilSourceRecord::getOrderId,orderId)
              .gt(OilSourceRecord::getNoPayBalance,0L)
               .eq(OilSourceRecord::getTenantId,tenantId)
               .orderByDesc(OilSourceRecord::getCreateTime);
        return this.list(lambda);
    }

    @Override
    public void recallOil(Long userId, String orderNum, Long sourceUserId, Long subjectsId,
                          Long tenantId, Map<String, Object> inparam, Integer recallType,LoginInfo user) {
        if (tenantId == null || tenantId < 1) {
            throw new BusinessException("请输入租户id");
        }
        if (userId < 1) {
            throw new BusinessException("请输入用户id");
        }
        if (sourceUserId < 1) {
            throw new BusinessException("请输入资金来源用户id");
        }
        if (StringUtils.isBlank(orderNum)) {
            throw new BusinessException("输入的订单号不合法");
        }
        if (subjectsId < 1) {
            throw new BusinessException("请输入的科目");
        }
        if (inparam == null) {
            throw new BusinessException("请输入回退金额明细");
        }
        if (recallType == null || recallType <= 0) {
            throw new BusinessException("请输入是原路 回退还是回退到转移账户");
        }
        Long noPayOil = (Long) inparam.get("noPayOil");
        Long noRebateOil = (Long) inparam.get("noRebateOil");
        Long noCreditOil = (Long) inparam.get("noCreditOil");
        if (noPayOil == null) {
            throw new BusinessException("充值现金不能为空");
        }
        if (noRebateOil == null) {
            throw new BusinessException("返利不能为空");
        }
        if (noCreditOil == null) {
            throw new BusinessException("授信不能为空");
        }
        if ((noPayOil + noRebateOil + noCreditOil) <= 0) {
            throw new BusinessException("回退金额不能小于等于0");
        }
        List<OilSourceRecord> list = this.getOilSourceRecordNoPayBalance(orderNum, tenantId);
        if (list == null || list.size() <= 0) {
            throw new BusinessException("根据订单号：" + orderNum + " 租户id：" + tenantId + " 未找到分配油资金来源关系记录");
        }
        List<OilSourceRecord> noPayOilList = new ArrayList<OilSourceRecord>();
        List<OilSourceRecord> noRebateOilList = new ArrayList<OilSourceRecord>();
        List<OilSourceRecord> noCreditOilList = new ArrayList<OilSourceRecord>();
        for (OilSourceRecord osr : list) {
            OilRechargeAccountDetails detail = oilRechargeAccountDetailsService.getById(osr.getRechargeId());
            if (detail == null) {
                throw new BusinessException("根据id：" + osr.getRechargeId() +  " 未找到母卡明细记录");
            }
            if (detail.getSourceType() != null && detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE1) {//返利
                noRebateOilList.add(osr);
            } else if (detail.getSourceType() != null && (detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2
                    || detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5 || detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE6)) {//现金
                noPayOilList.add(osr);
            } else if (detail.getSourceType() != null && detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3) {//现金
                noCreditOilList.add(osr);
            }
        }
        Map<String, Long> inParam = new HashMap<String, Long>();
        this.matchAmount(noPayOil, noPayOilList, inParam,user);
        this.matchAmount(noRebateOil, noRebateOilList, inParam,user);
        this.matchAmount(noCreditOil, noCreditOilList, inParam,user);

        //回退接口
        oilRechargeAccountFlowService.recallOilAccount(userId, orderNum, sourceUserId,
                inParam, subjectsId,recallType,user);
    }

    @Override
    public List<OilSourceRecord> getOilSourceRecordByOrderId(String orderId, Long tenantId) {
        LambdaQueryWrapper<OilSourceRecord> lambda=Wrappers.lambdaQuery();
        lambda.eq(OilSourceRecord::getOrderId,orderId)
              .eq(OilSourceRecord::getTenantId,tenantId);
        return this.list(lambda);
    }

    @Override
    public OilSourceRecord createOilSourceRecord(Long rechargeId, String orderId,
                                                 Long balance, Long noPayBalance,
                                                 Long paidBalance, Integer sourceRecordType,
                                                 Long tenantId,LoginInfo baseUser) {
        OilSourceRecord osr = new OilSourceRecord();
        osr.setRechargeId(rechargeId);
        osr.setOrderId(orderId);
        osr.setBalance(balance);
        osr.setNoPayBalance(noPayBalance);
        osr.setPaidBalance(paidBalance);
        osr.setSourceRecordType(sourceRecordType);
        osr.setTenantId(tenantId);
        osr.setBillBalance(0L);
        if (baseUser != null) {
            osr.setOpId(baseUser.getId());
            osr.setUpdateOpId(baseUser.getId());
        }
        osr.setCreateTime(LocalDateTime.now());
        osr.setUpdateDate(LocalDateTime.now());
        return osr;
    }


    public void matchAmount(Long amount, List<OilSourceRecord> list, Map<String,Long> inParam, LoginInfo user) {

        if (amount == null || amount < 0) {
            throw new BusinessException("匹配金额不合法");
        }
        if (list == null) {
            throw new BusinessException("匹配记录集合不能为空");
        }
        if (inParam == null) {
            throw new BusinessException("入参map不能为空");
        }
        matchAmountUtil.matchAmountOilSourceRecord(amount, 0L, 0L,  list);
        long totalAmount = 0;
        for (OilSourceRecord osr : list) {
            if (osr.getMatchAmount() != null && osr.getMatchAmount().longValue() > 0) {
                if (inParam.get(String.valueOf(osr.getRechargeId())) != null) {
                    Long value = inParam.get(String.valueOf(osr.getRechargeId()));
                    inParam.put(String.valueOf(osr.getRechargeId()), osr.getMatchAmount() + value);
                } else {
                    inParam.put(String.valueOf(osr.getRechargeId()), osr.getMatchAmount());
                }
                totalAmount += osr.getMatchAmount();
                osr.setNoPayBalance(osr.getNoPayBalance() - osr.getMatchAmount());
                osr.setBalance(osr.getBalance() - osr.getMatchAmount());
                osr.setUpdateDate(LocalDateTime.now());
                osr.setUpdateOpId(user.getId());
                this.saveOrUpdate(osr);
            }
        }

        if (amount != totalAmount) {
            throw new BusinessException("回退金额与账户明细分配金额不一致");
        }
    }
}
