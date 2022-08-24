package com.youming.youche.record.provider.mapper.sys;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.sys.SysOperLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 操作日志表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {


    int insertSysOperLog(@Param("table") String table, @Param("sysOperLog") SysOperLog sysOperLog);

    List<SysOperLog> querySysOperLogOrderByCreateDate(@Param("tableName") String tableName,
                                                      @Param("busiCode") Integer busiCode,
                                                      @Param("busiId") Long busiId,
                                                      @Param("tenantId") Long tenantId);
}
