package com.youming.youche.system.api.tenant;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.tenant.BillPlatform;
import com.youming.youche.system.dto.BillPlatformDto;
import com.youming.youche.system.dto.Rate;

import java.util.List;

/**
 * <p>
 * 票据平台表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
public interface IBillPlatformService extends IBaseService<BillPlatform> {

    /**
     * 查询票据平台信息列表
     *
     * @param param （根据属性值过滤记录，查询所有记录传null）
     * @return
     */
    List<BillPlatformDto> queryBillPlatformList(BillPlatform param);


    /**
     * 根据用户编号查询票据档案信息,启用或停用的都查出来
     * @param userId
     * @return
     */
    BillPlatform queryAllBillPlatformByUserId(long userId);
}
