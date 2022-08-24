package com.youming.youche.record.business.controller.apply;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.record.api.apply.IApplyRecordService;
import com.youming.youche.record.domain.apply.ApplyRecord;
import com.youming.youche.record.dto.VehicleApplyRecordDto;
import com.youming.youche.record.vo.PriceAuditVo;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.constant.AuditConsts;
import com.youming.youche.system.dto.AuditCallbackDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Date:2021/12/23
 */
@RestController
@RequestMapping("apply/record")
public class ApplyRecordController extends BaseController<ApplyRecord, IApplyRecordService> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplyRecordController.class);

    @DubboReference(version = "1.0.0")
    IApplyRecordService iApplyRecordService;

    @DubboReference(version = "1.0.0")
    IAuditSettingService iAuditSettingService;


    @Override
    public IApplyRecordService getService() {
        return iApplyRecordService;
    }

    /**
     * 14512
     * 查看车辆邀请详情-小程序接口
     */
    @GetMapping("getVehicleApplyRecordForMiniProgram")
    public ResponseResult getVehicleApplyRecordForMiniProgram(Long applyRecordId) {
        if (applyRecordId == null || applyRecordId < 0) {
            throw new BusinessException("邀请记录主键错误！");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        VehicleApplyRecordDto vehicleApplyRecordForMiniProgram = iApplyRecordService.getVehicleApplyRecordForMiniProgram(applyRecordId, accessToken);
        return ResponseResult.success(vehicleApplyRecordForMiniProgram);
    }

    /**
     * 接口编号：14000
     * 接口入参：
     * objId	         申请编号
     * 接口出参：
     * busiId  主键id，处理邀请时回调用
     * tenantName	          申请车队
     * tenantLinkPhone	      车队电话
     * plateNumber          转移车辆  applyType为2的时候才有值
     * createDate	         申请时间	 	格式：2015-05-06
     * applyTypeName       申请类型
     * applyRemark         申请说明
     * applyFileUrl            申请附件,小图
     * state              状态0 : 处理中  1 : 已通过 2：被驳回 3：平台仲裁中 4：平台仲裁通过 5：平台仲裁未通过  处理中的需要可以审核
     * stateName              状态中文
     * applyType          类型，1 司机 2 车辆  司机车辆最后调用的审核方法不一样
     * vehicles[           车辆列表 applyType为1的时候，需要处理
     * plateNumber   车牌
     * vehicleTypeString   车辆类型
     * checked      查看详情的时候该车是否已经被选中
     * ]
     * <p>
     * 查询申请详情包括司机和车辆
     */
    @GetMapping("getApplyRecord")
    public ResponseResult getApplyRecord(Long objId) {

        return ResponseResult.success(
                iApplyRecordService.getApplyRecord(objId)
        );

    }
}
