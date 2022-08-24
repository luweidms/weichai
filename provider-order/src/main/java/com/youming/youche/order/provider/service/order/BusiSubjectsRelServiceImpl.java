package com.youming.youche.order.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.ISubjectsInfoService;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.domain.order.SubjectsInfo;
import com.youming.youche.order.dto.order.PayoutIntfDto;
import com.youming.youche.order.provider.mapper.order.BusiSubjectsRelMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
@Service
public class BusiSubjectsRelServiceImpl extends BaseServiceImpl<BusiSubjectsRelMapper, BusiSubjectsRel> implements IBusiSubjectsRelService {
    private static final Log log = LogFactory.getLog(BusiSubjectsRelServiceImpl.class);
    @Autowired
    private ISubjectsInfoService subjectsInfoService;

    @Override
    public BusiSubjectsRel createBusiSubjectsRel(Long subjectsId, Long amount) {
        BusiSubjectsRel busiSubjectsRel = new BusiSubjectsRel();
        busiSubjectsRel.setSubjectsId(subjectsId);
        busiSubjectsRel.setAmountFee(amount);
        return busiSubjectsRel;
    }

    @Override
    public List<BusiSubjectsRel> feeCalculation(Long businessId, List<BusiSubjectsRel> subjectsList) {
        List<BusiSubjectsRel> listFee = new ArrayList<BusiSubjectsRel>();
        if (businessId == 0) {
            throw new BusinessException("业务ID不能为空!");
        }
        List<BusiSubjectsRel> list = this.getBusiSubjectsRel(businessId, -1l);
        // 查询出科目配置关系表的数据
        if (list != null && list.size() > 0) {
            Long[] subjectsIds = new Long[list.size()];
            for (int i = 0; i < list.size(); i++) {
                BusiSubjectsRel bussSubjectsRel = list.get(i);
                subjectsIds[i] = bussSubjectsRel.getSubjectsId();
            }
            Map<Long, SubjectsInfo> subMap = subjectsInfoService.getSubjectsInfo(subjectsIds, -1l);
            for (int i = 0; i < list.size(); i++) {
                BusiSubjectsRel bussSubjectsRel = list.get(i);
                // 按顺序加入结果集合里
                listFee.add(bussSubjectsRel);
                // 根据费用科目查询配置的科目名称设置到结果集里
                SubjectsInfo subjectsInfo = subMap.get(bussSubjectsRel.getSubjectsId());
                if (subjectsInfo != null) {
                    // 设置费用科目名称
                    bussSubjectsRel.setSubjectsName(subjectsInfo.getSubjectsName());
                    // 设置费用科目类型
                    bussSubjectsRel.setSubjectsType(subjectsInfo.getSubjectsType());
                    // 设置备注
                    bussSubjectsRel.setRemarks(subjectsInfo.getSubjectsName());
                } else {
                    throw new BusinessException("费用科目不正确!");
                }
                boolean hasSubject = false;
                if (subjectsList != null && subjectsList.size() > 0) {
                    // 根据输入的费用和费用科目逻辑
                    for (int j = 0; j < subjectsList.size(); j++) {
                        BusiSubjectsRel bussSubjects = subjectsList.get(j);
                        if (Objects.equals(bussSubjectsRel.getSubjectsId(),bussSubjects.getSubjectsId())
                                && bussSubjectsRel.getRuleType() == EnumConsts.PayInter.NON_FIXATION) {
                            // 非固定费用
                            bussSubjectsRel.setAmountFee(bussSubjects.getAmountFee());
                            bussSubjectsRel.setIncome(bussSubjects.getIncome());
                            bussSubjectsRel.setBackIncome(bussSubjects.getBackIncome());
                            bussSubjectsRel.setOtherId(bussSubjects.getOtherId());
                            bussSubjectsRel.setOtherName(bussSubjects.getOtherName());
                            bussSubjectsRel.setIncomeTenantId(bussSubjects.getIncomeTenantId());
                            bussSubjectsRel.setPoundageFee(bussSubjects.getPoundageFee());
                            hasSubject = true;
                        }
                        if (Objects.equals(bussSubjectsRel.getSubjectsId(),bussSubjects.getSubjectsId())
                                && bussSubjectsRel.getRuleType() == EnumConsts.PayInter.PROPORTION) {
                            // 比例
                            hasSubject = true;
                        }
                        if (Objects.equals(bussSubjectsRel.getSubjectsId(),bussSubjects.getSubjectsId())
                                && bussSubjectsRel.getRuleType() == EnumConsts.PayInter.CLASS) {
                            // 实现类
                            hasSubject = true;
                        }
                        if (Objects.equals(bussSubjectsRel.getSubjectsId(),bussSubjects.getSubjectsId())
                                && bussSubjectsRel.getRuleType() == EnumConsts.PayInter.FIXED) {
                            // 固定值 有输入金额 取输入的金额
                            bussSubjectsRel.setAmountFee(bussSubjects.getAmountFee());
                            bussSubjectsRel.setIncome(bussSubjects.getIncome());
                            bussSubjectsRel.setBackIncome(bussSubjects.getBackIncome());
                            bussSubjectsRel.setOtherId(bussSubjects.getOtherId());
                            bussSubjectsRel.setOtherName(bussSubjects.getOtherName());
                            bussSubjectsRel.setIncomeTenantId(bussSubjects.getIncomeTenantId());
                            bussSubjectsRel.setPoundageFee(bussSubjects.getPoundageFee());
                            hasSubject = true;
                        } else {
                            if (bussSubjectsRel.getRuleType() == EnumConsts.PayInter.FIXED) {
                                // 固定值 没有输入金额 取配置表的金额
                                bussSubjectsRel.setAmountFee(Long.parseLong(bussSubjectsRel.getRuleValue()));
                                bussSubjectsRel.setIncome(bussSubjects.getIncome());
                                bussSubjectsRel.setBackIncome(bussSubjects.getBackIncome());
                                bussSubjectsRel.setOtherId(bussSubjects.getOtherId());
                                bussSubjectsRel.setOtherName(bussSubjects.getOtherName());
                                bussSubjectsRel.setIncomeTenantId(bussSubjects.getIncomeTenantId());
                                bussSubjectsRel.setPoundageFee(bussSubjects.getPoundageFee());
                            }
                        }
                        // 优惠费用科目
                        if (bussSubjectsRel.getDisSubjectsId() > 0) {
                            // 判断有输入优惠金额
                            if (Objects.equals(bussSubjectsRel.getSubjectsId(),bussSubjects.getSubjectsId())) {
                                // 设置优惠费用科目
                                setDisSubjects(bussSubjectsRel, listFee, bussSubjects.getAmountFee(), businessId);
                            } else {
                                // 判断没有输入优惠金额
                                if (Objects.equals(bussSubjectsRel.getDisSubjectsId(),bussSubjects.getSubjectsId())) {
                                    // 设置优惠费用科目
                                    setDisSubjects(bussSubjectsRel, listFee,
                                            Long.parseLong(bussSubjectsRel.getRuleValue()), businessId);
                                }
                            }
                        }
                    }
                    if (!hasSubject) {
                        listFee.remove(bussSubjectsRel);
                    }
                } else {
                    // 没有输入费用和费用科目逻辑
                    if (bussSubjectsRel.getRuleType() == EnumConsts.PayInter.NON_FIXATION) {
                        // 非固定费用
                        bussSubjectsRel.setAmountFee(Long.parseLong(bussSubjectsRel.getRuleValue()));
                    }
                    if (bussSubjectsRel.getRuleType() == EnumConsts.PayInter.PROPORTION) {
                        // 比例
                    }
                    if (bussSubjectsRel.getRuleType() == EnumConsts.PayInter.CLASS) {
                        // 实现类
                    }
                    if (bussSubjectsRel.getRuleType() == EnumConsts.PayInter.FIXED) {
                        // 固定值
                        bussSubjectsRel.setAmountFee(Long.parseLong(bussSubjectsRel.getRuleValue()));
                    }
                    // 设置优惠科目
                    if (bussSubjectsRel.getDisSubjectsId() > 0) {
                        // 设置优惠费用科目
                        setDisSubjects(bussSubjectsRel, listFee, Long.parseLong(bussSubjectsRel.getRuleValue()),
                                businessId);
                    }
                }
            }
        }
        log.info("算费接口：费用科目条数" + listFee.size());
        return listFee;
    }

    @Override
    public List<BusiSubjectsRel> getBusiSubjectsRel(Long businessId, Long tenantId) {
        LambdaQueryWrapper<BusiSubjectsRel> lambda= Wrappers.lambdaQuery();
        lambda.eq(BusiSubjectsRel::getBusinessId,businessId)
              .orderByAsc(BusiSubjectsRel::getSortid);
        return this.list(lambda);
    }

    @Override
    public void paySucessOilCallBack(PayoutIntfDto payoutIntfDto) {
        if (payoutIntfDto == null) {
            throw new BusinessException("打款记录不能为空");
        }
        PayoutIntf payoutIntf = new PayoutIntf();
        BeanUtil.copyProperties(payoutIntfDto, payoutIntf);
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel OilFeeSubjectsRel = this.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_FEE_PRESTORE_RELEASE_1686,
                payoutIntf.getTxnAmt());
        busiList.add(OilFeeSubjectsRel);
    }

    @Override
    public List<BusiSubjectsRel> getBusiSubjectsRelList() {
        return baseMapper.selectList(null);
    }


    public void setDisSubjects(BusiSubjectsRel bussSubjectsRel, List<BusiSubjectsRel> listFee, Long AmountFee, Long businessId) {
        // 根据优惠费用科目ID查询科目表的定义
        SubjectsInfo subjects = subjectsInfoService.getSubjectsInfo(bussSubjectsRel.getDisSubjectsId(), -1L);
        BusiSubjectsRel disSubjectsRel = new BusiSubjectsRel();
        if (subjects != null) {
            // 设置优惠费用科目名称
            disSubjectsRel.setSubjectsName(subjects.getSubjectsName());
            // 设置优惠费用科目类型
            disSubjectsRel.setSubjectsType(subjects.getSubjectsType());
            // 设置备注
            disSubjectsRel.setRemarks(subjects.getSubjectsName());
        }
        // 设置优惠费用科目ID
        disSubjectsRel.setSubjectsId(bussSubjectsRel.getDisSubjectsId());
        // 优惠费用科目ID没有设置为0
        disSubjectsRel.setDisSubjectsId(0l);
        // 设置优惠费用金额
        disSubjectsRel.setAmountFee(AmountFee);
        // 设置优惠费用大类业务编号
        disSubjectsRel.setBusinessId(businessId);
        // 设置优惠费用业务编号
        disSubjectsRel.setBusinessType(bussSubjectsRel.getBusinessType());
        // 优惠费用规则类型
        disSubjectsRel.setRuleType(bussSubjectsRel.getRuleType());
        // 优惠费用状态
        disSubjectsRel.setStatus(1);
        disSubjectsRel.setBookType(bussSubjectsRel.getBookType());
        // 把优惠费用加入结果集
        listFee.add(disSubjectsRel);
    }
}
