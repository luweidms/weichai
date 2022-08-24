package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.ISysRoleOperRelService;
import com.youming.youche.order.domain.order.SysRoleOperRel;
import com.youming.youche.order.provider.mapper.order.SysRoleOperRelMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
@DubboService(version = "1.0.0")
@Service
public class SysRoleOperRelServiceImpl extends ServiceImpl<SysRoleOperRelMapper, SysRoleOperRel> implements ISysRoleOperRelService {


}
