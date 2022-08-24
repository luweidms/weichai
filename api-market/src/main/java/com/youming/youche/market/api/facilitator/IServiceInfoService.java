package com.youming.youche.market.api.facilitator;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.dto.facilitator.*;
import com.youming.youche.market.vo.facilitator.*;
import com.youming.youche.market.vo.facilitator.criteria.ServiceInfoQueryCriteria;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务商表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
public interface IServiceInfoService extends IService<ServiceInfo> {
    /**
     * 服务商档案分页条件查询
     *
     * @param serviceInfoQueryCriteria
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<FacilitatorVo> queryFacilitator(ServiceInfoQueryCriteria serviceInfoQueryCriteria, Integer pageNum, Integer pageSize, String accessToken) ;

    /**
     * 服务商档案保存
     *
     * @param serviceSaveInDto
     * @return
     */
    ResponseResult saveFacilitator(ServiceSaveInDto serviceSaveInDto, String accessToken);


    /**
     * 查看服务商详细
     *
     * @param serviceUserId
     * @param accessToken
     * @return
     */
    ServiceInfoVo seeServiceInfoMotorcade(Long serviceUserId, String accessToken);



    /**
     * 查看服务商详细
     *
     * @param serviceUserId
     * @param
     * @return
     */
    ServiceInfoVo seeServiceInfoMotorcade(Long serviceUserId);

    /**
     * 查看服务商信息
     *
     * @return
     * @throws Exception
     */
    ServiceinfoDetailedVo seeServiceInfo(Long serviceUserId, Integer hasAudit, String accessToken);

    /**
     * 校验服务商有效性
     *
     * @param productSaveIn
     * @param serviceUserId
     * @param isUpdate
     * @return
     */
    Boolean checkServiceInfo(ProductSaveDto productSaveIn, Long serviceUserId, Boolean isUpdate);


    /**
     * 是否存在
     *
     * @param userId
     * @return
     */
    Boolean isService(Long userId);

    /**
     * 修改服务商档案
     *
     * @param serviceSaveIn
     * @return
     * @throws Exception
     */
    ResponseResult modifyService(ServiceSaveInDto serviceSaveIn, String accessToken);

    /**
     * 保存与新增
     *
     * @param serviceInfo
     * @param isUpdate
     */
    void doSaveOrUpdate(ServiceInfo serviceInfo, Boolean isUpdate, LoginInfo user);

    /**
     * 通过serviceuserid查询服务商
     *
     * @param serviceUserId
     * @return
     */
    ServiceInfo getServiceInfoByServiceUserId(Long serviceUserId);

    /**
     * 检验账号是否已经注册
     *
     * @param loginAcct
     * @return info 0：未注册 1：已注册为有服务商信息 2：已注册存在服务商信息
     * @throws Exception
     */
    ServiceInfoDto checkRegisterService(String loginAcct, String accessToken) throws Exception;

    /**
     * 添加服务商用户走审核流程
     *
     * @return
     * @throws Exception
     */
    Long doAddServ(ServiceUserInDto serviceUserIn, LoginInfo user,ServiceSaveInDto serviceSaveIn) throws Exception;


    /**
     * 保存服务商基础信息
     *
     * @return
     * @throws Exception
     */
    ServiceInfo saveServiceInfo(Long serviceUserId, ServiceSaveInDto serviceSaveIn, LoginInfo user);


    /**
     * 查看服务商信息
     *
     * @param serviceUserId
     * @return
     * @throws Exception
     */
    ServiceInfoVo getServiceInfo(Long serviceUserId, LoginInfo user);

    /**
     * 暂停、启用站点
     * @return
     * @throws Exception
     */
    ResponseResult sure(Long serviceUserId, Boolean pass, String describe,String accessToken);

    /**
     * 启用服务商
     * @param serviceUserId
     * @throws Exception
     */
    void enableService(Long serviceUserId, String desc,LoginInfo user);


    /**
     * 禁用服务商
     * @param serviceUserId
     * @throws Exception
     */
     void disableService(Long serviceUserId,String desc,LoginInfo user);
    /**
     * 查询服务商基础信息
     *
     * @param userId
     * @return
     * @throws Exception
     */
    ServiceInfoBasisVo  getServiceInfo(Long userId);

    /**
     * 通过站点名称检验是否存在服务商
     *
     * @param productName
     * @return
     * @throws Exception
     */
    ServiceProdcutByNameVo getServiceProdcutByName(String productName, Long serviceId, String accessToken);

    /**
     * 车队端服务商分页
     * @param serviceInfoFleetDto
     * @return
     */
    Page<ServiceInfoFleetVo> queryServiceInfoPage(Integer pageSize,Integer pageNum,ServiceInfoFleetDto serviceInfoFleetDto,String accessToken);


    /**
     * 车队端服务商保存
     * @param serviceSaveIn
     * @param accessToken
     * @return
     */
    ResponseResult doSaveService(ServiceSaveInDto serviceSaveIn,String accessToken);

    /**
     * 我邀请的分页
     * @return
     */
    Page<ServiceInvitationVo> queryServiceInvitation(ServiceInvitationDto serviceInvitationDto,String accessToken,Integer pageSize,Integer pageNum);


    /**
     * 查询合作详情
     * @param serviceId
     * @param id
     * @return
     */
    InvitationInfoVo getInvitationInfo(Long serviceId, Long id,String accessToken);


    /**
     * 删除 服务商
     * @param userId
     * @param accessToken
     * @return
     */
    Boolean delService(Long userId,String accessToken);

    /**
     * 申请服务商档案
     * @param serviceSaveIn
     * @return
     */
    ApplyServiceVo applyService(ServiceSaveInDto serviceSaveIn,String accessToken);

    /**
     * 成功回调
     *
     * @param busiId    业务主键
     * @param desc      描述
     * @param paramsMap
     * @param token
     */
    void sucess(Long busiId, String desc, Map paramsMap, String token);


    /**
     * 失败回调
     *
     * @param busiId    业务主键
     * @param desc      描述
     * @param paramsMap
     * @param token
     */
    void fail(Long busiId, String desc, Map paramsMap, String token);

    /**
     * 是否是服务商
     * @param userId
     * @param tenantId
     * @return
     */
    Boolean isService(Long userId, Long tenantId);


    /**
     *  接口编码：40018
     * 个人信息接口
     * @param serviceUserId
     * @return
     */

    ServiceInfoVo seeServiceInfo(Long serviceUserId);

    /**
     * id获取服务商信息
     */
    ServiceInfo getServiceInfoById(long userId);

    /**
     * token获取服务商信息
     * @return
     */
    ServiceInfoDto seeServiceInfo(String accessToken);

    /**
     * 手机号验证服务
     * @return
     */
    String seeServiceInfoChek(String phone);




    /**
     * 校验服务商共享
     油品服务商的站点分为两种：共享油站和非共享油站，只有非共享的油站才能与车队建立合作关系。
     共享：
     当服务商勾选共享时，需要检测当前油站是否已存在共享油站：
     若已存在，则不允许再次共享，提示：该油站已是共享油站，不需再共享。
     若不存在，则可以提交申请为共享油站。服务商提交成为共享油站的申请后，由平台管理人员在运营管理后台进行审核。
     取消共享：
     当服务商取消共享时，只需要将油站不展示在共享中，但油站的属性还是共享油站，也不能被车队邀请为合作油站。
     */
    Map<String,Object> checkServiceShare(Long productId);

    /**
     * 营运工作台  服务商数量
     */
    List<WorkbenchDto> getTableManagerCount();

    /**
     * 动态更新 服务商账单
     */
    void dynamicUpdateServiceInfoBill();
}
