package com.youming.youche.order.business.controller.order;


import cn.hutool.core.util.StrUtil;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IOrderDriverSwitchInfoService;
import com.youming.youche.order.domain.order.OrderDriverSwitchInfo;
import com.youming.youche.order.dto.OrderDriverSwitchInfoOutDto;
import com.youming.youche.order.dto.order.QueryOrderDriverSwitchDto;
import com.youming.youche.order.vo.ScanOrderDriverSwitchVo;
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
 * @author CaoYaJie
 * @since 2022-03-24
 */
@RestController
@RequestMapping("order/driver/switchInfo")
public class OrderDriverSwitchInfoController extends BaseController<OrderDriverSwitchInfo, IOrderDriverSwitchInfoService> {

    @DubboReference(version = "1.0.0")
    IOrderDriverSwitchInfoService orderDriverSwitchInfoService;


    @Override
    public IOrderDriverSwitchInfoService getService() {
        return orderDriverSwitchInfoService;
    }

    /**
     * @author 卢威
     * 切换司机分页列表查询
     * @date 16:48 2022/2/25
     * @Param []
     */
    @GetMapping("/doQuery")
    public ResponseResult doQuery(@RequestParam(value = "orderId", required = false) Long orderId,
                                  @RequestParam(value = "receiveUserName", required = false) String receiveUserName,
                                  @RequestParam(value = "formerUserName", required = false) String formerUserName,
                                  @RequestParam(value = "originUserName", required = false) String originUserName,
                                  @RequestParam(value = "state", defaultValue = "-1",required = false) Integer state,
                                  @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize) {
        return ResponseResult.success(orderDriverSwitchInfoService.getOrderDriverSwitchInfos(orderId, formerUserName, receiveUserName, originUserName, state, pageNum, pageSize));
    }

    /**
     * @author 卢威
     * 切换司机详情查询
     * @date 16:48 2022/2/25
     * @Param []
     */
    @GetMapping("/queryDetails")
    public ResponseResult queryDetails(@RequestParam("switchId") String switchId) {
        if(StrUtil.isEmpty(switchId)){
            throw new BusinessException("id不能为空");
        }
        return ResponseResult.success(orderDriverSwitchInfoService.queryDetails(Long.valueOf(switchId)));
    }

    /**
     * @author 卢威 30069
     * 切换司机手机号查询
     * @date 16:48 2022/2/25
     * @Param []
     */
    @GetMapping("/getDriverByPhone")
    public ResponseResult getDriverByPhone(@RequestParam("billId") String billId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(orderDriverSwitchInfoService.getDriverByPhone(billId, accessToken));
    }

    /**
     * @author 卢威
     * 切换司机
     * @date 16:48 2022/2/25
     * @Param []
     */
    @PostMapping("/doDriverSwitch")
    public ResponseResult doDriverSwitch(@RequestParam(value = "orderId", required = false) String orderId,
                                         @RequestParam(value = "receiveUserId", required = false) String receiveUserId,
                                         @RequestParam(value = "recevieMileage", required = false) String recevieMileage,
                                         @RequestParam(value = "mileageFileId", required = false) String mileageFileId,
                                         @RequestParam(value = "mileageFileUrl", required = false) String mileageFileUrl,
                                         @RequestParam(value = "receiveRemark", required = false) String receiveRemark) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if(StrUtil.isEmpty(orderId)){
            throw new BusinessException("订单id不能为空");
        }
        if(StrUtil.isEmpty(receiveUserId)){
            throw new BusinessException("司机用户id不能为空");
        }
        orderDriverSwitchInfoService.doDriverSwitch(Long.valueOf(orderId), Long.valueOf(receiveUserId), recevieMileage, mileageFileId, mileageFileUrl, receiveRemark, accessToken);
        return ResponseResult.success("切换成功");
    }

    /**
     * 查询司机切换列表  接口编号：30101
     * @param orderId
     */
    @GetMapping("queryOrderDriverSwitch")
    public ResponseResult queryOrderDriverSwitch(Long orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        QueryOrderDriverSwitchDto queryOrderDriverSwitchDto = orderDriverSwitchInfoService.queryOrderDriverSwitch(orderId, accessToken);
        return ResponseResult.success(queryOrderDriverSwitchDto);
    }

    /**
     * App接口-填写切换司机信息 30104
     */
    @PostMapping("formerSwithcInfo")
    public ResponseResult formerSwithcInfo(Long orderId, Long formerUserId, Long formerMileage, String formerMileageFileId, String formerMileageFileUrl, String formerRemark, Long receiveUserId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        orderDriverSwitchInfoService.formerSwithcInfo(orderId, formerUserId, formerMileage, formerMileageFileId, formerMileageFileUrl, formerRemark, receiveUserId, accessToken);
        return ResponseResult.success();
    }


    /**
     * App接口-查询司机切换详情(30103)
     * @param switchId
     * @return
     */
    @GetMapping("/queryDataById")
    public ResponseResult queryDataById(Long switchId){
        OrderDriverSwitchInfoOutDto outDto = orderDriverSwitchInfoService.queryDataById(switchId);
        return ResponseResult.success(outDto);
    }

    /**
     *  确认司机切换(30105)
     * @param switchId
     * @param receiveMileage
     * @param receiveMileageId
     * @param receiveMileageUrl
     * @param receiveRemark
     * @return
     */
    @PostMapping("/confirmOrderDriverSwitch")
    public ResponseResult confirmOrderDriverSwitch(Long switchId,String receiveMileage,String receiveMileageId,String receiveMileageUrl,String receiveRemark){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean b = orderDriverSwitchInfoService.confirmOrderDriverSwitch(switchId, Long.valueOf(receiveMileage), receiveMileageId, receiveMileageUrl, receiveRemark, accessToken);
        return ResponseResult.success(b);
    }

    /**
     * 拒绝司机切换(30106)
     * @param switchId
     * @param state
     * @return
     */
    @PostMapping("/refuseOrderDriverSwitch")
    public ResponseResult refuseOrderDriverSwitch(Long switchId,String state){
        boolean b = orderDriverSwitchInfoService.refuseOrderDriverSwitch(switchId, state);
        return ResponseResult.success(b);
    }

    /**
     * 扫码切换司机(30108)
     * @param scanOrderDriverSwitchVo
     * @return
     */
    @PostMapping("scanOrderDriverSwitch")
    public ResponseResult scanOrderDriverSwitch(ScanOrderDriverSwitchVo scanOrderDriverSwitchVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean b = orderDriverSwitchInfoService.scanOrderDriverSwitch(scanOrderDriverSwitchVo, accessToken);
        return ResponseResult.success(b);
    }




}
