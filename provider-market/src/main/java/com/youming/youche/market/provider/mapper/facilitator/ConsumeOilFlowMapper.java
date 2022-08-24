package com.youming.youche.market.provider.mapper.facilitator;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.market.domain.facilitator.ConsumeOilFlow;
import com.youming.youche.market.domain.facilitator.OilTransaction;
import com.youming.youche.market.dto.facilitator.ConsumeOilFlowDto;
import com.youming.youche.market.dto.facilitator.ServiceUnexpiredDetailDto;
import com.youming.youche.market.dto.facilitator.UserRepairInfoDto;
import com.youming.youche.market.dto.youca.ConsumeOilFlowDtos;
import com.youming.youche.market.vo.youca.ConsumeOilFlowVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-09
 */
public interface ConsumeOilFlowMapper extends BaseMapper<ConsumeOilFlow> {

    /**
     * 查询油的交易记录
     * @return
     * @throws Exception
     */
    Page<ConsumeOilFlowDto> getUserOilFlow(Page<ConsumeOilFlowDto> page,
                                            @Param("oilTransaction") OilTransaction oilTransaction,
                                            @Param("userType")Integer userType,
                                            @Param("payUserType") Integer payUserType,
                                            @Param("tenantId") Long tenantId);


    /**
     * 查询油的交易记录
     * @return
     * @throws Exception
     */
    List<ConsumeOilFlowDto> getUserOilFlowByUserId(@Param("serviceUserId") Long serviceUserId,@Param("statrtTime") String statrtTime,
                                                   @Param("endTime") String endTime,Long tenantId);

    List<ConsumeOilFlowDto> getUserOilFlowExport(@Param("orderId") String orderId, @Param("tradeTimeStart") String tradeTimeStart,
                                            @Param("tradeTimeEnd") String tradeTimeEnd, @Param("consumerName") String consumerName,
                                            @Param("consumerBill") String consumerBill, @Param("settlTimeStart") String settlTimeStart,
                                            @Param("settlTimeEnd") String settlTimeEnd, @Param("state") Integer state,
                                            @Param("tenantId") Long tenantId, @Param("productId") Long productId,
                                            @Param("userType") Integer userType, @Param("payUserType") Integer payUserType);

    /**
     * 统计
     * @return
     */
    ConsumeOilFlowDto totalUserOilFlow(@Param("oilTransaction") OilTransaction oilTransaction,@Param("userType") Integer userType,
                                       @Param("payUserType") Integer payUserType,@Param("tenantId")Long tenantId);


    /**
     * 查询维修交易
     * @return
     * @throws Exception
     */
    List<UserRepairInfoDto> getUserRepairListByUserId(@Param("serviceUserId") Long serviceUserId,@Param("statrtTime") String statrtTime,@Param("endTime") String endTime,@Param("tenantId")Long tenantId);



    /**
     * 查询维修交易
     * @return
     * @throws Exception
     */
    Page<UserRepairInfoDto> getUserRepairList(Page<UserRepairInfoDto> page,@Param("oilTransaction") OilTransaction oilTransaction,
                                              @Param("tenantId")Long tenantId,@Param("isTotal")Integer isTotal);

    /**
     * 查询维修交易
     * @return
     * @throws Exception
     */
    List<UserRepairInfoDto> getUserRepairListCount(@Param("oilTransaction") OilTransaction oilTransaction,
                                              @Param("tenantId")Long tenantId,@Param("isTotal")Integer isTotal);

    List<UserRepairInfoDto> getUserRepairListExport(@Param("orderId") String orderId, @Param("tradeTimeStart") String tradeTimeStart,
                                                    @Param("tradeTimeEnd") String tradeTimeEnd, @Param("consumerName") String consumerName,
                                                    @Param("consumerBill") String consumerBill, @Param("settlTimeStart") String settlTimeStart,
                                                    @Param("settlTimeEnd") String settlTimeEnd, @Param("state") Integer state,
                                                    @Param("tenantId") Long tenantId, @Param("productId") Long productId,@Param("isTotal") Integer isTotal);

    List<Long> queryTenantId(Long tenantId);

    Page<com.youming.youche.market.dto.youca.ConsumeOilFlowDto> queryAll(Page<com.youming.youche.market.dto.youca.ConsumeOilFlowDto> page, Long tenantId, ConsumeOilFlowVo consumeOilFlowVo);

    /**
     * 油卡消费记录-扫码加油导出
     * @param tenantId
     * @param consumeOilFlowVo
     * @return
     */
    List<com.youming.youche.market.dto.youca.ConsumeOilFlowDto> queryAllExport(Long tenantId, ConsumeOilFlowVo consumeOilFlowVo);


    List<com.youming.youche.market.dto.youca.ConsumeOilFlowDto> queryAllListExport(Long tenantId);

    List<ConsumeOilFlowDtos> queryAllSum(@Param("tenantId") Long tenantId,@Param("consumeOilFlowVo") ConsumeOilFlowVo consumeOilFlowVo);

    Page<ServiceUnexpiredDetailDto> getServiceUnexpiredDetail(Page<ServiceUnexpiredDetailDto> page,
                                                              @Param("orderId") String orderId,
                                                              @Param("name") String name,
                                                              @Param("sourceTenantId") String sourceTenantId,
                                                              @Param("userId") Long userId,
                                                              @Param("serviceType") Integer serviceType);


    List<ConsumeOilFlowVo> getConsumeOilFlowProductIds(  @Param("productIds")List<Long> productIds);

    /**
     * 获取指定车队指定服务商的未到期金额总和
     */
    Long getTotalMarginBalance(@Param("serviceUserId") Long serviceUserId, @Param("tenantId") Long tenantId);

    /**
     * 获取指定车队指定服务商的未到期金额总和
     */
    Long getTotalMarginBalanceTwo(@Param("serviceUserId") Long serviceUserId, @Param("tenantId") Long tenantId);
}
