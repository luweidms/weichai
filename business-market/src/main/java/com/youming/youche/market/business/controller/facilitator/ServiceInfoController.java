package com.youming.youche.market.business.controller.facilitator;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.market.annotation.Dict;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.dto.facilitator.*;
import com.youming.youche.market.vo.facilitator.*;
import com.youming.youche.market.vo.facilitator.criteria.ServiceInfoQueryCriteria;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务商表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
@RestController
@RequestMapping("/facilitator/data/info")
public class ServiceInfoController {
    @DubboReference(version = "1.0.0")
    IServiceInfoService serviceInfoService;
    @Resource
    HttpServletRequest request;

    public IServiceInfoService getService() {
        return serviceInfoService;
    }


    /**
     * 服务商档案分页条件查询
     *
     * @param serviceInfoQueryCriteria 条件查询传入参数
     * @param pageNum                  分页参数
     * @param pageSize                 分页参数
     * @return
     */
    @GetMapping("pageServiceinfo")
    public ResponseResult doQueryFacilitatorAll(ServiceInfoQueryCriteria serviceInfoQueryCriteria,
                                                @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<FacilitatorVo> facilitatorPage = serviceInfoService.queryFacilitator(serviceInfoQueryCriteria, pageNum, pageSize, accessToken);
        return ResponseResult.success(facilitatorPage);

    }

    /**
     * 添加服务商or修改服务商
     *
     * @param serviceSaveInDto 要添加或修改的服务商信息
     * @return
     */
    @PostMapping("saveServiceInfo")
    public ResponseResult saveServiceInfo(@RequestBody ServiceSaveInDto serviceSaveInDto) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        serviceInfoService.saveFacilitator(serviceSaveInDto, accessToken);
        return ResponseResult.success();
    }

    /**
     * 点击服务商账号的时候 查看服务商详细
     *
     * @param serviceUserId 用户编码
     * @param hasAudit      1表示要查历史一般要传(可以不传)
     * @return
     */
    @GetMapping("seeServiceInfo")
    public ResponseResult seeServiceInfo(@RequestParam("serviceUserId") Long serviceUserId,
                                         @RequestParam(name = "hasAudit", defaultValue = "0") Integer hasAudit) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ServiceinfoDetailedVo serviceinfoDetailedVo = serviceInfoService.seeServiceInfo(serviceUserId, hasAudit, accessToken);
            return ResponseResult.success(serviceinfoDetailedVo);
        } catch (Exception e) {
            return ResponseResult.failure("网络异常");
        }
    }

    /**
     * 点击修改的时候 保存 触发的操作
     *
     * @param serviceSaveIn
     * @return
     */
    @PostMapping("modifyService")
    public ResponseResult modifyService(@RequestBody ServiceSaveInDto serviceSaveIn) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return serviceInfoService.modifyService(serviceSaveIn, accessToken);
    }

    /**
     * 根据手机号查询服务商
     *
     * @param loginAcct 联系人手机号
     * @return
     */
    @GetMapping("checkRegisterService")
    public ResponseResult checkRegisterService(String loginAcct) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ServiceInfoDto serviceInfoDto = null;
        try {
            serviceInfoDto = serviceInfoService.checkRegisterService(loginAcct, accessToken);
        } catch (Exception e) {
            ResponseResult.failure("网络异常");
        }
        return ResponseResult.success(serviceInfoDto);
    }

    /**
     * 啓動或停止
     *
     * @param sureDto serviceUserId 服务商ID
     *                pass 状态 true启用 false 停用
     *                describe 原因描述
     */
    @PostMapping("sure")
    public ResponseResult sure(@RequestBody SureDto sureDto) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Long userId = Long.parseLong(sureDto.getServiceUserId());
            return serviceInfoService.sure(userId, sureDto.getPass(), sureDto.getDescribe(), accessToken);
        } catch (Exception e) {
            return ResponseResult.failure();
        }
    }

    /**
     * 点击服务商站点添加的时候 查询服务商基础信息
     *
     * @param userId 服务商编号
     * @return
     */
    @GetMapping("getServiceInfo")
    public ResponseResult getServiceInfo(@RequestParam("userId") Long userId) {
        ServiceInfoBasisVo serviceInfo = serviceInfoService.getServiceInfo(userId);
        return ResponseResult.success(serviceInfo);
    }

    /**
     * 通过站点名称检验是否存在服务商
     *
     * @param productName 站点名称
     * @param serviceId   服务商ID
     * @return
     */
    @GetMapping("getServiceProdcutByName")
    public ResponseResult getServiceProdcutByName(@RequestParam("productName") String productName,
                                                  @RequestParam("serviceId") Long serviceId) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ServiceProdcutByNameVo prodcut = serviceInfoService.getServiceProdcutByName(productName, serviceId, accessToken);
            return ResponseResult.success(prodcut);
        } catch (Exception e) {
            return ResponseResult.failure("网络异常");
        }
    }

    /**
     * 车队端服务商分页 -- 服务商管理列表查询
     *
     * @param serviceInfoFleetDto loginAcct 服务商账号
     *                            serviceName 服务商名称
     *                            linkman 联系人
     *                            serviceType 服务商类型
     *                            state 账号状态
     *                            authState 认证状态
     * @param pageNum             分页参数
     * @param pageSize            分页参数
     * @return
     */
    @GetMapping("queryServiceInfoPage")
    public ResponseResult queryServiceInfoPage(ServiceInfoFleetDto serviceInfoFleetDto,
                                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<ServiceInfoFleetVo> infoFleetVoPage = serviceInfoService.queryServiceInfoPage(pageSize, pageNum, serviceInfoFleetDto, accessToken);
        return ResponseResult.success(infoFleetVoPage);
    }

    /**
     * 车队端服务商保存或修改
     *
     * @param serviceSaveInDto 服务商需新增，修改的服务商信息
     * @return
     */
    @PostMapping("doSaveService")
    public ResponseResult doSaveService(@RequestBody ServiceSaveInDto serviceSaveInDto) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return serviceInfoService.doSaveService(serviceSaveInDto, accessToken);
    }

    /**
     * 车队端服务商 我邀請的列表
     *
     * @return
     */
    @GetMapping("queryServiceInvitation")
    public ResponseResult queryServiceInvitation(ServiceInvitationDto serviceInvitationDto,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<ServiceInvitationVo> page = serviceInfoService.queryServiceInvitation(serviceInvitationDto, accessToken, pageSize, pageNum);
        return ResponseResult.success(page);
    }

    /**
     * 我邀请的点击（查看） 查询合作详情
     *
     * @param serviceId 用户编号
     * @param id        服务商申请合作主键id
     * @return
     */
    @GetMapping("getInvitationInfo")
    public ResponseResult getInvitationInfo(@RequestParam("serviceId") Long serviceId, @RequestParam("id") Long id) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        InvitationInfoVo invitationInfo = serviceInfoService.getInvitationInfo(serviceId, id, accessToken);
        return ResponseResult.success(invitationInfo);
    }

    /**
     * 车队端服务商查看详情
     *
     * @param serviceUserId 服务商用户编号
     * @return
     */
    @GetMapping("seeServiceInfoMotorcade")
    public ResponseResult seeServiceInfoMotorcade(@RequestParam("serviceUserId") Long serviceUserId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ServiceInfoVo serviceInfoVo = serviceInfoService.seeServiceInfoMotorcade(serviceUserId, accessToken);
        return ResponseResult.success(serviceInfoVo);
    }

    /**
     * 删除车队端服务商
     *
     * @param serviceUserId 服务商用户编号
     * @return
     */
    @GetMapping("delService")
    public ResponseResult delService(@RequestParam("serviceUserId") Long serviceUserId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Boolean state = serviceInfoService.delService(serviceUserId, accessToken);
        return state ? ResponseResult.success("删除服务商成功") : ResponseResult.failure("删除失败");
    }

    /**
     * 申请服务商档案
     *
     * @param serviceSaveInDto 申请的服务商记录
     * @return
     */
    @PostMapping("applyService")
    public ResponseResult applyService(@RequestBody ServiceSaveInDto serviceSaveInDto) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ApplyServiceVo applyServiceVo = serviceInfoService.applyService(serviceSaveInDto, accessToken);
        return ResponseResult.success(applyServiceVo);
    }

    /**
     * 接口编码：40018
     * 个人信息接口
     *
     * @param serviceUserId 服务商用户编号
     * @return
     */
    @GetMapping("seeServiceInfoData")
    @Dict
    public ResponseResult seeServiceInfoData(@RequestParam("serviceUserId") Long serviceUserId) {
        return ResponseResult.success(
                serviceInfoService.seeServiceInfo(serviceUserId)
        );
    }

    /**
     * 接口编码：40018
     * 个人信息接口
     *
     * @return
     */
    @GetMapping("seeServiceInfoDataToken")
    @Dict
    public ResponseResult seeServiceInfoData() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        return ResponseResult.success(serviceInfoService.seeServiceInfo(accessToken));
    }

    /**
     * 接口编码：40018
     * 个人信息接口
     *
     * @param phone 手机号/用户账号
     * @return
     */
    @GetMapping("seeServiceInfoChek")
    @Dict
    public ResponseResult seeServiceInfoChek(String phone) {
        return ResponseResult.success(serviceInfoService.seeServiceInfoChek(phone));
    }

    /**
     * 校验服务商共享
     * 油品服务商的站点分为两种：共享油站和非共享油站，只有非共享的油站才能与车队建立合作关系。
     * 共享：
     * 当服务商勾选共享时，需要检测当前油站是否已存在共享油站：
     * 若已存在，则不允许再次共享，提示：该油站已是共享油站，不需再共享。
     * 若不存在，则可以提交申请为共享油站。服务商提交成为共享油站的申请后，由平台管理人员在运营管理后台进行审核。
     * 取消共享：
     * 当服务商取消共享时，只需要将油站不展示在共享中，但油站的属性还是共享油站，也不能被车队邀请为合作油站。
     *
     * @param productId 服务商id
     */
    @GetMapping("checkServiceShare")
    public ResponseResult checkServiceShare(@RequestParam("productId")Long productId){
        return ResponseResult.success(serviceInfoService.checkServiceShare(productId));
    }

    /**
     * 动态更新 服务商账单
     *
     * @return
     */
    @GetMapping("dynamicUpdateServiceInfoBill")
    public ResponseResult dynamicUpdateServiceInfoBill() {
        serviceInfoService.dynamicUpdateServiceInfoBill();
        return ResponseResult.success();
    }

}
