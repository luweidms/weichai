package com.youming.youche.system.business.controller.audit;

import cn.hutool.core.util.StrUtil;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.domain.audit.AuditNodeUser;
import com.youming.youche.system.vo.AuditRuleNodeVo;
import com.youming.youche.system.vo.AuditVo;
import com.youming.youche.system.vo.SaveNodeAuditStaffVo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @version:
 * @Title: AuditSettingController
 * @Package: com.youming.youche.system.business.controller.audit
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/10 9:36
 * @company:
 */
@RestController
@RequestMapping("/system/auditSetting")
public class AuditSettingController extends BaseController {

    /**
     * 基础数据审核
     */
    private static final int BASIC_BUSI_TYPE = 1;
    /**
     * 业务数据审核
     */
    private static final int BUSI_TYPE = 2;

    @DubboReference(version = "1.0.0")
    IAuditSettingService iAuditSettingService;

    @Override
    public IBaseService getService() {
        return null;
    }

    /**
     * 获取基础资料审核流程设置
     *
     * @return
     * @throws Exception
     */
    @GetMapping({"getBaseDataAudit"})
    public ResponseResult queryBaseDataAudit() {
        try {
            String token = Header.getAuthorization(request.getHeader("Authorization"));
            return iAuditSettingService.queryAuditFlow(BASIC_BUSI_TYPE, token);
        } catch (Exception e) {
            return ResponseResult.failure();
        }
    }

    /**
     * 获取业务资料审核流程设置
     *
     * @return
     * @throws Exception
     */
    @GetMapping({"getBusiDataAudit"})
    public ResponseResult queryBusiDataAudit() {
        try {
            String token = Header.getAuthorization(request.getHeader("Authorization"));
            return iAuditSettingService.queryAuditFlow(BUSI_TYPE, token);
        } catch (Exception e) {
            return ResponseResult.failure();
        }
    }

    /**
     * 获取审核人列表
     *
     * @return
     * @throws Exception
     */
    @GetMapping({"getAuditStaffs"})
    public ResponseResult queryAuditStaffs(Long nodeId) {
        try {
            if (nodeId == null || nodeId <= 0) {
                throw new BusinessException("流程节点ID不能为空！");
            }
            List<AuditNodeUser> auditStaffs = iAuditSettingService.getAuditNodeUserList(nodeId);
            return ResponseResult.success(auditStaffs);
        } catch (Exception e) {
            return ResponseResult.failure();
        }
    }

    /**
     * 退回发起人
     *
     * @return
     * @throws Exception
     */
    @PostMapping({"rollbackOriginator"})
    public ResponseResult rollbackOriginator(Integer flag, String auditCode) {
        if (flag == null || StrUtil.isEmpty(auditCode)) {
            throw new BusinessException("业务编码不能为空");
        }
        String token = Header.getAuthorization(request.getHeader("Authorization"));
        iAuditSettingService.rollbackOriginator(flag, auditCode, token);
        return ResponseResult.success("设置成功");
    }


    /**
     * 保存流程节点及对应的审核人
     *
     * @return
     * @throws Exception
     */
    @PostMapping("saveNodeAuditStaff")
    public ResponseResult saveNodeAuditStaff(@RequestBody SaveNodeAuditStaffVo saveNodeAuditStaffVo) {
        try {
            if (saveNodeAuditStaffVo == null) {
                throw new BusinessException("数据为空！");
            }
            if (StringUtils.isEmpty(saveNodeAuditStaffVo.getAuditCode())) {
                throw new BusinessException("业务编码不能为空！");
            }
            if ((iAuditSettingService.getSysStaticData("AUDIT_CODE", saveNodeAuditStaffVo.getAuditCode())).getCodeName() == null) {
                throw new BusinessException("业务编码不存在！");
            }
            if (saveNodeAuditStaffVo.getTargetObjType() == null || 1 != saveNodeAuditStaffVo.getTargetObjType()
                    && 0 != saveNodeAuditStaffVo.getTargetObjType() && 2 != saveNodeAuditStaffVo.getTargetObjType()) {
                throw new BusinessException("审核对象类型不存在！");
            }
            if (StringUtils.isEmpty(saveNodeAuditStaffVo.getTargetObjId())) {
                throw new BusinessException("请选择该审核节点的审核人员！");
            }
            String token = Header.getAuthorization(request.getHeader("Authorization"));
            iAuditSettingService.saveOrUpdateAudit(saveNodeAuditStaffVo, token);
            return ResponseResult.success();
        } catch (Exception e) {
            return ResponseResult.failure();
        }
    }


    /**
     * 移除审核流程节点
     *
     * @return
     * @throws Exception
     */
    @PostMapping("removeNode")
    public ResponseResult removeNode(Long nodeId) {
        try {
            if (nodeId == null || nodeId <= 0) {
                throw new BusinessException("该流程节点不存在！");
            }
            String token = Header.getAuthorization(request.getHeader("Authorization"));
            String message = iAuditSettingService.delAuditNode(nodeId, token);
            if (message.equals("Y")) {
                return ResponseResult.success();
            }
            return ResponseResult.failure(message);
        } catch (Exception e) {
            return ResponseResult.failure();
        }

    }

    /**
     * 获取订单价格审核规则
     *
     * @return
     * @throws Exception
     */
    @GetMapping("getAuditRule")
    public ResponseResult getAuditRule() {
        try {
            String token = Header.getAuthorization(request.getHeader("Authorization"));
            Map<String, Object> resultMap = iAuditSettingService.getAuditRule(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE, token);
            return ResponseResult.success(resultMap);
        } catch (Exception e) {
            return ResponseResult.failure();
        }
    }

    /**
     * 保存订单价格审核规则
     *
     * @return
     * @throws Exception
     */
    @PostMapping("saveAuditRule")
    public ResponseResult saveAuditRule(@RequestBody AuditRuleNodeVo auditRuleNodeVo) {
        try {
            String token = Header.getAuthorization(request.getHeader("Authorization"));
            iAuditSettingService.saveOrUpdateRuleNode(auditRuleNodeVo, token);
            return ResponseResult.success();
        } catch (Exception e) {
            return ResponseResult.failure();
        }
    }

    @PostMapping("sure")
    public ResponseResult sure(@RequestBody AuditVo auditVo) {
        try {
            if (auditVo == null) {
                return ResponseResult.failure("参数错误");
            }
            if (AuditConsts.RESULT.FAIL == auditVo.getChooseResult() && StringUtils.isBlank(auditVo.getDesc())) {
                return ResponseResult.failure("审核不通过，审核原因需要填写");
            }
            String token = Header.getAuthorization(request.getHeader("Authorization"));
            iAuditSettingService.sure(auditVo, token, false);
            return ResponseResult.success();
        } catch (Exception e) {
            return ResponseResult.failure();
        }
    }


}
