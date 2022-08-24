package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IOrderStatementService;
import com.youming.youche.order.dto.order.IntelligentMatchCustomerDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* <p>
* 客户线路经停点表 前端控制器
* </p>
* @author wuhao
* @since 2022-03-30
*/
@RestController
@RequestMapping("cm/customer/line/subway")
    public class CmCustomerLineSubwayController extends BaseController {

    @Resource
    private IOrderStatementService orderStatementService;

    @Override
    public IBaseService getService() {
        return null;
    }
    /**
     * 获取常用线路
     * @return
     * @throws Exception
     */
    @GetMapping("/queryIntelligentMatchCustomer")
    public ResponseResult queryIntelligentMatchCustomer(boolean isCustomer)throws Exception{
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<IntelligentMatchCustomerDto> list = orderStatementService.queryIntelligentMatchCustomer(isCustomer, accessToken);
        return ResponseResult.success(list);
    }
}
