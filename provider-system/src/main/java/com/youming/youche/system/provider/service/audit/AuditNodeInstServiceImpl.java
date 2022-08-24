package com.youming.youche.system.provider.service.audit;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditNodeInstVerService;
import com.youming.youche.system.api.audit.IAuditUserService;
import com.youming.youche.system.constant.AuditConsts;
import com.youming.youche.system.domain.audit.AuditNodeInst;
import com.youming.youche.system.domain.audit.AuditNodeInstVer;
import com.youming.youche.system.domain.audit.AuditUser;
import com.youming.youche.system.provider.mapper.audit.AuditNodeInstMapper;
import com.youming.youche.system.provider.mapper.audit.AuditSettingMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 审核节点实例表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */

@DubboService(version = "1.0.0")
@Service
public class AuditNodeInstServiceImpl extends ServiceImpl<AuditNodeInstMapper, AuditNodeInst>
        implements IAuditNodeInstService {

    @Resource
    private IAuditUserService iAuditUserService;

    @Resource
    private AuditSettingMapper auditSettingMapper;

    @Resource
    IUserDataInfoService iUserDataInfoService;

    @Resource
    RedisUtil redisUtil;

    @Resource
    IAuditNodeInstVerService iAuditNodeInstVerService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditNodeInstServiceImpl.class);


/*    @DubboReference(version = "1.0.0")
    ICmCustomerInfoService cmCustomerInfoService;

    @DubboReference(version = "1.0.0")
    ICmCustomerLineService cmCustomerLineService;

    @DubboReference(version = "1.0.0")
    IUserService iUserService;

    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService iVehicleDataInfoService;

    @DubboReference(version = "1.0.0")
    IAuditCallBackService auditCallBackService;

    @DubboReference(version = "1.0.0")
    ITrailerManagementService iTrailerManagementService;*/

//    @DubboReference(version = "1.0.0")
//    IPayFeeLimitVerService iPayFeeLimitVerService;
//
//    @DubboReference(version = "1.0.0")
//    IServiceProductService iServiceProductService;
//
////    @DubboReference(version = "1.0.0")
////    IServiceInfoService serviceInfoService;

    @Resource
    private AuditNodeInstMapper auditNodeInstMapper;


    @Override
    public List<AuditNodeInst> queryAuditIng(String busiCode, List<Long> busiId) {
        QueryWrapper<AuditNodeInst> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("audit_Code", busiCode).
                eq("STATUS", AuditConsts.Status.AUDITING).
                eq("AUDIT_RESULT", AuditConsts.RESULT.TO_AUDIT).in("BUSI_ID", busiId);
        return auditNodeInstMapper.selectList(queryWrapper);
    }

    @Override
    public AuditNodeInst queryAuditIng(String busiCode, Long busiId, Long tenantId) {
        QueryWrapper<AuditNodeInst> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("audit_Code", busiCode)
                .eq("busi_Id", busiId)
                .eq("tenant_Id", tenantId)
                .eq("status", AuditConsts.Status.AUDITING)
                .eq("audit_result", AuditConsts.RESULT.TO_AUDIT);
        LOGGER.info("审核参数busiCode"+busiCode,"busiId"+busiId,"tenantId"+tenantId);
        List<AuditNodeInst> auditNodeInsts = baseMapper.selectList(queryWrapper);
        if (auditNodeInsts != null && auditNodeInsts.size() > 0) {
            if (auditNodeInsts.size() == 1) {
                return auditNodeInsts.get(0);
            } else {
                throw new BusinessException("业务编码[" + busiCode + "],业务主键[" + busiId + "] 数据有多条在审核的流程");
            }
        }
        return null;
    }

    @Override
    public List<AuditNodeInst> queryAuditIngList(String busiCode, Long busiId, Long tenantId) {
        QueryWrapper<AuditNodeInst> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("audit_Code", busiCode)
                .eq("busi_Id", busiId)
                .eq("tenant_Id", tenantId)
                .eq("status", AuditConsts.Status.AUDITING)
                .eq("audit_result", AuditConsts.RESULT.TO_AUDIT);
        return baseMapper.selectList(queryWrapper);

    }

    @Override
    public List<AuditNodeInst> queryAuditNodeInst(String busiCode, Long busiId, Long tenantId) {
        return selectByBusiCodeAndBusiIdAndTenantIdAntStatus(busiCode, busiId, tenantId, AuditConsts.Status.FINISH);
    }

    @Override
    public List<AuditNodeInst> selectByBusiCodeAndBusiIdAndTenantIdAntStatus(String busiCode, Long busiId, Long tenantId,
                                                                             Integer status) {
        LambdaQueryWrapper<AuditNodeInst> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AuditNodeInst::getAuditCode, busiCode).eq(AuditNodeInst::getBusiId, busiId)
                .eq(AuditNodeInst::getTenantId, tenantId).eq(AuditNodeInst::getStatus, status);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public Map<Long, Boolean> isHasPermission(String auditCode, List<Long> busiIdList, String accessToken) {
        if(busiIdList==null || busiIdList.size()==0){
            throw new BusinessException("传入的判断的数据列表为空");
        }
        Map<Long, Boolean> returnMap=new HashMap<Long, Boolean>();
        for (Long busiId : busiIdList) {
            returnMap.put(busiId, false);
        }
        LoginInfo user = iUserDataInfoService.getLoginInfoByAccessToken(accessToken);
//		Map<String,Object> userAttr= user.getAttrs();
//		List<Long> rolesIds=(List<Long>)userAttr.get("roleIds");
        List<AuditNodeInst> auditNodeInsts= this.queryAuditIng(auditCode, busiIdList);
        if(auditNodeInsts!=null && auditNodeInsts.size()>0){
            for (AuditNodeInst auditNodeInst : auditNodeInsts) {
                if(returnMap.get(auditNodeInst.getBusiId())!=null && this.isHasPermission(user.getUserInfoId(), auditNodeInst)){
                    //有权限
                    returnMap.put(auditNodeInst.getBusiId(), true);
                }
            }
        }
        return returnMap;
    }

    @Override
    public boolean isHasPermission(Long userId, Long auditNodeInstId) {
        AuditNodeInst auditNodeInst = getById(auditNodeInstId);
        return isHasPermission(userId, auditNodeInst);
    }

    @Override
    public boolean isHasPermission(Long userId, AuditNodeInst auditNodeInst) {
        Long nodeId = auditNodeInst.getNodeId();
        if (nodeId == null) {
            return false;
        }
        AuditUser auditUser = iAuditUserService.getAuditUser(nodeId, userId, AuditConsts.TargetObjType.USER_TYPE);
        if (auditUser != null) {
            return true;
        }

        return false;
    }

    @Override
    public Long getAuditingNodeNum(String busiCode, Long busiId) {
        QueryWrapper<AuditNodeInst> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("audit_Code", busiCode)
                .eq("busi_Id", busiId)
                .eq("status", AuditConsts.Status.AUDITING);
        Integer count = baseMapper.selectCount(queryWrapper);
        return Long.valueOf(count);
    }


    @Override
    public String getAuditUserNameByNodeId(Long nodeId, Integer targetObjType) throws BusinessException {
        List<Long> userIdList = iAuditUserService.selectTargetObjIdByNodeIdAndTarGetObjType(nodeId, targetObjType);
        StringBuffer alert = new StringBuffer();
        if (userIdList != null && userIdList.size() > 0) {
            alert.append("人:");
            List<String> userNameList = iUserDataInfoService.querStaffName(userIdList);
            alert.append(listToString(userNameList));
        } else {
            throw new BusinessException("下个节点的审核的对象类型设置错误");
        }
        return alert.toString();
    }

    public String listToString(List<String> list) {
        if (list == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean first = true;
        //第一个前面不拼接","
        for (String string : list) {
            if (first) {
                first = false;
            } else {
                result.append(",");
            }
            result.append(string);
        }
        return result.toString();
    }

    @Override
    public void updateAuditInstToFinish(Long auditId, Long busiId) {
        auditSettingMapper.updateAuditInstToFinish(auditId, busiId);
    }

    @Override
    public void delInstToVer(String busiCode, Long busiId, Long tenantId) throws BusinessException {
        QueryWrapper<AuditNodeInst> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("audit_Code", busiCode)
                .eq("busi_Id", busiId)
                .eq("tenant_Id", tenantId)
                .eq("status", AuditConsts.Status.FINISH);
        List<AuditNodeInst> auditNodeInsts = baseMapper.selectList(queryWrapper);
        if (auditNodeInsts != null && auditNodeInsts.size() > 0) {
            for (AuditNodeInst auditNodeInst : auditNodeInsts) {
                AuditNodeInstVer auditNodeInstVer = new AuditNodeInstVer();
                BeanUtil.copyProperties(auditNodeInst, auditNodeInstVer);
                auditNodeInstVer.setVerId(auditNodeInst.getId());
                iAuditNodeInstVerService.save(auditNodeInstVer);
                removeById(auditNodeInst.getId());
            }
        }
    }

    @Override
    public void auditCallback(Long busiId, Integer result, String desc, Map paramsMap, String callback, String token) throws Exception {
        //Class cls = Class.forName(callback);
        //Method m=null;
        if (AuditConsts.RESULT.SUCCESS == result) {
    /*        if (callback.equals("com.youming.youche.record.api.cm.ICmCustomerInfoService")) {
                //客户档案审核成功
                cmCustomerInfoService.doPublicAuth(busiId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE2, desc);
            } else if (callback.equals("com.youming.youche.record.api.cm.ICmCustomerLineService")) {
                //客户线路审核
                cmCustomerLineService.doPublicAuth(busiId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE2, desc);
            } else if (callback.equals("com.youming.youche.record.api.user.IUserService")) {
                //司机审核成功
                iUserService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.vehicle.IVehicleDataInfoService")) {
                //车辆审核成功
                iVehicleDataInfoService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.vehicle.VehicleAuditCallBackService")) {
                //审核邀请车辆
                auditCallBackService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.trailer.ITrailerManagementService")) {
                //挂车审核
                iTrailerManagementService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.capital.api.IPayFeeLimitVerService")) {
                //资金风控审核成功
                iPayFeeLimitVerService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.market.api.facilitator.IServiceProductService")) {
                //后服站点审核成功
                iServiceProductService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.market.api.facilitator.IServiceInfoService")) {
                serviceInfoService.sucess(busiId, desc, paramsMap, token);
            }*/
            //	m = cls.getDeclaredMethod("sucess",new Class[]{Long.class,String.class,Map.class});
        } else if (AuditConsts.RESULT.FAIL == result) {
          /*  //	m = cls.getDeclaredMethod("fail",new Class[]{Long.class,String.class,Map.class});
            if (callback.equals("com.youming.youche.record.api.cm.ICmCustomerInfoService")) {
                //客户档案审核失败
                cmCustomerInfoService.doPublicAuth(busiId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE3, desc);
            } else if (callback.equals("com.youming.youche.record.api.cm.ICmCustomerLineService")) {
                //客户线路审核
                cmCustomerLineService.doPublicAuth(busiId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE3, desc);
            } else if (callback.equals("com.youming.youche.record.api.user.IUserService")) {
                //司机审核失败
                iUserService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.vehicle.IVehicleDataInfoService")) {
                //车辆审核失败
                iVehicleDataInfoService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.vehicle.VehicleAuditCallBackService")) {
                //审核邀请车辆
                auditCallBackService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.trailer.ITrailerManagementService")) {
                //挂车审核
                iTrailerManagementService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.capital.api.IPayFeeLimitVerService")) {
                //资金风控审核失败
                iPayFeeLimitVerService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.market.api.facilitator.IServiceProductService")) {
                //后服站点审核失败
                iServiceProductService.fail(busiId, desc, paramsMap);
            } else if (callback.equals("com.youming.youche.market.api.facilitator.IServiceInfoService")) {
                serviceInfoService.fail(busiId, desc, paramsMap);
            }*/
        } else {
            throw new BusinessException("传入的回调方法的结果类型只能1，2，传入的是[" + result + "]");
        }
    }

    @Override
    public List<Long> getBusiIdByUserId(String auditCode, Long userId, Long tenantId, Integer pageSize) {
        return auditNodeInstMapper.getBusiIdByUserId(auditCode, userId, tenantId, pageSize, EnumConsts.RESULT.TO_AUDIT);
    }


    @Override
    public Integer getAuditingNodeNum(String auditCode, Long busiId, Integer status) {
        LambdaQueryWrapper<AuditNodeInst> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AuditNodeInst::getAuditCode,auditCode).eq(AuditNodeInst::getBusiId,busiId).eq(AuditNodeInst::getStatus,status);
        return count(wrapper);
    }



    @Override
    public List<Map> getAuditNodeInstNew(String auditCode, long busiId, long tenantId, boolean isDesc, Integer... state) {
        StringBuffer stateSb = new StringBuffer();
        if (state != null && state.length > 0) {
            for (Integer i : state) {
                stateSb.append("'").append(i).append("',");
            }
        }
        String stateSbs = null;
        if (stateSb.length()>1){
            stateSbs = stateSb.substring(0, stateSb.length() - 1);
        }
        List<Map> list = auditNodeInstMapper.getAuditNodeInstNew(auditCode, busiId, tenantId, isDesc, stateSbs);
        if (list==null || list.size()<=0){
            list  = auditNodeInstMapper.getAuditNodeInstNewVer(auditCode, busiId, tenantId, isDesc, stateSbs);
        }
        if (list != null && list.size() > 0) {
            for (Map map : list) {
                queryResult(map, "userType", EnumConsts.SysStaticData.USER_TYPE);
                int auditResult = DataFormat.getIntKey(map, "auditResult");
                if (auditResult == com.youming.youche.conts.AuditConsts.RESULT.TO_AUDIT) {
                    map.put("auditResultName", "待审核");
                } else if (auditResult == com.youming.youche.conts.AuditConsts.RESULT.SUCCESS) {
                    map.put("auditResultName", "审核通过");
                } else if (auditResult == com.youming.youche.conts.AuditConsts.RESULT.FAIL) {
                    map.put("auditResultName", "审核不通过");
                }
            }
        }
        return list;
    }

    @Override
    public Boolean isAudited(String busiCode, Long busiId, Long tenantId) {
        LambdaQueryWrapper<AuditNodeInst> qw = new LambdaQueryWrapper<>();
        qw.eq(AuditNodeInst::getAuditCode, busiCode);
        qw.eq(AuditNodeInst::getBusiId, busiId);
        qw.eq(AuditNodeInst::getTenantId, tenantId);
        int count = this.count(qw);
        if (count > 0) {
            return true;
        }
        //查询历史表
        count = iAuditNodeInstVerService.queryCountByMultipleConditions(busiCode, busiId, tenantId);
        if (count > 0) {
            return true;
        }
        return false;
    }


    /**
     * 变量转换 map的值 key+'Name'
     *
     * @param map
     * @param key
     */
    private void queryResult(Map map, String key, String codeType) {

        String codeValue = DataFormat.getStringKey(map, key);
        if (Integer.parseInt(codeValue) >= 0) {
            List<SysStaticData> sysStaticDatas = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
            for (SysStaticData sysStaticData : sysStaticDatas) {
                if (sysStaticData.getCodeValue().equals(codeValue)) {
                    map.put(key + "Name", sysStaticData.getCodeName());
                } else {
                    map.put(key + "Name", "");
                }
            }
        } else {
            map.put(key + "Name", "");
        }

    }

}
