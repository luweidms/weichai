package com.youming.youche.order.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.OrderDriverSubsidy;
import com.youming.youche.order.dto.OrderDriverSubsidyDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-12
 */
public interface IOrderDriverSubsidyService extends IBaseService<OrderDriverSubsidy> {
    /**
     * @param userId     司机id
     * @param driverType 司机类型，1主驾；2副驾；3切换司机
     * @param tenantId   租户id
     * @return
     */
    OrderDriverSubsidy getSubsidyByUserId(Long userId, Integer driverType, Long tenantId);

    /**
     * 修改版本记录过期状态
     */
    void updateDriverSubsidyVer(Long orderId, Long userId, Integer driverType, Integer verStatus);


    /**
     * 查找订单版本记录
     *
     * @param orderId
     * @param userId
     * @param tenantId
     * @return
     */
    List<OrderDriverSubsidy> findDriverSubsidysByOrder(Long orderId, Long userId, Long tenantId);


    /**
     * 获取司机补贴
     *
     * @param orderId
     * @param userId
     * @param isHis
     * @return
     * @throws Exception
     */
    List<OrderDriverSubsidy> getDriverSubsidys(Long orderId, Long userId,
                                               Boolean isHis, LoginInfo user);


    /**
     * 查找司机补贴
     *
     * @param startDate      补贴开始时间
     * @param endDate        补贴结束时间
     * @param orderId        订单号
     * @param userId
     * @param tenantId
     * @param isHis          是否查历史
     * @param excludeOrderId 排除订单
     * @return
     */
    List<OrderDriverSubsidy> findDriverSubsidys(LocalDateTime startDate, LocalDateTime endDate, Long orderId,
                                                Long userId, Long tenantId, Boolean isHis, Long excludeOrderId);


    /**
     * 是否需要支付补贴
     *
     * @param orderId 订单号
     * @return
     * @throws Exception
     */
    Boolean isPayDriverSubsidy(Long orderId, LoginInfo user);


    /**
     * 查找司机未付补贴
     *
     * @param orderId       订单号
     * @param userId
     * @param carDriverId   排除主驾ID
     * @param copilotUserId 排除副驾ID
     * @param tenantId
     * @param isHis         是否查历史
     * @return
     */
    List<OrderDriverSubsidyDto> findDriverNoPaySubsidys(Long orderId, Long userId, Long carDriverId, Long copilotUserId, Long tenantId, Boolean isHis);


    /**
     * 同步司机补贴（从版本记录同步到现有表）
     *
     * @param orderId
     */
    void sycDriverSubsidyFromVer(Long orderId, LoginInfo user);


    /***
     * 删除司机在订单下面的补贴
     * @param orderId
     */
    void deleteDriverSubsidy(Long orderId, Long userId, Integer driverType);

    /**
     * 获取订单司机补贴
     *
     * @param orderId
     * @param userId
     * @param isPayed 是否支付
     * @return
     */
    Long findOrderDriverSubSidyFee(Long orderId, Long userId, Long carDriverId, Long copilotUserId, Boolean isVer, Integer isPayed);


    /**
     * 获取订单司机补贴
     *
     * @param orderId
     * @param userId
     * @param isPayed 是否支付
     * @return 历史查询
     * @throws Exception
     */
    Long findOrderHDriverSubSidyFee(Long orderId, Long userId, Long carDriverId, Long copilotUserId, Integer isPayed);

    /**
     * 修改司机补贴支付状态
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @param isPayed 支付状态
     */
    String updateDriverSubsidyPayType(Long orderId, Long userId, Integer isPayed);

    /**
     * 获取各司机类型版本的记录
     * 0：主驾的补贴，1副驾的补贴，2修改后的司机补贴
     *
     * @param orderId
     * @return
     */
    String[] getDriverSubsidyVer(Long orderId,Long tenantId);

}
