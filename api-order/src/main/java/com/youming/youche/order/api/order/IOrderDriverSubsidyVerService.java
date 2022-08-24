package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.OrderDriverSubsidyVer;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* <p>
    *  服务类
    * </p>
* @author liangyan
* @since 2022-03-23
*/
    public interface IOrderDriverSubsidyVerService extends IBaseService<OrderDriverSubsidyVer> {

    /**
     * 保存司机补贴版本
     * @param orderId
     * @param userId
     * @param subsidyTime
     * @param driverType 1主驾；2副驾；3更换司机
     * @param isPayed
     */
    public void saveDriverSubsidyVer(Long orderId, Long userId, String subsidyTime, Long subsidySalary, Integer driverType, Integer isPayed, LoginInfo user);




    /**
     * 保存司机补贴
     * @param orderId
     * @param userId
     * @param subsidyDate
     * @param driverType 1主驾；2副驾；3更换司机
     */
     Boolean saveDriverSubsidy(Long orderId, Long userId, LocalDateTime subsidyDate,
                               Long subsidySalary, Integer driverType,LoginInfo user);
}
