package com.youming.youche.system.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.ac.PayFeeLimit;
import com.youming.youche.system.vo.TenantQueryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车队表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
public interface SysTenantDefMapper extends BaseMapper<SysTenantDef> {

    // @Select("SELECT a.* FROM tenant_staff_rel a" +
    // " LEFT JOIN sys_tenant_def std ON a.TENANT_ID = std.TENANT_ID" +
    // " WHERE a.USER_ID = #{userId}" +
    // " AND a.STATE = 1" +
    // " AND a.LOCK_FLAG = 1" +
    // " AND std.STATE = 1")
    // List<TenantStaffRel> getTenantStaffRelByUserId(long userId);

    @Select("SELECT std.* FROM tenant_staff_rel a" + " LEFT JOIN sys_tenant_def std ON a.tenant_id = std.id"
            + " WHERE a.user_info_id = #{userInfoId}" + " AND a.state = 1" + " AND a.lock_flag = 1"
            + " AND std.state = 1")
    List<SysTenantDef> selectAllByUserId(Long userId);

    List<SysTenantDef> selectByPreSaleOrgIdOrAfterSaleOrgId(@Param("orgIds") List<Long> orgIds);

    /***
     * @Description: 运营平台-根据条件查询车队档案
     * @Author: luwei
     * @Date: 2022/1/9 11:45 上午
     * @Param tenantQueryIn:
     * @return: java.util.List<com.youming.youche.system.domain.SysTenantDef>
     * @Version: 1.0
     **/
    List<SysTenantDef> queryTenant(@Param("tenantQueryIn") TenantQueryVo tenantQueryIn);

    /***
     * @Description: 添加登陆信息
     * @Author: luwei
     * @Date: 2022/1/12 6:50 下午
     * @Param sysUser:
     * @return: int
     * @Version: 1.0
     **/
    int addSysOperator(@Param("sysUser") SysUser sysUser);

    /***
     * @Description: 查询用户登陆表
     * @Author: luwei
     * @Date: 2022/1/12 9:32 下午
     * @Param userId:
     * @return: com.youming.youche.commons.domain.SysUser
     * @Version: 1.0
     **/
    SysUser getSysOperatorByUserId(@Param("userId") Long userId);

    /***
     * @Description: 修改用户登陆信息
     * @Author: luwei
     * @Date: 2022/1/20 3:30 下午
     * @Param sysUser:
     * @return: int
     * @Version: 1.0
     **/
    int updateSysOperator(@Param("sysUser") SysUser sysUser);

    /***
     * @Description: 将其他车队邀请本车队司机或车辆的未审核记录全部设置为被驳回
     * @Author: luwei
     * @Date: 2022/1/20 4:27 下午

     * @return: int
     * @Version: 1.0
     **/
    int doVehicleApplyRecordLoseEfficacy(
            @Param("auditRemark") String auditRemark,
            @Param("stateNew") Integer stateNew,
            @Param("stateOld") Integer stateOld,
            @Param("auditDate") String auditDate,
            @Param("applyTenantId") Long applyTenantId,
            @Param("beApplyTenantId") Long beApplyTenantId,
            @Param("applyType") Integer applyType
    );

    List<PayFeeLimit> queryPayFeeLimit(Map map);

    /**
     * 根据车队名称模糊查询车队
     * @param name
     * @return
     */
    List<SysTenantDef> getSysTenantDefByName(String name);


    int updateTenantDefById(@Param("sysTenantDef") SysTenantDef sysTenantDef);

    Integer isTenantBindCard(@Param("tenantId")Long tenantId);

    Long getTenantIdCount();

    List<Long> getTenantId(@Param("startLimit") Integer startLimit, @Param("endLimit") Integer endLimit);
}
