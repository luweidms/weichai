package com.youming.youche.system.provider.mapper.audit;

import com.youming.youche.system.domain.audit.AuditConfig;
import com.youming.youche.system.domain.audit.AuditNodeOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @version:
 * @Title: AuditSettingMapper
 * @Package: com.youming.youche.system.provider.mapper.audit
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/10 9:58
 * @company:
 */
@Mapper
public interface AuditSettingMapper {

    List<AuditNodeOut> getAuditNodeConfigList(@Param("auditCodes") String auditCodes,
                                              @Param("tenantId")Long tenantId, @Param("type")int type) throws Exception;

    List<Long> getUserFromAuditUser(@Param("nodeId") Long nodeId,@Param("targetObjType") Integer targetObjType);

    /***
     * @Description: 审核配置信息
     * @Author: luwei
     * @Date: 2022/2/17 10:30 下午
     * @Param auditCodes:
     * @return: java.util.List<com.youming.youche.system.domain.audit.AuditConfig>
     * @Version: 1.0
     **/
    List<AuditConfig> getAuditConfigInCode(@Param("auditCodes") String auditCodes);

    Integer updateAuditInstToFinish(@Param("auditId")Long auditId,@Param("busiId") Long busiId);

    Integer rollbackOriginator(@Param("flag") Integer flag, @Param("auditId") Long auditId,@Param("tenantId") Long tenantId);
}
