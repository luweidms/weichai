package com.youming.youche.market.provider.mapper.facilitator;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.market.domain.facilitator.Facilitator;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.dto.facilitator.ServiceInfoFleetDto;
import com.youming.youche.market.dto.facilitator.ServiceInvitationDto;
import com.youming.youche.market.vo.facilitator.FacilitatorVo;
import com.youming.youche.market.vo.facilitator.ServiceInfoBasisVo;
import com.youming.youche.market.vo.facilitator.ServiceInfoFleetVo;
import com.youming.youche.market.vo.facilitator.ServiceInvitationVo;
import com.youming.youche.market.vo.facilitator.criteria.ServiceInfoQueryCriteria;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 服务商表Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-22
 */
public interface ServiceInfoMapper extends BaseMapper<ServiceInfo> {
    /**
     * 服务商分页条件查询
     *
     * @param serviceInfoQueryCriteria
     * @return
     */
    Page<Facilitator> queryFacilitator(Page<Facilitator> page,
                                       @Param("serviceInfoQueryCriteria") ServiceInfoQueryCriteria serviceInfoQueryCriteria,
                                       @Param("user") LoginInfo user);


    /**
     * 添加
     *
     * @param serviceInfo
     * @return
     */
    Long saveServiceInfo(ServiceInfo serviceInfo);

    ServiceInfoBasisVo getServiceInfo(@Param("userId") Long userId);

    /**
     * 车队端服务商分页
     *
     * @param serviceInfoFleetDto
     * @return
     */
    Page<ServiceInfoFleetVo> queryServiceInfoPage(Page<ServiceInfoFleetVo> page,
                                                  @Param("serviceInfoFleetDto") ServiceInfoFleetDto serviceInfoFleetDto,
                                                  @Param("user") LoginInfo user);

    /**
     * 车队端所有服务商
     * @return
     */
    List<ServiceInfoFleetVo> queryServiceInfo();

    /**
     * 服务商我邀请的分页（车队端）
     *
     * @param page
     * @param serviceInvitationDto
     * @return
     */
    Page<ServiceInvitationVo> queryServiceInvitation(Page<ServiceInvitationVo> page,
                                                     @Param("serviceInvitationDto") ServiceInvitationDto serviceInvitationDto,
                                                     @Param("user") LoginInfo user);

    /**
     * 营运工作台  服务商数量
     */
    List<WorkbenchDto> getTableManagerCount();
}
