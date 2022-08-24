package com.youming.youche.capital.provider.mapper;

import com.youming.youche.capital.domain.PayFeeLimit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* <p>
* Mapper接口
* </p>
* @author Terry
* @since 2022-03-02
*/
public interface PayFeeLimitMapper extends BaseMapper<PayFeeLimit> {

    void insertPay(PayFeeLimit newPayFeeLimit);
}
