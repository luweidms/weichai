package com.youming.youche.market.provider.mapper.etc;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.market.domain.etc.CmEtcInfo;
import com.youming.youche.market.dto.etc.CmEtcInfoDto;
import com.youming.youche.market.vo.etc.CalculatedEtcFeeVo;
import com.youming.youche.market.vo.etc.CmEtcInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper接口
 * </p>
 * @author Terry
 * @since 2022-03-11
 */
public interface CmEtcInfoMapper extends BaseMapper<CmEtcInfo> {

    /**
     * 聂杰伟
     * 订单ETC
     * @param page
     * @param plate_number 车牌号
     * @param order_id 订单号
     * @param est_arrive_date 靠台开始
     * @param real_arrive_date 考台结束
     * @param cost_model 成本模式
     * @param vehicle_type 车辆类型
     * @param tenantId
     * @return
     */
    List<CmEtcInfoDto> OrderETC (@Param("vo") CmEtcInfoVo vo,
                               @Param("tenantId")Long tenantId);


    /**
     * 根据车辆找订单(ETC扣费)
     *
     * @param tenantId   租户ID
     *
     * @return
     */
    com.youming.youche.market.dto.etc.order.OrderSchedulerDto queryOrderInfoByCar( @Param("tenantId")Long tenantId,
                                          @Param("id") Long id);

    com.youming.youche.market.dto.etc.order.OrderSchedulerDto queryOrderInfoByCarh(@Param("tenantId")Long tenantId,
                                                                                   @Param("id") Long id);

    /**
     * 获取指定对账单消费总金额
     * @param accountStatementNo
     * @return
     */
    Long calculatedEtcFeeMoney(@Param("accountStatementNo") String accountStatementNo);

    /**
     *
     * @param calculatedEtcFeeVo
     * @return
     */
    List<CmEtcInfo> calculatedEtcVoList(@Param("calculatedEtcFeeVo") CalculatedEtcFeeVo calculatedEtcFeeVo);

    /**
     * 获取指定对账单消费总金额
     * @param calculatedEtcFeeVo
     * @return
     */
    Long calculatedEtcFeeVoMoney(@Param("calculatedEtcFeeVo") CalculatedEtcFeeVo calculatedEtcFeeVo);


    /**
     * etc 消费列表查询
     * @param page
     * @param vo
     * @param tenantId
     * @return
     */
    Page<CmEtcInfo> getAll (Page<CmEtcInfo> page,
                             @Param("vo") CmEtcInfoVo vo,
                             @Param("tenantId") Long tenantId);


    /**
     * 条件查询etc管理
     *
     * @param plateNumber    车牌号
     * @param tenantId       车队id
     * @param month          消费时间
     * @param receiverPhone  接收人手机
     * @param receiverUserId 接收人id
     */
    List<CmEtcInfo> getCmEtcAccountStatement(@Param("plateNumber") String plateNumber, @Param("tenantId") Long tenantId, @Param("month") String month, @Param("receiverPhone") String receiverPhone, @Param("receiverUserId") Long receiverUserId);

    /**
     * 修改etc管理
     *
     * @param plateNumber    车牌号
     * @param tenantId       车队id
     * @param month          消费时间
     * @param receiverPhone  接收人手机
     * @param receiverUserId 接收人id
     */
    void updateCmEtcAccountStatement(@Param("plateNumber") String plateNumber,
                                     @Param("tenantId") Long tenantId,
                                     @Param("month") String month,
                                     @Param("receiverPhone") String receiverPhone,
                                     @Param("receiverUserId") Long receiverUserId,
                                     @Param("billNumber") String billNumber);
}
