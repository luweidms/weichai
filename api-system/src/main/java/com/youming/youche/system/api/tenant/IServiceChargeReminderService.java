package com.youming.youche.system.api.tenant;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.tenant.ServiceChargeReminder;

/**
 * <p>
 * 平台服务费到期记录表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-21
 */
public interface IServiceChargeReminderService extends IBaseService<ServiceChargeReminder> {

    /***
     * @Description: 操作车队服务费逾期记录
     * @Author: luwei
     * @Date: 2022/1/21 3:59 下午
     * @Param sysTenantDef:
     * @return: void
     * @Version: 1.0
     **/
    public void operateServiceChargeReminder(SysTenantDef sysTenantDef) throws Exception;

    /**
     * * @Description: 该函数的功能描述:根据业务编号查询服务费到期提醒详情
     */
    ServiceChargeReminder getServiceChargeReminder(String reminderId);

}
