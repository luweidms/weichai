package com.youming.youche.finance.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.IZhangPingRecordService;
import com.youming.youche.finance.domain.ZhangPingRecord;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
* <p>
*  前端控制器
* </p>
* @author WuHao
* @since 2022-04-13
*/
    @RestController
@RequestMapping("zhang/ping/record")
        public class ZhangPingRecordController extends BaseController<ZhangPingRecord, IZhangPingRecordService> {

    @DubboReference(version = "1.0.0")
    private IZhangPingRecordService iZhangPingRecordService;

    @Override
    public IZhangPingRecordService getService() {
        return iZhangPingRecordService;
    }

    /**
     * 强制帐平
     * @return
     * @throws Exception
     */
    @PostMapping("saveForceZhangPing")
    public ResponseResult saveForceZhangPing(Long userId, String zhangPingType, Integer userType){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iZhangPingRecordService.saveForceZhangPingNew(userId,zhangPingType,userType,accessToken);
        return ResponseResult.success("平帐操作成功");
    }

}
