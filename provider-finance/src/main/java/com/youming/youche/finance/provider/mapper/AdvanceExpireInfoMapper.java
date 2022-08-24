package com.youming.youche.finance.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.finance.domain.AdvanceExpireInfo;
import com.youming.youche.finance.dto.AdvanceExpireOutDto;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.vo.AdvanceExpireOutVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;


/**
 * <p>
 * 手动到期沉淀表Mapper接口
 * </p>
 *
 * @author luona
 * @since 2022-04-13
 */
public interface AdvanceExpireInfoMapper extends BaseMapper<AdvanceExpireInfo> {


    /**
     * 即将到期列表，包含查询手动到期
     * 订单尾款到期、油到期、维修到期
     *
     * @param page
     * @param advanceExpireOutVo
     * @return
     */
    Page<OrderLimit> queryUndueExpires(@Param("page") Page<AdvanceExpireOutDto> page, @Param("advanceExpireOutVo") AdvanceExpireOutVo advanceExpireOutVo);


    /**
     * 根据业务流水号、到期类型查询手动到期数据
     *
     * @param flowId   业务流水 订单的未到期显示订单号、消费类显示流水号、维修类显示消费号、加油类显示系统流水号
     * @param signType 到期类型 1司机尾款到期 2油到期 3维修到期
     * @return
     */
    AdvanceExpireInfo getAdvanceExpireInfoByFlowId(long flowId, String signType);


    /**
     * 根据业务单号、到期类型查询手动到期数据
     *
     * @param flowIds  业务流水 订单的未到期显示订单号、消费类显示流水号、维修类显示消费号、加油类显示系统流水号
     * @param signType 到期类型 1司机尾款到期 2油到期 3维修到期
     * @return
     */
    Long getAdvanceExpireInfoByIds(String flowIds, String signType);


    /**
     * 修改尾款到期业务状态
     *
     * @param expireType
     * @param finalPlanDate
     * @param orderId
     * @return
     */
    int updateOrderLimit(@Param("expireType") Integer expireType, @Param("finalPlanDate") LocalDateTime finalPlanDate, @Param("orderId") Long orderId);


    /**
     * 修改油费业务状态
     *
     * @param fromFlowId
     * @param getDate
     * @return
     */
    int updateConsumeOilFlow(@Param("fromFlowId") Long fromFlowId, @Param("getDate") LocalDateTime getDate);

    /**
     * 修改维修保养业务状态
     * @param orderId
     * @param getDate
     * @return
     */
    int updateUserRepairMargin(@Param("orderId") Long orderId, @Param("getDate") LocalDateTime getDate);

}
