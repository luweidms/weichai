package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.order.AccountBankUserTypeRel;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 银行卡跟用户类型的关系表Mapper接口
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
public interface AccountBankUserTypeRelMapper extends BaseMapper<AccountBankUserTypeRel> {

    /**
     * 判断用户是否已绑定指定类型的银行卡
     */
    Integer isUserTypeBindCard(@Param("userId") Long userId, @Param("bankType") Integer bankType,
                               @Param("userType") Integer userType);

    /**
     * 判断用户是否已绑定银行卡
     *
     * @param userId 用户编号
     * @return true-已绑定，false-未绑定
     */
    Integer isUserTypeBindCardTwo(@Param("userId") Long userId, @Param("userType") Integer userType);

}
