package com.youming.youche.table.provider.mapper.workbench;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.table.domain.workbench.WechatOperationWorkbenchInfo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
* <p>
* Mapper接口
* </p>
* @author zengwen
* @since 2022-05-25
*/
public interface WechatOperationWorkbenchInfoMapper extends BaseMapper<WechatOperationWorkbenchInfo> {
    /**
     * 获取微信车队小程序运营报表数据
     * @param tenantId
     * @param type
     * @param time
     * @return
     */
    WechatOperationWorkbenchInfo getWechatOperationWorkbenchInfo(@Param("tenantId") Long tenantId, @Param("type") Integer type, @Param("time") String time);

    WechatOperationWorkbenchInfo getWechatOperationWorkbenchInfoNew(@Param("tenantId") Long tenantId, @Param("type") Integer type, @Param("time") String time, @Param("localDateTime") LocalDateTime localDateTime);

    /**
     * 获取最新时间
     */
    LocalDateTime maxLocalDateTime(@Param("type") Integer type);
}
