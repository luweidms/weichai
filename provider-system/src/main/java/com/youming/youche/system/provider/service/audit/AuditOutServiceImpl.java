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
//     * 1 根据审核业务编码查询 AUDIT_CONFIG，AUDIT_NODE_CONFIG 两个表，获取是否配置审核节点
//     * 2 获取节点的规则 AUDIT_NODE_RULE_CONFIG，如果有规则，判断是否满足条件
//     * 3 满足条件后，把 内容写入 AUDIT_NODE_INST 表
//     *
//     *
//     *
//     */
//    public boolean startProcess(String auditCode, Long busiId, SysOperLogConst.BusiCode busiCode, Map<String, Object> params, String token, Long... tenantIds) throws Exception {
//        return this.startProcess(auditCode, busiId, busiCode, params, null, null, token, tenantIds);
//    }
//
//    /***
//     * 1 根据审核业务编码查询 AUDIT_CONFIG，AUDIT_NODE_CONFIG 两个表，获取是否配置审核节点
//     * 2 获取节点的规则 AUDIT_NODE_RULE_CONFIG，如果有规则，判断是否满足条件
//     * 3 满足条件后，把 内容写入 AUDIT_NODE_INST 表
//     *
//     *
//     *
//     */
//    public boolean startProcess(String auditCode, Long busiId, SysOperLogConst.BusiCode busiCode, Map<String, Object> params, Long opId, String opName, String token, Long... tenantIds) throws Exception {
//        if (StringUtils.isBlank(auditCode)) {
//            throw new BusinessException("审核业务编码不能为空");
//        }
//        if (busiId == null) {
//            throw new BusinessException("审核业务主键不能为空");
//        }
//
//        AuditConfig auditConfig = auditSV.getAuditConfigByCode(auditCode);
//        if (auditConfig == null) {
//            log.error("审核业务编码[" + auditCode + "] 没有配置审核流程");
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
//            log.error("审核业务编码[" + auditCode + "] 没有配置审核流程的节点,audid_config表的audit_id[" + auditConfig.getId() + "],tenant_id[" + tenantId + "]");
//            return false;
//        }
//
//        if (auditSV.isAuditIng(auditCode, busiId, tenantId)) {
//            //已经有审核的节点开始审核了
//            throw new BusinessException("该流程已经有节点进行审核了，需要等审核流程结束才能重新发起");
//        }
//
//        AuditNodeInst preAuditNodeInst = auditSV.queryAuditIng(auditCode, busiId, tenantId);
//        if (preAuditNodeInst != null) {
//            AuditNodeInstVer auditNodeInstVer = new AuditNodeInstVer();
//            BeanUtils.copyProperties(auditNodeInstVer, preAuditNodeInst);
//            //审核流程-已完成
//            auditNodeInstVer.setStatus(AuditConsts.Status.FINISH);
//            //审核流程-取消，发起了新的审核
//            auditNodeInstVer.setAuditResult(AuditConsts.RESULT.CANCEL);
//            //把原表的数据移除到ver表
//            auditSV.save(auditNodeInstVer);
//            auditSV.delete(preAuditNodeInst);
////			return true;
//        }
//        Integer ruleVersion = auditSV.getAuditRuleVersionNo(auditNodeConfig.getNodeId());
//        //判断第一个节点是否有前置规则,如果不满足，直接返回，不需要处理
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
//        //节点的回调方法
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
//        //第一个前面不拼接","
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
//        //第一个前面不拼接","
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
            throw new BusinessException("传入的判断的数据列表为空");
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
                    log.error("用户没有需要审核的节点");
                    continue;
                }

                if (returnMap.get(auditNodeInst.getBusiId()) != null && auditNodeInstMapper.getAuditUser(nodeId, loginInfo.getUserInfoId(), AuditConsts.TargetObjType.USER_TYPE) != null) {
                    //有权限
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
//     * 查询业务
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
//                } else {//map中不存在，新建key，用来存放数据
//                    List<AuditNodePage> tmpList = new ArrayList<AuditNodePage>();
//                    tmpList.add(auditNodePage);
//                    resultMap.put(auditNodePage.getAuditCode(), tmpList);
//                }
//            }
//        } catch (Exception e) {
//            throw new Exception("按照数据进行分组时出现异常", e);
//        }
//
//        return resultMap;
//    }
//
//
//    /**
//     * 递归寻找夫类
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
//     * 寻找子类节点
//     *
//     * @param id
//     * @return
//     */
//    private static List<Long> getChildAudit(Long id, List<AuditNodeOut> auditNodeConfigs) {
//        List<Long> ids = new ArrayList<>();
//        for (AuditNodeOut auditNodeOut : auditNodeConfigs) {
//            if (auditNodeOut.getParentNodeId() != null && id != null && auditNodeOut.getParentNodeId().longValue() == id.longValue()) {
//                //添加第一次父id符合的
//                ids.add(auditNodeOut.getNodeId());
//                //添加嵌套父id符合的
//                ids.addAll(getChildAudit(auditNodeOut.getNodeId(), auditNodeConfigs));
//            }
//        }
//        Collections.sort(ids);
//        return ids;
//    }
//
//    /**
//     * 审核节点-审核人查询
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
//     * 审核记录查询（查询最新的一批次查询）
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
//                    map.put("auditResultName", "待审核");
//                } else if (auditResult == AuditConsts.RESULT.SUCCESS) {
//                    map.put("auditResultName", "审核通过");
//                } else if (auditResult == AuditConsts.RESULT.FAIL) {
//                    map.put("auditResultName", "审核不通过");
//                }
//            }
//        }
//        return list;
//    }
//
//
//    /**
//     * 检验是否有权限
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
//     * 批量获取节点配置信息
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
//     * 查询业务下一节点
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
     * 获取业务审核当前操作
     *
     * @param userId   操作人
     * @param busiCode 业务编码
     * @param busiIds  业务主键集合
     * @param tenantId 车队Id
     * @return
     * @throws Exception
     */
    @Override
    public Map<Long, Map<String, Object>> queryAuditRealTimeOperation(Long userId, String busiCode, List<Long> busiIds, long tenantId)  {
        Map<Long, Map<String, Object>> returnMap = new HashMap<Long, Map<String, Object>>();
        if (userId == null) {
            throw new BusinessException("当前登录人不能为空！");
        }
        if (busiIds == null || busiIds.size() == 0) {
            return null;
        }
        //查询待审核的数据
        List<AuditNodeInst> auditNodeInsts = iAuditNodeInstService.queryAuditIng(busiCode, busiIds);

        if (auditNodeInsts != null && auditNodeInsts.size() > 0) {
            //查询全部配置
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
                StringBuffer auditUserBf = new StringBuffer("待审核");

//                List<Long> userIdList = iAuditUserService.selectIdByNodeIdAndTarGetObjType(auditNodeInst.getNodeId(), AuditConsts.TargetObjType.USER_TYPE);
                List<Long> userIdList = listMap.get(auditNodeInst.getNodeId());

                if (userIdList != null && userIdList.size() > 0) {
                    auditUserBf.append("人:");
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
                    throw new BusinessException("下个节点的审核的对象类型设置错误");
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
                map.put("isFinallyNode", isFinallyNode);//是否最后节点
                map.put("isAuditJurisdiction", isAuditJurisdiction);//是否有审核权限
                map.put("auditUserName", auditUserBf.toString());//待审人
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
            //审核流程-已完成
            preAuditNodeInst.setStatus(AuditConsts.Status.FINISH);
            //审核流程-取消，发起了新的审核
            preAuditNodeInst.setAuditResult(AuditConsts.RESULT.CANCEL);
            auditNodeInstService.save(preAuditNodeInst);
//            auditNodeInstMapper.insert(preAuditNodeInst);

            //把该流程的状态都改为流程完成
            auditNodeInstService.updateAuditInstToFinish(preAuditNodeInst.getAuditId(), busiId);
            //移除到历史表
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
