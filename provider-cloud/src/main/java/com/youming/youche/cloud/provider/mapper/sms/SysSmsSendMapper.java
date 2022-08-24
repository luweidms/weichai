package com.youming.youche.cloud.provider.mapper.sms;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.cloud.dto.sys.UnReadCountMiddleDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-12
 */
public interface SysSmsSendMapper extends BaseMapper<SysSmsSend> {

    List<UnReadCountMiddleDto> unReadCount(@Param("billId") String billId);

}
