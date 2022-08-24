package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.order.domain.order.OilRechargeAccount;
import com.youming.youche.order.dto.OilRechargeAccountDto;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface OilRechargeAccountMapper extends BaseMapper<OilRechargeAccount> {


    Page<OilRechargeAccountDto> queryOilAccountDetails(Page<OilRechargeAccountDto> page ,
                                                       @Param("userId") Long userId,
                                                       @Param("billId") String billId,
                                                       @Param("tenantName") String tenantName,
                                                       @Param("userType")Integer userType);


    /**
     * 账户明细列表
     * @return
     * @throws Exception
     */
    Page<OilRechargeAccountDto> doQuery(Page<OilRechargeAccountDto> page ,
                                        @Param("type")Integer type,
                                        @Param("tenantId")Long tenantId,
                                        @Param("userId")Long userId,
                                        @Param("sourceTypes")Integer[] sourceTypes);
}


