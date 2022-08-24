package com.youming.youche.market.provider.mapper.facilitator;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.domain.facilitator.ServiceInvitation;
import com.youming.youche.market.dto.facilitator.CooperationDto;
import com.youming.youche.market.vo.facilitator.ServiceInvitationVxVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 服务商申请合作Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
public interface ServiceInvitationMapper extends BaseMapper<ServiceInvitation> {
    /**
     * 查询合作列表
     * @param id
     * @return
     */
    List<CooperationDto> queryCooperationList(@Param("id") Long id);


    Page<ServiceInvitationVxVo> queryBusinessInvitation(Page<ServiceInvitationVxVo> page,
                                                        @Param("tenantName") String tenantName,
                                                        @Param("cooperationType") List<Integer> cooperationType,
                                                        @Param("authState") List<Integer> authState,
                                                        @Param("linkPhone") String linkPhone,
                                                        @Param("linkman") String linkman,
                                                        @Param("user")LoginInfo user);
}
