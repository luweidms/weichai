package com.youming.youche.system.provider.service.audit;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.system.api.audit.IAuditConfigService;
import com.youming.youche.system.api.audit.IAuditNodeConfigService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditNodeRuleConfigService;
import com.youming.youche.system.api.audit.IAuditRuleConfigService;
import com.youming.youche.system.api.audit.IAuditUserService;
import com.youming.youche.system.domain.audit.*;
import com.youming.youche.system.provider.mapper.audit.AuditConfigMapper;
import com.youming.youche.system.provider.mapper.audit.AuditNodeConfigMapper;
import com.youming.youche.system.provider.mapper.audit.AuditNodeInstMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import com.youming.youche.util.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 审核配置主表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@DubboService(version = "1.0.0")
public class AuditConfigServiceImpl extends ServiceImpl<AuditConfigMapper, AuditConfig>
        implements IAuditConfigService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private IAuditNodeConfigService auditNodeConfigService;

    @Resource
    private IAuditRuleConfigService auditRuleConfigService;

    @Resource
    private IAuditUserService auditUserService;

    @Resource
    private IAuditNodeRuleConfigService auditNodeRuleConfigService;

    @Resource
    IAuditNodeInstService iAuditNodeInstService;

    @Resource
    AuditNodeInstMapper auditNodeInstMapper;

    @Resource
    AuditNodeConfigMapper auditNodeConfigMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditConfigServiceImpl.class);

    @Override
    public AuditConfig getAuditConfigByCode(String auditCode) {
        QueryWrapper<AuditConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("audit_code", auditCode);
        List<AuditConfig> auditConfigs = baseMapper.selectList(queryWrapper);
        if (auditConfigs == null || auditConfigs.size() == 0) {
            return null;
        } else {
            if (auditConfigs.size() == 1) {
                return auditConfigs.get(0);
            } else {
                throw new BusinessException("配置的审核业务编码重复，审核配置编码[" + auditCode + "]");
            }
        }
    }

    @Override
    public AuditNodeInst queryAuditIng(String busiCode, Long busiId, Long tenantId) {
        QueryWrapper<AuditNodeInst> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("BUSI_ID", busiId).eq("AUDIT_CODE", busiCode).
                eq("STATUS", AuditConsts.Status.AUDITING).eq("TENANT_ID", tenantId)
                .eq("AUDIT_RESULT", AuditConsts.RESULT.TO_AUDIT);
        List<AuditNodeInst> auditNodeInsts = auditNodeInstMapper.selectList(queryWrapper);
        LOGGER.info("审核参数busiCode"+busiCode,"busiId"+busiId,"tenantId"+tenantId);
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
    public AuditNodeConfig getFirstAuditNodeConfig(Long auditId, String token, Long tenantId) {
        Param param = new Param();
        param.addParam("audit_id", auditId);
        param.addParam("parent_node_id", -1L);
        if (tenantId != null) {
            param.addParam("tenant_id", tenantId);
        }
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + token);
        boolean noTenantId = true;
        QueryWrapper<AuditNodeConfig> auditNodeConfigQueryWrapper = new QueryWrapper<>();
        if (param != null) {
            List<String> allcolumns = param.getAllcolumns();
            for (String column : allcolumns) {
                if ("tenant_id".equals(column)) {
                    noTenantId = false;
                }
                Object paramValue = param.getParamValue(column);
                if (paramValue instanceof Collection) {
                    auditNodeConfigQueryWrapper.in(column, paramValue);
                } else {
                    auditNodeConfigQueryWrapper.eq(column, param.getParamValue(column));
                }
            }
        }
        if (noTenantId) {
            auditNodeConfigQueryWrapper.eq("tenantId", loginInfo.getTenantId());
        }
        List<AuditNodeConfig> auditConfigs = auditNodeConfigService.list(auditNodeConfigQueryWrapper);
        if (auditConfigs == null || auditConfigs.size() == 0) {
            return null;
        } else {
            if (auditConfigs.size() == 1) {
                return auditConfigs.get(0);
            } else {
                throw new BusinessException("配置的审核节点的第一个节点重复，审核配置主键[" + auditId + "]");
            }
        }
    }

    @Override
    public AuditNodeConfig getFirstAuditNodeConfig(Long auditId, Long tenantId) {
        QueryWrapper<AuditNodeConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("AUDIT_ID", auditId).eq("PARENT_NODE_ID", -1L);
        if (tenantId != null) {
            queryWrapper.eq("TENANT_ID", tenantId);
        }

        List<AuditNodeConfig> auditConfigs = auditNodeConfigMapper.selectList(queryWrapper);
        if (auditConfigs == null || auditConfigs.size() == 0) {
            return null;
        } else {
            if (auditConfigs.size() == 1) {
                return auditConfigs.get(0);
            } else {
                throw new BusinessException("配置的审核节点的第一个节点重复，审核配置主键[" + auditId + "]");
            }
        }
    }

    @Override
    public Integer getAuditRuleVersionNo(Long nodeId) {
        List<Integer> list = auditNodeInstMapper.getAuditRuleVersionNo(nodeId);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public void initAuditNode(Long tenantId, Long rootUserId, String token) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + token);
        List<SysStaticData> sysStaticDatas = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("AUDIT_CODE"));
        List<String> auditCode = new ArrayList<>();
        if (sysStaticDatas != null && sysStaticDatas.size() > 0) {
            for (SysStaticData sysStaticData : sysStaticDatas) {
                auditCode.add(sysStaticData.getCodeValue());
            }
            QueryWrapper<AuditConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("auditCode", auditCode);
            List<AuditConfig> auditConfigs = baseMapper.selectList(queryWrapper);
            List<Long> auditIds = new ArrayList<>();
            if (auditConfigs != null && auditConfigs.size() > 0) {
                for (AuditConfig auditConfig : auditConfigs) {
                    auditIds.add(auditConfig.getId());
                }
                QueryWrapper<AuditNodeConfig> queryWrapperA = new QueryWrapper();
                queryWrapperA.in("auditId", auditIds);
                queryWrapperA.eq("tenantId", tenantId);
                List<AuditNodeConfig> auditNodeConfigs = auditNodeConfigService.list(queryWrapperA);
                List<AuditRuleConfig> auditRuleConfigByCode = auditRuleConfigService.list(new QueryWrapper<AuditRuleConfig>().eq("ruleCode", AuditConsts.RULE_CODE.EXCEED_GUIDE_PRICE));

                for (AuditConfig auditConfig : auditConfigs) {
                    if (auditNodeConfigs != null && auditNodeConfigs.size() > 0) {
                        for (AuditNodeConfig auditNodeConfig : auditNodeConfigs) {
                            if (auditNodeConfig.getAuditId().longValue() == auditConfig.getId().longValue()) {
                                log.error("已经存在节点配置信息，不做初始化！租户ID：" + tenantId + "auditCode：" + auditConfig.getAuditCode());
                                continue;
                            }
                        }
                    }
                    AuditNodeConfig auditNodeConfig = new AuditNodeConfig();
                    auditNodeConfig.setAuditId(auditConfig.getId());
                    auditNodeConfig.setParentNodeId(-1L);
                    auditNodeConfig.setNodeName(auditConfig.getBusiName() + "单节点");
                    auditNodeConfig.setTenantId(tenantId);
                    auditNodeConfig.setOpId(loginInfo.getUserInfoId());
                    auditNodeConfig.setVersion(1);
                    auditNodeConfig.setUpdateTime(LocalDateTime.now());
                    auditNodeConfig.setOpId(loginInfo.getUserInfoId());
                    auditNodeConfigService.saveOrUpdate(auditNodeConfig);
                    AuditUser auditUser = new AuditUser();
                    auditUser.setTargetObjType(AuditConsts.TargetObjType.USER_TYPE);
                    auditUser.setTargetObjId(rootUserId);
                    auditUser.setNodeId(auditNodeConfig.getId());
                    auditUser.setVersion(auditNodeConfig.getVersion().longValue());
                    auditUserService.saveOrUpdate(auditUser);

                    //只有价格审核才需要初始化配置
                    if (AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE.equals(auditConfig.getAuditCode())) {
                        if (auditRuleConfigByCode == null || auditRuleConfigByCode.size() == 0) {
                            throw new BusinessException("价格审核节点未配置规则配置，请配置！");
                        }
                        QueryWrapper<AuditNodeRuleConfig> auditNodeRuleConfigQueryWrapper = new QueryWrapper<>();
                        auditNodeRuleConfigQueryWrapper.eq("audit_id", auditConfig.getId());
                        auditNodeRuleConfigQueryWrapper.eq("rule_id", auditRuleConfigByCode.get(0).getId());
                        auditNodeRuleConfigQueryWrapper.eq("tenant_id", tenantId);
                        List<AuditNodeRuleConfig> auditNodeRuleConfigs = auditNodeRuleConfigService.list(auditNodeRuleConfigQueryWrapper);
                        if (auditNodeRuleConfigs != null && auditNodeRuleConfigs.size() > 0) {
                            log.error("已经存在规则配置信息，不做初始化！租户ID：" + tenantId + "auditCode：" + auditConfig.getAuditCode());
                            continue;
                        }
                        AuditNodeRuleConfig auditNodeRuleConfig = new AuditNodeRuleConfig();
                        auditNodeRuleConfig.setAuditId(auditNodeConfig.getAuditId());
                        auditNodeRuleConfig.setNodeId(auditNodeConfig.getId());
                        auditNodeRuleConfig.setRuleValue("20");
                        auditNodeRuleConfig.setRuleId(auditRuleConfigByCode.get(0).getId());
                        auditNodeRuleConfig.setVersion(1);
                        auditNodeRuleConfig.setTenantId(tenantId);
                        auditNodeRuleConfigService.saveOrUpdate(auditNodeRuleConfig);
                    }
                }
            } else {
                throw new BusinessException("未配置审核模板！");
            }
        } else {
            throw new BusinessException("未配置审核类型！");
        }

    }

    @Override
    public boolean isAuditIng(String busiCode, Long busiId, Long tenantId) {
        QueryWrapper<AuditNodeInst> auditNodeInstQueryWrapper = new QueryWrapper<>();
        auditNodeInstQueryWrapper.eq("", busiCode);
        auditNodeInstQueryWrapper.eq("", busiId);
        auditNodeInstQueryWrapper.eq("", tenantId);
        auditNodeInstQueryWrapper.eq("", AuditConsts.Status.AUDITING);
        int coutn = iAuditNodeInstService.count(auditNodeInstQueryWrapper);
        if (coutn > 0) {
            return true;
        }
        return false;
    }


    @Override //getAuditNodeRuleConfigByNodeVer   getAuditNodeRuleConfigByNodeVer
    public boolean checkNodePreRule(Long nodeId, Long busiId, Map params, Integer ruleVersion, Long tenantId, Boolean isStart) throws BusinessException {
        boolean isNew = checkVersionRule(ruleVersion, nodeId, tenantId);
        Map<String, Object> map = getAuditNodeRuleConfigByNodeVer(nodeId, ruleVersion, isNew, tenantId, isStart);
        //节点有配置规则，如果有多个规则，有一个满足就可以到下一个节点
        if (map != null) {
            boolean pass = false;
            List<AuditRuleConfig> ruleList = (List<AuditRuleConfig>) map.get("AuditRuleConfig");
            if (null != ruleList) {
                for (int i = 0; i < ruleList.size(); i++) {
                    AuditRuleConfig auditRuleConfig = ruleList.get(i);
                    String ruleValue = "";
                    if (isNew) {
                        List<AuditNodeRuleConfig> list = (List<AuditNodeRuleConfig>) map.get("AuditNodeRuleConfig");
                        if (list != null && list.size() > 0) {
                            AuditNodeRuleConfig auditNodeRuleConfig = list.get(i);
                            ruleValue = auditNodeRuleConfig.getRuleValue();
                        }
                    } else {
                        List<AuditNodeRuleConfigVer> list = (List<AuditNodeRuleConfigVer>) map.get("AuditNodeRuleConfigVer");
                        if (list != null && list.size() > 0) {
                            AuditNodeRuleConfigVer auditNodeRuleConfigVer = list.get(i);
                            ruleValue = auditNodeRuleConfigVer.getRuleValue();
                        }
                    }
                    if (checkRule(auditRuleConfig.getRuleType(), ruleValue, auditRuleConfig.getTargetObj(), auditRuleConfig.getRuleKey(), busiId, params)) {
                        pass = true;
                        break;
                    }
                }
                //前置规则都没有满足，不需要走流程
                return pass;
            }
        }
        return true;
    }

    @Override
    public Map<String, Boolean> checkNodePreRule(String auditCode, Long busiId,
                                                 Map<String, Object> params, LoginInfo baseUser) {
        Map<String, Boolean> retMap = new HashMap<String, Boolean>();

        if (StringUtils.isBlank(auditCode)) {
            throw new BusinessException("审核业务编码不能为空");
        }
        if (busiId == null) {
            throw new BusinessException("审核业务主键不能为空");
        }

        AuditConfig auditConfig = getAuditConfigByCode(auditCode);
        if (auditConfig == null) {
            log.error("审核业务编码[" + auditCode + "] 没有配置审核流程");
            return retMap;
        }

        Long tenantId = baseUser.getTenantId();


        AuditNodeConfig auditNodeConfig = getFirstAuditNodeConfig(auditConfig.getId(), tenantId);
        if (auditNodeConfig == null) {
            log.error("审核业务编码[" + auditCode + "] 没有配置审核流程的节点");
            return retMap;
        }

        Long nodeId = auditNodeConfig.getId();
        Integer ruleVersion = getAuditRuleVersionNo(nodeId);
        boolean isNew = checkVersionRule(ruleVersion, nodeId, tenantId);

        Map<String, Object> map = getAuditNodeRuleConfigByNodeVer(nodeId, ruleVersion, isNew, tenantId, true);
        //节点有配置规则，如果有多个规则，有一个满足就可以到下一个节点
        if (map != null) {
            boolean pass = false;
            List<AuditRuleConfig> ruleList = (List<AuditRuleConfig>) map.get("AuditRuleConfig");
            if (null != ruleList) {
                for (int i = 0; i < ruleList.size(); i++) {
                    AuditRuleConfig auditRuleConfig = ruleList.get(i);
                    String ruleValue = "";
                    if (isNew) {
                        List<AuditNodeRuleConfig> list = (List<AuditNodeRuleConfig>) map.get("AuditNodeRuleConfig");
                        if (list != null && list.size() > 0) {
                            AuditNodeRuleConfig auditNodeRuleConfig = list.get(i);
                            ruleValue = auditNodeRuleConfig.getRuleValue();
                        }
                    } else {
                        List<AuditNodeRuleConfigVer> list = (List<AuditNodeRuleConfigVer>) map.get("AuditNodeRuleConfigVer");
                        if (list != null && list.size() > 0) {
                            AuditNodeRuleConfigVer auditNodeRuleConfigVer = list.get(i);
                            ruleValue = auditNodeRuleConfigVer.getRuleValue();
                        }
                    }
                    if (checkRule(auditRuleConfig.getRuleType(), ruleValue, auditRuleConfig.getTargetObj(), auditRuleConfig.getRuleKey(), busiId, params)) {
                        retMap.put(auditRuleConfig.getRuleCode(), true);
                        break;
                    }
                }
                //前置规则都没有满足，不需要走流程
                return retMap;
            }
        }
        return retMap;


//        Map config = getAuditNodeRuleConfigByNode(nodeId, ruleVersion, isNew, tenantId, true);
//        //节点有配置规则，如果有多个规则，有一个满足就可以到下个节点
//        if(config!=null && config.size()>0){
//            List<AuditRuleConfig> ruleList = (List<AuditRuleConfig>) config.get("auditNodeConfigs");
//            for (AuditRuleConfig auditRuleConfig : ruleList) {
//                retMap.put(auditRuleConfig.getRuleCode(), false);
//                String ruleValue = "";
//                if(isNew){
//                    List<AuditNodeRuleConfig> auditNodeRuleConfig = (List<AuditNodeRuleConfig>) config.get("auditNodeRuleConfigs");
//                    if(auditNodeRuleConfig != null && auditNodeRuleConfig.size() > 0){
//                        ruleValue = auditNodeRuleConfig.get(0).getRuleValue();
//                    }
//
//                }else {
//                    AuditNodeRuleConfigVer auditNodeRuleConfigVer =(AuditNodeRuleConfigVer)config.get("auditNodeRuleConfigVers");
//                    ruleValue = auditNodeRuleConfigVer.getRuleValue();
//                }
//                if(checkRule(auditRuleConfig.getRuleType(), ruleValue, auditRuleConfig.getTargetObj(), auditRuleConfig.getRuleKey(), busiId, params)){
//                    retMap.put(auditRuleConfig.getRuleCode(), true);
//                }
//            }
//        }
//        return retMap;
    }


    /**
     * 校验是否最新版本的规则
     *
     * @param version
     * @param nodeId
     * @return
     */
    public boolean checkVersionRule(Integer version, long nodeId, Long tenantId) {
        Map map = new HashMap();
        if (null != tenantId) {
            map.put("tenantId", tenantId);
        }
        Integer i = auditNodeInstMapper.checkVersionRule(version, nodeId, tenantId);
        if (i > 0) {
            return true;
        }
        return false;
    }

    private Map getAuditNodeRuleConfigByNode(Long nodeId, Integer version, boolean isNew, Long tenantId, Boolean isStart) {
        Map map = new HashMap();
        List<AuditRuleConfig> auditNodeConfigs = null;
        List<AuditNodeRuleConfig> auditNodeRuleConfigs = null;
        List<AuditNodeRuleConfigVer> auditNodeRuleConfigVers = null;
        if (isNew) {
            auditNodeConfigs = auditNodeInstMapper.getAuditRuleConfigData(nodeId);
            auditNodeRuleConfigs = auditNodeInstMapper.getAuditRuleConfigDatinfo(tenantId, version, nodeId);
            if (auditNodeConfigs != null) {
                map.put("auditNodeConfigs", auditNodeConfigs);
            }
            if (auditNodeRuleConfigs != null) {
                map.put("auditNodeRuleConfigs", auditNodeRuleConfigs);
            }
        } else {
            auditNodeConfigs = auditNodeInstMapper.getAuditRuleConfigDataInfo(tenantId);
            auditNodeRuleConfigVers = auditNodeInstMapper.getAuditNodeRuleConfigVer(tenantId, version, nodeId);
            if (isStart) {
                if (auditNodeConfigs != null && auditNodeRuleConfigVers != null && auditNodeConfigs.size() > 0 && auditNodeRuleConfigVers.size() > 0) {
                    if (auditNodeConfigs != null) {
                        map.put("auditNodeConfigs", auditNodeConfigs);
                    }
                    if (auditNodeRuleConfigVers != null) {
                        map.put("auditNodeRuleConfigVers", auditNodeRuleConfigVers);
                    }
                } else {
                    return null;
                }
            } else {
                if (auditNodeConfigs != null) {
                    map.put("auditNodeConfigs", auditNodeConfigs);
                }
                if (auditNodeRuleConfigVers != null) {
                    map.put("auditNodeRuleConfigVers", auditNodeRuleConfigVers);
                }
            }
        }
        return map;
    }

    /**
     * @param nodeId
     * @param version
     * @param isNew
     * @param isStart true开启审核，false 审核流程
     * @return
     * @throws Exception
     */
    private Map getAuditNodeRuleConfigByNodeVer(Long nodeId, Integer version, boolean isNew, Long tenantId, Boolean isStart) throws BusinessException {
        Map map = new HashMap();
        List<AuditNodeRuleConfig> auditNodeConfigs = null;
        List<AuditRuleConfig> auditNodeRuleConfigs = null;
        List<AuditNodeRuleConfigVer> auditNodeRuleConfigVers = null;
        if (isNew) {
            auditNodeConfigs = auditNodeInstMapper.getAuditNodeRuleConfigData(nodeId);
            auditNodeRuleConfigs = auditNodeInstMapper.getAuditRuleConfigData(nodeId);
            if (auditNodeConfigs != null) {
                map.put("AuditNodeRuleConfig", auditNodeConfigs);
            }
            if (auditNodeRuleConfigs != null) {
                map.put("AuditRuleConfig", auditNodeRuleConfigs);
            }
        } else {
            auditNodeRuleConfigs = auditNodeInstMapper.getAuditRuleConfigDatainfo(tenantId, version, nodeId);
            auditNodeRuleConfigVers = auditNodeInstMapper.getAuditNodeRuleConfigVer(tenantId, version, nodeId);
            if (isStart) {
                if (auditNodeRuleConfigs != null && auditNodeRuleConfigVers != null && auditNodeRuleConfigs.size() > 0 && auditNodeRuleConfigVers.size() > 0) {
                    if (auditNodeRuleConfigs != null) {
                        map.put("AuditRuleConfig", auditNodeRuleConfigs);
                    }
                    if (auditNodeRuleConfigVers != null) {
                        map.put("AuditNodeRuleConfigVer", auditNodeRuleConfigVers);
                    }
                } else {
                    return null;
                }
            }
        }
        return map;
    }


    public boolean checkRule(Integer ruleType, String ruleValue,
                             String targetObj, String ruleKey, Long busiId, Map<String, Object> params) throws BusinessException {
        try {
            if (targetObj.equals("AuditDefaultRule")) {
                if (ruleType == null) {
                    throw new BusinessException("审核的规则的规则类型没有配置");
                }
                if (ruleType < 0 || ruleType > 5) {
                    throw new BusinessException("审核的规则的规则类型值配置不对，只能是1到5的值");
                }

                if (StringUtils.isBlank(ruleValue)) {
                    throw new BusinessException("审核的规则的规则的校验值没有配置");
                }

                if (StringUtils.isBlank(ruleKey)) {
                    throw new BusinessException("审核的规则的校验的key值没有配置");
                }

                Object checkValue = params.get(ruleKey);

                if (checkValue == null) {
                    throw new BusinessException("审核的规则的获取校验的值为空，请检查启动传入的数据是否正确");
                }
                Long ruleValueL = Long.parseLong(ruleValue);
                if (AuditConsts.RuleType.GT == ruleType) {
                    if (Long.parseLong(checkValue.toString()) > ruleValueL) {
                        return true;
                    }
                } else if (AuditConsts.RuleType.GE == ruleType) {
                    if (Long.parseLong(checkValue.toString()) >= ruleValueL) {
                        return true;
                    }
                } else if (AuditConsts.RuleType.EQ == ruleType) {
                    if (Long.parseLong(checkValue.toString()) == ruleValueL) {
                        return true;
                    }
                } else if (AuditConsts.RuleType.LT == ruleType) {
                    if (Long.parseLong(checkValue.toString()) < ruleValueL) {
                        return true;
                    }
                } else if (AuditConsts.RuleType.LE == ruleType && Long.parseLong(checkValue.toString()) <= ruleValueL) {
                    return true;
                }
            } else if (targetObj.equals("AuditGuidePriceRule")) {
                if (params.get(AuditConsts.RuleMapKey.GUIDE_PRICE) == null) {
                    return false;
                }
                if (params.get(AuditConsts.RuleMapKey.TOTAL_FEE) == null) {
                    return false;
                }
                Long guidePrice = Long.valueOf(params.get(AuditConsts.RuleMapKey.GUIDE_PRICE).toString());
                Long totalFell = Long.valueOf(params.get(AuditConsts.RuleMapKey.TOTAL_FEE).toString());

                if ((guidePrice > 0 || totalFell > 0) && totalFell > ((100 + Float.valueOf(ruleValue)) * guidePrice / 100)) {
                    return true;
                }
            } else if (targetObj.equals("AuditPreTotalFeeRule")) {
                if (params.get(AuditConsts.RuleMapKey.PRE_TOTAL_SCALE_STANDARD) == null) {
                    return false;
                }
                if (params.get(AuditConsts.RuleMapKey.PRE_TOTAL_SCALE) == null) {
                    return false;
                }
                Long preTotal = Long.valueOf(params.get(AuditConsts.RuleMapKey.PRE_TOTAL_SCALE_STANDARD).toString());
                Long preCash = Long.valueOf(params.get(AuditConsts.RuleMapKey.PRE_TOTAL_SCALE).toString());
                if ((preTotal > 0 || preCash > 0) && preCash > preTotal) {
                    return true;
                }
            } else if (targetObj.equals("AuditOilScaleRule")) {
                if (params.get(AuditConsts.RuleMapKey.PRE_OIL_TOTAL_STANDARD) == null) {
                    return false;
                }
                if (params.get(AuditConsts.RuleMapKey.PRE_OIL_TOTAL) == null) {
                    return false;
                }
                long preOilTotal = Long.parseLong(params.get(AuditConsts.RuleMapKey.PRE_OIL_TOTAL).toString());
                long preOilTotalStandard = Long.parseLong(params.get(AuditConsts.RuleMapKey.PRE_OIL_TOTAL_STANDARD).toString());
                if (preOilTotal > 0 || preOilTotalStandard > 0) {
                    if (AuditConsts.RuleType.GT == ruleType) {
                        if (preOilTotal > preOilTotalStandard) {
                            return true;
                        }
                    } else if (AuditConsts.RuleType.LT == ruleType && preOilTotal < preOilTotalStandard) {
                        return true;
                    }
                }
            } else if (targetObj.equals("AuditETCScaleRule")) {
                if (params.get(AuditConsts.RuleMapKey.PRE_ETC_TOTAL_STANDARD) == null) {
                    return false;
                }
                if (params.get(AuditConsts.RuleMapKey.PRE_ETC_TOTAL) == null) {
                    return false;
                }
                long preEtcTotal = Long.parseLong(params.get(AuditConsts.RuleMapKey.PRE_ETC_TOTAL).toString());
                long preEtcTotalStandard = Long.parseLong(params.get(AuditConsts.RuleMapKey.PRE_ETC_TOTAL_STANDARD).toString());
                if (preEtcTotal > 0 || preEtcTotalStandard > 0) {
                    if (AuditConsts.RuleType.GT == ruleType) {
                        if (Long.parseLong(params.get(AuditConsts.RuleMapKey.PRE_ETC_TOTAL).toString()) > Long.parseLong(params.get(AuditConsts.RuleMapKey.PRE_ETC_TOTAL_STANDARD).toString())) {
                            return true;
                        }
                    } else if (AuditConsts.RuleType.LT == ruleType && Long.parseLong(params.get(AuditConsts.RuleMapKey.PRE_ETC_TOTAL).toString()) < Long.parseLong(params.get(AuditConsts.RuleMapKey.PRE_ETC_TOTAL_STANDARD).toString())) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
