//package com.youming.youche.record.provider.service.impl.sys;
//
//
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.youming.youche.commons.util.RedisUtil;
//import com.youming.youche.conts.SysStaticDataEnum;
//import com.youming.youche.record.api.sys.ISysStaticDataMarketService;
//import com.youming.youche.record.api.user.IUserDataInfoRecordService;
//import com.youming.youche.record.domain.sys.SysStaticData;
//import com.youming.youche.record.domain.user.UserDataInfo;
//import com.youming.youche.record.provider.mapper.sys.SysStaticDataMapper;
//import org.apache.dubbo.common.utils.StringUtils;
//import org.apache.dubbo.config.annotation.DubboService;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * <p>
// * 静态数据表 服务实现类
// * </p>
// *
// * @author Terry
// * @since 2021-11-21
// */
//@DubboService(version = "1.0.0")
//public class SysStaticDataServiceImpl extends ServiceImpl<SysStaticDataMapper, SysStaticData> implements ISysStaticDataMarketService {
//
//
//
//    @Resource
//    IUserDataInfoRecordService iUserDataInfoRecordService;
//
//    @Resource
//    RedisUtil redisUtil;
//
//
//    @Override
//    public List<SysStaticData> getSysStaticDataList(String codeType, String accessToken) throws Exception {
//        if (StringUtils.isNotEmpty(codeType)) {
//            long tenantId = -1L;
//            UserDataInfo user = iUserDataInfoRecordService.getUserDataInfoByAccessToken(accessToken);
//            if (user != null && user.getTenantId() != null) {
//                tenantId = user.getTenantId();
//            }
//            List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
//            List<SysStaticData> staticDataList = new ArrayList<>();
//            for (SysStaticData sysStaticDate : list
//            ) {
//                if(sysStaticDate.getTenantId().equals(tenantId)){
//                    staticDataList.add(sysStaticDate);
//                }
//            }
//            return staticDataList;
//        } else {
//            return null;
//        }
//    }
//
//    @Override
//    public SysStaticData getSysStaticData(String codeType, String codeValue, String accessToken) throws Exception {
//        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
//        if (list != null && list.size() > 0) {
//            for (SysStaticData sysStaticData : list) {
//                if (sysStaticData.getCodeValue().equals(codeValue)) {
//                    return sysStaticData;
//                }
//            }
//        }
//        return new SysStaticData();
//    }
//
//    @Override
//    public List<SysStaticData> getSysStaticDataListByCodeName(String vehicleLength) {
//        List<SysStaticData> sysStaticDataCodeName = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(vehicleLength));
//        return sysStaticDataCodeName;
//    }
//}
