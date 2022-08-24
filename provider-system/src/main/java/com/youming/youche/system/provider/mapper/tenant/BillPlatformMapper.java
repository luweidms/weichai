package com.youming.youche.system.provider.mapper.tenant;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.tenant.BillPlatform;
import com.youming.youche.system.dto.BillPlatformDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 票据平台表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
public interface BillPlatformMapper extends BaseMapper<BillPlatform> {

    /***
     * @Description: 查询票据平台信息列表
     * @Author: luwei
     * @Date: 2022/1/22 10:30 上午
     * @Param param:
     * @return: java.util.List<com.youming.youche.system.domain.tenant.BillPlatform>
     * @Version: 1.0
     **/
    List<BillPlatformDto> queryBillPlatformList(@Param("param") BillPlatform param);

}
