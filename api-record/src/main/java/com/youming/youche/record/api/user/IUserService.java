package com.youming.youche.record.api.user;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.dto.ApplyRecordQueryDto;
import com.youming.youche.record.dto.UserInfoDto;
import com.youming.youche.record.dto.driver.DoQueryDriversDto;
import com.youming.youche.record.dto.driver.DriverApplyRecordNumDto;
import com.youming.youche.record.dto.driver.DriverQueryDto;
import com.youming.youche.record.vo.ApplyRecordQueryVo;
import com.youming.youche.record.vo.AuditApplyVo;
import com.youming.youche.record.vo.BeApplyRecordVo;
import com.youming.youche.record.vo.CfgVO;
import com.youming.youche.record.vo.driver.DoAddApplyAgainVo;
import com.youming.youche.record.vo.driver.DoAddApplyVo;
import com.youming.youche.record.vo.driver.DoAddDriverVo;
import com.youming.youche.record.vo.driver.DoAddOBMDriver;
import com.youming.youche.record.vo.driver.DoQueryDriversVo;
import com.youming.youche.record.vo.driver.DoQueryOBMDriversVo;
import com.youming.youche.record.vo.driver.DriverApplyRecordVo;
import com.youming.youche.record.vo.driver.DriverAuditVo;
import com.youming.youche.record.vo.driver.DriverUserInfoQuickVo;
import com.youming.youche.record.vo.driver.QuickAddDriverVo;

import java.util.List;
import java.util.Map;

public interface IUserService {

    /**
     * 查询我邀请的以及邀请我的数量
     *
     * @param
     * @return key:driverInvite(我邀请的) value:数量 key:driverBeInvite(邀请我的) value:数量
     */
    public DriverApplyRecordNumDto queryInviteNum(String accessToken) throws Exception;

    /**
     * 查询所有司机信息
     *
     * @author qlf
     */
    public Page doQueryCarDriver(DoQueryDriversVo doQueryDriversVo, Integer page, Integer pageSize, String accessToken) throws Exception;

    /**
     * 条件导出所有司机信息
     *
     * @author qlf
     */
    public void doQueryCarDriverList(DoQueryDriversVo doQueryDriversVo, String accessToken, ImportOrExportRecords record) throws Exception;

    /***
     * @Description: 司机导入
     * @Author: luwei
     * @Date: 2022/2/22 9:38 下午
     * @Param file:
     * @Param records:
     * @Param token:
     * @return: void
     * @Version: 1.0
     **/
    void driverImport(byte[] bytes, ImportOrExportRecords records, String token);

    /***
     * 会员管理/会员(司机)档案列表查询
     * @return
     * @throws Exception
     */
    Page doQueryOBMCarDriver(DoQueryOBMDriversVo doQueryOBMDriversVo, String token) throws Exception;

    /**
     * 司机列表查询
     * 入参：loginAcct 账号
     * linkman 姓名
     * carUserType 司机类型
     * attachTenantName 归属车队名称
     * attachTennantLinkman 车队联系人
     * attachTennantLinkPhone 车队联系人电话
     * state 认证状态
     * 出参：loginAcct 账号
     * linkman 姓名
     * carUserType 司机类型
     * carUserTypeName 司机类型名称
     * attachTenantId 归属车队id
     * attachTenantName 归属车队名称
     * attachTennantLinkman 车队联系人
     * attachTennantLinkPhone 车队联系人电话
     * vehicleNum 车辆数量
     * state 认证状态
     * stateName 认证状态名称
     * hasVer 审核状态
     * userId 用户编号
     * createDate 创建时间
     */
//    public PageInfo doQueryDriverCar(Map<String, Object> inParam,Integer page,Integer pageSize) throws Exception;


    List<TenantUserRel> getAllTenantUserRels(long userId);


    /**
     * 判断手机号码是否已经注册
     *
     * @param loginAcct
     * @return
     * @throws Exception
     */
    public Map isExistMobile(String loginAcct, String accessToken, boolean againFlag) throws Exception;

    /**
     * 运营端判断手机号码是否已经注册
     *
     * @param loginAcct
     * @return
     * @throws Exception
     */
    public Map isExistMobile(String loginAcct) throws BusinessException;

    /**
     * 查询身份证是否有效
     *
     * @param identification 省份证号
     * @param userId         用户编号
     */
    String checkExistIdentification(String identification, Long userId) throws BusinessException;

    /**
     * 新增司机 obms 放到这里 方便有问题一起改
     *
     * @param
     * @return
     * @throws Exception
     */
    public void doAddDriver(DoAddDriverVo doAddDriverVo, String accessToken) throws Exception;

    /***
     * 运营端司机添加
     * @return
     * @throws Exception
     */
    public void doAddDriverForOBMS(DoAddOBMDriver doAddOBMDriver, String token) throws BusinessException;

    /**
     * 快速新增车辆
     *
     * @return
     */
    String quick(QuickAddDriverVo quickAddDriverVo, String accessToken) throws Exception;

    /**
     * 模糊查询车辆信息
     *
     * @param plateNumber 车牌号
     * @return 车辆信息
     */
    Page doQueryVehicleForQuick(String plateNumber) throws Exception;

    /**
     * 模糊查询当前车队车辆信息
     *
     * @param plateNumber 车牌号
     * @return 车队车辆信息
     */
    Page doQueryModernVehicleForQuick(String plateNumber, String accessToken) throws Exception;

    /**
     * 司机查看
     */
    public Map doGetDriverForOBMS(long userId, String accessToken) throws Exception;

    /***
     * 会员管理/会员(司机)档案查询 审核查询单个司机信息
     * @return
     * @throws Exception
     */
    Map doGetDriver(long userId, String accessToken) throws Exception;


    /**
     * 删除司机时展示司机在车队内所有绑定的车辆
     */
    List<TenantVehicleRel> getDriverAllVehicle(Long userId, Long tenantId);

    /***
     * 会员管理/会员(司机)移除
     */
    void deleteDriver(Long tenantUserRelId, String vehicleCode, String accessToken) throws Exception;

    /***
     * 修改司机手机号码
     */
    String doUpdateDriverMobile(String mobilePhone, String oldMobilePhone, Long userId, Long relId, String accessToken);

    /**
     * 快速创建外调车时获取司机信息
     *
     * @param loginAcct
     * @return
     * @throws Exception
     */
    DriverUserInfoQuickVo getDriverUserInfoForQuick(String loginAcct, String accessToken) throws Exception;

    /**
     * 修改司机档案，不变更司机类型的情况,或者自有车改成其他车
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public void doUpdateDriver(DoAddDriverVo doAddDriverVo, String accessToken) throws Exception;

    /**
     * 查询我邀请的列表
     */
    public Page doQueryApplyRecords(ApplyRecordQueryDto applyRecordQueryDto, Integer page, Integer pageSize, String accessToken) throws Exception;

    /**
     * 查询邀请我的列表
     */
    public Page doQueryBeApplyRecords(ApplyRecordQueryDto applyRecordQueryDto, Integer page, Integer pageSize, String accessToken) throws Exception;

    /**
     * 历史档案
     *
     * @param driverQueryDto
     * @return
     * @throws Exception
     */
    public Page doQueryDriverHis(DriverQueryDto driverQueryDto, Integer page, Integer pageSize, String accessToken) throws Exception;

    /**
     * 查询申请详情 我邀请的
     *
     * @param applyId 申请id
     * @return 申请信息详情
     * @throws Exception
     */
    DriverApplyRecordVo selectApplyRecordVo(Long applyId) throws Exception;

    /**
     * 查询申请详情 被邀请的
     *
     * @param applyId 申请id
     * @return 申请信息详情
     * @throws Exception
     */
    BeApplyRecordVo getBeApplyRecord(Long applyId, String token) throws Exception;

    /**
     * 司机档案 邀请 -- 再次邀请
     *
     * @param doAddApplyAgainVo 邀请信息
     * @return
     */
    String doAddApplyAgain(DoAddApplyAgainVo doAddApplyAgainVo, String accessToken) throws Exception;

    /**
     * 添加司机邀请
     */
    String doAddApply(DoAddApplyVo doAddApplyVo, String accessToken);

    /**
     * @param driverAuditVo
     * @return
     * @throws Exception
     */
    public boolean doAuditForOBMS(DriverAuditVo driverAuditVo, String token) throws Exception;

    /**
     * 流程结束，审核通过的回调方法
     *
     * @param busiId 业务的主键
     * @param desc   结果的描述
     * @return
     */
    public void sucess(Long busiId, String desc, Map paramsMap, String token) throws BusinessException;

    /**
     * 流程结束，审核不通过的回调方法
     *
     * @param busiId 业务的主键
     * @param desc   结果的描述
     * @return
     */
    public void fail(Long busiId, String desc, Map paramsMap, String token) throws BusinessException;

    /**
     * 审核被邀请的
     */
    public boolean doAuditApply(AuditApplyVo auditApplyVo, String token) throws Exception;

    /**
     * 接口编号：14002
     * 接口入参：
     * mobilePhone	       手机号码
     * tenantName          车队名称
     * carUserTypes        邀请种类，多个类型逗号隔开
     * states              状态,多个逗号隔开
     * <p>
     * 接口出参：
     * id                               邀请主键
     * mobilePhone                      邀请账号
     * applyCarUserTypeName	          邀请种类
     * applyTenantName	              申请车队
     * createDate	                      申请时间	 	格式：2015-05-06
     * state                            状态0 : 处理中  1 : 已通过 2：被驳回 3：平台仲裁中 4：平台仲裁通过 5：平台仲裁未通过
     * stateName                        状态中文
     * auditFlg                         审核标志  1可以审核  0 不可审核
     * <p>
     * 查询司机邀请列表 小程序
     */
    Page doQueryBeApplyRecordsNew(ApplyRecordQueryVo vo, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 11004 APP获取系统参数数据（批量获取、单个获取）
     * 接口入参：
     * cfgName 系统数据名称 （可以不传（不传查所有的APP控制参数）入参为数组（可以传空数组（查所有的APP控制参数），单个、多个） 字段数据值为数据）
     * 接口出参：
     * 具体出参格式看报文
     * cfgValue	系统参数值
     * cfgName	 系统参数名
     * cfgRemark	系统备注
     */
    List<SysCfg> getSysCfgInfo(CfgVO cfgVO, String accessToken);

    /**
     * 接口编号：14006
     * 接口入参：
     * id	         邀请编号
     * 接口出参：
     * id  主键id，处理邀请时回调用
     * tenantName	          申请车队
     * tennantLinkPhone	      车队电话
     * createDate	         申请时间	 	格式：2015-05-06
     * mobilePhone         邀请账号
     * applyCarUserTypeName       申请类型
     * applyRemark         申请说明
     * applyFileUrl            申请附件,小图
     * state              状态0 : 处理中  1 : 接受邀请 2：驳回邀请 3：平台仲裁中 4：平台仲裁通过 5：平台仲裁未通过  处理中的需要可以审核
     * stateName              状态中文
     * auditRemark        理由
     * applyDriverPhone    司机手机
     * applyDriverName    原有车辆司机
     * vehicles[           车辆列表
     * plateNumber   车牌
     * vehicleTypeString   车辆类型
     * checked      查看详情的时候该车是否已经被选中
     * ]
     * <p>
     * 查询申请详情
     */
    BeApplyRecordVo getBeApplyRecordWx(Long id, String accessToken);

    /**
     * 接口编号：14003
     * 司机列表查询
     * 入参：
     * linkman 姓名
     * plateNumber 车牌号码
     * loginAcct 手机号码
     * 出参：loginAcct 手机号码
     * linkman 姓名
     * carUserType 司机类型
     * carUserTypeName 司机类型名称
     * userId 用户编号
     */
    Page<DoQueryDriversDto> doQueryCarDrivers(String plateNumber, Integer vehicleClass, String linkman, String loginAcct, Integer pageNum, Integer pageSize, String accessToken, Long userId);

    /**
     * 司机列表查询
     */
    Page<DoQueryDriversDto> doQueryCarDrivers(Integer vehicleClass, String linkman, String loginAcct, Integer pageNum, Integer pageSize, String accessToken, Long... userId);

    /**
     * 审核被邀请的司机
     *
     * @param busiCode     100011审核司机邀请
     * @param busiId       applyRecordId
     * @param desc         审核原因
     * @param chooseResult 是否审核通过
     * @param driverUserId 接收司机    被申请转移自有司机时，需要选择接收车辆的司机
     * @param plateNumbers 合作车辆    被邀请司机时，需要选择至少一辆车作为合作车辆
     */
    Boolean doAudit(String busiCode, Long busiId, String desc, Integer chooseResult, String driverUserId, String plateNumbers, String accessToken);

    UserInfoDto loginIn(Integer platformType, String accessToken);

    /***
     * 接口名称：用户登录接口(司机版)
     * 接口编码:10007
     * 接口入参：
     * 	      loginType         登录方式类型（1:密码登录，2：验证码登录）
     *         sendCode          验证码 （需加密（base64））
     * 		  pwd	                              登录密码（需加密（base64））
     *         billId	                    登录手机号码
     *         pushAppId	                    推送系统APPID
     *         pushChannelId	          推送系统channelId
     *         pushUserId	          推送系统user_id
     *         appVerNo	        APP版本号
     *         appOsVer	        APP系统版本号
     *         mobileType	          手机型号
     *         mobileBrand	          手机品牌
     *         nand	                               纬度
     *         eand	                               经度
     *
     *
     *接口出参
     *         pushRelatId	      关系ID
     *         billId	                    手机号
     *         userId          用户编号
     *         userType	                    用户类型
     *         userPicture	      用户头像url
     *         userName	                    用户名称
     *         orgId	                                  组织编号
     *         companyName	       公司名称
     *         checkFlag	                    是否认证	    是否认证1、未认证 2、认证中 3、未通过 5、已认证
     *         isSetPasswd     是否已设置登录密码 0-否，1-是
     *
     * 用户登录接口(司机版)
     *
     */
    UserInfoDto doLogin(/*WechatUserRel wechatUserRel,*/ int appCode, String accessToken);

    UserInfoDto loginMain(/*WechatUserRel wechatUserRel,*/ int appCode, String accessToken);

    /**
     * 根据的登陆渠道判断用户角色(仅根据登陆入口判断，不检查登陆的合法性)
     * <br/>
     * 车队长、运营后台、小程序车队版(驻场号)：          <br/>
     * 超管登陆自己的车队时=> 超管                <br/>
     * 超管登陆其他车队时=> 员工                  <br/>
     * app => 司机                                   <br/>
     * 服务商、小程序服务商版 => 服务商                 <br/>
     * 小程序收款人版 => 收款人                        <br/>
     *
     * @param userId    用户ID
     * @param tenantId  登陆租户ID  车队长、运营后台、小程序车队版时，必传，其他传null
     * @param loginFrom 登陆渠道
     */
    Integer getUserType(long userId, Long tenantId, int loginFrom);

    /**
     * 营运工作台 驾驶员预警数量
     */
    List<WorkbenchDto> getTableUserDriverCount();

    /**
     * 营运工作台 从业资格预警数量
     */
    List<WorkbenchDto> getTableUserQcCertiCount();

    /**
     * 营运工作台  司机档案数量
     */
    List<WorkbenchDto> getTableDriverCount();


}
