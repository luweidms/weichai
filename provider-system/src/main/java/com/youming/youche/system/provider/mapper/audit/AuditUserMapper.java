package com.youming.youche.system.provider.mapper.audit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.audit.AuditUser;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 * 节点审核人Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface AuditUserMapper extends BaseMapper<AuditUser> {

    int removeByNodeId(@Param("nodeId") Long nodeId);
}
