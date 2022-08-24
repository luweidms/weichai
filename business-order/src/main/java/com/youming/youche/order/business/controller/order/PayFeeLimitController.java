package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.domain.order.PayFeeLimit;
import com.youming.youche.order.dto.PayFeeLimitDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import com.youming.youche.commons.base.BaseController;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
@RestController
@RequestMapping("payfeelimit")
public class PayFeeLimitController extends BaseController<PayFeeLimit, IPayFeeLimitService> {
    @DubboReference(version = "1.0.0")
    IPayFeeLimitService payFeeLimitService;
    @Override
    public IPayFeeLimitService getService() {
        return payFeeLimitService;
    }
    
    /**
     * 获取中标价最高限额
     * @author zag
     * @date 2022/3/30 20:40
     * @param tenantId 
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("/getAmountLimitCfgVal/{tenantId}")
    public ResponseResult getAmountLimitCfgVal(@PathVariable Long tenantId) {
        Long maxAmount = payFeeLimitService.getAmountLimitCfgVal(tenantId, SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.MAX_BID_AMOUNT_101);
        maxAmount = (maxAmount < 0 ? 0 : maxAmount);
        return ResponseResult.success(maxAmount);
    }

    /**
     * 14008
     * 查询资金风控
     *
     * @param tenantId--租户编号 type--风控类型，具体值如下
     *                       <p>
     *                       1	中标价上限
     *                       2	成本异常上限
     *                       3	收入异常上限
     *                       4	司机借支上限
     *                       5	招商车费用补款上限
     *                       6	司机基本工资上限
     *                       7	司机补贴上限
     *                       8	自有车司机总工资上限
     *                       9	合作个体户总工资上限
     *                       10	里程单价上限
     *                       11	扫码加油上限
     *                       12	扫码加油次数上限
     *                       13	是否自动打款
     */
    @PostMapping("queryPayFeeLimit")
    public ResponseResult queryPayFeeLimit(Long tenantId, Integer subType) {
        if (tenantId < 0 || subType < 0) {
            throw new BusinessException("查询参数不合法，请检查");
        }
        PayFeeLimitDto payFeeLimitDto = payFeeLimitService.queryPayFeeLimit(tenantId, subType);
        return ResponseResult.success(payFeeLimitDto);
    }

    /**
     * 查询租户资金风控的配置值
     * 接口编号 10042
     *
     * @param tenantId 租户编号
     * @param cfgType  1	中标价上限 ,返回结果为金额类型，单位为分
     *                 2	成本异常上限,返回结果为金额类型，单位为分
     *                 3	收入异常上限,返回结果为金额类型，单位为分
     *                 4	司机借支上限,返回结果为金额类型，单位为分
     *                 5	招商车费用补款上限,返回结果为金额类型，单位为分
     *                 6	司机基本工资上限,返回结果为金额类型，单位为分
     *                 7	基本模式补贴上限,返回结果为金额类型，单位为分
     *                 8	自有车司机总工资上限,返回结果为金额类型，单位为分
     *                 9	合作个体户总工资上限,返回结果为金额类型，单位为分
     *                 10	里程模式补贴上限,返回结果为金额类型，单位为分
     *                 11	扫码加油上限,返回结果为金额类型，单位为分
     *                 12	扫码加油次数上限,返回结果为整数
     *                 13	是否自动打款   （0-否，1-是）
     *                 14	自有车运费 （0-私人账户付款，1-对公账户付款）
     *                 15	司机工资 （0-私人账户付款，1-对公账户付款）
     *                 16      自有车油费是否需要发票（1-需要，0-不需要）
     */
    @PostMapping("getAmountLimitCfgVal")
    public ResponseResult getAmountLimitCfgVal(Long tenantId, Integer cfgType) {
        Long value = payFeeLimitService.getAmountLimitCfgVal(tenantId, cfgType);
        return ResponseResult.success(value);
    }

}
