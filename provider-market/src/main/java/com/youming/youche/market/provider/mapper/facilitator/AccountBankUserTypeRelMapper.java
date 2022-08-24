package com.youming.youche.market.provider.mapper.facilitator;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.market.domain.facilitator.AccountBankUserTypeRel;
import com.youming.youche.market.vo.maintenanceaudit.AccountBankByTenantIdVO;
import com.youming.youche.market.vo.maintenanceaudit.AccountBankUserTypeRelVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 银行卡跟用户类型的关系表Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-24
 */
public interface AccountBankUserTypeRelMapper extends BaseMapper<AccountBankUserTypeRel> {

    List<String> isUserTypeBindCard(@Param("userId") Long userId,
                                                    @Param("bankType") Integer bankType,
                                                    @Param("userType") Integer userType);

    /**
     * 根据用户编号查询账号信息
     * @param userId 用户编号-必填
     * @return
     */
    List<AccountBankUserTypeRelVo> queryAccountBankRel(@Param("userId") Long userId);

    /**
     * 根据租户id查询租户收款账号和收款人
     * @param tenantId
     * @return
     */
    List<AccountBankByTenantIdVO> getAccountBankRel(@Param("tenantId") Long tenantId);
}
