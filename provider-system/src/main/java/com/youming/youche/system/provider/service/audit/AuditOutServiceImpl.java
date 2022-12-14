package com.youming.youche.system.provider.service.audit;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ITenantStaffRelService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditConfigService;
import com.youming.youche.system.api.audit.IAuditNodeConfigService;
import com.youming.youche.system.api.audit.IAuditNodeConfigVerService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditNodeInstVerService;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.api.audit.IAuditUserService;
import com.youming.youche.system.constant.AuditConsts;
import com.youming.youche.system.domain.audit.AuditNodeConfig;
import com.youming.youche.system.domain.audit.AuditNodeConfigVer;
import com.youming.youche.system.domain.audit.AuditNodeInst;
import com.youming.youche.system.domain.audit.AuditNodeInstVer;
import com.youming.youche.system.provider.mapper.audit.AuditNodeInstMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liyiye
 */
@DubboService(version = "1.0.0")
public class AuditOutServiceImpl implements IAuditOutService {
    private static final Log log = LogFactory.getLog(AuditOutServiceImpl.class);
//
//    @DubboReference(version = "1.0.0")
//    IAuditConfigService auditSV;
//
//    @DubboReference(version = "1.0.0")
//    ISysOperLogService sysOperLogSV;
//
//    @DubboReference(version = "1.0.0")
//    private ITenantStaffRelService staffSV;

    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private IAuditNodeInstService auditNodeInstService;
    @Autowired
    private IAuditNodeInstVerService auditNodeInstVerService;

    @Resource
    private AuditNodeInstMapper auditNodeInstMapper;
    @Autowired
    private IAuditNodeInstService iAuditNodeInstService;

    @Autowired
    private IAuditUserService iAuditUserService;

    @Autowired
    IUserDataInfoService iUserDataInfoService;

    @Autowired
    IAuditNodeConfigService iAuditNodeConfigService;

    @Autowired
    IAuditNodeConfigVerService iAuditNodeConfigVerService;

//    @Override
//    /***
//     * 1 ?????????????????????????????? AUDIT_CONFIG???AUDIT_NODE_CONFIG ??????????????????????????????????????????
//     * 2 ????????????????????? AUDIT_NODE_RULE_CONFIG?????????????????????????????????????????????
//     * 3 ????????????????????? ???????????? AUDIT_NODE_INST ???
//     *
//     *
//     *
//     */
//    public boolean startProcess(String auditCode, Long busiId, SysOperLogConst.BusiCode busiCode, Map<String, Object> params, String token, Long... tenantIds) throws Exception {
//        return this.startProcess(auditCode, busiId, busiCode, params, null, null, token, tenantIds);
//    }
//
//    /***
//     * 1 ?????????????????????????????? AUDIT_CONFIG???AUDIT_NODE_CONFIG ??????????????????????????????????????????
//     * 2 ????????????????????? AUDIT_NODE_RULE_CONFIG?????????????????????????????????????????????
//     * 3 ????????????????????? ???????????? AUDIT_NODE_INST ???
//     *
//     *
//     *
//     */
//    public boolean startProcess(String auditCode, Long busiId, SysOperLogConst.BusiCode busiCode, Map<String, Object> params, Long opId, String opName, String token, Long... tenantIds) throws Exception {
//        if (StringUtils.isBlank(auditCode)) {
//            throw new BusinessException("??????????????????????????????");
//        }
//        if (busiId == null) {
//            throw new BusinessException("??????????????????????????????");
//        }
//
//        AuditConfig auditConfig = auditSV.getAuditConfigByCode(auditCode);
//        if (auditConfig == null) {
//            log.error("??????????????????[" + auditCode + "] ????????????????????????");
//            return false;
//        }
//        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + token);
//        Long tenantId = loginInfo.getTenantId();
//        if (tenantIds.length > 0) {
//            tenantId = tenantIds[0];
//        }
//
//        AuditNodeConfig auditNodeConfig = auditSV.getFirstAuditNodeConfig(auditConfig.getId(), token, tenantId);
//        if (auditNodeConfig == null) {
//            log.error("??????????????????[" + auditCode + "] ?????????????????????????????????,audid_config??????audit_id[" + auditConfig.getId() + "],tenant_id[" + tenantId + "]");
//            return false;
//        }
//
//        if (auditSV.isAuditIng(auditCode, busiId, tenantId)) {
//            //???????????????????????????????????????
//            throw new BusinessException("???????????????????????????????????????????????????????????????????????????????????????");
//        }
//
//        AuditNodeInst preAuditNodeInst = auditSV.queryAuditIng(auditCode, busiId, tenantId);
//        if (preAuditNodeInst != null) {
//            AuditNodeInstVer auditNodeInstVer = new AuditNodeInstVer();
//            BeanUtils.copyProperties(auditNodeInstVer, preAuditNodeInst);
//            //????????????-?????????
//            auditNodeInstVer.setStatus(AuditConsts.Status.FINISH);
//            //????????????-??????????????????????????????
//            auditNodeInstVer.setAuditResult(AuditConsts.RESULT.CANCEL);
//            //???????????????????????????ver???
//            auditSV.save(auditNodeInstVer);
//            auditSV.delete(preAuditNodeInst);
////			return true;
//        }
//        Integer ruleVersion = auditSV.getAuditRuleVersionNo(auditNodeConfig.getNodeId());
//        //??????????????????????????????????????????,????????????????????????????????????????????????
//        if (!auditSV.checkNodePreRule(auditNodeConfig.getNodeId(), busiId, params, ruleVersion, tenantId)) {
//            return false;
//        }
//
//        AuditNodeInst auditNodeInst = new AuditNodeInst();
//
//        auditNodeInst.setAuditId(auditConfig.getAuditId());
//        auditNodeInst.setAuditCode(auditConfig.getAuditCode());
//        auditNodeInst.setNodeId(auditNodeConfig.getNodeId());
//        auditNodeInst.setNodeName(auditNodeConfig.getNodeName());
//        auditNodeInst.setStatus(AuditConsts.Status.AUDITING);
//        auditNodeInst.setAuditResult(AuditConsts.RESULT.TO_AUDIT);
//        auditNodeInst.setParamsMap(JsonHelper.json(params));
//        auditNodeInst.setBusiId(busiId);
//        auditNodeInst.setLogBusiCode(busiCode.getCode());
//        auditNodeInst.setTenantId(tenantId);
//        auditNodeInst.setAuditBatch(getBatchId());
//        auditNodeInst.setVersion(auditNodeConfig.getVersion());
//        auditNodeInst.setRuleVersion(ruleVersion);
//        auditNodeInst.setNodeIndex(1);
//        auditSV.save(auditNodeInst);
//        //?????????????????????
//        List<Long> auditUserList = auditSV.getUserFromAuditUser(auditNodeInst.getNodeId(), TargetObjType.USER_TYPE);
//        auditSV.nodeAuditCallback(auditNodeInst.getNodeIndex(), busiId, TargetObjType.USER_TYPE, listLongToStr(auditUserList), auditConfig.getNodeCallback());
//        return true;
//    }
//
//
//    public List<Long> stringToList(String strs) {
//        List<Long> userIdList = new ArrayList<Long>();
//        for (String userIdStr : strs.split(",")) {
//            userIdList.add(Long.valueOf(userIdStr));
//        }
//        return userIdList;
//    }
//
//    public List<Integer> stringToListInt(String strs) {
//        List<Integer> userIdList = new ArrayList<Integer>();
//        for (String userIdStr : strs.split(",")) {
//            userIdList.add(Integer.valueOf(userIdStr));
//        }
//        return userIdList;
//    }
//
//    public String listToString(List<String> list) {
//        if (list == null) {
//            return null;
//        }
//        StringBuilder result = new StringBuilder();
//        boolean first = true;
//        //????????????????????????","
//        for (String string : list) {
//            if (first) {
//                first = false;
//            } else {
//                result.append(",");
//            }
//            result.append(string);
//        }
//        return result.toString();
//    }
//
//    public String listLongToStr(List<Long> list) {
//        if (list == null) {
//            return null;
//        }
//        StringBuilder result = new StringBuilder();
//        boolean first = true;
//        //????????????????????????","
//        for (Long string : list) {
//            if (first) {
//                first = false;
//            } else {
//                result.append(",");
//            }
//            result.append(string);
//        }
//        return result.toString();
//    }
//
//    public void setAuditSV(IAuditSV auditSV) {
//        this.auditSV = auditSV;
//    }
//
//
    @Override
    public Map<Long, Boolean> isHasPermission(String accessToken,
                                              String auditCode,
                                              List<Long> busiIdList) {

        if (busiIdList == null || busiIdList.size() == 0) {
            throw new BusinessException("????????????????????????????????????");
        }

        List<AuditNodeInst> auditNodeInsts = auditNodeInstMapper.queryAuditIngs(auditCode, busiIdList);
        Map<Long, Boolean> returnMap = new HashMap<Long, Boolean>();
        for (Long busiId : busiIdList) {
            returnMap.put(busiId, false);
        }

        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);

        if (auditNodeInsts != null && auditNodeInsts.size() > 0) {
            for (AuditNodeInst auditNodeInst : auditNodeInsts) {
                Long nodeId = auditNodeInst.getNodeId();

                if(nodeId == null){
                    log.error("?????????????????????????????????");
                    continue;
                }

                if (returnMap.get(auditNodeInst.getBusiId()) != null && auditNodeInstMapper.getAuditUser(nodeId, loginInfo.getUserInfoId(), AuditConsts.TargetObjType.USER_TYPE) != null) {
                    //?????????
                    returnMap.put(auditNodeInst.getBusiId(), true);

                }
            }
        }


        return returnMap;
    }
//
//
//    @Override
//    public List<AuditNodeInst> queryAuditNodeInst(String auditCode,
//                                                  Long busiId, Boolean isAsc) {
//        return auditSV.queryAuditNodeInst(auditCode, busiId, isAsc);
//    }
//
//
//    /**
//     * ????????????
//     *
//     * @param busiType
//     * @return
//     */
//    public Map<String, List<AuditNodePage>> getAuditConfigList(int busiType) throws Exception {
//        BaseUser user = SysContexts.getCurrentOperator();
//        Map<String, List<AuditNodePage>> map = new HashMap<>();
//        List<SysStaticData> sysStaticDatas = SysStaticDataUtil.getSysStaticDataList("AUDIT_CODE");
//        List<String> auditCodes = new ArrayList<>();
//        if (sysStaticDatas != null && sysStaticDatas.size() > 0) {
//            for (SysStaticData sysStaticData : sysStaticDatas) {
//                auditCodes.add(sysStaticData.getCodeValue());
//            }
//            List<AuditNodePage> auditNodePages = new ArrayList<>();
//            IAuditSV auditSV = (IAuditSV) SysContexts.getBean("auditSV");
//            List<AuditNodeOut> list = auditSV.getAuditNodeConfigList(auditCodes, user.getTenantId(), busiType);
//            IUserSV userSV = (IUserSV) SysContexts.getBean("userSV");
//            Set<Long> userIdSet = new HashSet<>();
//            if (list != null && list.size() > 0) {
//                for (AuditNodeOut auditNodeOut : list) {
//                    List<Long> nodeAuditUser = auditSV.getUserFromAuditUser(auditNodeOut.getNodeId(), TargetObjType.USER_TYPE);
//                    for (Long str : nodeAuditUser) {
//                        userIdSet.add(str);
//                    }
//                }
//                List<Long> userIdList = new ArrayList<>();
//                userIdList.addAll(userIdSet);
//                List<UserDataInfo> userDataInfos = userSV.getUserDataInfoList(userIdList);
//                for (AuditNodeOut auditNodeOut : list) {
//                    AuditNodePage auditNodePage = new AuditNodePage();
//                    auditNodePage.setNodeId(auditNodeOut.getNodeId());
//                    auditNodePage.setParentNodeId(auditNodeOut.getParentNodeId());
//                    auditNodePage.setAuditCode(auditNodeOut.getAuditCode());
//
//                    List<Long> nodeAuditUser = auditSV.getUserFromAuditUser(auditNodeOut.getNodeId(), TargetObjType.USER_TYPE);
//                    List<AuditNodeUser> auditNodeUsers = new ArrayList<>();
//                    for (Long auditUserId : nodeAuditUser) {
//                        for (UserDataInfo userDataInfo : userDataInfos) {
//                            if (userDataInfo.getUserId().longValue() == auditUserId) {
//                                AuditNodeUser auditNodeUser = new AuditNodeUser();
//                                auditNodeUser.setUserId(auditUserId);
//                                auditNodeUser.setUserName(userDataInfo.getLinkman());
//                                auditNodeUser.setUserBill(userDataInfo.getMobilePhone());
//                                auditNodeUsers.add(auditNodeUser);
//                            }
//                        }
//                    }
//                    auditNodePage.setList(auditNodeUsers);
//                    auditNodePages.add(auditNodePage);
//                }
//                map = groupAuditDataByAuditCode(auditNodePages);
//            }
//        }
//        return map;
//    }
//
//
//    private Map<String, List<AuditNodePage>> groupAuditDataByAuditCode(List<AuditNodePage> auditNodePages) throws Exception {
//        Map<String, List<AuditNodePage>> resultMap = new HashMap<>();
//        try {
//            for (AuditNodePage auditNodePage : auditNodePages) {
//                if (resultMap.containsKey(auditNodePage.getAuditCode())) {
//                    resultMap.get(auditNodePage.getAuditCode()).add(auditNodePage);
//                } else {//map?????????????????????key?????????????????????
//                    List<AuditNodePage> tmpList = new ArrayList<AuditNodePage>();
//                    tmpList.add(auditNodePage);
//                    resultMap.put(auditNodePage.getAuditCode(), tmpList);
//                }
//            }
//        } catch (Exception e) {
//            throw new Exception("???????????????????????????????????????", e);
//        }
//
//        return resultMap;
//    }
//
//
//    /**
//     * ??????????????????
//     *
//     * @return
//     */
//    private static Map<Long, Integer> getLevelAudit(List<AuditNodeOut> auditNodeConfigs) {
//        Map<Long, Integer> auditMap = new HashMap<>();
//        for (AuditNodeOut auditNodeOut : auditNodeConfigs) {
//            auditMap.put(auditNodeOut.getNodeId(), getChildAudit(auditNodeOut.getNodeId(), auditNodeConfigs).size());
//        }
//        return auditMap;
//    }
//

//
//    /**
//     * ??????????????????
//     *
//     * @param id
//     * @return
//     */
//    private static List<Long> getChildAudit(Long id, List<AuditNodeOut> auditNodeConfigs) {
//        List<Long> ids = new ArrayList<>();
//        for (AuditNodeOut auditNodeOut : auditNodeConfigs) {
//            if (auditNodeOut.getParentNodeId() != null && id != null && auditNodeOut.getParentNodeId().longValue() == id.longValue()) {
//                //??????????????????id?????????
//                ids.add(auditNodeOut.getNodeId());
//                //???????????????id?????????
//                ids.addAll(getChildAudit(auditNodeOut.getNodeId(), auditNodeConfigs));
//            }
//        }
//        Collections.sort(ids);
//        return ids;
//    }
//
//    /**
//     * ????????????-???????????????
//     *
//     * @param nodeId
//     * @return
//     */
//    public List<AuditNodeUser> getAuditNodeUserList(long nodeId) throws Exception {
//        List<AuditNodeUser> auditNodeUsers = new ArrayList<>();
//        IAuditSV iAuditSV = (IAuditSV) SysContexts.getBean("auditSV");
//        IUserSV userSV = (IUserSV) SysContexts.getBean("userSV");
//        List<Long> userIdList = auditSV.getUserFromAuditUser(nodeId, TargetObjType.USER_TYPE);
//        List<UserDataInfo> userDataInfos = null;
//        if (userIdList.size() > 0) {
//            userDataInfos = userSV.getUserDataInfoList(userIdList);
//            if (userDataInfos != null && userDataInfos.size() > 0) {
//                for (UserDataInfo userDataInfo : userDataInfos) {
//                    AuditNodeUser auditNodeUser = new AuditNodeUser();
//                    auditNodeUser.setUserBill(userDataInfo.getMobilePhone());
//                    auditNodeUser.setUserId(userDataInfo.getUserId());
//                    auditNodeUser.setUserName(userDataInfo.getLinkman());
//                    auditNodeUser.setNodeId(nodeId);
//                    auditNodeUser.setUserTypeName(userDataInfo.getUserTypeName());
//                    auditNodeUsers.add(auditNodeUser);
//                }
//            }
//
//        }
//        return auditNodeUsers;
//    }
//
//    public static String getBatchId() {
//        return DateUtil.formatDate(new Date(), DateUtil.DATETIME12_FORMAT2);
//    }
//
//    /**
//     * ??????????????????????????????????????????????????????
//     *
//     * @param auditCode
//     * @param busiId
//     * @param tenantId
//     * @return
//     */
//    public List<Map> getAuditNodeInstNew(String auditCode, long busiId, long tenantId, boolean isDesc, Integer... state) {
//        IAuditSV iAuditSV = (IAuditSV) SysContexts.getBean("auditSV");
//        List<Map> list = iAuditSV.getAuditNodeInstNew(auditCode, busiId, tenantId, isDesc, state);
//        if (list != null && list.size() > 0) {
//            for (Map map : list) {
//                CommonUtil.queryResult(map, "userType", 1, EnumConsts.SysStaticData.USER_TYPE);
//                int auditResult = DataFormat.getIntKey(map, "auditResult");
//                if (auditResult == AuditConsts.RESULT.TO_AUDIT) {
//                    map.put("auditResultName", "?????????");
//                } else if (auditResult == AuditConsts.RESULT.SUCCESS) {
//                    map.put("auditResultName", "????????????");
//                } else if (auditResult == AuditConsts.RESULT.FAIL) {
//                    map.put("auditResultName", "???????????????");
//                }
//            }
//        }
//        return list;
//    }
//
//
//    /**
//     * ?????????????????????
//     *
//     * @param list
//     * @param keyId
//     * @param busiIdList
//     */
//    public void isHasPermissionListMap(List<Map> list, String keyId, List<Long> busiIdList, String auditCode) {
//        Map<Long, Boolean> hasPermissionMap = null;
//        if (!busiIdList.isEmpty()) {
//            hasPermissionMap = isHasPermission(auditCode, busiIdList);
//        }
//        for (Map map : list) {
//            Long busiId = DataFormat.getLongKey(map, keyId);
//            if (hasPermissionMap != null && hasPermissionMap.containsKey(busiId) && hasPermissionMap.get(busiId)) {
//                map.put("hasVer", 0);
//            } else {
//                map.put("hasVer", 1);
//            }
//        }
//    }
//
//
    @Override
    public List<Long> getBusiIdByUserId(String auditCode, long userId, Long tenantId) {
        return auditNodeInstMapper.getBusiIdByUserId(auditCode, userId, tenantId, 500, EnumConsts.RESULT.TO_AUDIT);
    }
//
    @Override
    public List<Long> getBusiIdByUserId(String auditCode, long userId, long tenantId, int pageSize) {
        return  auditNodeInstMapper.getBusiIdByUserId(auditCode, userId, tenantId, pageSize, EnumConsts.RESULT.TO_AUDIT);
    }
//
//    /**
//     * ??????????????????????????????
//     *
//     * @param nodeIds
//     * @param tenantId
//     * @return
//     * @throws Exception
//     */
//    @Override
//    public Map<Long, AuditNodeOut> getAuditNodeConfigListByNodeId(List<Long> nodeIds, long tenantId) throws Exception {
//        Map<Long, AuditNodeOut> out = new HashMap<>();
//        IAuditSV iAuditSV = (IAuditSV) SysContexts.getBean("auditSV");
//        List<AuditNodeConfig> auditNodeConfigs = iAuditSV.getAuditNodeConfigListByNodeId(nodeIds, tenantId);
//        if (auditNodeConfigs != null && auditNodeConfigs.size() > 0) {
//            for (AuditNodeConfig auditNodeConfig : auditNodeConfigs) {
//                AuditNodeOut auditNodeOut = new AuditNodeOut();
//                BeanUtils.copyProperties(auditNodeOut, auditNodeConfig);
//                out.put(auditNodeConfig.getNodeId(), auditNodeOut);
//            }
//        }
//        return out;
//    }
//
//    /**
//     * ????????????????????????
//     *
//     * @param nodeIds
//     * @param tenantId
//     * @return
//     * @throws Exception
//     */
//    @Override
//    public Map<Long, AuditNodeOut> getNextAuditNodeConfig(List<Long> nodeIds, long tenantId) throws Exception {
//        Map<Long, AuditNodeOut> out = new HashMap<>();
//        IAuditSV iAuditSV = (IAuditSV) SysContexts.getBean("auditSV");
//        List<AuditNodeConfig> auditNodeConfigs = iAuditSV.getNextAuditNodeConfig(nodeIds, tenantId);
//        if (auditNodeConfigs != null && auditNodeConfigs.size() > 0) {
//            for (AuditNodeConfig auditNodeConfig : auditNodeConfigs) {
//                AuditNodeOut auditNodeOut = new AuditNodeOut();
//                BeanUtils.copyProperties(auditNodeOut, auditNodeConfig);
//                out.put(auditNodeConfig.getParentNodeId(), auditNodeOut);
//            }
//        }
//        return out;
//    }
//
    /**
     * ??????????????????????????????
     *
     * @param userId   ?????????
     * @param busiCode ????????????
     * @param busiIds  ??????????????????
     * @param tenantId ??????Id
     * @return
     * @throws Exception
     */
    @Override
    public Map<Long, Map<String, Object>> queryAuditRealTimeOperation(Long userId, String busiCode, List<Long> busiIds, long tenantId)  {
        Map<Long, Map<String, Object>> returnMap = new HashMap<Long, Map<String, Object>>();
        if (userId == null) {
            throw new BusinessException("??????????????????????????????");
        }
        if (busiIds == null || busiIds.size() == 0) {
            return null;
        }
        //????????????????????????
        List<AuditNodeInst> auditNodeInsts = iAuditNodeInstService.queryAuditIng(busiCode, busiIds);

        if (auditNodeInsts != null && auditNodeInsts.size() > 0) {
            //??????????????????
            List<AuditNodeConfig> auditNodeConfigs = iAuditNodeConfigService.getAll();
            Map<Long,AuditNodeConfig> nodeConfigMap = new HashMap<>();
            Map<Long,List<AuditNodeConfig>> nodeConfigParentMap = new HashMap<>();
            for (AuditNodeConfig auditNodeConfig : auditNodeConfigs) {
                nodeConfigMap.put(auditNodeConfig.getId(),auditNodeConfig);
                if (auditNodeConfig.getParentNodeId()!=-1){
                    if(nodeConfigParentMap.containsKey(auditNodeConfig.getParentNodeId())){
                        nodeConfigParentMap.get(auditNodeConfig.getParentNodeId()).add(auditNodeConfig);
                    }else {
                        nodeConfigParentMap.put(auditNodeConfig.getParentNodeId(),new ArrayList<AuditNodeConfig>(){{add(auditNodeConfig);}});
                    }
                }
            }

            List<Long> nodeIds = new ArrayList<>();
            for (AuditNodeInst auditNodeInst : auditNodeInsts) {
                nodeIds.add(auditNodeInst.getNodeId());
            }
            Map<Long, List<Long>> listMap = iAuditUserService.selectIdByNodeIdAndTarGetObjType(nodeIds, AuditConsts.TargetObjType.USER_TYPE);
            List<Long> ids = new ArrayList<>();
            for(List<Long> longs:listMap.values()){
                ids.addAll(longs);
            }
            Map<Long, String> userNameMap = iUserDataInfoService.querStaffNameAndIdByUserIds(ids);
            for (AuditNodeInst auditNodeInst : auditNodeInsts) {

                boolean isAuditJurisdiction = false;
                boolean isFinallyNode = false;
                StringBuffer auditUserBf = new StringBuffer("?????????");

//                List<Long> userIdList = iAuditUserService.selectIdByNodeIdAndTarGetObjType(auditNodeInst.getNodeId(), AuditConsts.TargetObjType.USER_TYPE);
                List<Long> userIdList = listMap.get(auditNodeInst.getNodeId());

                if (userIdList != null && userIdList.size() > 0) {
                    auditUserBf.append("???:");
//                    List<String> userNameList = iUserDataInfoService.querStaffNameByUserIds(userIdList);
                    List<String> userNameList = new ArrayList<>();
                    for(Map.Entry<Long,String> entry:userNameMap.entrySet()){
                        userIdList.forEach(id->{
                            if (entry.getKey().equals(id)){
                                userNameList.add(entry.getValue());
                            }
                        });
                    }
                    auditUserBf.append(listToString(userNameList));
                    isAuditJurisdiction = userIdList.contains(userId);
                } else {
                    throw new BusinessException("????????????????????????????????????????????????");
                }

//                AuditNodeConfig auditNodeConfig = iAuditNodeConfigService.getAuditNodeConfig(auditNodeInst.getNodeId());
                AuditNodeConfig auditNodeConfig = nodeConfigMap.get(auditNodeInst.getNodeId());
                if (auditNodeConfig == null || !auditNodeConfig.getVersion().equals(auditNodeInst.getVersion())) {
                    AuditNodeConfigVer nextAuditNode = iAuditNodeConfigVerService.getAuditNodeConfigVer(auditNodeInst.getVersion(),
                            auditNodeInst.getNodeId(), true, tenantId);
                    if (nextAuditNode == null) {
                        isFinallyNode = true;
                    }
                } else {
//                    AuditNodeConfig nextNodeConfig = iAuditNodeConfigService.getNextAuditNodeConfig(auditNodeConfig.getId());
                    AuditNodeConfig nextNodeConfig = iAuditNodeConfigService.getNextAuditNodeConfig(auditNodeConfig.getId(),nodeConfigParentMap.get(auditNodeConfig.getId()));
                    if (nextNodeConfig == null) {
                        isFinallyNode = true;
                    }
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("isFinallyNode", isFinallyNode);//??????????????????
                map.put("isAuditJurisdiction", isAuditJurisdiction);//?????????????????????
                map.put("auditUserName", auditUserBf.toString());//?????????
                returnMap.put(auditNodeInst.getBusiId(), map);
            }
        }
        return returnMap;
    }
    public String listToString(List<String> list) {
        if (list == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean first = true;
        //????????????????????????","
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
//
//
//    @Override
//    public String getAuditNextNodeUsers(String auditCode, Long busiId, Long tenantId) throws Exception {
//        AuditNodeInst auditNodeInst = auditSV.queryAuditIng(auditCode, busiId, tenantId);
//        if (auditNodeInst == null) {
//            return null;
//        }
//
//        Long nodeId = null;
//        if (auditSV.checkVersionNode(auditNodeInst.getVersion(), auditNodeInst.getNodeId())) {
//            AuditNodeConfig auditNodeConfig = auditSV.getObjectById(AuditNodeConfig.class, auditNodeInst.getNodeId());
//            if (auditNodeConfig == null) {
//                return null;
//            }
//            nodeId = auditNodeConfig.getNodeId();
//        } else {
//            AuditNodeConfigVer auditNodeConfigVer = auditSV.getAuditNodeConfigVer(auditNodeInst.getVersion(), auditNodeInst.getNodeId(), false);
//            if (auditNodeConfigVer == null) {
//                return null;
//            }
//            nodeId = auditNodeConfigVer.getNodeId();
//        }
//        return auditTF.getAuditUserNameByNodeId(nodeId, TargetObjType.USER_TYPE);
//    }

    @Override
    public void cancelProcess(String auditCode, Long busiId, Long tenantId) {
        AuditNodeInst preAuditNodeInst = auditNodeInstService.queryAuditIng(auditCode, busiId, tenantId);
        if (preAuditNodeInst != null) {
            //????????????-?????????
            preAuditNodeInst.setStatus(AuditConsts.Status.FINISH);
            //????????????-??????????????????????????????
            preAuditNodeInst.setAuditResult(AuditConsts.RESULT.CANCEL);
            auditNodeInstService.save(preAuditNodeInst);
//            auditNodeInstMapper.insert(preAuditNodeInst);

            //??????????????????????????????????????????
            auditNodeInstService.updateAuditInstToFinish(preAuditNodeInst.getAuditId(), busiId);
            //??????????????????
            List<AuditNodeInst> auditNodeInsts = auditNodeInstService.selectByBusiCodeAndBusiIdAndTenantIdAntStatus(
                    preAuditNodeInst.getAuditCode(), preAuditNodeInst.getBusiId(), preAuditNodeInst.getTenantId(),
                    AuditConsts.Status.FINISH);
            if (auditNodeInsts != null && auditNodeInsts.size() > 0) {
                for (AuditNodeInst auditNodeInst : auditNodeInsts) {
                    AuditNodeInstVer bean = new AuditNodeInstVer();
                    BeanUtils.copyProperties(auditNodeInst,bean);
                    bean.setVerId(auditNodeInst.getId());
                    auditNodeInstVerService.save(bean);
                    auditNodeInstService.removeById(auditNodeInst.getId());
//                    auditNodeInstService.getBaseMapper().deleteByMap(JSONObject.parseObject(JSONObject.toJSONString(auditNodeInst), Map.class));
                }
            }
        }
    }


//    @Override
//    public List<Long> getBusiIdByUserId(String auditCode, long userId, long tenantId, String startTime, String endTime) {
//        IAuditSV iAuditSV = (IAuditSV) SysContexts.getBean("auditSV");
//        return iAuditSV.getBusiIdByUserId(auditCode, userId, tenantId, 500, startTime, endTime);
//    }

}
