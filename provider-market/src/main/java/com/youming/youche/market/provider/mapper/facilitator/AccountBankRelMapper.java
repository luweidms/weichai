package com.youming.youche.market.provider.mapper.facilitator;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.market.domain.facilitator.AccountBankRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-24
 */
public interface AccountBankRelMapper extends BaseMapper<AccountBankRel> {
     /**
      * 查询归属下是否有银行
      * @param userId
      * @param bankType
      * @param userType
      * @return
      */
     List<String>  queryBank(@Param("userId") long userId,@Param("bankType") int bankType,@Param("userType") int userType);

}
