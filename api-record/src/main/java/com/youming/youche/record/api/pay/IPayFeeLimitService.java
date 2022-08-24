package com.youming.youche.record.api.pay;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.pay.PayFeeLimit;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 付款限制表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-20
 */
public interface IPayFeeLimitService extends IService<PayFeeLimit> {

    /**
     * 查询车队付款限制
     *
     * @param map subType 科目
     *            type 类别
     *            tenantId 车队id
     */
    List<PayFeeLimit> queryPayFeeLimit(Map map) throws Exception;

    /**
     * 判断费用支出输入值与配置项比较
     * @param value  比较值
     * @param type  类型
     * @param subType   科目
     * @return
     * @throws Exception
     */
    public String judgePayFeeLimit(Long value, String type, String subType, Long tenantId,String accessToken);
}
