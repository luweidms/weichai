package com.youming.youche.capital.provider.mapper.audit;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.audit.AuditUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 节点审核人Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface AuditUserMapper extends BaseMapper<AuditUser> {

    List<Long> getUserFromAuditUser(@Param("nodeId") Long nodeId,
                                    @Param("targetObjType") Integer targetObjType);


    List<AuditUser> getAuditUserList(@Param("nodeId") Long nodeId,
                                     @Param("targetObjType") Integer targetObjType,
                                     @Param("targetObjId") Long targetObjId);

    /**
     * 查询用户待审核的数据
     */
    List<Long> getUserFromAuditUser2(@Param("nodeId") Long nodeId,
                                     @Param("targetObjType") Integer targetObjType);

    Integer updateAuditInstToFinish(@Param("auditId") Long auditId, @Param("busiId") Long busiId);

}
