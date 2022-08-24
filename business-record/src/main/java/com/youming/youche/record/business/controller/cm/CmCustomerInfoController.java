package com.youming.youche.record.business.controller.cm;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.record.api.cm.ICmCustomerInfoService;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.domain.cm.CmCustomerInfo;
import com.youming.youche.record.dto.BackUserDto;
import com.youming.youche.record.vo.cm.CmCustomerInfoVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExcelParse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author 向子俊
 * @version 1.0.0
 * @ClassName CmCustomerInfoController.java
 * @Description TODO
 * @createTime 2021年12月22日 10:33:00
 */
@RestController
@RequestMapping("/customer")
public class CmCustomerInfoController extends BaseController<CmCustomerInfo, ICmCustomerInfoService> {
    @DubboReference(version = "1.0.0")
    ICmCustomerInfoService customerInfoService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CmCustomerInfoController.class);

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public ICmCustomerInfoService getService() {
        return customerInfoService;
    }

    /**
     * 查询客户列表信息
     */
    @GetMapping("/get/list")
    public ResponseResult getCustomerList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                          CmCustomerInfoVo customerInfo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page<CmCustomerInfo> customerInfoPage = new Page<>(pageNum, pageSize);
            Page<CmCustomerInfo> customerList = customerInfoService.findCustomerList(customerInfoPage, customerInfo, accessToken);
            return ResponseResult.success(customerList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询失败");
        }

    }

    /**
     * @return java.lang.String
     * @author 向子俊
     * @Description //TODO 根据id查询客户档案
     * @date 16:34 2021/12/22 0022
     * @Param [id]
     */
    @GetMapping("/get/customer")
    public ResponseResult getCustomerById(Long customerId, @RequestParam(value = "isEdit", defaultValue = "0") Integer isEdit) {

        if (customerId == null) {
            throw new BusinessException("主键id不能为空");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List customerById = customerInfoService.getCustomerInfo(customerId, isEdit, accessToken);
        return ResponseResult.success(customerById);

    }

    /*
     * @author 向子俊
     * @Description //TODO 新增/修改客户档案
     * @date 19:10 2022/1/22 0022
     * @Param [customerInfo]
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("/save/update/customer")
    public ResponseResult saveOrUpdateCustomer(@RequestBody CmCustomerInfoVo customerInfo) throws Exception {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String customer = "";
        if (customerInfo != null) {
            if (StringUtils.isEmpty(customerInfo.getReciveAddress())) {
                throw new BusinessException("亲，请输入回单地址！");
            }
            if (customerInfo.getReciveProvinceId() < 0) {
                throw new BusinessException("亲，请输入回单地址！");
            }
            if (customerInfo.getReciveCityId() < 0) {
                throw new BusinessException("亲，请输入回单地址！");
            }
            if (StringUtils.isEmpty(customerInfo.getLineName())) {
                throw new BusinessException("亲，请输入联系人！");
            }
            if (StringUtils.isEmpty(customerInfo.getLinePhone())) {
                throw new BusinessException("亲，请输入联系手机！");
            }
            if (StringUtils.isEmpty(customerInfo.getCompanyName())) {
                throw new BusinessException("亲，请输入公司全称！");
            }
            if (StringUtils.isEmpty(customerInfo.getCustomerName())) {
                throw new BusinessException("亲，请输入客户简称！");
            }
            if (StringUtils.isEmpty(customerInfo.getLookupName())) {
                throw new BusinessException("亲，请输入发票抬头！");
            }
            if (customerInfo.getOddWay() < 0) {
                throw new BusinessException("亲，请输入正确的回单类型！");
            }
            if (customerInfo.getPayWay() < 0) {
                throw new BusinessException("亲，请选择结算方式！");
            }
            if (customerInfo.getPayWay() == 1 || customerInfo.getPayWay() == 2) {
                if (StringUtils.isEmpty(customerInfo.getReciveTime())) {
                    throw new BusinessException("亲，请输入回单期限！");
                }
                    /*if (customerInfo.getReconciliationTime() == null) {
                        throw new BusinessException("亲，请输入正确的对账期限！");
                    }
                    if (customerInfo.getInvoiceTime() == null) {
                        throw new BusinessException("亲，请输入正确的开票期限！");
                    }*/
                if (customerInfo.getPayWay() == 2 && (StringUtils.isEmpty(customerInfo.getCollectionTime()))) {
                    throw new BusinessException("亲，请输入收款期限！");
                }
                if (customerInfo.getPayWay() == 1) {
                    customerInfo.setCollectionTime(null);
                }
            } else {
                if (customerInfo.getReciveMonth() <= 0 || customerInfo.getReciveMonth() > 12) {
                    throw new BusinessException("回单期限月数不能为空或不正确，请重填！");
                }
                if (customerInfo.getReciveDay() <= 0 || customerInfo.getReciveDay() > 31) {
                    throw new BusinessException("回单期限日期不能为空或不正确，请重选！");
                }
                if (customerInfo.getReconciliationMonth() > 12) {
                    throw new BusinessException("对账期限月数不正确，请重填！");
                }
                if (customerInfo.getReconciliationMonth() > 0 && customerInfo.getReconciliationMonth() < 12 && (customerInfo.getReconciliationDay() <= 0 || customerInfo.getReconciliationDay() > 31)) {
                    throw new BusinessException("对账期限日期不能为空或不正确，请重选！");
                }
                if (customerInfo.getInvoiceMonth() > 12) {
                    throw new BusinessException("开票期限月数不正确，请重填！");
                }
                if (customerInfo.getInvoiceMonth() > 0 && customerInfo.getInvoiceMonth() < 12 && (customerInfo.getInvoiceDay() <= 0 || customerInfo.getInvoiceDay() > 31)) {
                    throw new BusinessException("开票期限日期不能为空或不正确，请重选！");
                }
                if (customerInfo.getCollectionMonth() <= 0 || customerInfo.getCollectionMonth() > 12) {
                    throw new BusinessException("收款期限月数不能为空或不正确，请重填！");
                }
                if (customerInfo.getCollectionDay() <= 0 || customerInfo.getCollectionDay() > 31) {
                    throw new BusinessException("收款期限日期不能为空或不正确，请重选！");
                }
            }
            if (customerInfo.getOrgId() < 0) {
                throw new BusinessException("归属部门不能为空，请重选！");
            }

                /*if (!StringUtils.isEmpty(customerInfo.getLineTel())) {
                    throw new BusinessException("亲，输入的座机电话格式错误，请重新输入！");
                }*/
            if (!StringUtils.isEmpty(customerInfo.getLinePhone()) && !CommonUtil.isCheckMobiPhone(customerInfo.getLinePhone())) {
                throw new BusinessException("亲，输入的手机号码格式错误，请重新输入！");
            }
            customer = customerInfoService.saveOrUpdateCustomer(customerInfo, accessToken);
        }
        if ("Y".equals(customer)) {
            return ResponseResult.success("新增成功!");
        } else if ("YM".equals(customer)) {
            return ResponseResult.success("修改成功!");
        } else return ResponseResult.failure("操作失败!");

    }

    /*
     * @author 向子俊
     * @Description //TODO 新增/修改客户档案
     * @date 19:10 2022/1/22 0022
     * @Param [customerInfo]
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("/saveOrUpdate/customer")
    public ResponseResult saveOrUpdate(@RequestBody CmCustomerInfoVo customerInfo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String flag = "";
        //进行数据校验
        String companyName = customerInfo.getCompanyName();
        String customerName = customerInfo.getCustomerName();
        String lookupName = customerInfo.getLookupName();
        String lineName = customerInfo.getLineName();
        String lineTel = customerInfo.getLineTel();
        String linePhone = customerInfo.getLinePhone();
        Integer oddWay = customerInfo.getOddWay();
        Integer payWay = customerInfo.getPayWay();
        Integer reconciliationTime = customerInfo.getReconciliationTime();
        Integer reciveTime = customerInfo.getReciveTime();
        Integer invoiceTime = customerInfo.getInvoiceTime();
        Integer collectionTime = customerInfo.getCollectionTime();
        Integer reciveMonth = customerInfo.getReciveMonth();
        Integer reciveDay = customerInfo.getReciveDay();
        Integer reconciliationMonth = customerInfo.getReconciliationMonth();
        Integer reconciliationDay = customerInfo.getReconciliationDay();
        Integer invoiceMonth = customerInfo.getInvoiceMonth();
        Integer invoiceDay = customerInfo.getInvoiceDay();
        Integer collectionMonth = customerInfo.getCollectionMonth();
        Integer collectionDay = customerInfo.getCollectionDay();


        String reciveAddress = customerInfo.getReciveAddress();
        long reciveProvinceId = customerInfo.getReciveProvinceId();
        long reciveCityId = customerInfo.getReciveCityId();
        long orgId = customerInfo.getOrgId();
        if (org.apache.commons.lang.StringUtils.isEmpty(reciveAddress)) {
            throw new BusinessException("亲，请输入回单地址！");
        }
        if (reciveProvinceId < 0) {
            throw new BusinessException("亲，请输入回单地址！");
        }
        if (reciveCityId < 0) {
            throw new BusinessException("亲，请输入回单地址！");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(lineName)) {
            throw new BusinessException("亲，请输入联系人！");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(linePhone)) {
            throw new BusinessException("亲，请输入联系手机！");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(companyName)) {
            throw new BusinessException("亲，请输入公司全称！");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(customerName)) {
            throw new BusinessException("亲，请输入客户简称！");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(lookupName)) {
            throw new BusinessException("亲，请输入发票抬头！");
        }
        if (oddWay < 0) {
            throw new BusinessException("亲，请输入正确的回单类型！");
        }
        if (payWay < 0) {
            throw new BusinessException("亲，请选择结算方式！");
        }
        if (payWay == 1 || payWay == 2) {
            if (reciveTime == null || reciveTime <= 0) {
                throw new BusinessException("亲，请输入正确的回单期限！");
            }
            if (reconciliationTime != null && !CommonUtil.isNumber(String.valueOf(reconciliationTime))) {
                throw new BusinessException("亲，请输入正确的回单期限！");
            }
            if (invoiceTime != null && !CommonUtil.isNumber(String.valueOf(invoiceTime))) {
                throw new BusinessException("亲，请输入正确的开票期限！");
            }

            if (payWay == 2 && (collectionTime == null || collectionTime < 0)) {
                throw new BusinessException("亲，请输入收款期限！");
            }
            if (payWay == 1) {
                customerInfo.setCollectionTime(null);
            }
        } else {
            if (reciveMonth <= 0 || reciveMonth > 12) {
                throw new BusinessException("回单期限月数不能为空或不正确，请重填！");
            }
            if (reciveDay <= 0 || reciveDay > 31) {
                throw new BusinessException("回单期限日期不能为空或不正确，请重选！");
            }
            if (null != reconciliationMonth && reconciliationMonth > 12) {
                throw new BusinessException("对账期限月数不正确，请重填！");
            }
            if (null != reconciliationDay && null != reconciliationMonth) {
                if (reconciliationMonth > 0 && reconciliationMonth < 12 && (reconciliationDay <= 0 || reconciliationDay > 31)) {
                    throw new BusinessException("对账期限日期不能为空或不正确，请重选！");
                }
            }
            if (null != invoiceMonth && invoiceMonth > 12) {
                throw new BusinessException("开票期限月数不正确，请重填！");
            }
            if (null != invoiceDay && null != invoiceMonth) {
                if (invoiceMonth > 0 && invoiceMonth < 12 && (invoiceDay <= 0 || invoiceDay > 31)) {
                    throw new BusinessException("开票期限日期不能为空或不正确，请重选！");
                }
            }
            if (collectionMonth <= 0 || collectionMonth > 12) {
                throw new BusinessException("收款期限月数不能为空或不正确，请重填！");
            }
            if (collectionDay <= 0 || collectionDay > 31) {
                throw new BusinessException("收款期限日期不能为空或不正确，请重选！");
            }
//            inParam.put("reciveTime", null);
//            inParam.put("reconciliationTime", null);
//            inParam.put("invoiceTime", null);
//            inParam.put("collectionTime", null);
        }

        if (orgId < 0) {
            throw new BusinessException("归属部门不能为空，请重选！");
        }


        if (StrUtil.isNotEmpty(lineTel)) {
            if (!CommonUtil.isCheckMobiPhoneNew(lineTel)) {
                throw new BusinessException("亲，输入的座机电话格式错误，请重新输入！");
            }
        }
//            if (org.apache.commons.lang.StringUtils.isNotEmpty(linePhone)) {
//                if (!CommonUtil.isCheckPhone(linePhone)) {
//                    throw new BusinessException("亲，输入的手机号码格式错误，请重新输入！");
//                }
//            }
        flag = customerInfoService.saveOrUpdateCustomerCopy(customerInfo, accessToken);
        if ("Y".equals(flag)) {
            return ResponseResult.success("新增客户信息成功!");
        } else if ("YM".equals(flag)) {
            return ResponseResult.success("修改客户信息成功!");
        } else return ResponseResult.failure("操作失败!");

    }

    /**
     * @return java.lang.String
     * @author 向子俊
     * @Description //TODO 导入客户档案
     * @date 14:14 2022/1/15 0015
     * @Param [file]
     */
    @PostMapping("/batchImport/customer")
    public ResponseResult importCustomer(@RequestParam("file") MultipartFile file) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ExcelParse parse = new ExcelParse();
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            // 获取真实行数
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);
            if (rows > 300) {
                throw new BusinessException("最多一次性导入300条数据");
            }
            if (!"客户全称(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 1))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"客户简称(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 2))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"发票抬头(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 3))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"回单类型(必选)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 4))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"公司地址".equals(parse.readExcelByRowAndCell(sheetNo, 1, 5))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"回单省市(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 6))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"回单详细地址(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 7))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"联系人(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 8))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"联系电话".equals(parse.readExcelByRowAndCell(sheetNo, 1, 9))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"联系手机(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 10))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"结算方式(必选)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 11))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"回单期限(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 12))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"对账期限".equals(parse.readExcelByRowAndCell(sheetNo, 1, 13))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"开票期限".equals(parse.readExcelByRowAndCell(sheetNo, 1, 14))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"收款期限(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 15))) {
                throw new BusinessException("模板格式错误");
            }
            ImportOrExportRecords record = new ImportOrExportRecords();
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String mediaExcelPath = client.upload(file.getInputStream(), "客户信息.xlsx", file.getSize());
            record.setMediaUrl(mediaExcelPath);
            record.setName("客户档案导入");
            record.setMediaName(file.getOriginalFilename());
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            customerInfoService.batchImportCustomer(file.getBytes(), record, accessToken);
            return ResponseResult.success("正在导入文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("模版格式错误");
        }
    }

    /**
     * 查询自有车司机和员工
     *
     * @param pageNum  页码
     * @param pageSize 页面展示条数
     * @param linkman  联系人姓名
     * @return
     */
    @GetMapping("/doQueryBackUserList/customer")
    public ResponseResult doQueryBackUserList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                              String linkman) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<BackUserDto> backUserDtoPage = new Page<>(pageNum, pageSize);
        return ResponseResult.success(customerInfoService.doQueryBackUserList(backUserDtoPage, linkman, accessToken));
    }

    /**
     * 客户信息导出
     *
     * @param customerInfo 客户信息/客户档案
     * @return
     */
    @GetMapping("/export/customer")
    public ResponseResult exportCustomer(CmCustomerInfoVo customerInfo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("客户信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            customerInfoService.exportCustomer(customerInfo, accessToken, record);
        } catch (Exception e) {
            LOGGER.error("导出失败客户列表异常" + e);
            return ResponseResult.failure("导出客户列表异常");
        }
        return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
    }


    //获取流文件
    private void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
