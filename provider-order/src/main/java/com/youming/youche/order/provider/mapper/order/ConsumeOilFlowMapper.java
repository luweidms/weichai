package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.order.domain.order.ConsumeOilFlow;
import com.youming.youche.order.dto.ConsumeOilFlowDto;
import com.youming.youche.order.dto.ConsumeOilFlowWxDto;
import com.youming.youche.order.dto.ConsumeOilFlowWxOutDto;
import com.youming.youche.order.vo.AdvanceExpireOutVo;
import com.youming.youche.order.vo.ConsumeOilFlowVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 消费油记录表Mapper接口
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
public interface ConsumeOilFlowMapper extends BaseMapper<ConsumeOilFlow> {
    /**
     * 查询到期列表
     * @param page
     * @param advanceExpireOutVo
     * @return
     */
    Page<ConsumeOilFlow> queryConsumeOilFlowsNew(@Param("page") Page<ConsumeOilFlow> page, @Param("advanceExpireOutVo") AdvanceExpireOutVo advanceExpireOutVo);

    /**
     * 油老板未到期记录查询
     * @param costType 消费类型
     * @param states 状态
     * @param getDate 到期时间
     * @param userType
     * @param payUserType
     * @throws Exception
     * @return list
     */
    List<ConsumeOilFlow> getConsumeOilFlowNew(int costType, Integer[] states, Date getDate, Integer userType, Integer payUserType,String orderId);

    /**
     * 根据流水号查询司机加油记录
     * @param flowId 用户id
     * @throws Exception
     * @return list
     */
    ConsumeOilFlow getConsumeOilFlow(Long flowId);


    ConsumeOilFlowWxOutDto  getConsumeOilFlowSumByWx(@Param("consumeOilFlowVo") ConsumeOilFlowVo consumeOilFlowVo);


    /**
     * 查询某个产品的记录
     * @param consumeOilFlowVo
     * @return
     */
    Page<ConsumeOilFlowWxDto>  getConsumeOilFlowByWx(@Param("page") Page<ConsumeOilFlowWxDto> page, @Param("consumeOilFlowVo") ConsumeOilFlowVo consumeOilFlowVo);


    /**
     * 查询某个产品的记录
     * @param consumeOilFlowVo
     * @return
     */
    List<ConsumeOilFlowWxDto>  getConsumeOilFlowByWxOk(@Param("consumeOilFlowVo") ConsumeOilFlowVo consumeOilFlowVo);


    /**
     * 根据加油流水更改加油记录
     * @param flowIds
     */
    ConsumeOilFlowDto doQueryConsumeOilFlowTxm(@Param("flowIds") List<String> flowIds);

    ConsumeOilFlow getConsumeOilFlows(@Param("flowId") Long flowId);

    Page<ConsumeOilFlowDto> getConsumeOilFlowOut (Page<ConsumeOilFlowDto> page , @Param("vo")ConsumeOilFlowVo vo);


    ConsumeOilFlowDto doQueryOilSum (@Param("userId")Long userId,
                                     @Param("userType")Integer userType);

    ConsumeOilFlowDto doQuerySharedOilSum (@Param("userId")Long userId,
                                     @Param("userType")Integer userType);

    ConsumeOilFlowDto doQuerySharedOilSums (@Param("userId")Long userId,
                                     @Param("userType")Integer userType);


    List<ConsumeOilFlowDto> selectSqlOrder (@Param("userId")Long userId,
                                                 @Param("userType")Integer userType);

    List<ConsumeOilFlowDto> selectSqlOil (@Param("userId")Long userId,
                                            @Param("userType")Integer userType);
}
