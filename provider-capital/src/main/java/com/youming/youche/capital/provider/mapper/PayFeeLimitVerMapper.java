package com.youming.youche.capital.provider.mapper;

import com.youming.youche.capital.domain.PayFeeLimitVer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.capital.vo.PayFeeLimitVerVo;

import java.util.List;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-03-03
 */
public interface PayFeeLimitVerMapper extends BaseMapper<PayFeeLimitVer> {
    List<PayFeeLimitVerVo> queryCheckFailMsg();

    List<PayFeeLimitVer> selectByFee(PayFeeLimitVer payFeeLimitVer);

    void updateByFee(PayFeeLimitVer payFeeLimitVer);

    void updateByIdAndFlag(PayFeeLimitVer payFeeLimitVer);

    void updateReRemark(PayFeeLimitVer payFeeLimitVer);

    void insertPay(PayFeeLimitVer payFeeLimitVer);
}
