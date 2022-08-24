package com.youming.youche.market.api.facilitator;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.market.domain.facilitator.ServiceInfoVer;
import com.youming.youche.market.domain.facilitator.UserDataInfoVer;
import com.youming.youche.market.dto.facilitator.ServiceSaveInDto;
import com.youming.youche.market.vo.facilitator.ServiceInfoVo;

/**
 * <p>
 * 服务商版本表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-26
 */
public interface IServiceInfoVerService extends IBaseService<ServiceInfoVer> {
    /**
     * 保存版本记录
     * @param serviceInfoVer
     */
   void saveServiceInfoHis(ServiceInfoVer serviceInfoVer, LoginInfo user);
    /**
     * 保存服务商历史数据
     * @param serviceSaveIn
     * @throws Exception
     */
   void saveServiceInfoHis(ServiceSaveInDto serviceSaveIn, Long tenantId, Integer isAuth,LoginInfo baseUser);
    /**
     * 查看服务商最后修改的版本记录
     * @param serviceUserId
     * @return
     * @throws Exception
     */
    ServiceInfoVo getServiceInfoVer(Long serviceUserId,LoginInfo user);

    /**
     * 获取服务商用户待审核信息
     * @param userId  用户编号
     * @return
     * @throws Exception
     */
    UserDataInfoVer doGetServ(Long userId);


    /**
     * 查询最新版本的一条数据
     * @param relId
     * @return
     */
   ServiceInfoVer getServiceInfoVer(Long serviceUserId);

    /**
     * 审核服务商信息
     * @param serviceUserId
     * @param isPass 是否通过
     * @param auditReason 审核意见
     * @throws Exception
     */
    ResponseResult auditServiceInfo(Long serviceUserId, Boolean isPass, String auditReason, String accessToken);
}
