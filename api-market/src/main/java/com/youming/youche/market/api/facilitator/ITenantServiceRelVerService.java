package com.youming.youche.market.api.facilitator;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.domain.facilitator.TenantServiceRelVer;
import com.youming.youche.market.dto.facilitator.ServiceProductDto;
import com.youming.youche.market.dto.facilitator.TenantServiceDto;
import com.youming.youche.market.vo.facilitator.ServiceInfoVo;

/**
 * <p>
 * 服务商与租户关系 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-25
 */
public interface ITenantServiceRelVerService extends IService<TenantServiceRelVer> {

    /**
     * 保存或修改
     * @param tenantServiceRelVer
     * @return
     */
    boolean saveTenantServiceRelVer(TenantServiceRelVer tenantServiceRelVer);


    /**
     * 查询服务商历史档案
     * @return
     */
    IPage<TenantServiceDto> queryTenantServiceHis(Integer pageNum, Integer pageSize, String loginAcct,
                                                  String serviceName, String linkman, Integer serviceType,
                                                  String logInfoUser) throws Exception;

    /**
     * 查询站点历史档案
     * @return
     */
    IPage<ServiceProductDto> queryProductHis(Integer pageNum, Integer pageSize, String productName, String serviceCall,
                                             String address, String serviceName, String logInfoUser) throws Exception;

    /**
     * 查看历史档案详情
     * @param serviceUserId
     * @param relId
     * @return
     * @throws Exception
     */
    ServiceInfoVo seeServiceInfoHis(Long serviceUserId, Long relId, String logInfoUser);

    /**
     * 查询最新的一条数据历史数据
     * @param relId
     * @return
     */
    TenantServiceRelVer getTenantServiceRelVer(Long relId);



    /**
     * 查询最新的一条数据历史数据
     * @param relId
     * @return
     */
    TenantServiceRelVer getTenantServiceRelVerByInvitationId(Long relId,Long invitationId);

    /**
     * 查询最新的一条数据历史数据
     * @param tenantId
     * @param serviceUserId
     * @return
     */
    TenantServiceRelVer getTenantServiceRelVer(Long tenantId,Long serviceUserId);



    /**
     * 保存历史表
     * @param tenantServiceRelVer
     * @param isUpdate
     */
    void saveTenantServiceRelHis(TenantServiceRelVer tenantServiceRelVer, boolean isUpdate, LoginInfo baseUser);

}
