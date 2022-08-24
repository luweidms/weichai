package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.BillPlatform;

/**
 * <p>
 * 票据平台表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface IBillPlatformService extends IBaseService<BillPlatform> {
    /**
     *  获取票据平台收款账户 1:车队收款 2:司机收款
     * @param tenantId
     * @param vehicleAffiliation
     * @return
     * @throws Exception
     */
     Integer getPayAcctType(Long tenantId,String vehicleAffiliation);


    /**
     * 获取对应的平台开票的渠道
     * @param tenantId
     * @return
     */
    Long getBillMethodByTenantId(Long tenantId);

    /**
     * 根据用户编号查询票据档案信息，只查询启用的记录
     * @param userId
     * @return
     */
    BillPlatform queryBillPlatformByUserId(Long userId);

    /**

     * 根据用户id和票据平台，判断此用户id是否是对应的票据平台
     * @param userId
     * @param billPlatform SysStaticDataEnum.BILL_FORM_STATIC 枚举
     * @return
     * @throws Exception
     */
    Boolean judgeBillPlatform(Long openUserId,String billPlatform);
   /**
     * 根据用户编号查询票据档案信息,启用或停用的都查出来
     * @param userId
     * @return
     */
    BillPlatform queryAllBillPlatformByUserId(long userId);

    /**
     * 根据资金渠道类型获取开票方
     * @param userId
     * @return
     */
    String getPrefixByUserId(Long userId);

}
