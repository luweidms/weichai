package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.domain.order.PayFeeLimit;
import com.youming.youche.order.dto.PayFeeLimitDto;
import com.youming.youche.order.provider.mapper.order.PayFeeLimitMapper;
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
 * @since 2022-03-17
 */
@DubboService(version = "1.0.0")
@Service
public class PayFeeLimitServiceImpl extends BaseServiceImpl<PayFeeLimitMapper, PayFeeLimit> implements IPayFeeLimitService {

    @Resource
    private PayFeeLimitMapper payFeeLimitMapper;
    @Override
    public Long getAmountLimitCfgVal(Long tenantId, Integer cfgType) {

        if (tenantId < 0) {
            throw new BusinessException("租户编号参数错误");
        }

        if (cfgType < 0) {
            throw new BusinessException("cfgType参数错误");
        }
        LambdaQueryWrapper<PayFeeLimit> lambda=new QueryWrapper<PayFeeLimit>().lambda();
        lambda.eq(PayFeeLimit::getSubType,cfgType).eq(PayFeeLimit::getTenantId,tenantId);


        List<PayFeeLimit> list = this.list(lambda);
        if (list == null || list.isEmpty()) {

            if (cfgType > SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.AUTO_TRANSFER_401 && cfgType < SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.OIL_CARD_DEPOSIT_303)
                return 1l;
            else if (cfgType == SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.AUTO_TRANSFER_401) {
                return 0l;
            } else {
                return -1l;
            }

        }
        return (list.get(0)).getValue();

    }

    @Override
    public Boolean isMemberAutoTransfer(Long tendId) {
        LambdaQueryWrapper<PayFeeLimit> lambda= Wrappers.lambdaQuery();
        lambda.eq(PayFeeLimit::getType,SysStaticDataEnum.PAY_FEE_LIMIT_TYPE.AUTO_PAYMENT_4)
                .eq(PayFeeLimit::getSubType,SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.AUTO_TRANSFER_401);
        if (tendId != null && tendId > 0) {
            lambda.eq(PayFeeLimit::getTenantId, tendId);
        }
        List<PayFeeLimit> limitList = this.list(lambda);
        if (limitList == null || limitList.size() == 0) {
            return false;
        }

        if (limitList.get(0).getValue() == 1) {
            return true;
        } else {

            return false;
        }
    }

    @Override
    public void judgePayFeeLimit(Long value, String type, String subType, Long tenantId) {
        if (value != null) {
            if (type == null || "".equals(type)) {
                throw new BusinessException("费用支出类型不能为空");
            }
            if (subType == null || "".equals(subType)) {
                throw new BusinessException("费用支出科目不能为空");
            }
            if (tenantId == null || tenantId <= 0) {
                throw new BusinessException("请输入租户id");
            }
            String unit = EnumConsts.PAY_FEE_LIMIT.UNIT;
            if (EnumConsts.PAY_FEE_LIMIT.SUB_TYPE7.equals(subType)) {
                unit = EnumConsts.PAY_FEE_LIMIT.UNIT7;
            }
            if (EnumConsts.PAY_FEE_LIMIT.SUB_TYPE10.equals(subType)) {
                unit = EnumConsts.PAY_FEE_LIMIT.UNIT10;
            }
            if (EnumConsts.PAY_FEE_LIMIT.SUB_TYPE11.equals(subType)) {
                unit = EnumConsts.PAY_FEE_LIMIT.UNIT11;
            }
            if (EnumConsts.PAY_FEE_LIMIT.SUB_TYPE12.equals(subType)) {
                unit = EnumConsts.PAY_FEE_LIMIT.UNIT12;
            }
            List<PayFeeLimit> payFeeLimitList = this.queryPayFeeLimit(Integer.valueOf(type),
                    Integer.valueOf(subType),tenantId);
            if (payFeeLimitList != null && payFeeLimitList.size() > 0) {
                PayFeeLimit payFeeLimit = payFeeLimitList.get(0);
                if (payFeeLimit.getValue() != null) {
                    if (EnumConsts.PAY_FEE_LIMIT.SUB_TYPE12.equals(subType)) {
                        if (Math.abs(value) >= payFeeLimit.getValue()) {
                            throw new BusinessException(payFeeLimit.getSubTypeName() + "不能大于" + payFeeLimit.getValue() + unit);
                        }
                    } else if (EnumConsts.PAY_FEE_LIMIT.SUB_TYPE5.equals(subType)) {
                        if (Math.abs(value) > payFeeLimit.getValue()) {
                            throw new BusinessException("每辆车每个月导入(录入)" + payFeeLimit.getSubTypeName() + "不能大于" + payFeeLimit.getValue()/100.0 + unit);
                        }
                    } else {
                        if (Math.abs(value) > payFeeLimit.getValue()) {
                            throw new BusinessException(payFeeLimit.getSubTypeName() + "不能大于" + payFeeLimit.getValue()/100.0 + unit);
                        }
                    }
                } else {
                    throw new BusinessException(payFeeLimit.getSubTypeName() + "值配置项为空");
                }
            } else {
                throw new BusinessException("未找到车队id为" + tenantId + "的资金风控相关配置项");
            }
        } else {
            throw new BusinessException("输入比较值不能为空且必须大于0");
        }
    }

    @Override
    public List<PayFeeLimit> queryPayFeeLimit(Integer type, Integer subType, Long tenantId) {

        QueryWrapper<PayFeeLimit> payFeeLimitQueryWrapper = new QueryWrapper<>();


        if (subType != null && subType > 0) {
            payFeeLimitQueryWrapper.eq("sub_type",subType);
        }
        if (type != null && type > 0) {
            payFeeLimitQueryWrapper.eq("type",type);
        }
        if (tenantId != null && tenantId > 0) {
            payFeeLimitQueryWrapper.eq("tenant_id",tenantId);
        }
        List<PayFeeLimit> list = payFeeLimitMapper.selectList(payFeeLimitQueryWrapper);
        return list;
    }

    @Override
    public PayFeeLimitDto queryPayFeeLimit(Long tenantId, Integer subType) {
        List<PayFeeLimit> limitList = this.queryPayFeeLimit(null, subType, tenantId);
        if (limitList == null || limitList.size() == 0) {
            throw new BusinessException("该租户未设置该项资金风控信息");
        }

        PayFeeLimit payFeeLimit = limitList.get(0);

        PayFeeLimitDto dto = new PayFeeLimitDto();
        dto.setTenantId(tenantId);
        dto.setValue(payFeeLimit.getValueBig());

        return dto;
    }

}
