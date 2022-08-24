package com.youming.youche.order.provider.mapper.order;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.order.domain.order.PinganLockInfo;
import com.youming.youche.order.dto.order.AccountBankRelDto;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface PinganLockInfoMapper extends BaseMapper<PinganLockInfo> {


    Long getAccountLockSum(@Param("userId") Long userId);

    Page<AccountBankRelDto> queryLockBalanceDetails( Page<AccountBankRelDto> page,
                                                     @Param("userId") Long userId);
}
