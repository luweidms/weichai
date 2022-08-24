package com.youming.youche.system.provider.service;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditNodeConfigService;
import com.youming.youche.system.api.audit.IAuditNodeConfigVerService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditUserService;
import com.youming.youche.system.constant.AuditConsts;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.audit.AuditNodeConfig;
import com.youming.youche.system.domain.audit.AuditNodeConfigVer;
import com.youming.youche.system.domain.audit.AuditNodeInst;
import com.youming.youche.system.provider.mapper.SysOperLogMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 操作日志表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-14
 */
@DubboService(version = "1.0.0")
public class SysOperLogServiceImpl extends BaseServiceImpl<SysOperLogMapper, SysOperLog> implements ISysOperLogService {


    @Resource
    LoginUtils loginUtils;

    @Autowired
    IUserDataInfoService iUserDataInfoService;

    @Autowired
    IAuditNodeConfigService iAuditNodeConfigService;

    @Autowired
    IAuditNodeConfigVerService iAuditNodeConfigVerService;

    @Autowired
    IAuditNodeInstService iAuditNodeInstService;

    @Autowired
    IAuditUserService iAuditUserService;


    @Override
    public boolean save(SysOperLog sysOperLog, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = 0L;
        if (loginInfo == null) {
            throw new BusinessException("请重新登陆");
        }
        if (sysOperLog.getTenantId() != null) {
            tenantId = sysOperLog.getTenantId();
        } else {
            tenantId = loginInfo.getTenantId();
        }
        sysOperLog.setOpId(loginInfo.getId()).setOpName(loginInfo.getName()).setTenantId(tenantId);
        boolean save = save(sysOperLog);
        return save;
    }

    @Override
    public boolean save(SysOperLog sysOperLog, Long tenantId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        sysOperLog.setOpId(loginInfo.getId()).setOpName(loginInfo.getName()).setTenantId(tenantId);
        return save(sysOperLog);
    }

    @Override
    public boolean save(SysOperLogConst.BusiCode busiCode, Long busiId, SysOperLogConst.OperType operType, String operComment, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        return save(busiCode, busiId, operType, operComment, loginInfo);
    }

    @Override
    public boolean save(SysOperLogConst.BusiCode busiCode, Long busiId, SysOperLogConst.OperType operType, String operComment, String opName, Long opId, Long tenantId) {
        SysOperLog operLog = new SysOperLog();
        operLog.setOpName(opName);
        operLog.setOpId(opId);
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busiId);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operComment);
        operLog.setTenantId(tenantId);
        return save(operLog);
    }

    @Override
    public boolean save(SysOperLogConst.BusiCode busiCode, Long busiId, SysOperLogConst.OperType operType, String operComment, LoginInfo loginInfo) {
        SysOperLog operLog = new SysOperLog();
        operLog.setOpName(loginInfo.getName());
        operLog.setOpId(loginInfo.getId());
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busiId);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operComment);
        operLog.setTenantId(loginInfo.getTenantId());
        return save(operLog);
    }


    @Override
    public void saveSysOperLog(LoginInfo loginInfo, SysOperLogConst.BusiCode busiCode, Long busiId, SysOperLogConst.OperType operType, String operComment, Long... tenantId) {
        SysOperLog operLog = new SysOperLog();
        operLog.setOpName(loginInfo.getName());
        operLog.setOpId(loginInfo.getId());
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busiId);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operComment);
        operLog.setTenantId(loginInfo.getTenantId());

        if (tenantId != null && tenantId.length == 1) {
            operLog.setTenantId(tenantId[0]);
        }
        this.save(operLog);
    }

    @Override
    public void saveSysOperLogSys(SysOperLogConst.BusiCode busiCode, Long busiId, SysOperLogConst.OperType operType, String operComment, Long... tenantId) {
        SysOperLog operLog = new SysOperLog();
        operLog.setOpName("系统");
        operLog.setBusiCode(400009);
        operLog.setBusiName("服务商账单");
        operLog.setBusiId(busiId);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operComment);

        if (tenantId != null && tenantId.length == 1) {
            operLog.setTenantId(tenantId[0]);
        }
        this.save(operLog);
    }


    @Override
    public List<SysOperLog> selectAllByBusiCodeAndBusiIdAndAccessToken(Integer businessCode, Long businessId, Integer querySource, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LambdaQueryWrapper<SysOperLog> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysOperLog::getBusiCode, businessCode).eq(SysOperLog::getBusiId, businessId).eq(SysOperLog::getTenantId,
                loginInfo.getTenantId()).orderByDesc(SysOperLog::getId);
        List<SysOperLog> sysOperLogs = baseMapper.selectList(wrapper);
        /**追加待审人的日志*/
        if (sysOperLogs != null && querySource > 0) {
            if (querySource == 2) {//=2 回单审核查看日志，只显示回单审核待审
                sysOperLogs.addAll(0, this.queryAuditRealTimeOperation(businessId, new Long[]{Long.valueOf(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE)},accessToken));
            } else {
                sysOperLogs.addAll(0, this.queryAuditRealTimeOperation(businessId, new Long[]{Long.valueOf(AuditConsts.AUDIT_CODE.ORDER_UPDATE_CODE),Long.valueOf(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE)},accessToken));
            }
        }
        return sysOperLogs;
    }

    @Override
    public List<SysOperLog> querySysOperLogOrderByCreateDate(int code, Long busiId, Boolean isAsc, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        SysOperLogConst.BusiCode busiCode = SysOperLogConst.BusiCode.getBusiCode(code);
        return this.querySysOperLogOrderByCreateDate(busiCode, busiId, isAsc, loginInfo.getTenantId());
    }

    @Override
    public List<SysOperLog> querySysOperLogOrderByCreateDate(SysOperLogConst.BusiCode busiCode, Long busiId,
                                                             Boolean isAsc, Long tenantId) {
        LambdaQueryWrapper<SysOperLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysOperLog::getBusiCode, busiCode.getCode());
        queryWrapper.eq(SysOperLog::getBusiId, busiId);
        queryWrapper.eq(SysOperLog::getTenantId, tenantId);
        if (isAsc != null) {
            if (isAsc == true) {
                queryWrapper.orderByAsc(SysOperLog::getCreateTime);
            } else {
                queryWrapper.orderByDesc(SysOperLog::getCreateTime);
            }
        }
        List<SysOperLog> list = this.list(queryWrapper);
        return list;
    }

    /**
     * 获取待审日志
     */
    @Override
    public List<SysOperLog> queryAuditRealTimeOperation(Long orderId, Long[] busiCodes, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<SysOperLog> list = new ArrayList<SysOperLog>();
        for (Long busiCode : busiCodes) {
            Map<Long, Map<String, Object>> map = queryAuditRealTimeOperation(loginInfo.getUserInfoId(), String.valueOf(busiCode)
                    , Arrays.asList(new Long[]{orderId}), loginInfo.getTenantId());
            for (Map.Entry<Long, Map<String, Object>> obj : map.entrySet()) {
                SysOperLog log = new SysOperLog();
                log.setBusiCode(SysOperLogConst.BusiCode.OrderInfo.getCode());
                log.setBusiId(orderId);
                log.setBusiName(SysOperLogConst.OperType.AuditUser.getName());
                log.setOperType(SysOperLogConst.OperType.AuditUser.getCode());
                log.setOperTypeName(SysOperLogConst.OperType.AuditUser.getName());
                log.setOperComment(obj.getValue().get("auditUserName").toString());
                log.setCreateTime(null);
                list.add(log);
            }
        }
        return list;
    }

    @Override
    public List<SysOperLog> querySysOperLog(SysOperLogConst.BusiCode busiCode, Long busiId, Boolean isAsc, Long tenantId, String auditCode, Long auditBusiId) {

        LambdaQueryWrapper<SysOperLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysOperLog::getBusiCode, busiCode.getCode());
        queryWrapper.eq(SysOperLog::getBusiId, busiId);
        queryWrapper.eq(tenantId != null && tenantId != -1, SysOperLog::getTenantId, tenantId);
        if (isAsc != null) {
            if (isAsc == true) {
                queryWrapper.orderByAsc(SysOperLog::getId);
            } else {
                queryWrapper.orderByDesc(SysOperLog::getId);
            }
        }
        if (auditBusiId == null || -1 == auditBusiId.longValue()) {
            auditBusiId = busiId;
        }
        List<SysOperLog> list = this.list(queryWrapper);
        SysOperLog firstSysOperLog = getToAuditUser(auditCode, auditBusiId, tenantId);
        if (firstSysOperLog != null) {
            list.add(0, firstSysOperLog);
        }
        return list;
    }

    @Override
    public List<SysOperLog> querySysOperLogAll(int code, Long busiId, Boolean isAsc) {
        return querySysOperLogAll(code, busiId, isAsc, null, null, null);
    }

    @Override
    public List<SysOperLog> querySysOperLogAll(int code, Long busiId, Boolean isAsc, Long tenantId, String auditCode, Long auditBusiId) {
        SysOperLogConst.BusiCode busiCode = SysOperLogConst.BusiCode.getBusiCode(code);
        List<SysOperLog> sysOperLogs = this.querySysOperLog(busiCode, busiId, isAsc, null, null, null);

        if (auditBusiId == null || -1 == auditBusiId) {
            auditBusiId = busiId;
        }

        SysOperLog firstSysOperLog = getToAuditUser(auditCode, auditBusiId, tenantId);
        if (firstSysOperLog != null) {
            if (sysOperLogs == null) {
                sysOperLogs = new ArrayList<SysOperLog>(1);
                sysOperLogs.add(firstSysOperLog);
            } else {
                sysOperLogs.add(0, firstSysOperLog);
            }
        }
        return sysOperLogs;
    }

    @Override
    public void saveSysOperLogTime(SysOperLogConst.BusiCode busiCode, Long busiId, SysOperLogConst.OperType operType, String operComment) {

        SysOperLog operLog = new SysOperLog();
        operLog.setOpName("系统定时任务");
        operLog.setOpId(1L);
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busiId);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operComment);
        operLog.setTenantId(1L);

        this.save(operLog);

    }

    @Override
    public List<SysOperLog> querySysOperLogIntf(Integer code, Long busiId, Boolean isAsc, String auditCode, Long auditBusiId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        if (busiId == null || busiId <= 0) {
            throw new BusinessException("业务编号不予为空!");
        }
        if (code == null || code <= 0) {
            throw new BusinessException("业务类型不予为空!");
        }
        SysOperLogConst.BusiCode busiCode = SysOperLogConst.BusiCode.getBusiCode(code);
        if (busiCode != null && busiCode == SysOperLogConst.BusiCode.OrderInfo) {
            return querySysOperLogOrderByCreateDate(code, busiId, isAsc, accessToken);
        } else {
            return querySysOperLog(busiCode, busiId, isAsc, tenantId, auditCode, auditBusiId);
        }
    }

    @Override
    public void saveSysOperLog(LoginInfo user, SysOperLogConst.BusiCode busiCode, Long busiId, SysOperLogConst.OperType operType, String operComment, Long tenantId, Date opDate) {
        SysOperLog operLog = new SysOperLog();
        operLog.setOpName(user.getName());
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busiId);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operComment);

        if(tenantId!=null && tenantId > 0){
            operLog.setTenantId(tenantId);
        }
        if (opDate != null) {
            operLog.setCreateTime(LocalDateTimeUtil.of(opDate));
        }
        this.save(operLog);
    }

    private SysOperLog getToAuditUser(String auditCode, Long auditBusiId, Long tenantId) {
        if (tenantId != null && StringUtils.isNotBlank(auditCode)) {
            //租户为空的，是查询所有的日志，这个时候不需要显示待审核的记录
            String auditNodeUsers = getAuditNextNodeUsers(auditCode, auditBusiId, tenantId);
            if (StringUtils.isNotBlank(auditNodeUsers)) {
                SysOperLog firstSysOperLog = new SysOperLog();
                firstSysOperLog.setCreateTime(null);
                firstSysOperLog.setOpName("");
                firstSysOperLog.setOperType(SysOperLogConst.OperType.AuditUser.getCode());
                firstSysOperLog.setOperComment("下个审核" + auditNodeUsers);
                return firstSysOperLog;
            }
        }
        return null;
    }

    /**
     * 查询审核实例表，查询待审核的数据，下个审核节点的审核人列表
     * 如果不是待审核，返回空的列表
     *
     * @param tenantId 如果为空，取当前登录用户的租户id
     * @return 没有数据为 null
     * 有审核人返回：人:xxx,xxxx,xxx
     */
    String getAuditNextNodeUsers(String auditCode, Long busiId, Long tenantId) {
        AuditNodeInst auditNodeInst = iAuditNodeInstService.queryAuditIng(auditCode, busiId, tenantId);
        if (auditNodeInst == null) {
            return null;
        }

        Long nodeId = null;
        if (iAuditNodeConfigService.checkVersionNode(auditNodeInst.getVersion(), auditNodeInst.getNodeId())) {
            AuditNodeConfig auditNodeConfig = iAuditNodeConfigService.getById(auditNodeInst.getNodeId());
            if (auditNodeConfig == null) {
                return null;
            }
            nodeId = auditNodeConfig.getId();
        } else {
            AuditNodeConfigVer auditNodeConfigVer = iAuditNodeConfigVerService.getAuditNodeConfigVer(auditNodeInst.getVersion(), auditNodeInst.getNodeId(), false, tenantId);
            if (auditNodeConfigVer == null) {
                return null;
            }
            nodeId = auditNodeConfigVer.getId();
        }
        return getAuditUserNameByNodeId(nodeId, AuditConsts.TargetObjType.USER_TYPE);
    }

    public String getAuditUserNameByNodeId(Long nodeId, Integer targetObjType) {
        List<Long> userIdList = iAuditUserService.selectIdByNodeIdAndTarGetObjType(nodeId, targetObjType);
        StringBuffer alert = new StringBuffer();
        if (userIdList != null && userIdList.size() > 0) {
            alert.append("人:");
            List<String> userNameList = Lists.newArrayList();
            for (Long aLong : userIdList) {
                String userName = null;
                try {
                    userName = iUserDataInfoService.getUserName(aLong);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                userNameList.add(userName);
            }
            alert.append(listToString(userNameList));
        } else {
            throw new BusinessException("下个节点的审核的对象类型设置错误");
        }
        return alert.toString();
    }

    /**
     * 获取业务审核当前操作
     *
     * @param userId   操作人
     * @param busiCode 业务编码
     * @param busiIds  业务主键集合
     * @param tenantId 车队Id
     */
    public Map<Long, Map<String, Object>> queryAuditRealTimeOperation(Long userId, String busiCode, List<Long> busiIds, Long tenantId) {
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
            for (AuditNodeInst auditNodeInst : auditNodeInsts) {
                boolean isAuditJurisdiction = false;
                boolean isFinallyNode = false;
                StringBuffer auditUserBf = new StringBuffer("待审核");

                List<Long> userIdList = iAuditUserService.selectIdByNodeIdAndTarGetObjType(auditNodeInst.getNodeId(), AuditConsts.TargetObjType.USER_TYPE);
                if (userIdList != null && userIdList.size() > 0) {
                    auditUserBf.append("人:");
                    List<String> userNameList = iUserDataInfoService.querStaffNameByUserIds(userIdList);
                    auditUserBf.append(listToString(userNameList));
                    isAuditJurisdiction = userIdList.contains(userId);
                } else {
                    throw new BusinessException("下个节点的审核的对象类型设置错误");
                }
                AuditNodeConfig auditNodeConfig = iAuditNodeConfigService.getAuditNodeConfig(auditNodeInst.getNodeId());
                if (auditNodeConfig == null || auditNodeConfig.getVersion() != auditNodeInst.getVersion()) {
                    AuditNodeConfigVer nextAuditNode = iAuditNodeConfigVerService.getAuditNodeConfigVer(auditNodeInst.getVersion(),
                            auditNodeInst.getNodeId(), true, tenantId);
                    if (nextAuditNode == null) {
                        isFinallyNode = true;
                    }
                } else {
                    AuditNodeConfig nextNodeConfig = iAuditNodeConfigService.getNextAuditNodeConfig(auditNodeConfig.getId());
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

}
