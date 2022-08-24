package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IProblemVerOptService;
import com.youming.youche.order.domain.order.ProblemVerOpt;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author liangyan
 * @since 2022-03-29
 */
@RestController
@RequestMapping("order/problemveropt/info")
public class ProblemVerOptController extends BaseController<ProblemVerOpt, IProblemVerOptService> {
    @DubboReference(version = "1.0.0")
    IProblemVerOptService problemVerOptService;
    @Override
    public IProblemVerOptService getService() {
        return problemVerOptService;
    }


    /**
     * 初次审核(需填写处理金额) (弃用)
     *
     * @param problemId        异常ID
     * @param verifyDesc       审核信息
     * @param problemDealPrice 处理金额
     * @throws Exception
     */
    @GetMapping("verifyFirst")
    public ResponseResult verifyFirst(@RequestParam("problemId") Long problemId,
                                      @RequestParam("verifyDesc") String verifyDesc,
                                      @RequestParam("problemDealPrice")String problemDealPrice){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return problemVerOptService.verifyFirst(problemId,verifyDesc,problemDealPrice,accessToken)?
                ResponseResult.success("审核成功"):ResponseResult.failure("审核失敗");
    }

//    /**
//     * 第一次时效审核通过（弃用走公用审核）
//     * @return
//     * @throws Exception
//     */
//    @GetMapping("verifyFirst")
//    public ResponseResult auditAging(@RequestParam("agingId") Long agingId,@RequestParam("verifyDesc") String verifyDesc)throws Exception{
//
//        if(agingId==null||agingId<=0) {
//            throw new BusinessException("未找到时效罚款，请联系客服！");
//        }
//        if(StringUtils.isEmpty(verifyDesc)) {
//            throw new BusinessException("请输入审核意见！");
//        }
//        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//        problemVerOptService.verifyFirst(agingId, verifyDesc,accessToken);
//        return ResponseResult.success("审核成功");
//    }


    /**
     * 添加异常审核
     * liangyan
     * @param problemId
     * @return
     */
    @PostMapping("/saveProblemVerOpt")
    public ResponseResult saveProblemVerOpt(@RequestParam(value = "problemId") Long problemId,
                                            @RequestParam(value = "verifyDesc") String verifyDesc,
                                            @RequestParam(value = "problemDealPrice") String problemDealPrice) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return problemVerOptService.verifyFirst(problemId,verifyDesc,problemDealPrice,accessToken)?
                ResponseResult.success("审核成功"):ResponseResult.failure("审核失敗");

    }

}
