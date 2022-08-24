package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.order.domain.order.ClaimExpenseInfo;
import com.youming.youche.order.dto.ClaimExpenseCountDto;
import com.youming.youche.order.vo.ClaimExpenseInfoInVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 车管报销表Mapper接口
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
public interface ClaimExpenseInfoMapper extends BaseMapper<ClaimExpenseInfo> {
    /**
     * 根据订单查询报销的数量和金额
     *
     * @param orderId
     * @param userId
     */
    int queryClaimExpenseCount(@Param("orderId") Long orderId, @Param("userId") Long userId);

    Page<ClaimExpenseInfo> selectOr(@Param("infoIn")ClaimExpenseInfoInVo infoIn, @Param("split")List<String> split,
                                        @Param("hasAllDataPermission") Boolean hasAllDataPermission,
                                        @Param("lids")List<Long> lids, Page<Object> objectPage);
}
