package com.youming.youche.system.provider.mapper.tenant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.tenant.BillSetting;
import com.youming.youche.system.dto.Rate;
import com.youming.youche.system.dto.RateItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 车队的开票设置Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-09
 */
public interface BillSettingMapper extends BaseMapper<BillSetting> {

    @Select("select id,rate_name as rateName,create_time as createTime,update_time as updateTime from rate where id = #{rateId}")
    Rate getRateById(@Param("rateId") Long rateId);

    @Select("select id,rate_id as rateId,start_value,end_value,rate_value from rate_item where rate_id = #{rateId}")
    List<RateItem> queryRateItem(@Param("rateId") Long rateId);

    /***
     * @Description: 查询费率
     * @Author: luwei
     * @Date: 2022/1/23 6:18 下午
     * @return: java.util.List<com.youming.youche.system.dto.Rate>
     * @Version: 1.0
     **/
    @Select("select id,rate_name as rateName,create_time as createTime,update_time as updateTime from rate")
    List<Rate> queryRateAll();

}
