package com.youming.youche.record.business.controller.audit;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.record.api.audit.IAuditSettingService;
import com.youming.youche.record.vo.audit.AuditVo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version:
 * @Title: AuditCommonController 审核公共接口
 * @Package: com.youming.youche.system.business.controller.audit
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/19 17:39
 * @company:
 */
@RestController
@RequestMapping("/sys/auditCommon")
public class AuditCommonController extends BaseController {
    @Override
    public IBaseService getService() {
        return null;
    }


    @DubboReference(version = "1.0.0" )
    IAuditSettingService iAuditSettingService;

    /**
     * 审核操作
     * @return
     * @throws Exception
     */
    @PostMapping("sure")
    public ResponseResult sure(@RequestBody AuditVo auditVo){
        try{
            if(auditVo==null){
                return ResponseResult.failure("参数错误");
            }
            if(AuditConsts.RESULT.FAIL==auditVo.getChooseResult() && StringUtils.isBlank(auditVo.getDesc())){
                return ResponseResult.failure("审核不通过，审核原因需要填写");
            }
            String token = Header.getAuthorization(request.getHeader("Authorization"));
            iAuditSettingService.sure(auditVo,token);
            return ResponseResult.success();
        }catch (Exception e){
            return ResponseResult.failure();
        }
    }

    /**
     * 获取当前待审核的实例的主键的id
     *
     * @param busiCode 审核的业务编码
     * @param busiId   审核配置表主键
     * @return
     * @throws Exception
     */
    @GetMapping("getInstId")
    public ResponseResult getInstId(String busiCode, Long busiId) throws Exception {
        String token = Header.getAuthorization(request.getHeader("Authorization"));
        Long instId=iAuditSettingService.getInstId(busiCode, busiId,token);
        return ResponseResult.success(instId);
    }





}
