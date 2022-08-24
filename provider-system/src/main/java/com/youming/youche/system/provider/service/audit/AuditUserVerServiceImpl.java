package com.youming.youche.system.provider.service.audit;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.system.api.audit.IAuditUserVerService;
import com.youming.youche.system.domain.audit.AuditUserVer;
import com.youming.youche.system.provider.mapper.audit.AuditUserVerMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * <p>
 * 节点审核人版本表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@DubboService(version = "1.0.0")
public class AuditUserVerServiceImpl extends ServiceImpl<AuditUserVerMapper, AuditUserVer>
		implements IAuditUserVerService {

}
