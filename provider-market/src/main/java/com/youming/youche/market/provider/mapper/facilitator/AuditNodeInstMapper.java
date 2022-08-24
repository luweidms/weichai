package com.youming.youche.market.provider.mapper.facilitator;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.market.domain.facilitator.AuditNodeInst;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 审核节点实例表Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-07
 */
public interface AuditNodeInstMapper extends BaseMapper<AuditNodeInst> {


    List<AuditNodeInst> queryAuditIng(@Param("busiCode") String busiCode,
                                      @Param("list") List<Long> list,
                                      @Param("status") Integer status,
                                      @Param("auditResult") Integer auditResult);

    List<Long> getBusiIdByUserId(@Param("auditCode") String auditCode, @Param("userId") Long userId,
                                 @Param("tenantId") Long tenantId, @Param("pageSize") Integer pageSize,
                                 @Param("auditResult") Integer auditResult);

    /**
     * 审核记录查询（查询最新的一批次查询）
     */
    List<Map> getAuditNodeInstNew(@Param("auditCode") String auditCode, @Param("busiId") long busiId,
                                  @Param("tenantId") long tenantId, @Param("isDesc") boolean isDesc,
                                  @Param("state") String state);

}
