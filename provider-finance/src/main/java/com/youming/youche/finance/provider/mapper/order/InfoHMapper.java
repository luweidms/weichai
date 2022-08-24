package com.youming.youche.finance.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.order.InfoH;
import com.youming.youche.finance.dto.order.OrderDealInfoDto;
import com.youming.youche.finance.vo.order.OrderDealInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
* 新的订单表Mapper接口
* </p>
* @author liangyan
* @since 2022-03-15
*/
public interface InfoHMapper extends BaseMapper<InfoH> {

    List<OrderDealInfoDto> getDealOrderInfoList(@Param("orderDealInfoVo") OrderDealInfoVo orderDealInfoVo);

    Integer getDealOrderInfoCount(@Param("orderDealInfoVo")OrderDealInfoVo orderDealInfoVo);
}
