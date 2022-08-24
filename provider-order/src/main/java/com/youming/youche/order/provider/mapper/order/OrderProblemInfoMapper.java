package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.order.OrderProblemInfo;
import com.youming.youche.order.vo.QueryOrderProblemInfoQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单异常登记表Mapper接口
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
public interface OrderProblemInfoMapper extends BaseMapper<OrderProblemInfo> {

    List<OrderProblemInfo> queryOrderProblemInfoQuery(@Param("vo") QueryOrderProblemInfoQueryVo vo);

}
