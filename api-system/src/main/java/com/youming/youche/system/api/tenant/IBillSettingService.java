package com.youming.youche.system.api.tenant;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.tenant.BillSetting;
import com.youming.youche.system.dto.Rate;
import com.youming.youche.system.dto.RateItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 车队的开票设置 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-09
 */
public interface IBillSettingService extends IBaseService<BillSetting> {

    /***
     * @Description: 查询费率设置
     * @Author: luwei
     * @Date: 2022/1/19 9:29 下午
     * @Param rateId:
     * @return: com.youming.youche.system.dto.Rate
     * @Version: 1.0
     **/
    Rate getRateById(Long rateId);

    /***
     * @Description: 查询费率设置项（字表）
     * @Author: luwei
     * @Date: 2022/1/19 9:44 下午
     * @Param rateId:
     * @return: java.util.List<com.youming.youche.system.dto.RateItem>
     * @Version: 1.0
     **/
    List<RateItem> queryRateItem(Long rateId);
    
    /*** 
     * @Description: 查询费率
     * @Author: luwei
     * @Date: 2022/1/23 6:18 下午
     * @return: java.util.List<com.youming.youche.system.dto.Rate>
     * @Version: 1.0
     **/
    List<Rate> queryRateAll();

    /**
     *
     * @param tenantId
     * @return
     */
    BillSetting getBillSetting(Long tenantId);
}
