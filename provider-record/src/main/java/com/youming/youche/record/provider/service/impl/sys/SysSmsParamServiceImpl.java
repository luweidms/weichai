package com.youming.youche.record.provider.service.impl.sys;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.record.api.sys.ISysSmsParamService;
import com.youming.youche.record.domain.sys.SysSmsParam;
import com.youming.youche.record.provider.mapper.sys.SysSmsParamMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * <p>
 * 短信参数表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@DubboService(version = "1.0.0")
public class SysSmsParamServiceImpl extends ServiceImpl<SysSmsParamMapper, SysSmsParam> implements ISysSmsParamService {

}
