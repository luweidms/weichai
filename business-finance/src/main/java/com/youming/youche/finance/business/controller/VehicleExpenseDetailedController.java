package com.youming.youche.finance.business.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.IVehicleExpenseDetailedService;
import com.youming.youche.finance.domain.VehicleExpenseDetailed;
import com.youming.youche.finance.dto.CancelVehicleExpenseDto;
import com.youming.youche.finance.dto.CreateVehicleExpenseDto;
import com.youming.youche.finance.dto.GetVehicleExpenseDto;
import com.youming.youche.finance.vo.CreateVehicleExpenseVo;
import com.youming.youche.finance.vo.GetVehicleExpenseVo;
import com.youming.youche.finance.vo.VehicleExpenseVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

    import org.springframework.web.bind.annotation.RestController;
    import com.youming.youche.commons.base.BaseController;

import javax.validation.Valid;
import java.util.List;

/**
* <p>
* 车辆费用明细表 前端控制器
* </p>
* @author liangyan
* @since 2022-04-19
*/
@RestController
@RequestMapping("vehicle/expense/detailed")
public class VehicleExpenseDetailedController extends BaseController<VehicleExpenseDetailed, IVehicleExpenseDetailedService> {
    private  static  final Logger LOGGER = LoggerFactory.getLogger(VehicleExpenseDetailedController.class);

    @DubboReference(version = "1.0.0")
    IVehicleExpenseDetailedService vehicleExpenseDetailedService;
    @Override
    public IVehicleExpenseDetailedService getService() {
        return vehicleExpenseDetailedService;
    }
    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;


    /**
     * 车辆费用新增功能
     * @param domain
     * @return
     */
	@PostMapping({ "saveVehicleExpenseDetailed" })
	public ResponseResult saveVehicleExpenseDetailed(@RequestBody CreateVehicleExpenseDto domain) {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String flag = "";
        flag = vehicleExpenseDetailedService.doSaveOrUpdateNew(domain, accessToken);
        if("Y".equals(flag)){
            return ResponseResult.success("车辆费用新增成功!");
        }else if ("F".equals(flag)) {
            return ResponseResult.success("车辆费用新增失败!");
        } else return ResponseResult.failure("操作失败!");
	}

    /**
     * 车辆费用修改功能
     * @param domain
     * @return
     */
    @PostMapping({ "updateVehicleExpenseDetailed" })
    public ResponseResult updateVehicleExpenseDetailed(@RequestBody CreateVehicleExpenseDto domain) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        vehicleExpenseDetailedService.updateVehicleExpenseDetailed(domain,accessToken);
        return ResponseResult.success("修改成功");
    }


    /**
     * 车辆费用申请详情功能
     * @return
     */
    @GetMapping({ "getVehicleExpense" })
    public ResponseResult getVehicleExpense(String applyNo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        VehicleExpenseVo vehicleExpense = vehicleExpenseDetailedService.getVehicleExpense(applyNo, accessToken);

        return ResponseResult.success(vehicleExpense);
    }

    /**
     * 车辆费用明细列表功能
     * @return
     */
    @GetMapping({ "getVehicleExpenseDetailedList" })
    public ResponseResult getVehicleExpenseDetailedList(GetVehicleExpenseDto domain) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<GetVehicleExpenseVo> vehicleExpenseDetailedList = vehicleExpenseDetailedService.getVehicleExpenseDetailedList(domain, accessToken);

        return ResponseResult.success(vehicleExpenseDetailedList);
    }

    /**
     * 车辆费用明细列表计算申请总金额
     * @return
     */
    @GetMapping({ "getSumApplyAmount" })
    public ResponseResult getSumApplyAmount(GetVehicleExpenseDto domain) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Long sumApplyAmount = vehicleExpenseDetailedService.getSumApplyAmount(domain, accessToken);

        return ResponseResult.success(sumApplyAmount);
    }

    /**
     * 车辆费用明细列表导出功能
     * @return
     */
    @GetMapping({ "vehicleExpenseDetailedExport" })
    public ResponseResult vehicleExpenseDetailedExport(GetVehicleExpenseDto domain) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("车辆费用明细导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            vehicleExpenseDetailedService.vehicleExpenseDetailedList(domain,accessToken,record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("车辆费用明细导出异常" + e);
            return ResponseResult.failure("车辆费用明细导出异常");
        }
    }

    /**
     * 取消车辆费用审核
     * @return
     */
    @PostMapping({ "cancelVehicleExpense" })
    public ResponseResult cancelVehicleExpense(@RequestBody CancelVehicleExpenseDto domain) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String s = vehicleExpenseDetailedService.cancelVehicleExpense(domain, accessToken);
        return ResponseResult.success(s);
    }
}
