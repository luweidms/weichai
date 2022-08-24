package com.youming.youche.system.provider.mapper.tenant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.tenant.ServiceChargeReminder;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 平台服务费到期记录表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-21
 */
public interface ServiceChargeReminderMapper extends BaseMapper<ServiceChargeReminder> {

    /*** 
     * @Description: 3天之内修改失效状态
     * @Author: luwei
     * @Date: 2022/1/21 4:15 下午
     * @Param tenantId: 
     * @Param payDate:
     * @return: int
     * @Version: 1.0
     **/
    int updateStatus(@Param("tenantId") Long tenantId, @Param("payDate") Date payDate, @Param("state") Integer state);

    /***
     * @Description: 大于3天修改失效状态
     * @Author: luwei
     * @Date: 2022/1/21 6:10 下午
     * @Param tenantId:
     * @Param payDate:
     * @Param state:
     * @return: int
     * @Version: 1.0
     **/
    int updateStatusSan(@Param("tenantId") Long tenantId, @Param("state") Integer state);

    /***
     * @Description: 查询到期缴费状态 0失效 1未交费 2缴费(打款中)3支付成功
     * @Author: luwei
     * @Date: 2022/1/21 5:13 下午
     * @Param userId:
     * @Param expireDate:
     * @return: java.util.List<com.youming.youche.system.domain.tenant.ServiceChargeReminder>
     * @Version: 1.0
     **/
    List<ServiceChargeReminder> queryServiceChargeReminder(@Param("userId") Long userId, @Param("expireDate") Date expireDate);
}
