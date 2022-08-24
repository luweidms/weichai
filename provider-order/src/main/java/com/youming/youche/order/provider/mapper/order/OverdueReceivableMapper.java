package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.dto.OverdueReceivableDto;
import com.youming.youche.order.domain.order.OverdueReceivable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应收逾期账单表Mapper接口
 * </p>
 *
 * @author wuhao
 * @since 2022-07-02
 */
public interface OverdueReceivableMapper extends BaseMapper<OverdueReceivable> {

    /***
     * @Description: 统计应收逾期总额
     * @Author: luwei
     * @Date: 2022/7/4 14:14
     * @Param tenantId:
     * @return: java.lang.Long
     * @Version: 1.0
     **/
    List<OverdueReceivableDto> sumOverdueReceivable(@Param("tenantId") Long tenantId, @Param("name") String name, @Param("adminUserId") Long adminUserId,@Param("flag") Integer flag,@Param("type") Integer type);


    /**
     * 统计应付逾期数据  指派车队
     */
    List<OverdueReceivableDto> sumOverduePayable(@Param("tenantId") Long tenantId);
}
