package com.youming.youche.record.api.tenant;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.tenant.TenantStaffRel;
import com.youming.youche.record.dto.StaffDataInfoDto;

import java.util.List;

/**
* <p>
* 员工信息 服务类
* </p>
* @author Terry
* @since 2021-11-21
*/
public interface ITenantStaffRelService extends IService<TenantStaffRel> {

    /**
     * 查询未删除员工信息
     *
     * @param userId 用户编号
     */
    List<TenantStaffRel> getStaffRel(Long userId);

    /**
     * 更新员工信息<br/>
     * 不需要审核，直接更新，并且在ver表记录变更
     */
    public void updateStaffInfo(TenantStaffRel tenantStaffRel) ;

    /**
     * 查询员工列表
     * @param phone 用户账号
     * @return
     */
    List<String>  queryStaffInfo(String phone);

    /**
     * 不分页获取员工列表
     */
     List<StaffDataInfoDto> queryStaffInfoList(StaffDataInfoDto staffDataInfoDto);

    /**
     * 查询员工信息
     *
     * @param userId 用户编号
     */
    List<TenantStaffRel> getTenantStaffRel(Long userId);


    /**
     * 根据用户编号获取用户所有的所属车队，不查询停用和冻结的车队
     */
    List<TenantStaffRel> getTenantStaffRelByUserId(Long userId);

}
