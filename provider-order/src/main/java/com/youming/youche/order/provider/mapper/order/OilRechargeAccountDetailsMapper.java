package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.order.domain.order.OilRechargeAccountDetails;
import com.youming.youche.order.dto.order.OilRechargeAccountDetailsDto;
import com.youming.youche.order.vo.OilRechargeAccountDetailsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface OilRechargeAccountDetailsMapper extends BaseMapper<OilRechargeAccountDetails> {


    Page<OilRechargeAccountDetailsDto> getLockBalanceDetailsOrder( Page<OilRechargeAccountDetailsDto> page,
                                                                  @Param("vo") OilRechargeAccountDetailsVo vo);

    List<OilRechargeAccountDetailsDto> queryOrderPayee( @Param("orderId") Long orderId);

    List<OilRechargeAccountDetailsDto> queryOrderPayeeH( @Param("orderId") Long orderId);
}
