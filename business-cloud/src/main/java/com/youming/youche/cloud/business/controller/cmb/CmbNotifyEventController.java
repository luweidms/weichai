package com.youming.youche.cloud.business.controller.cmb;

import com.youming.youche.cloud.api.cmb.ICmbTransferService;
import com.youming.youche.commons.response.ResponseResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @ClassName CmbNotifyEventController
 * @Description 通知回调
 * @Author zag
 * @Date 2022/1/11 20:41
 */
@RestController
@RequestMapping("cmb")
public class CmbNotifyEventController {

    @DubboReference(version = "1.0.0")
    ICmbTransferService cmbTransferService;



    @GetMapping
    public String test() throws Exception {
        return "测试接口";
    }

    /**
     * 银行通知回调
     * @author zag
     * @param param
     * @param type
     * @param request
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("/notify")
    public ResponseResult rcvNotify(@RequestBody Map<String, Object> param, @RequestParam String type, HttpServletRequest request) throws Exception {
        String sign = request.getHeader("sign");
        cmbTransferService.notifyCallBack(param, type, sign);
        return ResponseResult.success();
    }
}
