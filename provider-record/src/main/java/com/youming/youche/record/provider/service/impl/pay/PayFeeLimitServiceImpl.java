package com.youming.youche.record.provider.service.impl.pay;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.record.api.pay.IPayFeeLimitService;
import com.youming.youche.record.api.user.IUserDataInfoRecordService;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.record.domain.pay.PayFeeLimit;
import com.youming.youche.record.provider.mapper.pay.PayFeeLimitMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 付款限制表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-20
 */
@DubboService(version = "1.0.0")
public class PayFeeLimitServiceImpl extends BaseServiceImpl<PayFeeLimitMapper, PayFeeLimit> implements IPayFeeLimitService {

    @Autowired
    PayFeeLimitMapper payFeeLimitMapper;

    @Autowired
    IUserDataInfoRecordService iUserDataInfoRecordService;


    @Override
    public List<PayFeeLimit> queryPayFeeLimit(Map map) {
        Integer type = DataFormat.getIntKey(map, "type");
        Integer subType = DataFormat.getIntKey(map, "subType");
        Long tenantId = DataFormat.getLongKey(map, "tenantId");
        QueryWrapper<PayFeeLimit> queryWrapper=new QueryWrapper<>();

        if (subType != null && subType > 0) {
            queryWrapper.eq("SUB_TYPE",subType);
        }
        if (type != null && type > 0) {
            queryWrapper.eq("TYPE",type);

        }
        if (tenantId != null && tenantId > 0) {
            queryWrapper.eq("TENANT_ID",tenantId);
        }
        List<PayFeeLimit> list = payFeeLimitMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public String judgePayFeeLimit(Long value, String type, String subType, Long tenantId,String accessToken){
        String message="";
        LoginInfo user= iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        tenantId=user.getTenantId();
        if (value != null) {
            if (type == null || "".equals(type)) {
                message="费用支出类型不能为空";
            }
            if (subType == null || "".equals(subType)) {
                message="费用支出科目不能为空";
            }
            if (tenantId == null || tenantId <= 0) {
                message="请输入租户id";
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
            Map<String, String> mapParam = new HashMap<String, String>();
            mapParam.put(EnumConsts.PAY_FEE_LIMIT.TYPE, type);
            mapParam.put(EnumConsts.PAY_FEE_LIMIT.SUB_TYPE, subType);
            mapParam.put("tenantId",tenantId + "");
            List<PayFeeLimit> payFeeLimitList = this.queryPayFeeLimit(mapParam);
            if (payFeeLimitList != null && payFeeLimitList.size() > 0) {
                PayFeeLimit payFeeLimit = payFeeLimitList.get(0);
                if (payFeeLimit.getValue() != null) {
                    if (EnumConsts.PAY_FEE_LIMIT.SUB_TYPE12.equals(subType)) {
                        if (Math.abs(value) >= payFeeLimit.getValue()) {
                            message=payFeeLimit.getSubTypeName() + "不能大于" + payFeeLimit.getValue() + unit;
                        }
                    } else if (EnumConsts.PAY_FEE_LIMIT.SUB_TYPE5.equals(subType)) {
                        if (Math.abs(value) > payFeeLimit.getValue()) {
                            message="每辆车每个月导入(录入)" + payFeeLimit.getSubTypeName() + "不能大于" + payFeeLimit.getValue()/100.0 + unit;
                        }
                    } else {
                        if (Math.abs(value) > payFeeLimit.getValue()) {
                            message=payFeeLimit.getSubTypeName() + "不能大于" + payFeeLimit.getValue()/100.0 + unit;
                        }
                    }
                }else {
                    message= payFeeLimit.getSubTypeName() + "值配置项为空";
                }
            }else{
                message="未找到车队id为" + tenantId + "的资金风控相关配置项";
            }
        }else {
            message="输入比较值不能为空且必须大于0";
        }
        return message;
    }

}
