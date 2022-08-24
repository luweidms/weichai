//package com.youming.youche.record.provider.service.impl.sys;
//
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.youming.youche.commons.domain.SysCfg;
//import com.youming.youche.record.api.sys.ISysCfgRecordService;
//import com.youming.youche.record.api.user.IUserDataInfoRecordService;
//import com.youming.youche.record.domain.user.UserDataInfo;
//import com.youming.youche.record.provider.mapper.sys.SysCfgRecordMapper;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * <p>
// * 系统配置表 服务实现类
// * </p>
// *
// * @author Terry
// * @since 2021-11-21
// */
//@DubboService(version = "1.0.0")
//@Service
//public class SysCfgRecordRecordServiceImpl extends ServiceImpl<SysCfgRecordMapper, SysCfg> implements ISysCfgRecordService {
//
//
//    @Resource
//    SysCfgRecordMapper sysCfgMapper;
//
//    @Resource
//    IUserDataInfoRecordService iUserDataInfoRecordService;
//
//
//    @Override
//    public SysCfg getSysCfg(String cfgName, String accessToken) {
//        long tenantId = -1L;
//        UserDataInfo user = iUserDataInfoRecordService.getUserDataInfoByAccessToken(accessToken);
//        if (user != null && user.getTenantId() != null) {
//            tenantId = user.getTenantId();
//        }
//        QueryWrapper<SysCfg> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("cfg_name", cfgName).eq("TENANT_ID", tenantId);
//        List<SysCfg> list = sysCfgMapper.selectList(queryWrapper);
//        if (list != null && list.size() > 0) {
//            return list.get(0);
//        }
//        return null;
//    }
//
//    @Override
//    public Boolean getCfgBooleanVal(String cfgName, Integer system) {
//        Boolean flag = false;
//        SysCfg cfg = sysCfgMapper.getCfgBooleanVal(cfgName);
//        if (cfg != null && (system == -1 || system.equals(cfg.getCfgSystem()))) {
//            flag = true;
//        }
//        return flag;
//    }
//}
