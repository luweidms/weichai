package com.youming.youche.record.provider.mapper.tenant;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.tenant.TenantStaffRel;
import com.youming.youche.record.dto.StaffDataInfoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
* 员工信息Mapper接口
* </p>
* @author Terry
* @since 2021-11-21
*/
public interface TenantStaffRelMapper extends BaseMapper<TenantStaffRel> {


    /***
     * @Description: 查询员工列表
     * @Author: luwei
     * @Date: 2022/2/22 10:46 下午
     * @Param phone:
     * @return: java.util.List<com.youming.youche.record.domain.tenant.TenantStaffRel>
     * @Version: 1.0
     **/
    List<String> queryStaffInfo(String phone);

    List<StaffDataInfoDto> selectPeople(@Param("staffDataInfoDto") StaffDataInfoDto staffDataInfoDto);

    List<TenantStaffRel> getTenantStaffRelByUserId(@Param("userId") Long userId);

}
