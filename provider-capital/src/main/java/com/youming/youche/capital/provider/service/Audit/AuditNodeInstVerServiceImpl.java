package com.youming.youche.capital.provider.service.Audit;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.capital.api.iaudit.IAuditNodeInstVerService;
import com.youming.youche.capital.domain.audit.AuditNodeInstVer;
import com.youming.youche.capital.provider.mapper.audit.AuditNodeInstVerMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * <p>
 * 审核节点实例表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@DubboService(version = "1.0.0")
public class AuditNodeInstVerServiceImpl extends ServiceImpl<AuditNodeInstVerMapper, AuditNodeInstVer>
		implements IAuditNodeInstVerService {

}
