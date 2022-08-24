package com.youming.youche.record.provider.mapper.tenant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.record.domain.tenant.TenantReceiverRel;
import com.youming.youche.record.domain.user.UserReceiverInfo;
import com.youming.youche.record.dto.tenant.TenantReceiverRelDto;
import com.youming.youche.record.vo.tenant.TenantReceiverRelVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface UserReceiverInfoMapper extends BaseMapper<UserReceiverInfo> {
    Page<TenantReceiverRelDto> queryAll(Page<TenantReceiverRelDto> page, @Param("tenantId")  Long tenantId, @Param("tenantReceiverRelVo")TenantReceiverRelVo tenantReceiverRelVo);

    TenantReceiverRel ContractById(Long id);

    boolean deleteTeantReceiverById(Long relId);



    void createTenantReceiverRel(TenantReceiverRel tenantReceiverRel1);

    void updateBytenant(@Param("tenantReceiverRel") TenantReceiverRel tenantReceiverRel);
//
//    UserReceiverInfo getUserReceiverInfoByUserId(Long id);

    TenantReceiverRel getTenantReceiverRel(Long receiverId, Long tenantId);

    List<TenantReceiverRel> getTenantReceiverRelByUserId(@Param("receiverId") Long receiverId, @Param("tenantId") Long tenantId);
//
//    void updateUserReceiverInfo(@Param("userReceiverInfo") UserReceiverInfo userReceiverInfo);
//
//    UserReceiverInfo getUserReceiverInfoById(@Param("receiverId") Long receiverId);

}
