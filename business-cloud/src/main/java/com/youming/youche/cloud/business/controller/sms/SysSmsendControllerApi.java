package com.youming.youche.cloud.business.controller.sms;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.api.sms.IMsgNotifySettingService;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.cloud.domin.sms.MsgNotifySetting;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.cloud.dto.sys.SysSmsSendDto;
import com.youming.youche.cloud.dto.sys.UnReadCountDto;
import com.youming.youche.cloud.vo.sys.MessageFlagDto;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cloud/sms")
public class SysSmsendControllerApi extends BaseController<SysSmsSend, ISysSmsSendService> {

    @DubboReference(version = "1.0.0")
    ISysSmsSendService sysSmsSendService;


//    /**
//     * 短信发送
//     */
//    @PostMapping({"sendSms"})
//    public ResponseResult sendSms(SysSmsSend sysSmsSend) {
//     //   String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//        sysSmsSendService.sendSms(sysSmsSend);
//        return ResponseResult.success();
//    }

    /***
     * @Description: 短信通知查询
     * @Author: luwei
     * @Date: 2022/1/22 12:56 下午

     * @return: java.lang.String
     * @Version: 1.0
     **/
    @PostMapping({"getMsgNotifySetting"})
    public ResponseResult getMsgNotifySetting(@RequestParam("tenantId") Long tenantId) {
        return ResponseResult.success(sysSmsSendService.getMsgNotifySettingByTenantId(tenantId));
    }

    /***
     * @Description: 短信通知
     * @Author: luwei
     * @Date: 2022/1/22 1:24 下午

     * @return: void
     * @Version: 1.0
     **/
    @PutMapping({"updateMsgNotifySetting"})
    public ResponseResult updateMsgNotifySetting(@RequestBody MsgNotifySetting msgNotifySetting) {
        if (sysSmsSendService.updateMsgNotifySetting(msgNotifySetting)) {
            return ResponseResult.success();
        } else {
            return ResponseResult.failure();
        }
    }

    @Override
    public ISysSmsSendService getService() {
        return sysSmsSendService;
    }

    /**
     * 接口编号：11011
     * 接口入参：
     * smsId	         消息记录编号
     * 接口出参：
     * title	标题
     * smsContent	          消息内容
     * smsTenantName	      来源租户名称，如果是平台的话就是同心智行
     * createDate	         时间	 	格式：2015-05-06
     * 系统提醒详情页面
     */
    @GetMapping("querySystemNotify")
    public ResponseResult querySystemNotify(Long smsId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        return ResponseResult.success(
                sysSmsSendService.querySystemNotify(smsId, accessToken)
        );
    }

}
