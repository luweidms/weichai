package com.youming.youche.system.provider.service.audit;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.shaded.com.google.common.base.Joiner;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IOrderAgingInfoService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.*;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.domain.audit.*;
import com.youming.youche.system.dto.AuditCallbackDto;
import com.youming.youche.system.dto.AuditNodePageDto;
import com.youming.youche.system.provider.mapper.audit.AuditSettingMapper;
import com.youming.youche.system.vo.AuditRuleNodeVo;
import com.youming.youche.system.vo.AuditVo;
import com.youming.youche.system.vo.SaveNodeAuditStaffVo;
import com.youming.youche.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Title: AuditSettingServiceImpl
 * @Package: com.youming.youche.system.provider.service.audit
 * @Description: TODO(????????????????????????????????????)
 * @author:DengYuanYe
 * @date: 2022/2/10 9:59
 * @company:
 */
@DubboService(version = "1.0.0")
@Service
public class AuditSettingServiceImpl implements IAuditSettingService {


    @Resource
    AuditSettingMapper auditSettingMapper;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private LoginUtils loginUtils;

    @Resource
    private IUserDataInfoService iUserDataInfoService;

    @Resource
    private IAuditConfigService iAuditConfigService;

    @Resource
    private IAuditNodeConfigService iAuditNodeConfigService;

    @Resource
    private IAuditNodeRuleConfigService iAuditNodeRuleConfigService;

    @Resource
    private IAuditUserService iAuditUserService;

    @Resource
    private IAuditRuleConfigService iAuditRuleConfigService;

    @Resource
    private IAuditNodeRuleConfigVerService iAuditNodeRuleConfigVerService;

    @Resource
    private IAuditNodeInstService iAuditNodeInstService;

    @DubboReference(version = "1.0.0")
    IOrderAgingInfoService orderAgingInfoService;

    @Resource
    ISysOperLogService isysOperLogService;

    @Resource
    IAuditNodeConfigVerService iAuditNodeConfigVerService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditSettingServiceImpl.class);

    @Override
    public Map<String, List<AuditNodePageDto>> getAuditConfigList(int Type, String token) throws Exception {
        LoginInfo user = loginUtils.get(token);
        if (user == null) {
            throw new BusinessException("????????????");
        }
        Map<String, List<AuditNodePageDto>> map = new HashMap<>();
        List<SysStaticData> sysStaticDatas = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("AUDIT_CODE"));
        List<String> auditCodes = new ArrayList<>();
        if (sysStaticDatas != null && sysStaticDatas.size() > 0) {
            for (SysStaticData sysStaticData : sysStaticDatas) {
                auditCodes.add(sysStaticData.getCodeValue());
            }
            List<AuditNodePageDto> auditNodePages = new ArrayList<>();
            String result = Joiner.on(",").join(auditCodes);
            List<AuditNodeOut> list = auditSettingMapper.getAuditNodeConfigList(result, user.getTenantId(), Type);
            Set<Long> userIdSet = new HashSet<>();
            if (list != null && list.size() > 0) {
                for (AuditNodeOut auditNodeOut : list) {
                    List<Long> nodeAuditUser = auditSettingMapper.getUserFromAuditUser(auditNodeOut.getNodeId(), 2);
                    for (Long str : nodeAuditUser) {
                        userIdSet.add(str);
                    }
                }
                List<Long> userIdList = new ArrayList<>();
                userIdList.addAll(userIdSet);
                List<UserDataInfo> userDataInfos = iUserDataInfoService.getUserDataInfoList(userIdList);
                for (AuditNodeOut auditNodeOut : list) {
                    AuditNodePageDto auditNodePage = new AuditNodePageDto();
                    auditNodePage.setNodeId(auditNodeOut.getNodeId());
                    auditNodePage.setParentNodeId(auditNodeOut.getParentNodeId());
                    auditNodePage.setAuditCode(auditNodeOut.getAuditCode());
                    List<Long> nodeAuditUser = auditSettingMapper.getUserFromAuditUser(auditNodeOut.getNodeId(), 2);
                    List<AuditNodeUser> auditNodeUsers = new ArrayList<>();
                    for (Long auditUserId : nodeAuditUser) {
                        if (userDataInfos != null && userDataInfos.size() > 0) {
                            for (UserDataInfo userDataInfo : userDataInfos) {
                                if (userDataInfo.getId().longValue() == auditUserId) {
                                    AuditNodeUser auditNodeUser = new AuditNodeUser();
                                    auditNodeUser.setUserId(auditUserId);
                                    auditNodeUser.setUserName(userDataInfo.getLinkman());
                                    auditNodeUser.setUserBill(userDataInfo.getMobilePhone());
                                    auditNodeUsers.add(auditNodeUser);
                                }
                            }
                        }
                    }
                    auditNodePage.setList(auditNodeUsers);
                    auditNodePages.add(auditNodePage);
                }
                map = groupAuditDataByAuditCode(auditNodePages);
            }
        }

        return map;
    }

    /**
     * ????????????????????????
     *
     * @return
     * @throws Exception
     */
    @Override
    public ResponseResult queryAuditFlow(int type, String token) throws Exception {
        LoginInfo loginInfo = loginUtils.get(token);
        Map<String, List<AuditNodePageDto>> auditNodes = getAuditConfigList(type, token);
        List<AuditFlowOut> results = new ArrayList<AuditFlowOut>(auditNodes.size());
        Iterator<String> iter = auditNodes.keySet().iterator();
        String key = null;
        while (iter.hasNext()) {
            key = iter.next();
            AuditFlowOut auditFlowOut = new AuditFlowOut();
            auditFlowOut.setAuditCode(key);
            auditFlowOut.setAuditName(getSysStaticData("AUDIT_CODE", key).getCodeName());
            auditFlowOut.setAuditNodes(auditNodes.get(key) == null ? new ArrayList<>() : auditNodes.get(key));
            AuditConfig auditConfig = iAuditConfigService.getAuditConfigByCode(key);
            List<AuditNodeConfig> auditNodeConfig = iAuditNodeConfigService.getAuditNodeConfigList(auditConfig.getId(), loginInfo.getTenantId());
            if (type == 2) {
                if (auditNodeConfig != null) {
                    auditFlowOut.setFlag(auditNodeConfig.get(0).getFlag());
                } else {
                    auditFlowOut.setFlag(0);
                }
            }
            results.add(auditFlowOut);
        }
        //???????????????????????????
        List<SysStaticData> auditFlows = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("AUDIT_CODE"));
        if (auditFlows != null) {
            for (SysStaticData flow : auditFlows) {
                if (flow.getCodeId() != type) {
                    continue;
                }
                AuditFlowOut auditFlowOut = new AuditFlowOut();
                auditFlowOut.setAuditCode(flow.getCodeValue());
                auditFlowOut.setAuditName(flow.getCodeName());
                if (!results.contains(auditFlowOut)) {
                    auditFlowOut.setAuditNodes(new ArrayList<>());
                    results.add(auditFlowOut);
                } else {
                    auditFlowOut = results.get(results.indexOf(auditFlowOut));
                }
                auditFlowOut.setSortId(flow.getSortId());
            }
            //??????
            results.sort(new AuditFlowOut());
        }
        return ResponseResult.success(results);
    }

    @Override
    public void rollbackOriginator(Integer flag, String auditCode, String token) {
        LoginInfo loginInfo = loginUtils.get(token);
        AuditConfig auditConfig = iAuditConfigService.getAuditConfigByCode(auditCode);
        Integer count = auditSettingMapper.rollbackOriginator(flag, auditConfig.getId(), loginInfo.getTenantId());
        LOGGER.info("????????????" + count + "?????????");
    }

    @Override
    public List<AuditNodeUser> getAuditNodeUserList(long nodeId) throws Exception {
        List<AuditNodeUser> auditNodeUsers = new ArrayList<>();
        List<Long> userIdList = auditSettingMapper.getUserFromAuditUser(nodeId, 2);
        List<UserDataInfo> userDataInfos = null;
        if (userIdList.size() > 0) {
            userDataInfos = iUserDataInfoService.getUserDataInfoList(userIdList);
            if (userDataInfos != null && userDataInfos.size() > 0) {
                for (UserDataInfo userDataInfo : userDataInfos) {
                    AuditNodeUser auditNodeUser = new AuditNodeUser();
                    auditNodeUser.setUserBill(userDataInfo.getMobilePhone());
                    auditNodeUser.setUserId(userDataInfo.getId());
                    auditNodeUser.setUserName(userDataInfo.getLinkman());
                    auditNodeUser.setNodeId(nodeId);
                    auditNodeUser.setUserTypeName(getSysStaticData(SysStaticDataEnum.SysStaticData.USER_TYPE, userDataInfo.getUserType() + "").getCodeName());
                    auditNodeUsers.add(auditNodeUser);
                }
            }
        }
        return auditNodeUsers;
    }

    @Override
    public SysStaticData getSysStaticData(String codeType, String codeValue) {
        List<SysStaticData> staticDataList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (staticDataList != null && staticDataList.size() > 0) {
            Iterator var3 = staticDataList.iterator();
            while (var3.hasNext()) {
                SysStaticData sysData = (SysStaticData) var3.next();
                if (sysData.getCodeValue().equals(codeValue)) {
                    return sysData;
                }
            }
        }
        return new SysStaticData();
    }

    @Override
    public String saveOrUpdateAudit(SaveNodeAuditStaffVo saveNodeAuditStaffVo, String token) throws Exception {
        LoginInfo user = loginUtils.get(token);
        if (user == null) {
            throw new BusinessException("????????????");
        }
        if (StringUtils.isBlank(saveNodeAuditStaffVo.getTargetObjId())) {
            throw new BusinessException("???????????????????????????????????????");
        }
        if (StringUtils.isBlank(saveNodeAuditStaffVo.getAuditCode())) {
            throw new BusinessException("????????????????????????");
        }
        boolean isUpdate = saveNodeAuditStaffVo.getNodeId() > 0;
        String flag = isUpdate ? "YM" : "Y";
        AuditConfig auditConfig = iAuditConfigService.getAuditConfigByCode(saveNodeAuditStaffVo.getAuditCode());
        if (auditConfig == null) {
            throw new BusinessException("??????????????????????????????");
        }
        if (auditConfig.getBusiType() == null) {
            throw new BusinessException("????????????????????????");
        }
        AuditNodeConfig auditNodeConfig = iAuditNodeConfigService.getById(saveNodeAuditStaffVo.getNodeId());
        //???????????????????????????????????????????????????
        int version = iAuditNodeConfigService.saveAuditNodeConfigVer(auditConfig.getId(), user.getTenantId(), user.getUserInfoId());
        if (isUpdate && auditNodeConfig == null) {
            throw new BusinessException("?????????????????????");
        }
        if (isUpdate) {
            auditNodeConfig.setOpId(user.getUserInfoId());
            auditNodeConfig.setVersion(version);
            iAuditNodeConfigService.saveOrUpdate(auditNodeConfig);
        } else {
            String nodeName = "";
            AuditNodeConfig auditNodeConfigParent = iAuditNodeConfigService.getById(saveNodeAuditStaffVo.getParentNodeId());
            auditNodeConfig = new AuditNodeConfig();
            if ("1".equals(auditConfig.getBusiType())) {
                nodeName = "?????????";
            } else {
                nodeName = "?????????";
            }
            auditNodeConfig.setAuditId(auditConfig.getId());
            auditNodeConfig.setParentNodeId(saveNodeAuditStaffVo.getParentNodeId());
            auditNodeConfig.setTenantId(user.getTenantId());
            auditNodeConfig.setCreateTime(LocalDateTime.now());
            auditNodeConfig.setOpId(user.getUserInfoId());
            auditNodeConfig.setNodeName(auditConfig.getBusiName() + nodeName);
            auditNodeConfig.setVersion(version);
            if (auditNodeConfigParent != null && auditNodeConfigParent.getFlag() != null) {
                auditNodeConfig.setFlag(auditNodeConfigParent.getFlag());
            }
            iAuditNodeConfigService.saveOrUpdate(auditNodeConfig);
        }
        //????????????????????????????????????ver??????????????????????????????
        iAuditUserService.delAuditUserByNodeId(auditNodeConfig.getId());
        //??????????????????????????????
        iAuditUserService.batchSaveAuditUser(auditNodeConfig.getId(), saveNodeAuditStaffVo.getTargetObjId(), 2, auditNodeConfig.getVersion().longValue());
        return flag;
    }

    @Override
    @Transactional
    public Map<String, Object> getAuditRule(String auditCode, String token) {
        if (StringUtils.isBlank(auditCode)) {
            throw new BusinessException("???????????????????????????????????????");
        }
        AuditConfig auditConfig = iAuditConfigService.getAuditConfigByCode(auditCode);
        if (auditConfig == null) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        LoginInfo user = loginUtils.get(token);
        List<AuditNodeConfig> list = iAuditNodeConfigService.getAuditNodeConfigByParentId(auditConfig.getId(), -1L, user.getTenantId());
        AuditNodeConfig auditNodeConfig = null;
        if (list != null && list.size() > 0) {
            auditNodeConfig = list.get(0);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("exceedGuidePrice", "");
        map.put("isHigherPrepayment", false);
        map.put("isHigherOil", false);
        map.put("isLowerOil", false);
        map.put("isHigherEtc", false);
        map.put("isLowerEtc", false);
        List<Long> ruleId = new ArrayList<>();
        Map<Long, String> price = new HashMap<>();
        if (auditNodeConfig != null) {
            List<AuditNodeRuleConfig> auditNodeRuleConfigs = iAuditNodeRuleConfigService.getAuditNodeRuleConfigList(auditNodeConfig.getId(), auditConfig.getId(), user.getTenantId());
            if (auditNodeRuleConfigs != null && auditNodeRuleConfigs.size() > 0) {
                for (AuditNodeRuleConfig auditNodeRuleConfig : auditNodeRuleConfigs) {
                    ruleId.add(auditNodeRuleConfig.getRuleId());
                    price.put(auditNodeRuleConfig.getRuleId(), auditNodeRuleConfig.getRuleValue());
                }
                List<AuditRuleConfig> auditRuleConfigs = iAuditRuleConfigService.getAuditRuleConfigByIdList(ruleId);
                if (auditRuleConfigs != null && auditRuleConfigs.size() > 0) {
                    for (AuditRuleConfig auditRuleConfig : auditRuleConfigs) {
                        if (AuditConsts.RULE_CODE.EXCEED_GUIDE_PRICE.equals(auditRuleConfig.getRuleCode())) {
                            map.put("exceedGuidePrice", price.get(auditRuleConfig.getId()));
                        }
                        if (AuditConsts.RULE_CODE.HIGHER_PREPAYMENT.equals(auditRuleConfig.getRuleCode())) {
                            map.put("isHigherPrepayment", true);
                        }
                        if (AuditConsts.RULE_CODE.HIGHER_OIL.equals(auditRuleConfig.getRuleCode())) {
                            map.put("isHigherOil", true);
                        }
                        if (AuditConsts.RULE_CODE.LOWER_OIL.equals(auditRuleConfig.getRuleCode())) {
                            map.put("isLowerOil", true);
                        }
                        if (AuditConsts.RULE_CODE.HIGHER_ETC.equals(auditRuleConfig.getRuleCode())) {
                            map.put("isHigherEtc", true);
                        }
                        if (AuditConsts.RULE_CODE.LOWER_ETC.equals(auditRuleConfig.getRuleCode())) {
                            map.put("isLowerEtc", true);
                        }
                    }
                }
            }
        }
        return map;
    }

    @Override
    public String saveOrUpdateRuleNode(AuditRuleNodeVo auditRuleNodeVo, String token) throws Exception {
        LoginInfo user = loginUtils.get(token);
        if (auditRuleNodeVo.getNodeId() < 0) {
            throw new BusinessException("????????????????????????????????????");
        }
        if (StringUtils.isBlank(auditRuleNodeVo.getAuditCode())) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        AuditConfig auditConfig = iAuditConfigService.getAuditConfigByCode(auditRuleNodeVo.getAuditCode());
        if (auditConfig == null) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        AuditNodeConfig auditNodeConfig = iAuditNodeConfigService.getAuditNodeConfig(auditRuleNodeVo.getNodeId());
        if (auditNodeConfig == null) {
            throw new BusinessException("???????????????????????????");
        }
        List<AuditRuleConfig> list = iAuditRuleConfigService.getAuditRuleConfigList(auditConfig.getId());
        List<AuditNodeRuleConfig> auditNodeRuleConfigs = iAuditNodeRuleConfigService.
                getAuditNodeRuleConfigList(auditNodeConfig.getId(), auditNodeConfig.getAuditId(), user.getTenantId());
        if ((list != null) && (list.size() > 0)) {
            List<String> ruleCodes = new ArrayList<>();
            for (AuditRuleConfig auditRuleConfig : list) {
                if (AuditConsts.RULE_CODE.EXCEED_GUIDE_PRICE.equals(auditRuleConfig.getRuleCode())) {
                    ruleCodes.add(AuditConsts.RULE_CODE.EXCEED_GUIDE_PRICE);
                } else if (AuditConsts.RULE_CODE.HIGHER_PREPAYMENT.equals(auditRuleConfig.getRuleCode())) {
                    ruleCodes.add(AuditConsts.RULE_CODE.HIGHER_PREPAYMENT);
                } else if (AuditConsts.RULE_CODE.HIGHER_OIL.equals(auditRuleConfig.getRuleCode())) {
                    ruleCodes.add(AuditConsts.RULE_CODE.HIGHER_OIL);
                } else if (AuditConsts.RULE_CODE.LOWER_OIL.equals(auditRuleConfig.getRuleCode())) {
                    ruleCodes.add(AuditConsts.RULE_CODE.LOWER_OIL);
                } else if (AuditConsts.RULE_CODE.HIGHER_ETC.equals(auditRuleConfig.getRuleCode())) {
                    ruleCodes.add(AuditConsts.RULE_CODE.HIGHER_ETC);
                } else if (AuditConsts.RULE_CODE.LOWER_ETC.equals(auditRuleConfig.getRuleCode())) {
                    ruleCodes.add(AuditConsts.RULE_CODE.LOWER_ETC);
                }
            }
            if (!ruleCodes.contains(AuditConsts.RULE_CODE.EXCEED_GUIDE_PRICE) && StringUtils.isNotBlank(auditRuleNodeVo.getExceedGuidePrice())) {
                throw new BusinessException("?????????[????????????????????????]?????????????????????");
            }
            if (!ruleCodes.contains(AuditConsts.RULE_CODE.HIGHER_PREPAYMENT) && auditRuleNodeVo.getIsHigherPrepayment()) {
                throw new BusinessException("?????????[??????????????????]?????????????????????");
            }
            if (!ruleCodes.contains(AuditConsts.RULE_CODE.HIGHER_OIL) && auditRuleNodeVo.getIsHigherOil()) {
                throw new BusinessException("?????????[??????????????????]?????????????????????");
            }
            if (!ruleCodes.contains(AuditConsts.RULE_CODE.LOWER_OIL) && auditRuleNodeVo.getIsLowerOil()) {
                throw new BusinessException("?????????[??????????????????]?????????????????????");
            }
            if (!ruleCodes.contains(AuditConsts.RULE_CODE.HIGHER_ETC) && auditRuleNodeVo.getIsHigherEtc()) {
                throw new BusinessException("?????????[??????ETC??????]?????????????????????");
            }
            if (!ruleCodes.contains(AuditConsts.RULE_CODE.LOWER_ETC) && auditRuleNodeVo.getIsLowerEtc()) {
                throw new BusinessException("?????????[??????ETC??????]?????????????????????");
            }
            int version = iAuditNodeRuleConfigVerService.saveAuditNodeRuleConfigVer(auditNodeRuleConfigs, user.getUserInfoId());
            for (AuditRuleConfig auditRuleConfig : list) {
                if (AuditConsts.RULE_CODE.EXCEED_GUIDE_PRICE.equals(auditRuleConfig.getRuleCode())) {
                    contrast(auditNodeRuleConfigs, auditRuleConfig, StringUtils.isNotBlank(auditRuleNodeVo.getExceedGuidePrice()), auditConfig.getId(), auditNodeConfig.getId(), auditRuleNodeVo, version, user);
                } else if (AuditConsts.RULE_CODE.HIGHER_PREPAYMENT.equals(auditRuleConfig.getRuleCode())) {
                    contrast(auditNodeRuleConfigs, auditRuleConfig, auditRuleNodeVo.getIsHigherPrepayment(), auditConfig.getId(), auditNodeConfig.getId(), auditRuleNodeVo, version, user);
                } else if (AuditConsts.RULE_CODE.HIGHER_OIL.equals(auditRuleConfig.getRuleCode())) {
                    contrast(auditNodeRuleConfigs, auditRuleConfig, auditRuleNodeVo.getIsHigherOil(), auditConfig.getId(), auditNodeConfig.getId(), auditRuleNodeVo, version, user);
                } else if (AuditConsts.RULE_CODE.LOWER_OIL.equals(auditRuleConfig.getRuleCode())) {
                    contrast(auditNodeRuleConfigs, auditRuleConfig, auditRuleNodeVo.getIsLowerOil(), auditConfig.getId(), auditNodeConfig.getId(), auditRuleNodeVo, version, user);
                } else if (AuditConsts.RULE_CODE.HIGHER_ETC.equals(auditRuleConfig.getRuleCode())) {
                    contrast(auditNodeRuleConfigs, auditRuleConfig, auditRuleNodeVo.getIsHigherEtc(), auditConfig.getId(), auditNodeConfig.getId(), auditRuleNodeVo, version, user);
                } else if (AuditConsts.RULE_CODE.LOWER_ETC.equals(auditRuleConfig.getRuleCode())) {
                    contrast(auditNodeRuleConfigs, auditRuleConfig, auditRuleNodeVo.getIsLowerEtc(), auditConfig.getId(), auditNodeConfig.getId(), auditRuleNodeVo, version, user);
                }
            }

        }
        return "Y";
    }

    @Override
    public String delAuditNode(Long nodeId, String token) throws Exception {
        LoginInfo user = loginUtils.get(token);
        if (nodeId < 0) {
            return "????????????????????????";
        }
        AuditNodeConfig auditNodeConfig = iAuditNodeConfigService.getAuditNodeConfig(nodeId);
        if (auditNodeConfig == null) {
            return "???????????????????????????";
        }
        boolean isOneNode = false;
        AuditConfig auditConfig = iAuditConfigService.getById(auditNodeConfig.getAuditId());
        List<AuditNodeConfig> list = iAuditNodeConfigService.getAuditNodeConfigList(auditNodeConfig.getAuditId(), user.getTenantId());
        if (list != null && list.size() == 1) {
            if (auditConfig.getDefaultNodeNum() != null && auditConfig.getDefaultNodeNum() >= 1) {
                return "?????????????????????????????????????????????????????????";
            }
            isOneNode = true;
        }
        //???????????????????????????????????????????????????
        int version = iAuditNodeConfigService.saveAuditNodeConfigVer(auditConfig.getId(), user.getTenantId(), user.getUserInfoId());
        List<AuditNodeRuleConfig> auditNodeRuleConfigs = iAuditNodeRuleConfigService.getAuditNodeRuleConfigList(auditNodeConfig.getId(), auditNodeConfig.getAuditId(), user.getTenantId());
        if (isOneNode && auditNodeRuleConfigs != null && auditNodeRuleConfigs.size() > 0) {
            for (AuditNodeRuleConfig auditNodeRuleConfig : auditNodeRuleConfigs) {
                iAuditNodeRuleConfigService.removeById(auditNodeRuleConfig.getId());
            }
        }
        AuditNodeConfig childNodeConfig = iAuditNodeConfigService.getAuditNodeByParentId(auditNodeConfig.getId());
        if (childNodeConfig != null) {
            childNodeConfig.setParentNodeId(auditNodeConfig.getParentNodeId());
            childNodeConfig.setVersion(version);
            iAuditNodeConfigService.saveOrUpdate(childNodeConfig);
        }
        if (!isOneNode && -1L == auditNodeConfig.getParentNodeId() && auditNodeRuleConfigs != null && auditNodeRuleConfigs.size() > 0) {
            for (AuditNodeRuleConfig auditNodeRuleConfig : auditNodeRuleConfigs) {
                auditNodeRuleConfig.setNodeId(childNodeConfig.getId());
            }
        }
        iAuditNodeConfigService.removeById(auditNodeConfig.getId());
        return "Y";
    }

    @Override
    public AuditCallbackDto sure(AuditVo auditVo, String token, Boolean isStart) throws BusinessException {
        return surePri(auditVo.getBusiCode(), auditVo.getBusiId(), auditVo.getDesc(), auditVo.getChooseResult(),
                auditVo.getInstId(), null, null, token, isStart);
    }

    @Override
    public AuditCallbackDto sure(String busiCode, Long busiId, String desc, Integer chooseResult, String token) {
        return surePri(busiCode, busiId, desc, chooseResult, null, null, null, token, false);
    }

    @Override
    public AuditCallbackDto sureDataInfo(String busiCode, Long busiId, String desc, Integer chooseResult, String token) {
        return surePriDatainfo(busiCode, busiId, desc, chooseResult, null, null, null, token, false);
    }

    @Override
    public AuditCallbackDto sure(String busiCode, Long busiId, String desc,
                                 Integer chooseResult, Long instId, Integer type, Long tennantId, String token) {
        return surePri(busiCode, busiId, desc, chooseResult, instId, type, tennantId, token, false);
    }

    @Override
    public Long getInstId(String busiCode, Long busiId, String token) throws Exception {
        LoginInfo user = loginUtils.get(token);
        AuditNodeInst auditNodeInst = iAuditNodeInstService.queryAuditIng(busiCode, busiId, user.getTenantId());
        if (auditNodeInst == null) {
            throw new BusinessException("???????????????????????????,busiCode:[" + busiCode + "],busiId:[" + busiId + "]");
        }
        return auditNodeInst.getId();
    }

    @Override
    public void initAuditNode(long tenantId, long rootUserId, String token) {
        LoginInfo user = loginUtils.get(token);
        List<SysStaticData> sysStaticDatas = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("AUDIT_CODE"));
        List<String> auditCode = new ArrayList<>();
        if (sysStaticDatas != null && sysStaticDatas.size() > 0) {
            for (SysStaticData sysStaticData : sysStaticDatas) {
                auditCode.add(sysStaticData.getCodeValue());
            }
            QueryWrapper<AuditConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("audit_code", auditCode);
            List<AuditConfig> auditConfigs = iAuditConfigService.list(queryWrapper);
            //???????????????id
            List<Long> auditIds = new ArrayList<>();
            if (auditConfigs != null && auditConfigs.size() > 0) {
                for (AuditConfig auditConfig : auditConfigs) {
                    auditIds.add(auditConfig.getId());
                }
                QueryWrapper<AuditNodeConfig> auditNodeConfigsQuery = new QueryWrapper<>();
                auditNodeConfigsQuery.in("audit_id", auditIds);
                auditNodeConfigsQuery.eq("tenant_id", tenantId);
                List<AuditNodeConfig> auditNodeConfigs = iAuditNodeConfigService.list(auditNodeConfigsQuery);
                QueryWrapper<AuditRuleConfig> auditRuleConfigQuery = new QueryWrapper<>();
                auditRuleConfigQuery.eq("rule_code", AuditConsts.RULE_CODE.EXCEED_GUIDE_PRICE);

                List<AuditRuleConfig> auditRuleConfigByCode = iAuditRuleConfigService.list(auditRuleConfigQuery);
                for (AuditConfig auditConfig : auditConfigs) {
                    if (auditNodeConfigs != null && auditNodeConfigs.size() > 0) {
                        for (AuditNodeConfig auditNodeConfig : auditNodeConfigs) {
                            if (auditNodeConfig.getAuditId().longValue() == auditConfig.getId().longValue()) {
                                LOGGER.error("?????????????????????????????????????????????????????????ID???" + tenantId + "auditCode???" + auditConfig.getAuditCode());
                                continue;
                            }
                        }
                    }
                    AuditNodeConfig auditNodeConfig = new AuditNodeConfig();
                    auditNodeConfig.setAuditId(auditConfig.getId());
                    auditNodeConfig.setParentNodeId(-1L);
                    auditNodeConfig.setFlag(0);
                    auditNodeConfig.setNodeName(auditConfig.getBusiName() + "?????????");
                    auditNodeConfig.setTenantId(tenantId);
                    auditNodeConfig.setCreateTime(LocalDateTime.now());
                    auditNodeConfig.setOpId(user.getId());
                    auditNodeConfig.setVersion(1);
                    iAuditNodeConfigService.saveOrUpdate(auditNodeConfig);
                    AuditUser auditUser = new AuditUser();
                    auditUser.setTargetObjType(AuditConsts.TargetObjType.USER_TYPE);
                    auditUser.setTargetObjId(rootUserId);
                    auditUser.setNodeId(auditNodeConfig.getId());
                    auditUser.setVersion(auditNodeConfig.getVersion().longValue());
                    iAuditUserService.saveOrUpdate(auditUser);

                    //??????????????????????????????????????????
                    if (AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE.equals(auditConfig.getAuditCode())) {
                        if (auditRuleConfigByCode == null || auditRuleConfigByCode.size() == 0) {
                            throw new BusinessException("??????????????????????????????????????????????????????");
                        }
                        QueryWrapper<AuditNodeRuleConfig> auditNodeRuleConfigQueryWrapper = new QueryWrapper<>();
                        auditNodeRuleConfigQueryWrapper.eq("audit_id", auditRuleConfigByCode.get(0).getId());
                        auditNodeRuleConfigQueryWrapper.eq("rule_id", auditConfig.getId());
                        auditNodeRuleConfigQueryWrapper.eq("tenant_id", tenantId);
                        List<AuditNodeRuleConfig> auditNodeRuleConfigs = iAuditNodeRuleConfigService.list(auditNodeRuleConfigQueryWrapper);
                        if (auditNodeRuleConfigs != null && auditNodeRuleConfigs.size() > 0) {
                            LOGGER.error("?????????????????????????????????????????????????????????ID???" + tenantId + "auditCode???" + auditConfig.getAuditCode());
                            continue;
                        }
                        AuditNodeRuleConfig auditNodeRuleConfig = new AuditNodeRuleConfig();
                        auditNodeRuleConfig.setAuditId(auditNodeConfig.getAuditId());
                        auditNodeRuleConfig.setNodeId(auditNodeConfig.getId());
                        auditNodeRuleConfig.setRuleValue("20");
                        auditNodeRuleConfig.setRuleId(auditRuleConfigByCode.get(0).getId());
                        auditNodeRuleConfig.setVersion(1);
                        auditNodeRuleConfig.setTenantId(tenantId);
                        iAuditNodeRuleConfigService.saveOrUpdate(auditNodeRuleConfig);
                    }
                }
            } else {
                throw new BusinessException("????????????????????????");
            }
        } else {
            throw new BusinessException("????????????????????????");
        }
    }

    @Override
    public AuditCallbackDto surePri(String busiCode, Long busiId, String desc, Integer chooseResult, Long instId,
                                    Integer type, Long tennantId, String token, Boolean isStart) throws BusinessException {
        LoginInfo user = loginUtils.get(token);

        if (tennantId == null) {
            tennantId = user.getTenantId();
        }
        AuditNodeInst auditNodeInst = iAuditNodeInstService.queryAuditIng(busiCode, busiId, tennantId);
        if (auditNodeInst == null) {
            throw new BusinessException("???????????????????????????");
        }

        if (instId != null && instId > 0) {
            if (instId.longValue() != auditNodeInst.getId().longValue()) {
                throw new BusinessException("????????????????????????????????????????????????????????????????????????");
            }
        }

        if (AuditConsts.RESULT.SUCCESS == chooseResult) {
            AuditCallbackDto sure = auditPass(auditNodeInst.getId(), busiCode, busiId, desc, auditNodeInst.getVersion(),
                    auditNodeInst.getRuleVersion(), type, user, token, isStart);
            //???????????????
            if (sure != null) {
                auditCallback(sure.getBusiId(), sure.getResult(), sure.getDesc(), sure.getParamsMap(), sure.getCallback(), sure.getToken());
            }
            return sure;
        } else {
            return auditFail(auditNodeInst.getId(), busiCode, busiId, desc, token, user);
        }
    }


    public AuditCallbackDto surePriDatainfo(String busiCode, Long busiId, String desc, Integer chooseResult, Long instId,
                                    Integer type, Long tennantId, String token, Boolean isStart) throws BusinessException {
        LoginInfo user = loginUtils.get(token);

        if (tennantId == null) {
            tennantId = user.getTenantId();
        }
        AuditNodeInst auditNodeInst = iAuditNodeInstService.queryAuditIng(busiCode, busiId, tennantId);
        if (auditNodeInst == null) {
            throw new BusinessException("???????????????????????????");
        }

        if (instId != null && instId > 0) {
            if (instId.longValue() != auditNodeInst.getId().longValue()) {
                throw new BusinessException("????????????????????????????????????????????????????????????????????????");
            }
        }

        if (AuditConsts.RESULT.SUCCESS == chooseResult) {
            AuditCallbackDto sure = auditPassDatainfo(auditNodeInst.getId(), busiCode, busiId, desc, auditNodeInst.getVersion(),
                    auditNodeInst.getRuleVersion(), type, user, token, isStart);
            //???????????????
            if (sure != null) {
                auditCallback(sure.getBusiId(), sure.getResult(), sure.getDesc(), sure.getParamsMap(), sure.getCallback(), sure.getToken());
            }
            return sure;
        } else {
            return auditFail(auditNodeInst.getId(), busiCode, busiId, desc, token, user);
        }
    }

    public void auditCallback(Long busiId, Integer result, String desc, Map paramsMap, String callback, String token) {
        //Class cls = Class.forName(callback);
        //Method m=null;
        if (result != null && AuditConsts.RESULT.SUCCESS == result) {

            if (callback.equals("com.youming.youche.order.provider.service.order.ProblemVerOptServiceImpl")) {
                //??????????????????
                orderAgingInfoService.verifyPass(busiId, desc, false, token);
            }
            //	m = cls.getDeclaredMethod("sucess",new Class[]{Long.class,String.class,Map.class});
        }
    }

    /**
     * 1 ??????????????????????????????????????? ?????????
     * 2 ?????????????????????????????????????????????????????????
     * 3 ??????????????????????????????
     * 4 ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * 5 ?????????????????????????????????????????????????????????????????????
     *
     * @return
     * @throws Exception
     */
    @Transactional
    public AuditCallbackDto auditPass(Long auditNodeInstId, String auditCode, Long busiId,
                                      String desc, Integer version, Integer ruleVersion, Integer type, LoginInfo user, String token, Boolean isStart) throws BusinessException {
        AuditCallbackDto auditCallbackDto = null;
        if (StringUtils.isBlank(auditCode)) {
            throw new BusinessException("???????????????????????????");
        }
        if (busiId == null) {
            throw new BusinessException("????????????????????????ID??????");
        }
//        try {
        if (checkAuditData(auditNodeInstId)) {
            if (type != null || iAuditNodeInstService.isHasPermission(user.getUserInfoId(), auditNodeInstId)) {
                Long nodeNum = iAuditNodeInstService.getAuditingNodeNum(auditCode, busiId);
                AuditNodeInst auditNodeInst = iAuditNodeInstService.getById(auditNodeInstId);
                Map<String, Object> paramsMap = JsonHelper.parseJSON2Map(auditNodeInst.getParamsMap());
                paramsMap.put(AuditConsts.CallBackMapKey.NODE_NUM, nodeNum);
                AuditConfig auditConfig = iAuditConfigService.getById(auditNodeInst.getAuditId());
                boolean isNext = false;
                boolean isVer = false;

                if (iAuditNodeConfigService.checkVersionNode(version, auditNodeInst.getNodeId())) {
                    isVer = false;
                    AuditNodeConfig nextAuditNode = iAuditNodeConfigService.getNextAuditNodeConfig(auditNodeInst.getNodeId());
                    if (nextAuditNode != null) {
                        auditCallbackDto = successNextAuditNode(auditNodeInst, auditConfig, desc, paramsMap, busiId,
                                nextAuditNode.getId(), nextAuditNode.getNodeName(), ruleVersion, user, token, isStart);
                        isNext = auditCallbackDto.getIsNext();
                    } else {
                        //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        return successAuditNodeInst(auditNodeInst, desc, busiId, paramsMap, auditConfig, type, user, token);
                    }

                } else {
                    isVer = true;
                    AuditNodeConfigVer auditNodeConfigVer = iAuditNodeConfigVerService.getAuditNodeConfigVer(version, auditNodeInst.getNodeId(), true, user.getTenantId());
                    if (auditNodeConfigVer != null) {
                        auditCallbackDto = successNextAuditNode(auditNodeInst, auditConfig, desc,
                                paramsMap, busiId, auditNodeConfigVer.getNodeId(), auditNodeConfigVer.getNodeName(), ruleVersion, user, token, isStart);
                        isNext = auditCallbackDto.getIsNext();

                    } else {
                        //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        return successAuditNodeInst(auditNodeInst, desc, busiId, paramsMap, auditConfig, type, user, token);
                    }
                }
                if (isNext) {
                    Long parentNodeId = null;
                    if (isVer) {
                        AuditNodeConfigVer auditNodeConfigVer = iAuditNodeConfigVerService.getAuditNodeConfigVer(version, auditNodeInst.getNodeId(), false, user.getTenantId());
                        if(auditNodeConfigVer != null){
                            parentNodeId = auditNodeConfigVer.getParentNodeId();
                        }else{
                            parentNodeId = -1L;
                        }

                    } else {
                        AuditNodeConfig auditNodeConfig = iAuditNodeConfigService.getById(auditNodeInst.getNodeId());
                        parentNodeId = auditNodeConfig.getParentNodeId();
                    }
                    if (parentNodeId == -1L) {
//                            try {
//                                Class cls = Class.forName(auditConfig.getCallback());
//                                Method m = cls.getDeclaredMethod("auditingCallBack", new Class[]{Long.class});
//                                m.invoke(cls.newInstance(), busiId);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
                        if (auditCallbackDto != null) {
                            auditCallbackDto.setIsNext(true);
                        }
                    }
                    return auditCallbackDto;
                }
            } else {
                throw new BusinessException("??????????????????????????????");
            }
        } else {
            throw new BusinessException("??????????????????????????????????????????????????????");
        }
//        } catch (Exception e) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//        }
        return auditCallbackDto;
    }

    /**
     * 1 ??????????????????????????????????????? ?????????
     * 2 ?????????????????????????????????????????????????????????
     * 3 ??????????????????????????????
     * 4 ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * 5 ?????????????????????????????????????????????????????????????????????
     *
     * @return
     * @throws Exception
     */
    public AuditCallbackDto auditPassDatainfo(Long auditNodeInstId, String auditCode, Long busiId,
                                      String desc, Integer version, Integer ruleVersion, Integer type, LoginInfo user, String token, Boolean isStart) throws BusinessException {
        AuditCallbackDto auditCallbackDto = null;
        if (StringUtils.isBlank(auditCode)) {
            throw new BusinessException("???????????????????????????");
        }
        if (busiId == null) {
            throw new BusinessException("????????????????????????ID??????");
        }
//        try {
//        if (checkAuditData(auditNodeInstId)) {
            if (type != null || iAuditNodeInstService.isHasPermission(user.getUserInfoId(), auditNodeInstId)) {
                Long nodeNum = iAuditNodeInstService.getAuditingNodeNum(auditCode, busiId);
                AuditNodeInst auditNodeInst = iAuditNodeInstService.getById(auditNodeInstId);
                Map<String, Object> paramsMap = JsonHelper.parseJSON2Map(auditNodeInst.getParamsMap());
                paramsMap.put(AuditConsts.CallBackMapKey.NODE_NUM, nodeNum);
                AuditConfig auditConfig = iAuditConfigService.getById(auditNodeInst.getAuditId());
                boolean isNext = false;
                boolean isVer = false;

                if (iAuditNodeConfigService.checkVersionNode(version, auditNodeInst.getNodeId())) {
                    isVer = false;
                    AuditNodeConfig nextAuditNode = iAuditNodeConfigService.getNextAuditNodeConfig(auditNodeInst.getNodeId());
                    if (nextAuditNode != null) {
                        auditCallbackDto = successNextAuditNode(auditNodeInst, auditConfig, desc, paramsMap, busiId,
                                nextAuditNode.getId(), nextAuditNode.getNodeName(), ruleVersion, user, token, isStart);
                        isNext = auditCallbackDto.getIsNext();
                    } else {
                        //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        return successAuditNodeInst(auditNodeInst, desc, busiId, paramsMap, auditConfig, type, user, token);
                    }

                } else {
                    isVer = true;
                    AuditNodeConfigVer auditNodeConfigVer = iAuditNodeConfigVerService.getAuditNodeConfigVer(version, auditNodeInst.getNodeId(), true, user.getTenantId());
                    if (auditNodeConfigVer != null) {
                        auditCallbackDto = successNextAuditNode(auditNodeInst, auditConfig, desc,
                                paramsMap, busiId, auditNodeConfigVer.getId(), auditNodeConfigVer.getNodeName(), ruleVersion, user, token, isStart);
                        isNext = auditCallbackDto.getIsNext();

                    } else {
                        //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        return successAuditNodeInst(auditNodeInst, desc, busiId, paramsMap, auditConfig, type, user, token);
                    }
                }
                if (isNext) {
                    Long parentNodeId = null;
                    if (isVer) {
                        AuditNodeConfigVer auditNodeConfigVer = iAuditNodeConfigVerService.getAuditNodeConfigVer(version, auditNodeInst.getNodeId(), false, user.getTenantId());
                        parentNodeId = auditNodeConfigVer.getParentNodeId();
                    } else {
                        AuditNodeConfig auditNodeConfig = iAuditNodeConfigService.getById(auditNodeInst.getNodeId());
                        parentNodeId = auditNodeConfig.getParentNodeId();
                    }
                    if (parentNodeId == -1L) {
//                            try {
//                                Class cls = Class.forName(auditConfig.getCallback());
//                                Method m = cls.getDeclaredMethod("auditingCallBack", new Class[]{Long.class});
//                                m.invoke(cls.newInstance(), busiId);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
                        if (auditCallbackDto != null) {
                            auditCallbackDto.setIsNext(true);
                        }
                    }
                    return auditCallbackDto;
                }
//            } else {
//                throw new BusinessException("??????????????????????????????");
//            }
        } else {
            throw new BusinessException("??????????????????????????????????????????????????????");
        }
//        } catch (Exception e) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//        }
        return auditCallbackDto;
    }


    @Transactional
    public AuditCallbackDto auditFail(Long auditNodeInstId, String auditCode, Long busiId,
                                      String desc, String token, LoginInfo user) throws BusinessException {
        if (StringUtils.isBlank(auditCode)) {
            throw new BusinessException("???????????????????????????");
        }

        if (busiId == null) {
            throw new BusinessException("????????????????????????ID??????");
        }
        AuditCallbackDto auditCallbackDto = AuditCallbackDto.of();
        if (checkAuditData(auditNodeInstId)) {
            if (iAuditNodeInstService.isHasPermission(user.getUserInfoId(), auditNodeInstId)) {
                String auditCodeName = "";
                Long nodeNum = iAuditNodeInstService.getAuditingNodeNum(auditCode, busiId);
                //??????????????????????????????????????????
                AuditNodeInst auditNodeInst = iAuditNodeInstService.getById(auditNodeInstId);
                AuditConfig auditConfig = iAuditConfigService.getById(auditNodeInst.getAuditId());
                //???????????????????????????????????????(1?????????????????????2????????????????????????3???flag = 1)
                AuditNodeConfig auditNodeConfig = iAuditNodeConfigService.getAuditNodeConfig(auditNodeInst.getNodeId());
                //???????????????????????????parentNodeId=-1?????????????????????????????????
                if (auditNodeConfig != null && auditNodeConfig.getParentNodeId() != -1 && auditNodeConfig.getFlag() != 1) {
                    //auditNodeConfig.getFlag() == 1 ???????????????????????????????????????
                    AuditNodeConfig lastAuditNodeConfig = new AuditNodeConfig();
//                    if(auditNodeConfig.getFlag() == 1){
//                        //???????????????
//                       List<AuditNodeConfig> AuditNodeConfigList = iAuditNodeConfigService.getAuditNodeConfigByParentId(auditConfig.getId(),-1L,user.getTenantId());
//                       if(AuditNodeConfigList != null && AuditNodeConfigList.size() > 0){
//                           lastAuditNodeConfig = AuditNodeConfigList.get(0);
//                       }
//                    }else {
                    lastAuditNodeConfig = iAuditNodeConfigService.getAuditNodeConfig(auditNodeConfig.getParentNodeId());
                    //    }
                    //??????????????????
                    auditNodeInst.setAuditManId(user.getUserInfoId());
                    auditNodeInst.setRemark(desc);
                    auditNodeInst.setAuditDate(LocalDateTime.now());
                    auditNodeInst.setAuditResult(AuditConsts.RESULT.FAIL);
                    iAuditNodeInstService.updateById(auditNodeInst);
                    //????????????????????????
                    AuditNodeInst lastAuditNodeInst = new AuditNodeInst();
                    lastAuditNodeInst.setAuditBatch(auditNodeInst.getAuditBatch());
                    lastAuditNodeInst.setAuditId(auditConfig.getId());
                    lastAuditNodeInst.setTenantId(user.getTenantId());
                    lastAuditNodeInst.setAuditCode(auditConfig.getAuditCode());
                    lastAuditNodeInst.setNodeId(lastAuditNodeConfig.getId());
                    lastAuditNodeInst.setNodeName(lastAuditNodeConfig.getNodeName());
                    lastAuditNodeInst.setStatus(AuditConsts.Status.AUDITING);
                    lastAuditNodeInst.setAuditResult(AuditConsts.RESULT.TO_AUDIT);
                    lastAuditNodeInst.setParamsMap(auditNodeInst.getParamsMap());
                    lastAuditNodeInst.setBusiId(busiId);
                    lastAuditNodeInst.setLogBusiCode(auditNodeInst.getLogBusiCode());
                    lastAuditNodeInst.setVersion(auditNodeInst.getVersion());
                    if (auditNodeInst.getNodeIndex() != null) {
                        //??????????????????
                        lastAuditNodeInst.setNodeIndex(auditNodeInst.getNodeIndex() + 1);
                    }
                    iAuditNodeInstService.save(lastAuditNodeInst);
                    auditCallbackDto.setToken(token).setBusiId(busiId).setResult(AuditConsts.RESULT.FAIL).setDesc(desc).setCallback(auditConfig.getCallback()).setIsAudit(false);
                } else {
                    Map<String, Object> paramsMap = JsonHelper.parseJSON2Map(auditNodeInst.getParamsMap());
                    paramsMap.put(AuditConsts.CallBackMapKey.NODE_NUM, nodeNum);
                    auditNodeInst.setAuditManId(user.getUserInfoId());
                    auditNodeInst.setRemark(desc);
                    auditNodeInst.setStatus(AuditConsts.Status.FINISH);
                    auditNodeInst.setAuditDate(LocalDateTime.now());
                    auditNodeInst.setAuditResult(AuditConsts.RESULT.FAIL);
                    iAuditNodeInstService.updateById(auditNodeInst);
                    //??????????????????????????????????????????
                    iAuditNodeInstService.updateAuditInstToFinish(auditNodeInst.getAuditId(), busiId);
                    //??????????????????
                    iAuditNodeInstService.delInstToVer(auditNodeInst.getAuditCode(), auditNodeInst.getBusiId(), auditNodeInst.getTenantId());
                    //???????????????????????????????????????????????????????????????
                    try {
                        auditCallbackDto.setToken(token).setBusiId(busiId).setResult(AuditConsts.RESULT.FAIL).setDesc(desc).setParamsMap(paramsMap).setCallback(auditConfig.getCallback()).setIsAudit(true);
                        //   iAuditNodeInstService.auditCallback(busiId, AuditConsts.RESULT.FAIL, desc, paramsMap, auditConfig.getCallback(), token);
                    } catch (BusinessException e) {
                        throw new BusinessException("???????????????????????????????????????");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                auditCodeName = getSysStaticData("AUDIT_CODE", auditConfig.getAuditCode()).getCodeName();
                if(!auditCode.equals("300008")){
                    saveSysOperLog(SysOperLogConst.BusiCode.getBusiCode(auditNodeInst.getLogBusiCode()), SysOperLogConst.OperType.Audit, auditCodeName + "???????????????,?????????[" + desc + "]", token, busiId);
                }
            } else {
                throw new BusinessException("??????????????????????????????");
            }
        } else {
            throw new BusinessException("??????????????????????????????????????????????????????");
        }
        return auditCallbackDto;
    }


    /**
     * ?????????????????????
     *
     * @param
     * @param auditNodeInst
     * @param auditConfig
     * @param desc
     * @param paramsMap
     * @param busiId
     * @param nodeId        ????????????????????????id
     * @param ruleVersion
     * @return
     * @throws Exception
     */
    public AuditCallbackDto successNextAuditNode(AuditNodeInst auditNodeInst, AuditConfig auditConfig, String desc,
                                                 Map paramsMap, Long busiId, Long nodeId,
                                                 String nodeName, Integer ruleVersion, LoginInfo user, String token, Boolean isStart) throws BusinessException {
        if (iAuditConfigService.checkNodePreRule(nodeId, busiId, paramsMap, ruleVersion, user.getTenantId(), isStart)) {
            //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            //???????????????????????????????????? ????????????????????????????????????????????????????????????
            auditNodeInst.setAuditManId(user.getUserInfoId());
            auditNodeInst.setRemark(desc);
            auditNodeInst.setAuditDate(LocalDateTime.now());
            auditNodeInst.setAuditResult(AuditConsts.RESULT.SUCCESS);
            iAuditNodeInstService.updateById(auditNodeInst);

            //????????????????????????
            AuditNodeInst nextAuditNodeInst = new AuditNodeInst();
            nextAuditNodeInst.setAuditBatch(auditNodeInst.getAuditBatch());
            nextAuditNodeInst.setAuditId(auditConfig.getId());
            nextAuditNodeInst.setTenantId(user.getTenantId());
            nextAuditNodeInst.setAuditCode(auditConfig.getAuditCode());
            nextAuditNodeInst.setNodeId(nodeId);
            nextAuditNodeInst.setNodeName(nodeName);
            nextAuditNodeInst.setStatus(AuditConsts.Status.AUDITING);
            nextAuditNodeInst.setAuditResult(AuditConsts.RESULT.TO_AUDIT);
            nextAuditNodeInst.setParamsMap(JSON.toJSONString(paramsMap));
            nextAuditNodeInst.setBusiId(busiId);
            nextAuditNodeInst.setLogBusiCode(auditNodeInst.getLogBusiCode());
            nextAuditNodeInst.setVersion(auditNodeInst.getVersion());
            if (auditNodeInst.getNodeIndex() != null) {
                //??????????????????
                nextAuditNodeInst.setNodeIndex(auditNodeInst.getNodeIndex() + 1);
            }
            iAuditNodeInstService.save(nextAuditNodeInst);
            //?????????????????????
            List<Long> auditUserList = auditSettingMapper.getUserFromAuditUser(nextAuditNodeInst.getNodeId(), AuditConsts.TargetObjType.USER_TYPE);
            nodeAuditCallback(nextAuditNodeInst.getNodeIndex() == null ? 0 : nextAuditNodeInst.getNodeIndex(), busiId, AuditConsts.TargetObjType.USER_TYPE, listLongToStr(auditUserList), auditConfig.getNodeCallback());
            StringBuffer alert = new StringBuffer("??????????????????");
            alert.append(iAuditNodeInstService.getAuditUserNameByNodeId(nextAuditNodeInst.getNodeId(), AuditConsts.TargetObjType.USER_TYPE));
            String msg = "";
            if (StringUtils.isNotBlank(auditNodeInst.getRemark())) {
                msg = "????????????[" + auditNodeInst.getRemark() + "]";
            }
            String auditCodeName = getSysStaticData("AUDIT_CODE", auditConfig.getAuditCode()).getCodeName();
            saveSysOperLog(SysOperLogConst.BusiCode.getBusiCode(auditNodeInst.getLogBusiCode()), SysOperLogConst.OperType.Audit, auditCodeName + "????????????" + msg, token, busiId);
            return AuditCallbackDto.of()
                    .setIsNext(true)
                    .setIsAudit(true)
                    .setCallback(auditConfig.getCallback())
                    .setToken(token)
                    .setBusiId(busiId)
                    .setDesc(desc);
        } else {
            //??????????????????????????????????????? ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            return successAuditNodeInst(auditNodeInst, desc, busiId, paramsMap, auditConfig, user, token);
        }
    }

    private AuditCallbackDto successAuditNodeInst(AuditNodeInst auditNodeInst, String desc, Long busiId, Map<String, Object> paramsMap, AuditConfig auditConfig, LoginInfo user, String token) throws BusinessException {
        return successAuditNodeInst(auditNodeInst, desc, busiId, paramsMap, auditConfig, null, user, token);
    }


    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param type 1:????????????????????????????????? ???????????????????????????????????????
     *             2 ??????????????????????????????????????????????????????
     * @return
     */
    private AuditCallbackDto successAuditNodeInst(AuditNodeInst auditNodeInst, String desc, Long busiId, Map<String, Object> paramsMap,
                                                  AuditConfig auditConfig, Integer type, LoginInfo user, String token) throws BusinessException {
        if (type == null || type.intValue() == 2) {

            auditNodeInst.setAuditManId(user.getUserInfoId());
            auditNodeInst.setRemark(desc);
            auditNodeInst.setAuditDate(LocalDateTime.now());
            auditNodeInst.setStatus(AuditConsts.Status.FINISH);
            auditNodeInst.setAuditResult(AuditConsts.RESULT.SUCCESS);
            iAuditNodeInstService.updateById(auditNodeInst);

            //??????????????????????????????????????????
            iAuditNodeInstService.updateAuditInstToFinish(auditNodeInst.getAuditId(), busiId);
            iAuditNodeInstService.delInstToVer(auditNodeInst.getAuditCode(), auditNodeInst.getBusiId(), auditNodeInst.getTenantId());
        }
        AuditCallbackDto auditCallbackDto = AuditCallbackDto.of();

        if (type == null || type.intValue() == AuditConsts.OperType.WEB) {
            //???????????????????????????????????????????????????????????????
        /*    try {
                iAuditNodeInstService.auditCallback(busiId, AuditConsts.RESULT.SUCCESS, desc, paramsMap, auditConfig.getCallback(), token);
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
//                e.printStackTrace();
            }*/
            //???????????????
            auditCallbackDto.setType(type).setBusiId(busiId).setResult(AuditConsts.RESULT.SUCCESS).setDesc(desc)
                    .setParamsMap(paramsMap).setCallback(auditConfig.getCallback()).setToken(token).setIsAudit(true);
        }
        if (type == null || type.intValue() == AuditConsts.OperType.TASK) {
            String msg = "";
            if (StringUtils.isNotBlank(auditNodeInst.getRemark())) {
                msg = "????????????[" + auditNodeInst.getRemark() + "]";
            }
            String auditCodeName = getSysStaticData("AUDIT_CODE", auditConfig.getAuditCode()).getCodeName();
            saveSysOperLog(SysOperLogConst.BusiCode.getBusiCode(auditNodeInst.getLogBusiCode()), SysOperLogConst.OperType.Audit,
                    auditCodeName + "????????????" + msg, token, busiId);
        }
        return auditCallbackDto;
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @return
     */

    @Override
    public boolean successAuditNodeInstClose(String auditCode, String desc, Long busiId, LoginInfo user) {
        List<AuditNodeInst> auditNodeInsts = iAuditNodeInstService.selectByBusiCodeAndBusiIdAndTenantIdAntStatus(auditCode, busiId, user.getTenantId(), 1);
        boolean flag = false;
        if (auditNodeInsts != null) {
            for (AuditNodeInst auditNodeInst : auditNodeInsts) {
                auditNodeInst.setAuditManId(user.getUserInfoId());
                auditNodeInst.setRemark(desc);
                auditNodeInst.setAuditDate(LocalDateTime.now());
                auditNodeInst.setStatus(AuditConsts.Status.FINISH);
                auditNodeInst.setAuditResult(AuditConsts.RESULT.SUCCESS);
                iAuditNodeInstService.updateById(auditNodeInst);
                //??????????????????????????????????????????
                iAuditNodeInstService.updateAuditInstToFinish(auditNodeInst.getAuditId(), busiId);
                iAuditNodeInstService.delInstToVer(auditNodeInst.getAuditCode(), auditNodeInst.getBusiId(), auditNodeInst.getTenantId());
                flag = true;
            }
        }
        return flag;
//        if (type == null || type.intValue() == AuditConsts.OperType.TASK) {
//            String msg = "";
//            if (StringUtils.isNotBlank(auditNodeInst.getRemark())) {
//                msg = "????????????[" + auditNodeInst.getRemark() + "]";
//            }
//            String auditCodeName = getSysStaticData("AUDIT_CODE", auditConfig.getAuditCode()).getCodeName();
//            saveSysOperLog(SysOperLogConst.BusiCode.getBusiCode(auditNodeInst.getLogBusiCode()), SysOperLogConst.OperType.Audit,
//                    auditCodeName + "????????????" + msg, token, busiId);
    }

    private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busid);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        isysOperLogService.save(operLog, accessToken);
    }


    public void nodeAuditCallback(int nodeIndex, long busiId, int targetObjectType, String targetObjId, String callBack) throws BusinessException {
        if (StringUtils.isEmpty(callBack)) {
            return;
        }
        try {
            Class cls = Class.forName(callBack);
            Method m = cls.getDeclaredMethod("notice", new Class[]{Integer.class, Long.class, Integer.class, String.class});
            m.invoke(cls.newInstance(), nodeIndex, busiId, targetObjectType, targetObjId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String listLongToStr(List<Long> list) {
        if (list == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean first = true;
        //????????????????????????","
        for (Long string : list) {
            if (first) {
                first = false;
            } else {
                result.append(",");
            }
            result.append(string);
        }
        return result.toString();
    }


    /**
     * ??????????????????????????????????????? ?????????
     *
     * @param auditNodeInstId ???????????????????????????
     * @return
     */
    private boolean checkAuditData(Long auditNodeInstId) {
        if (auditNodeInstId == null) {
            throw new BusinessException("???????????????????????????ID??????");
        }
        AuditNodeInst auditNodeInst = iAuditNodeInstService.getById(auditNodeInstId);
        if (auditNodeInst != null && AuditConsts.RESULT.TO_AUDIT == auditNodeInst.getAuditResult()) {
            return true;
        }
        return false;
    }

    private void contrast(List<AuditNodeRuleConfig> auditNodeRuleConfigs, AuditRuleConfig auditRuleConfig, boolean isBe, long auditId, long nodeId, AuditRuleNodeVo auditRuleNodeVo, Integer version, LoginInfo user) throws Exception {
        AuditNodeRuleConfig auditNodeRule = null;
        if (auditNodeRuleConfigs != null && auditNodeRuleConfigs.size() > 0) {
            for (AuditNodeRuleConfig auditNodeRuleConfig : auditNodeRuleConfigs) {
                if (auditRuleConfig.getId().equals(auditNodeRuleConfig.getRuleId())) {
                    auditNodeRule = auditNodeRuleConfig;
                }
            }
        }
        if (isBe) {
            if (auditNodeRule == null) {
                auditNodeRule = new AuditNodeRuleConfig();
                auditNodeRule.setAuditId(auditId);
                auditNodeRule.setNodeId(nodeId);
                auditNodeRule.setTenantId(user.getTenantId());
                auditNodeRule.setRuleId(auditRuleConfig.getId());
            }
            auditNodeRule.setOpId(user.getUserInfoId());
            auditNodeRule.setVersion(version);
            if (AuditConsts.RULE_CODE.EXCEED_GUIDE_PRICE.equals(auditRuleConfig.getRuleCode())) {
                auditNodeRule.setRuleValue(auditRuleNodeVo.getExceedGuidePrice());
            }
            iAuditNodeRuleConfigService.saveOrUpdate(auditNodeRule);
        } else {
            if (auditNodeRule != null) {
                iAuditNodeRuleConfigService.removeById(auditNodeRule.getId());
            }
        }
    }


    private Map<String, List<AuditNodePageDto>> groupAuditDataByAuditCode(List<AuditNodePageDto> auditNodePages) throws Exception {
        Map<String, List<AuditNodePageDto>> resultMap = new HashMap<>();
        try {
            for (AuditNodePageDto auditNodePage : auditNodePages) {
                if (resultMap.containsKey(auditNodePage.getAuditCode())) {
                    resultMap.get(auditNodePage.getAuditCode()).add(auditNodePage);
                } else {//map?????????????????????key?????????????????????
                    List<AuditNodePageDto> tmpList = new ArrayList<AuditNodePageDto>();
                    tmpList.add(auditNodePage);
                    resultMap.put(auditNodePage.getAuditCode(), tmpList);
                }
            }
        } catch (Exception e) {
            throw new Exception("???????????????????????????????????????", e);
        }
        return resultMap;
    }


}
