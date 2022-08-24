package com.youming.youche.record.provider.service.impl.sys;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.record.api.sys.ISysSmsLogService;
import com.youming.youche.record.domain.sys.SysSmsLog;
import com.youming.youche.record.provider.mapper.sys.SysSmsLogMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * <p>
 * 短信日志表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@DubboService(version = "1.0.0")
public class SysSmsLogServiceImpl extends ServiceImpl<SysSmsLogMapper, SysSmsLog> implements ISysSmsLogService {

}
