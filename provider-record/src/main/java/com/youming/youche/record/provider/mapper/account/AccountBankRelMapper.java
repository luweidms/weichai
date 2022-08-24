package com.youming.youche.record.provider.mapper.account;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.account.AccountBankRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 银行卡表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface AccountBankRelMapper extends BaseMapper<AccountBankRel> {

    /**
     * 查询用户银行账户编号
     * @param userId   用户编号
     * @param userType 用户类型
     * @return
     */
    List<String> isUserTypeBindCardAll(@Param("userId") Long userId,@Param("userType")  Integer userType);

    List<AccountBankRel> queryAccountBankRel(@Param("userId") long userId,@Param("userType")Integer userType, @Param("bankType")Integer bankType);

    /**
     * 通过车牌号查询用户银行卡信息
     *
     * @param platNumber 车牌号
     * @return
     */
    List<AccountBankRel> getTenantCardListByPlatNumber(@Param("platNumber") String platNumber);
}
