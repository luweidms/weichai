package com.youming.youche.market.business.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.market.api.user.IUserRepairInfoService;
import com.youming.youche.market.domain.facilitator.ServiceSerial;
import com.youming.youche.market.domain.user.UserRepairInfo;
import com.youming.youche.market.dto.facilitator.RepairInfoDto;
import com.youming.youche.market.dto.facilitator.UserRepairInfoOutDto;
import com.youming.youche.market.dto.facilitator.UserRepairInfoVx;
import com.youming.youche.market.dto.user.UserRepairInfoDetailDto;
import com.youming.youche.market.dto.user.UserRepairInfoDto;
import com.youming.youche.market.vo.facilitator.ServiceVo;
import com.youming.youche.market.vo.facilitator.UserRepairInfoOutVo;
import com.youming.youche.market.vo.user.UserRepairInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 维修记录
 *
 * @author hzx
 * @date 2022/3/12 11:16
 */
@RestController
@RequestMapping("user/repair/info")
public class UserRepairInfoController extends BaseController<UserRepairInfo, IUserRepairInfoService> {

    @DubboReference(version = "1.0.0")
    private IUserRepairInfoService iUserRepairInfoService;

    @Override
    public IUserRepairInfoService getService() {
        return iUserRepairInfoService;
    }

    /**
     * 查询维修保养
     *
     * @param userRepairInfoVo epairCode 维修单号
     *                         serviceName 服务商名
     *                         productName 产品名称
     *                         plateNumber 车牌号码
     *                         driverName 司机名称
     *                         driverBill 司机手机
     *                         repairDateBegin  开始时间
     *                         repairDateEnd 结束时间
     *                         state 审核状态
     *                         maintenanceType 维保类型
     *                         to do 待我处理
     * @param pageNum          分页参数
     * @param pageSize         分页参数
     */
    @GetMapping("queryUserRepairAuth")
    public ResponseResult queryUserRepairAuth(UserRepairInfoVo userRepairInfoVo,
                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<UserRepairInfoVo> page = new Page<UserRepairInfoVo>(pageNum, pageSize);
        Page<UserRepairInfoDto> userRepairInfoDtoPage = iUserRepairInfoService.queryUserRepairAuth(page, userRepairInfoVo, accessToken);

        return ResponseResult.success(userRepairInfoDtoPage);

    }

    /**
     * 维修详情
     *
     * @param repairId 维修主键id
     */
    @PostMapping("getUserRepairDetail")
    public ResponseResult getUserRepairDetail(@RequestParam("repairId") Long repairId) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        UserRepairInfoDetailDto userRepairDetail = iUserRepairInfoService.getUserRepairDetail(repairId, accessToken);
        return ResponseResult.success(userRepairDetail);

    }

    /**
     * 校验是否存在交易待确认收款的
     *
     * @return
     */
    @GetMapping("checkIsCash")
    public ResponseResult checkIsCash(){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(
                iUserRepairInfoService.checkIsCash(accessToken)
        );
    }

    /**
     * 接口编码：40021
     * 维修交易
     * @param inParam
     * @return
     * @throws Exception
     */
    @GetMapping("queryRepairInfo")
    public ResponseResult queryRepairInfo(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                          RepairInfoDto inParam){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(
                iUserRepairInfoService.queryRepairInfo(inParam,accessToken,pageSize,pageNum)
        );
    }
    /**
     * 微信保存维修单
     * @param userRepairInfoVx
     * @return
     * @throws Exception
     */
    @PostMapping("doSaveUserRepairInfo")
    public ResponseResult doSaveUserRepairInfo(@RequestBody UserRepairInfoVx userRepairInfoVx){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(
                iUserRepairInfoService.doSaveUserRepairInfo(userRepairInfoVx,accessToken)
        );
    }


    /**
     * 40023
     * 司机确认接口
     * niejiewei
     * @param vo
     * @return
     */
    @PostMapping("/confirmPayRepairInfo")
    public  ResponseResult confirmPayRepairInfo(@RequestBody UserRepairInfoOutVo vo ){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        UserRepairInfoOutDto userRepairInfoOutDto = iUserRepairInfoService.confirmPayRepairInfo(vo, accessToken);
        return ResponseResult.success(userRepairInfoOutDto);
    }
    /**
     * 接口编码：40031
     * 现金收款
     * @param repairId 维修主键id
     */
    @PostMapping("cashReceipt")
    public ResponseResult cashReceipt(Long repairId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if(repairId < 0){
            throw new BusinessException("请选择维修单！");
        }
        String s = iUserRepairInfoService.cashReceipt(repairId,accessToken);
        return ResponseResult.success(s);
    }

    /**
     * 查询app交易记录详情
     * niejiewei
     * 40026
     * @param vo
     * @return
     */
    @GetMapping("/getRepairDetail")
    public  ResponseResult getRepairDetail(UserRepairInfoOutVo vo ){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        UserRepairInfoOutDto repairDetail = iUserRepairInfoService.getRepairDetail(vo, accessToken);
        return ResponseResult.success(repairDetail);
    }

    /**
     * @param serviceUserId 服务商ID
     * @param startTime     交易开始时间
     * @param endTime       交易结束时间
     * @param serialNumber  账单流水号
     */
    @GetMapping("getUserRepairInfoList")
    public ResponseResult getUserRepairInfoList(Long serviceUserId,String startTime,
                                                Integer serviceType,String endTime,String serialNumber){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<ServiceSerial> repairInfoList = iUserRepairInfoService.getUserRepairInfoList(serviceUserId, startTime, endTime, serviceType,serialNumber,accessToken);
        return ResponseResult.success(repairInfoList);
    }

}
