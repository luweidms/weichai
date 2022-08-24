package com.youming.youche.system.business.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.IServiceProviderBillService;
import com.youming.youche.system.domain.ServiceProviderBill;
import com.youming.youche.system.dto.BillRecordsDto;
import com.youming.youche.system.dto.ServiceBillDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* <p>
* 服务商账单表 前端控制器
* </p>
* @author liangyan
* @since 2022-03-17
*/
@RestController
@RequestMapping("service/provider/bill")
public class ServiceProviderBillController extends BaseController<ServiceProviderBill, IServiceProviderBillService> {

    @DubboReference(version = "1.0.0")
    IServiceProviderBillService serviceProviderBillService;
    @Override
    public IServiceProviderBillService getService() {
        return serviceProviderBillService;
    }


    /**
     * 通过条件查询服务商账单列表查询
     * @param serviceProviderName 服务商名称
     * @param billRecordsNo 业务单号
     * @param serviceProviderType 服务商类型 1.油站、2.维修、3.etc供应商
     * @param paymentStatus 打款状态 0：未打款:1：待确认:2：打款中:3：已打款
     * @return
     */
    @GetMapping("getServiceProviderBillList")
    public ResponseResult getServiceProviderBillList(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                     @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
                                                     String serviceProviderName,
                                                      String billRecordsNo,
                                                      Integer serviceProviderType,
                                                      Integer paymentStatus,String billNo){
        try {

            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            PageInfo<ServiceProviderBill> serviceProviderBillList = serviceProviderBillService.getServiceProviderBillList(pageNum, pageSize, serviceProviderName, billRecordsNo, serviceProviderType, paymentStatus,billNo, accessToken);

            return ResponseResult.success(serviceProviderBillList);
        } catch (Exception e) {
            return ResponseResult.failure(e.getMessage());
        }
    }

    /**
     * 账单记录
     * @param serviceProviderType
     * @param billNo
     * @param tenantId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getServiceProviderBillRecords")
    public  ResponseResult getServiceProviderBillRecords(Integer serviceProviderType,String billNo,Long tenantId,
                                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<BillRecordsDto> serviceProviderBillRecords = serviceProviderBillService.getServiceProviderBillRecords(serviceProviderType,billNo, tenantId, pageNum, pageSize, accessToken);
        return ResponseResult.success(serviceProviderBillRecords);
    }


    /**
     * 账单结算信息
     * @param billNo
     * @return
     */
    @GetMapping("/getServiceProviderBillInfo")
    public ResponseResult getServiceProviderBillInfo(Long billNo){
        ServiceBillDto serviceProviderBillInfo = serviceProviderBillService.getServiceProviderBillInfo(billNo);
        return  ResponseResult.success(serviceProviderBillInfo);
    }


    /**
     * 账单结算
     * @param realityBillAmout
     * @param billNo
     * @param tenantId
     * @return
     */
    @PostMapping("/ServiceProviderBillBalance")
    ResponseResult ServiceProviderBillBalance(Double realityBillAmout, String billNo, Long tenantId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        int i = serviceProviderBillService.ServiceProviderBillBalance(realityBillAmout, billNo, tenantId, accessToken);
        return i>0?ResponseResult.success("结算成功"):ResponseResult.failure("结算失败");
    }
}
