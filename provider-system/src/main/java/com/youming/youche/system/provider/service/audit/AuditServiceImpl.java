package com.youming.youche.system.provider.service.audit;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.dto.AuditDto;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditConfigService;
import com.youming.youche.system.api.audit.IAuditNodeConfigService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditNodeInstVerService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.constant.AuditConsts;
import com.youming.youche.system.domain.audit.AuditConfig;
import com.youming.youche.system.domain.audit.AuditNodeConfig;
import com.youming.youche.system.domain.audit.AuditNodeInst;
import com.youming.youche.system.domain.audit.AuditNodeInstVer;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @version:
 * @Title: AuditServiceImpl
 * @Package: com.youming.youche.other.provider.service.impl.Audit
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2021/12/10 15:30
 * @company:
 */
@DubboService(version = "1.0.0")
@Service
public class AuditServiceImpl implements IAuditService {

    @Resource
    IAuditNodeInstService iAuditNodeInstService;

    @Resource
    IAuditConfigService iAuditConfigService;

    @Resource
    IAuditNodeConfigService iAuditNodeConfigService;

    @Resource
    IAuditNodeInstVerService iAuditNodeInstVerService;

    @Resource
    IUserDataInfoService iUserDataInfoService;


    @Override
    public boolean startProcess(String auditCode, Long busiId, SysOperLogConst.BusiCode busiCode, Map<String, Object> params, String accessToken, Long... tenantId) throws BusinessException {
        return this.startProcess(auditCode,busiId,busiCode,params,null,null,accessToken,tenantId);
    }

    @Override
    public boolean startProcessOrder(String auditCode, Long busiId, SysOperLogConst.BusiCode busiCode, Map<String, Object> params, String accessToken, Long... tenantId) throws BusinessException {
        return this.startProcessOrder(auditCode,busiId,busiCode,params,null,null,accessToken,tenantId);
    }

    @Override
    public boolean startProcess(AuditDto auditDto) throws BusinessException {
        if (null==auditDto.getTenantId()){
            return startProcess(auditDto.getAuditCode(),auditDto.getBusiId(),auditDto.getBusiCode(),auditDto.getParams(),
                    auditDto.getAccessToken());
        }
        return startProcess(auditDto.getAuditCode(),auditDto.getBusiId(),auditDto.getBusiCode(),auditDto.getParams(),
                auditDto.getAccessToken(),auditDto.getTenantId());
    }

    @Override
    public Map<Long, Boolean> isHasPermission(String auditCode, List<Long> busiIdList) {


        return null;
    }


    /***
     * 1 根据审核业务编码查询 AUDIT_CONFIG，AUDIT_NODE_CONFIG 两个表，获取是否配置审核节点
     * 2 获取节点的规则 AUDIT_NODE_RULE_CONFIG，如果有规则，判断是否满足条件
     * 3 满足条件后，把 内容写入 AUDIT_NODE_INST 表
     *
     *
     *
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean startProcess(String auditCode, Long busiId, SysOperLogConst.BusiCode busiCode, Map<String, Object> params, Long opId, String opName, String accessToken, Long...tenantIds) throws BusinessException {
        if(StringUtils.isBlank(auditCode)){
            throw new BusinessException("审核业务编码不能为空");
        }
        if(busiId==null){
            throw new BusinessException("审核业务主键不能为空");
        }

        AuditConfig auditConfig= iAuditConfigService.getAuditConfigByCode(auditCode);
        if(auditConfig==null){
            // log.error("审核业务编码["+auditCode+"] 没有配置审核流程");
            return false;
        }
        LoginInfo user=iUserDataInfoService.getLoginInfoByAccessToken(accessToken);
        Long tenantId=user.getTenantId();
        if(tenantIds!=null && tenantIds.length>0){
            try {
                tenantId=tenantIds[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AuditNodeConfig auditNodeConfig= iAuditConfigService.getFirstAuditNodeConfig(auditConfig.getId(),tenantId);
        if(auditNodeConfig==null){
            //log.error("审核业务编码["+auditCode+"] 没有配置审核流程的节点,audid_config表的audit_id["+auditConfig.getAuditId()+"],tenant_id["+tenantId+"]");
            return false;
        }
        if(isAuditIng(auditCode, busiId,tenantId)){
            //已经有审核的节点开始审核了
            throw new BusinessException("该流程已经有节点进行审核了，需要等审核流程结束才能重新发起");
        }
        AuditNodeInst preAuditNodeInst= iAuditConfigService.queryAuditIng(auditCode, busiId,tenantId);
        if(preAuditNodeInst!=null){
            AuditNodeInstVer auditNodeInstVer=new AuditNodeInstVer();
            BeanUtil.copyProperties(preAuditNodeInst,auditNodeInstVer);
            auditNodeInstVer.setVerId(preAuditNodeInst.getId());
            //审核流程-已完成
            auditNodeInstVer.setStatus(AuditConsts.Status.FINISH);
            //审核流程-取消，发起了新的审核
            auditNodeInstVer.setAuditResult(AuditConsts.RESULT.CANCEL);
            //把原表的数据移除到ver表
            iAuditNodeInstVerService.save(auditNodeInstVer);
            iAuditNodeInstService.removeById(preAuditNodeInst);
        }
        Integer ruleVersion = iAuditConfigService.getAuditRuleVersionNo(auditNodeConfig.getId());
        //判断第一个节点是否有前置规则,如果不满足，直接返回，不需要处理
        if(!iAuditConfigService.checkNodePreRule(auditNodeConfig.getId(), busiId, params,ruleVersion,tenantId,true)){
            return false;
        }
        AuditNodeInst auditNodeInst=new AuditNodeInst();
        auditNodeInst.setAuditId(auditConfig.getId());
        auditNodeInst.setAuditCode(auditConfig.getAuditCode());
        auditNodeInst.setNodeId(auditNodeConfig.getId());
        auditNodeInst.setNodeName(auditNodeConfig.getNodeName());
        auditNodeInst.setStatus(AuditConsts.Status.AUDITING);
        auditNodeInst.setAuditResult(AuditConsts.RESULT.TO_AUDIT);
        auditNodeInst.setParamsMap(JSONUtil.toJsonStr(params));
        auditNodeInst.setBusiId(busiId);
        auditNodeInst.setLogBusiCode(busiCode.getCode());
        auditNodeInst.setTenantId(tenantId);
        auditNodeInst.setAuditBatch(getBatchId());
        auditNodeInst.setVersion(auditNodeConfig.getVersion());
        auditNodeInst.setRuleVersion(ruleVersion);
        auditNodeInst.setNodeIndex(1);
        iAuditNodeInstService.save(auditNodeInst);
        //节点的回调方法
        // List<Long> auditUserList=iAuditNodeInstService.getUserFromAuditUser(auditNodeInst.getNodeId(), com.youming.youche.conts.AuditConsts.TargetObjType.USER_TYPE);
        // iAuditNodeInstService.nodeAuditCallback(auditNodeInst.getNodeIndex(),busiId, com.youming.youche.conts.AuditConsts.TargetObjType.USER_TYPE, listLongToStr(auditUserList), auditConfig.getNodeCallback());
        return true;
    }

    /***
     * 1 根据审核业务编码查询 AUDIT_CONFIG，AUDIT_NODE_CONFIG 两个表，获取是否配置审核节点
     * 2 获取节点的规则 AUDIT_NODE_RULE_CONFIG，如果有规则，判断是否满足条件
     * 3 满足条件后，把 内容写入 AUDIT_NODE_INST 表
     *
     *
     *
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean startProcessOrder(String auditCode, Long busiId, SysOperLogConst.BusiCode busiCode, Map<String, Object> params, Long opId, String opName, String accessToken, Long...tenantIds) throws BusinessException {
        if(StringUtils.isBlank(auditCode)){
            throw new BusinessException("审核业务编码不能为空");
        }
        if(busiId==null){
            throw new BusinessException("审核业务主键不能为空");
        }

        AuditConfig auditConfig= iAuditConfigService.getAuditConfigByCode(auditCode);
        if(auditConfig==null){
            // log.error("审核业务编码["+auditCode+"] 没有配置审核流程");
            return false;
        }
        LoginInfo user=iUserDataInfoService.getLoginInfoByAccessToken(accessToken);
        Long tenantId=user.getTenantId();
        if(tenantIds!=null && tenantIds.length>0){
            try {
                tenantId=tenantIds[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AuditNodeConfig auditNodeConfig= iAuditConfigService.getFirstAuditNodeConfig(auditConfig.getId(),tenantId);
        if(auditNodeConfig==null){
            //log.error("审核业务编码["+auditCode+"] 没有配置审核流程的节点,audid_config表的audit_id["+auditConfig.getAuditId()+"],tenant_id["+tenantId+"]");
            return false;
        }
        if(isAuditIng(auditCode, busiId,tenantId)){
            //已经有审核的节点开始审核了
            throw new BusinessException("该流程已经有节点进行审核了，需要等审核流程结束才能重新发起");
        }
        AuditNodeInst preAuditNodeInst= iAuditConfigService.queryAuditIng(auditCode, busiId,tenantId);
        if(preAuditNodeInst!=null){
            AuditNodeInstVer auditNodeInstVer=new AuditNodeInstVer();
            BeanUtil.copyProperties(preAuditNodeInst,auditNodeInstVer);
            auditNodeInstVer.setVerId(preAuditNodeInst.getId());
            //审核流程-已完成
            auditNodeInstVer.setStatus(AuditConsts.Status.FINISH);
            //审核流程-取消，发起了新的审核
            auditNodeInstVer.setAuditResult(AuditConsts.RESULT.CANCEL);
            //把原表的数据移除到ver表
            iAuditNodeInstVerService.save(auditNodeInstVer);
            iAuditNodeInstService.removeById(preAuditNodeInst);
        }
        Integer ruleVersion = iAuditConfigService.getAuditRuleVersionNo(auditNodeConfig.getId());
        //判断第一个节点是否有前置规则,如果不满足，直接返回，不需要处理
//        if(!iAuditConfigService.checkNodePreRule(auditNodeConfig.getId(), busiId, params,ruleVersion,tenantId,true)){
//            return false;
//        }
        AuditNodeInst auditNodeInst=new AuditNodeInst();
        auditNodeInst.setAuditId(auditConfig.getId());
        auditNodeInst.setAuditCode(auditConfig.getAuditCode());
        auditNodeInst.setNodeId(auditNodeConfig.getId());
        auditNodeInst.setNodeName(auditNodeConfig.getNodeName());
        auditNodeInst.setStatus(AuditConsts.Status.AUDITING);
        auditNodeInst.setAuditResult(AuditConsts.RESULT.TO_AUDIT);
        auditNodeInst.setParamsMap(JSONUtil.toJsonStr(params));
        auditNodeInst.setBusiId(busiId);
        auditNodeInst.setLogBusiCode(busiCode.getCode());
        auditNodeInst.setTenantId(tenantId);
        auditNodeInst.setAuditBatch(getBatchId());
        auditNodeInst.setVersion(auditNodeConfig.getVersion());
        auditNodeInst.setRuleVersion(ruleVersion);
        auditNodeInst.setNodeIndex(1);
        iAuditNodeInstService.save(auditNodeInst);
        //节点的回调方法
        // List<Long> auditUserList=iAuditNodeInstService.getUserFromAuditUser(auditNodeInst.getNodeId(), com.youming.youche.conts.AuditConsts.TargetObjType.USER_TYPE);
        // iAuditNodeInstService.nodeAuditCallback(auditNodeInst.getNodeIndex(),busiId, com.youming.youche.conts.AuditConsts.TargetObjType.USER_TYPE, listLongToStr(auditUserList), auditConfig.getNodeCallback());
        return true;
    }
    @Override
    public boolean isAuditIng(String busiCode, Long busiId,Long tenantId) {
        QueryWrapper<AuditNodeInst> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("busi_Id",busiId).
                eq("AUDIT_CODE",busiCode).
                eq("tenant_Id",tenantId).
                eq("status", AuditConsts.Status.AUDITING).
                isNotNull("audit_Date").select("count(*) as nums");
        Map map =iAuditNodeInstService.getMap(queryWrapper);
        Number nums = (Number) map.get("nums");
        if(map != null && nums != null && nums.intValue()>0){
            return true;
        }
        return false;
    }



    public static String getBatchId(){
        return DateUtil.formatDate(new Date(), DateUtil.DATETIME12_FORMAT2);
    }


}
