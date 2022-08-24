package com.youming.youche.finance.business.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.IAdvanceExpireInfoService;
import com.youming.youche.finance.domain.AdvanceExpireInfo;
import com.youming.youche.finance.dto.AdvanceExpireOutDto;
import com.youming.youche.order.vo.AdvanceExpireOutVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



/**
* <p>
* 手动到期沉淀表 前端控制器
* </p>
* @author luona
* @since 2022-04-13
*/
@RestController
@RequestMapping("advance/expire/info")
public class AdvanceExpireInfoController extends BaseController<AdvanceExpireInfo, IAdvanceExpireInfoService> {
    @DubboReference(version = "1.0.0")
    IAdvanceExpireInfoService advanceExpireInfoService;

    @Override
    public IAdvanceExpireInfoService getService() {
        return null;
    }


    /**
     * 即将到期列表，包含查询手动到期
     * 订单尾款到期、油到期、维修到期、全部
     * @param advanceExpireOutVo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/doQuery")
    public ResponseResult doQuery( AdvanceExpireOutVo advanceExpireOutVo,
                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<AdvanceExpireOutDto> orderLimitPage = advanceExpireInfoService.queryUndueExpires(advanceExpireOutVo, pageNum, pageSize, accessToken);
        return ResponseResult.success(orderLimitPage);
    }

    /**
     * 手动到期操作
     * @param flowId
     * @param userId
     * @param userType
     * @param signType
     * @return
     */
    @PostMapping("/doExpire")
    public ResponseResult doExpire(Long flowId,Long userId,Integer userType,String signType){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean b = advanceExpireInfoService.doExpire(flowId, userId, userType, signType, accessToken);
        return b?ResponseResult.success("立即到期成功"):ResponseResult.success("立即到期失败");
    }
}
