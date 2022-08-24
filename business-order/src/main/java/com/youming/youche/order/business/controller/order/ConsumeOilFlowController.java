package com.youming.youche.order.business.controller.order;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.market.dto.youca.ProductNearByOutDto;
import com.youming.youche.order.api.order.IConsumeOilFlowService;
import com.youming.youche.order.domain.order.ConsumeOilFlow;
import com.youming.youche.order.dto.ConsumeOilFlowDetailsOutDto;
import com.youming.youche.order.dto.ConsumeOilFlowDetailsWxOutDto;
import com.youming.youche.order.dto.ConsumeOilFlowDto;
import com.youming.youche.order.dto.ConsumeOilFlowWxOutDto;
import com.youming.youche.order.dto.MarginBalanceDetailsOut;
import com.youming.youche.order.vo.ConsumeOilFlowVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 消费油记录表 前端控制器
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@RestController
@RequestMapping("consume/oil/flow")
public class ConsumeOilFlowController extends BaseController<ConsumeOilFlow, IConsumeOilFlowService> {
    @DubboReference(version = "1.0.0")
    IConsumeOilFlowService consumeOilFlowService;

    @Override
    public IConsumeOilFlowService getService() {
        return consumeOilFlowService;
    }

    /**
     * 微信接口-油站-交易记录-流水
     *
     * @param consumeOilFlowVo
     * @return
     */
    @GetMapping("/getConsumeOilFlowByWx")
    public ResponseResult getConsumeOilFlowByWx(ConsumeOilFlowVo consumeOilFlowVo,
                                                @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        ConsumeOilFlowWxOutDto consumeOilFlowByWx = consumeOilFlowService.getConsumeOilFlowByWx(consumeOilFlowVo, pageNum, pageSize);
        return ResponseResult.success(consumeOilFlowByWx);
    }


    /**
     * 微信接口-油站-交易记录-流水详情
     *
     * @param consumeOilFlowVo
     * @return
     */
    @GetMapping("/getConsumeOilFlowDetailsByWx")
    public ResponseResult getConsumeOilFlowDetailsByWx(ConsumeOilFlowVo consumeOilFlowVo) {
        ConsumeOilFlowDetailsWxOutDto consumeOilFlowDetailsByWx = consumeOilFlowService.getConsumeOilFlowDetailsByWx(consumeOilFlowVo);
        return ResponseResult.success(consumeOilFlowDetailsByWx);
    }

    /**
     * 司机小程序
     * niejeiwei
     * APP接口-优惠加油-加油记录
     * 50000
     *
     * @param vo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getConsumeOilFlow")
    public ResponseResult getConsumeOilFlow(ConsumeOilFlowVo vo,
                                            @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (vo.getUserId() <= 0) {
            throw new BusinessException("用户ID不合法!");
        }
        Page<ConsumeOilFlowDto> consumeOilFlowOut = consumeOilFlowService.getConsumeOilFlowOut(vo, pageNum, pageSize, accessToken);
        return ResponseResult.success(consumeOilFlowOut);
    }

    /**
     * niejiewei
     * 司机小程序
     * APP接口-优惠加油-加油记录-加油详情
     * 50001
     *
     * @param vo
     * @return
     */
    @GetMapping("/getConsumeOilFlowDetails")
    public ResponseResult getConsumeOilFlowDetails(ConsumeOilFlowVo vo) {
        ConsumeOilFlowDetailsOutDto consumeOilFlowDetails = consumeOilFlowService.getConsumeOilFlowDetails(vo.getFlowId());
        return ResponseResult.success(consumeOilFlowDetails);
    }

    /**
     * niejiewei
     * 司机小程序
     * APP接口-优惠加油-加油记录-评
     * 50002
     *
     * @param vo
     * @return
     */
    @PostMapping("/evaluateConsumeOilFlow")
    public ResponseResult evaluateConsumeOilFlow(ConsumeOilFlowVo vo) {
        ConsumeOilFlowDetailsOutDto dto = consumeOilFlowService.evaluateConsumeOilFlow(vo);
        return ResponseResult.success(dto);
    }

    /**
     * niejiewei
     * 司机小程序
     * APP接口-优惠加油-确认支付
     * 50003
     *
     * @param vo
     * @return
     */
    @PostMapping("/confirmPayForOil")
    public ResponseResult confirmPayForOil(ConsumeOilFlowVo vo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ConsumeOilFlowDetailsOutDto dto = consumeOilFlowService.confirmPayForOil(vo, accessToken);
        return ResponseResult.success(dto);
    }

    /**
     * APP接口-预支界面
     * 司机小程序
     * niejiewei
     * 50006
     * @param vo
     * @return
     */
    @GetMapping("/advanceUI")
    public ResponseResult advanceUI(ConsumeOilFlowVo vo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Long aLong = consumeOilFlowService.advanceUI(vo, accessToken);
        return ResponseResult.success(aLong);
    }


    /**
     * niejiewei
     * 司机小程序
     * APP接口-预支-查询预支手续费
     * 50007
     *
     * @param vo
     * @return
     */
    @GetMapping("/getAdvanceFee")
    public ResponseResult getAdvanceFee(ConsumeOilFlowVo vo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Long advanceFee = consumeOilFlowService.getAdvanceFee(vo, accessToken);
        return ResponseResult.success(advanceFee);
    }


    /**
     * niejiewei
     * 司机小程序
     * APP接口-预支-预支
     * 50008
     * @param vo
     * @return
     */
    @PostMapping("/confirmAdvance")
    public ResponseResult confirmAdvance(@RequestBody ConsumeOilFlowVo vo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ConsumeOilFlowDetailsOutDto dto = consumeOilFlowService.confirmAdvance(vo, accessToken);
        return ResponseResult.success(dto.getFlag());
    }

    /**
     * niejiewei
     * 司机小程序
     * APP接口-预支-可预支金额详情
     * 50009
     * @param vo
     * @return
     */
    @GetMapping("/getAdvanceDetails")
    public ResponseResult getAdvanceDetails(ConsumeOilFlowVo vo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<MarginBalanceDetailsOut> advanceDetails = consumeOilFlowService.getAdvanceDetails(vo, accessToken);
        return ResponseResult.success(advanceDetails);
    }

    /**
     * niejiewei
     * 司机小程序
     * APP接口-优惠加油-预支手续费
     * 50010
     * @param
     * @return
     */
    @GetMapping("/getAdvanceFeeByConsuemOil")
    public ResponseResult getAdvanceFeeByConsuemOil() {
//        public Map getAdvanceFeeByConsuemOil(Map<String, String> inParam) throws Exception {
//            Map<String, Object> resultMap = new HashMap<String, Object>();
//            resultMap.put("advanceFee", 0);
//            return resultMap;
        // 老代码如上  表示很疑惑
        ConsumeOilFlowDetailsOutDto  dto = new ConsumeOilFlowDetailsOutDto();
        dto.setAdvanceFee(0L);
        return ResponseResult.success(dto);
    }


    /**
     * 找油网加油_支付
     * niejiewei
     * 司机小程序
     * 50036
     * @param vo
     * @return
     */
    @PostMapping("/payForOrderOil")
    public  ResponseResult payForOrderOil(@RequestBody ConsumeOilFlowVo vo ){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ConsumeOilFlowDetailsOutDto dto = consumeOilFlowService.payForOrderOil(vo, accessToken);
        return  ResponseResult.success(dto);
    }

    /**
     * 油账户列表
     * niejiewei
     * 司机小程序
     * 50040
     * @param vo
     * @return
     */
    @GetMapping("/getOilAccount")
    public  ResponseResult  getOilAccount (ConsumeOilFlowVo vo  ,
                                           @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ConsumeOilFlowDetailsOutDto oilAccount = consumeOilFlowService.getOilAccount(vo, accessToken,pageNum,pageSize);
        return  ResponseResult.success(oilAccount);
    }


    /**
     * niejiewei
     * 司机小程序
     * 油账户列表油站详情
     * 50041
     * @param vo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getOilStationDetails")
    public  ResponseResult getOilStationDetails (ConsumeOilFlowVo vo  ,
                                                 @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<ProductNearByOutDto> oilStationDetails = consumeOilFlowService.getOilStationDetails(vo, accessToken, pageNum, pageSize);
        return  ResponseResult.success(oilStationDetails);
    }

    /**
     * 交易取消(72004)
     * @param tradeId
     * @return
     */
    @PostMapping("/dealCancel")
    public ResponseResult dealCancel(String tradeId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean b = consumeOilFlowService.dealCancel(tradeId,accessToken);
        return ResponseResult.success(b);
    }



}
