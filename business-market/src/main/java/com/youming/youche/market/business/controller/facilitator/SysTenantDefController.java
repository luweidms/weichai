//package com.youming.youche.market.business.controller.facilitator;
//
//
//import com.youming.youche.commons.base.BaseController;
//import com.youming.youche.commons.response.ResponseResult;
//import com.youming.youche.market.api.facilitator.ISysTenantDefService;
//import com.youming.youche.market.domain.facilitator.SysTenantDef;
//import com.youming.youche.market.vo.facilitator.SysTenantOutVo;
//import com.youming.youche.system.api.ISysTenantDefService;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * <p>
// * 车队表 前端控制器
// * </p>
// *
// * @author CaoYaJie
// * @since 2022-02-07
// */
//@RestController
//@RequestMapping("/facilitator/systenantdef/data/info")
//public class SysTenantDefController extends BaseController<SysTenantDef, ISysTenantDefService> {
//    @DubboReference(version = "1.0.0")
//    ISysTenantDefService sysTenantDefService;
//    @Override
//    public ISysTenantDefService getService() {
//        return sysTenantDefService;
//    }
//
//    @GetMapping("getTenantById")
//    public ResponseResult getTenantById(@RequestParam("tenantId") Long tenantId){
//        try {
//            SysTenantOutVo tenantById = sysTenantDefService.getTenantById(tenantId);
//            return ResponseResult.success(tenantById);
//        } catch (Exception e) {
//            return ResponseResult.failure("查询异常");
//        }
//    }
//}
