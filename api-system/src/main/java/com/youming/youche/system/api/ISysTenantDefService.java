package com.youming.youche.system.api;

import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.TenantStaffRel;
import com.youming.youche.system.domain.ioer.ImportOrExportRecords;
import com.youming.youche.system.domain.tenant.BillSetting;
import com.youming.youche.system.dto.CheckPhoneDto;
import com.youming.youche.system.dto.SysTenantOutDto;
import com.youming.youche.system.dto.user.TenantOrUserBindCardDto;
import com.youming.youche.system.vo.SysTenantInfoVo;
import com.youming.youche.system.vo.SysTenantVo;
import com.youming.youche.system.vo.TenantQueryVo;

import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车队表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
public interface ISysTenantDefService extends IBaseService<SysTenantDef> {

    /**
     * 方法实现说明 获取当前登录者所能登录的车队
     *
     * @param tenantStaffRelList
     * @return java.util.List<com.youming.youche.system.domain.SysTenantDef>
     * @throws
     * @author terry
     * @date 2022/1/4 18:42
     */
    List<SysTenantDef> getByIds(List<TenantStaffRel> tenantStaffRelList);

    /**
     * 方法实现说明 获取当前登录者的所属车队信息
     * @author      terry
     * @param accessToken 令牌
     * @return      java.util.List<com.youming.youche.system.domain.SysTenantDef>
     * @exception
     * @date        2022/5/31 14:38
     */
    List<SysTenantDef> selectByUserId(String accessToken);

    /**
     * 方法实现说明 查询当前是否是车队管理员，是：返回车队信息
     * @author      terry
     * @param userInfoId
     * @return      com.youming.youche.system.domain.SysTenantDef
     * @exception
     * @date        2022/5/31 14:47
     */
    SysTenantDef selectByAdminUser(Long userInfoId);

    /**
     * 方法实现说明 选择车队，并绑定token
     * @author      terry
     * @param accessToken
     * @param tenantId
     * @return      boolean
     * @exception
     * @date        2022/5/31 14:48
     */
    boolean choose(String accessToken, Long tenantId);

    /**
     * 查询车队的部门是否是其他车队的责任部门
     *
     * @param orgId
     * @return boolean
     * @throws
     * @author terry
     * @date 2022/1/9 15:34
     */
    boolean selectByPreSaleOrgIdOrAfterSaleOrgId(List<Long> orgId);

    //SysTenantDef selectByTenantIdAndState(Long tenantId,Integer state);

    /***
     * @Description: 运营平台车队列表查询
     * @Author: luwei
     * @Date: 2022/1/9 6:58 下午
     * @Param tenantQueryIn:
     * @Param pageNum:
     * @Param pageSize:
     * @return: com.github.pagehelper.PageInfo<com.youming.youche.system.domain.SysTenantDef>
     * @Version: 1.0
     **/
    PageInfo<SysTenantDef> queryTenant(TenantQueryVo tenantQueryVo, Integer pageNum, Integer pageSize);

    /***
     * @Description: 运营平台车队列表查询(导出)
     * @Author: luwei
     * @Date: 2022/2/18 1:41 上午
     * @Param tenantQueryVo:
     * @return: java.util.List<com.youming.youche.system.domain.SysTenantDef>
     * @Version: 1.0
     **/
    void queryTenantExport(TenantQueryVo tenantQueryVo,ImportOrExportRecords importOrExportRecords);

    /**
     * 方法实现说明 查询车队信息列表
     *
     * @author      terry
     * @param tenantQueryVo
     * @return      java.util.List<com.youming.youche.system.domain.SysTenantDef>
     * @exception
     * @date        2022/5/31 14:53
     */
    List<SysTenantDef> queryTenantList(TenantQueryVo tenantQueryVo);

    /***
     * @Description: 新增车队（运营端）
     * @Author: luwei
     * @Date: 2022/1/12 3:22 下午
     * @Param sysTenantIn: 入参
     * @return: void
     * @Version: 1.0
     **/
    Integer saveAndBuild(SysTenantVo sysTenantIn, String accessToken);

    /**
     * 创建车队时，填完手机号码后，需要通过此接口判断手机号码是否可用<br/>
     * 如果可用,availavble会设为true，<br/>
     * 如果手机号码存在记录，会返回姓名及身份证号码(如果返回姓名及身份证号码，这两个数据不可修改)
     *
     * @see CheckPhoneDto
     */
    CheckPhoneDto checkPhone(String phone);

    /***
     * @Description: 查询车队信息（运行端）
     * @Author: luwei
     * @Date: 2022/1/19 4:58 下午
     * @Param tenantId:
     * @return: com.youming.youche.system.dto.SysTenantDto
     * @Version: 1.0
     **/
    SysTenantVo getTenantById(Long tenantId);

    /***
     * @Description: 更新车队档案信息 不允许修改基本信息中的超级管理员手机号码、姓名、身份证
     * @Author: luwei
     * @Date: 2022/1/19 10:02 下午
     * @Param sysTenantVo:
     * @Param accessToken:
     * @return: java.lang.Integer
     * @Version: 1.0
     **/
    Integer updateTenant(SysTenantVo sysTenantVo, String accessToken);

    /**
     * 更新车队档案信息 不允许修改基本信息中的超级管理员手机号码、姓名、身份证
     * (“公司资质”)
     * @param sysTenantVo
     * @param accessToken
     * @return
     */
    Integer updateTenantInfo(SysTenantInfoVo sysTenantVo, String accessToken);


    /***
     * @Description: 重置密码
     * @Author: luwei
     * @Date: 2022/1/20 3:25 下午
     * @Param tenantId:
     * @return: java.lang.Integer
     * @Version: 1.0
     **/
    Integer resetPassword(Long tenantId,String accessToken);

    /***
     * @Description: 更新车队的状态
     * @Author: luwei
     * @Date: 2022/1/20 3:49 下午
     * @Param tenantId: 车队id
     * @Param state: 0、禁用， 1、在用
     * @Param reason:
     * @return: void
     * @Version: 1.0
     **/
    Integer updateState(Long tenantId, Integer state, String reason, String accessToken);

    /**
     * 更新票据信息
     *
     * @param paramBillSetting
     * @return
     * @throws Exception
     */
    Integer doUpdateBillSetting(BillSetting paramBillSetting);

    /**
     * 审核
     *
     * @param id
     * @param auditState   审核状态：1-待审核 2-审核通过 3-审核不通过
     * @param auditContent 审核原因
     */
    void audit(Long id, Integer auditState, String auditContent, String accessToken);

    /***
     * @Description: 修改入场日期
     * @Author: luwei
     * @Date: 2022/1/21 2:38 下午
     * @Param tenantId:
     * @Param payDate:
     * @Param leaveDate:
     * @Param entranceDate:
     * @return: void
     * @Version: 1.0
     **/
    void updatePayDateAndLeaveDate(Long tenantId, String payDate, String leaveDate, String entranceDate, String accessToken) throws Exception;

    /***
     * @Description: todo
     * @Author: 导出
     * @Date: 2022/2/18 5:40 下午
     * @Param records:
     * @return: boolean
     * @Version: 1.0
     **/
    boolean export(ImportOrExportRecords records);

    /**
     * 10027 APP接口-查询租户办公地址
     * 查询租户信息
     *
     * @param tenantId 租户编号
     * @return tenantName 租户名称
     * tenantLogo 租户loggo
     * detailAddr 办公地址
     */
    Map getTenantInfo(long tenantId);

    /**
     * 方法实现说明 查询车队管理员
     * @author      terry
     * @param tenantId 车队id
     * @return      java.lang.Long
     * @exception
     * @date        2022/5/31 15:08
     */
    SysTenantDef getSysTenantDef(long tenantId, boolean isAllTenant);

    /**
     * 方法实现说明 查询正常状态车队信息
     * @author      terry
     * @param tenantId 车队id
     * @return      java.lang.Long
     * @exception
     * @date        2022/5/31 15:08
     */
    SysTenantDef getSysTenantDef(long tenantId);

    /**
     * 方法实现说明 查询车队管理员
     * @author      terry
     * @param tenantId 车队id
     * @return      java.lang.Long
     * @exception
     * @date        2022/5/31 15:08
     */
    Long getTenantAdminUser(Long tenantId);

    /**
     * 方法实现说明 根据管理id查询车队
     * @author      terry
     * @param userId
     * @return      com.youming.youche.system.domain.SysTenantDef
     * @exception
     * @date        2022/5/31 15:07
     */
    SysTenantDef getSysTenantDefByAdminUserId(Long userId);

    /**
     * 方法实现说明 查询当前登录者车队信息
     * @author      terry
     * @param accessToken
     * @return      com.youming.youche.system.domain.SysTenantDef
     * @exception
     * @date        2022/5/31 15:06
     */
    SysTenantDef getSysTenantDef(String accessToken);

    /**
     * 判断是否为超级管理员
     * @param userId
     * @param tenantId
     * @return
     */
    Boolean isAdminUser(Long userId, Long tenantId);

    /**
     * 车队档案添加字段--缴费日期回填
     */
    void updatePayDate(Long tenantId, Date payDate);

    /**
     * 资金会写付款状态
     * @param state （0、未到期；1、付款中；2、付款成功；3、已逾期）
     */
    void updatePayState(long tenantId, int state);

    /**
     * 是否设置支付密码，编号 10021
     *
     * @param userId   用户编号
     * @param userType 用户类型 1-员工，2-其他 （如果是员工，车队超管设置了支付密码则为设置了支付密码）
     * @return result Y-已设置，N-未设置
     */
    Boolean isSetPayPasswd(Long userId, Integer userType, String accessToken);

    /**
     * 根据租户Id获取租户信息及账户信息
     *
     * @param tenantId
     * @return
     */
    SysTenantOutDto getSysTenantDefById(Long tenantId);

    /**
     * 根据车队名称模糊查询车队
     * @param name
     * @return
     */
    List<SysTenantDef> getSysTenantDefByName(String name);

    /**
     * 根据userId判断是不是某个租户的超级管理员
     */
    Boolean isAdminUser(Long userId);

    /**
     * 方法实现说明 根据管理员用户id查询车队
     * @author      terry
     * @param userId
     * @param isAllTenant
     * @return      com.youming.youche.system.domain.SysTenantDef
     * @exception
     * @date        2022/5/31 14:59
     */
    SysTenantDef getSysTenantDefByAdminUserId(Long userId, Boolean isAllTenant);

    /**
     * 接口编号 10030
     * <p>
     * 判断租户或用户是否已绑定银行卡
     *
     * @param tenantId 租户编号
     */
    TenantOrUserBindCardDto isTenantOrUserBindCard(Long tenantId, Long userId, Integer type);

    /**
     * 获取对应车队名称
     *
     * @param tenantId 车队id
     * @return 车队名称
     */
    String getTenantName(Long tenantId);

    /**
     * 查询车队信息
     *
     * @param tenantName 租户名称(车队全称)
     * @return
     */
    List<SysTenantDef> querySysTenantDef(String tenantName);

    /**
     * 查询车队信息
     *
     * @param tenantName      租户名称(车队全称)
     * @param excludeTenantId 车队id
     * @return
     */
    List<SysTenantDef> querySysTenantDef(String tenantName, Long excludeTenantId);

    /**
     * 查询车队信息
     *
     * @param tenantName      租户名称(车队全称)
     * @param excludeTenantId 车队id
     * @param virtualState    小车队状态（0、不是小车队；1、新建的小车队；2、申请升级；3、审核不通过；4、审核通过）
     * @return
     */
    List<SysTenantDef> querySysTenantDef(String tenantName, Long excludeTenantId, Integer virtualState);

    Long getTenantIdCount();

    List<Long> getTenantId(Integer startLimit, Integer endLimit);

}
