package com.youming.youche.finance.business.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.IOaLoanThreeService;
import com.youming.youche.finance.constant.OaLoanConsts;
import com.youming.youche.finance.domain.OaLoan;
import com.youming.youche.finance.dto.AccountBankRelDto;
import com.youming.youche.finance.dto.OaLoanOutDto;
import com.youming.youche.finance.dto.QueryOaloneDto;
import com.youming.youche.finance.dto.order.OrderSchedulerDto;
import com.youming.youche.finance.vo.OaFilesVo;
import com.youming.youche.finance.vo.OaLoanOutVo;
import com.youming.youche.finance.vo.OaLoanVo;
import com.youming.youche.order.annotation.Dict;
import com.youming.youche.system.api.ISysMenuBtnService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysMenuBtn;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//import javafx.scene.control.Pagination;

/**
 * <p>
 * 借支信息表 前端控制器
 * </p>
 *
 * @author niejiewei
 * @since 2022-02-07
 */
@RestController
@RequestMapping("oa/loan")
public class OaLoanController extends BaseController<OaLoan, IOaLoanThreeService> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OaLoanController.class);
    //VehicleDateCostRel
    @DubboReference(version = "1.0.0")
    IOaLoanThreeService oaLoanThreeService;

    @DubboReference(version = "1.0.0")
    ISysMenuBtnService sysMenuBtnService;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService sysOrganizeService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public IOaLoanThreeService getService() {
        return oaLoanThreeService;
    }
    /**
     * 方法实现说明
     * @author      terry
     * @param pageNum
    * @param pageSize
    * @param waitDeal 待我处理
    * @param queryType 查询状态   1 员工借支  2车管借支   3 司机借支
    * @param oaLoanId 借支号
    * @param state 状态
    * @param loanSubject 借支科目
    * @param userName 申请人
    * @param orderId 订单号
    * @param plateNumber 车牌号
    * @param startTime 申请开始时间
     * @param endTime 申请结束时间
    * @param flowId 支付ID
     * @return      com.youming.youche.commons.response.ResponseResult
     * @exception
     * @date        2022/2/7 10:35
     */
    @GetMapping({"/getPageAll"})
    public ResponseResult gets(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
                               @RequestParam(value = "waitDeal", required = true, defaultValue = "false") Boolean waitDeal,
                               @RequestParam(value = "queryType", required = false) Integer queryType,
                               @RequestParam(value = "oaLoanId", required = false) String oaLoanId,
                               @RequestParam(value = "state", required = false) Integer state,
                               @RequestParam(value = "loanSubject", required = false) Integer loanSubject,
                               @RequestParam(value = "userName", required = false) String userName,
                               @RequestParam(value = "accName", required = false) String acctName,
                               @RequestParam(value = "mobilePhone", required = false) String mobilePhone,
                               @RequestParam(value = "orderId", required = false) String orderId,
                               @RequestParam(value = "plateNumber", required = false) String plateNumber,
                               @RequestParam(value = "startTime", required = false) String startTime,
                               @RequestParam(value = "endTime", required = false) String endTime,
                               @RequestParam(value = "flowId", required = false) String flowId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<SysMenuBtn> sysMenuBtns = sysMenuBtnService.selectAllByUserIdAndData(accessToken);
        List<Long> dataPermissionIds = Lists.newArrayList();
        for (SysMenuBtn sysMenuBtn : sysMenuBtns) {
            dataPermissionIds.add(sysMenuBtn.getId());
        }

        List<Long> subOrgList = sysOrganizeService.getSubOrgList(accessToken);
        Page<OaLoanOutDto> oaLoanPage = oaLoanThreeService.selectAllByMore(pageNum,pageSize,waitDeal,queryType,oaLoanId,state,
                loanSubject,userName,orderId,plateNumber,startTime,endTime,flowId,dataPermissionIds,subOrgList,acctName,
                mobilePhone,accessToken );
//        T domain = this.getService().get(Id);

        return ResponseResult.success(oaLoanPage);
    }

    /**
     * njw
     * 2022-3-3 : 14:12
     * 方法说明 借支导出 :司机 员工
     * @return
     */
    @GetMapping("/borrowingExcel")
    public  ResponseResult borrowing_Excel(@RequestParam(value = "waitDeal", required = true, defaultValue = "false") Boolean waitDeal,
                                           @RequestParam(value = "queryType", required = false) Integer queryType,
                                           @RequestParam(value = "oaLoanId", required = false) String oaLoanId,
                                           @RequestParam(value = "state", required = false) Integer state,
                                           @RequestParam(value = "loanSubject", required = false) Integer loanSubject,
                                           @RequestParam(value = "userName", required = false) String userName,
                                           @RequestParam(value = "acctName", required = false) String acctName,
                                           @RequestParam(value = "mobilePhone", required = false) String mobilePhone,
                                           @RequestParam(value = "orderId", required = false) String orderId,
                                           @RequestParam(value = "plateNumber", required = false) String plateNumber,
                                           @RequestParam(value = "startTime", required = false) String startTime,
                                           @RequestParam(value = "endTime", required = false) String endTime,
                                           @RequestParam(value = "flowId", required = false) String flowId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<SysMenuBtn> sysMenuBtns = sysMenuBtnService.selectAllByUserIdAndData(accessToken);
        List<Long> dataPermissionIds = Lists.newArrayList();
        for (SysMenuBtn sysMenuBtn : sysMenuBtns) {
            dataPermissionIds.add(sysMenuBtn.getId());
        }

        List<Long> subOrgList = sysOrganizeService.getSubOrgList(accessToken);

            String fileName = "借支信息导出";
            if (queryType == OaLoanConsts.QUERY_TYPE.vehicle) {//车管借支
                fileName= "员工借支导出";
            }else if (queryType == OaLoanConsts.QUERY_TYPE.driver){
                fileName= "司机借支导出";
            }
            com.youming.youche.commons.domain.ImportOrExportRecords record = new com.youming.youche.commons.domain.ImportOrExportRecords();
            record.setName(fileName);
            record.setBussinessType(2);
            record.setState(1);
            record= importOrExportRecordsService.saveRecords(record, accessToken);
            oaLoanThreeService.borrowing_Excel(waitDeal,queryType,oaLoanId,state,loanSubject,
                    userName,orderId,plateNumber,startTime,endTime,flowId,dataPermissionIds,
                    subOrgList,acctName,mobilePhone,accessToken,record);
            return  ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
    }
    /**
     *  方法说明 借支详情
     * @param id 借支id
     * @return 借支信息
     */
    @GetMapping("/queryOaLoanById")
    @Dict
    public  ResponseResult queryOaLoanById(@RequestParam(value = "id") Long id,
                                           @RequestParam(value = "busiCode") String busiCode){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OaLoanOutDto oaLoanOutDto = oaLoanThreeService.queryOaLoanById(id, busiCode, accessToken);
        return  ResponseResult.success(oaLoanOutDto);
    }

    /**21012
     *  方法说明 借支详情
     * @param id 借支id
     * @return 借支信息
     */
    @GetMapping("/queryOaLoanByIds")
    @Dict
    public  ResponseResult queryOaLoanByIds(@RequestParam(value = "id", required = true) Long id,
                                           @RequestParam(value = "busiCode") String busiCode){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OaLoanOutDto oaLoanOutDto = oaLoanThreeService.queryOaLoanByIds(id, busiCode, accessToken);
        return  ResponseResult.success(oaLoanOutDto);
    }

    /**
     *  方法说明 核销查询
     *  @param pageNum 分页参数
     *  @param pageSize
     *  @param waitDeal    待我处理
     *  @param queryType   查询状态   1 员工借支  2车管借支   3 司机借支
     *  @param oaLoanId    借支号
     *  @param state       状态
     *  @param loanSubject 借支科目
     *  @param userName    申请人
     *  @param orderId     订单号
     *  @param plateNumber 车牌号
     *  @param startTime   申请开始时间
     *  @param endTime     申请结束时间
     *  @param flowId      支付ID
     * @return
     */
    @GetMapping("/selectcancel")
    public  ResponseResult selectCancel(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
                                         @RequestParam(value = "waitDeal", required = true, defaultValue = "false") Boolean waitDeal,
                                         @RequestParam(value = "queryType", required = false) Integer queryType,
                                         @RequestParam(value = "oaLoanId", required = false) String oaLoanId,
                                         @RequestParam(value = "state", required = false) Integer state,
                                         @RequestParam(value = "loanSubject", required = false) Integer loanSubject,
                                         @RequestParam(value = "userName", required = false) String userName,
                                         @RequestParam(value = "acctName", required = false) String acctName,
                                         @RequestParam(value = "mobilePhone", required = false) String mobilePhone,
                                         @RequestParam(value = "orderId", required = false) String orderId,
                                         @RequestParam(value = "plateNumber", required = false) String plateNumber,
                                         @RequestParam(value = "startTime", required = false) String startTime,
                                         @RequestParam(value = "endTime", required = false) String endTime,
                                         @RequestParam(value = "flowId", required = false) String flowId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<SysMenuBtn> sysMenuBtns = sysMenuBtnService.selectAllByUserIdAndData(accessToken);//查询用户的数据操作权限
        List<Long> dataPermissionIds = Lists.newArrayList();
        for (SysMenuBtn sysMenuBtn : sysMenuBtns) {
            dataPermissionIds.add(sysMenuBtn.getId());
        }

        List<Long> subOrgList = sysOrganizeService.getSubOrgList(accessToken);//获取当前部门以及所有子部门
        IPage<OaLoanOutDto> oaLoanPage = oaLoanThreeService.selectCancelAll(pageNum, pageSize, waitDeal, queryType, oaLoanId, state,
                loanSubject, userName, orderId, plateNumber, startTime, endTime, flowId, dataPermissionIds, subOrgList, acctName,
                mobilePhone, accessToken);
        return ResponseResult.success(oaLoanPage);
    }


    /**
     * 方法说明   核销报表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param loanType 类别
     * @param loanSubject 科目
     * @param loanClassify 分类
     * @param orderId 订单id
     * @param oaLoanId 操作人组织id
     * @param userName 申请人
     * @param mobilePhone 申请人手机
     * @param acctName  帐户姓名
     * @param amountStar 开始金额
     * @param amountEnd 结束金额
     * @param plateNumber 车牌号码
     * @param queryType 借支人属性
     * @param state 状态
     * @param flowId 支付id
     * @param noPayedStar 未报销金额
     * @param noPayedEnd 未报销金额
     * @return
     */
    @GetMapping("/queryOaLoanTable")
    public ResponseResult  queryOaLoanTable (@RequestParam(value = "startTime", required = false) String startTime,
                                             @RequestParam(value = "endTime", required = false) String endTime,
                                             @RequestParam(value = "loanType", required = false) Integer loanType,
                                             @RequestParam(value = "loanSubject", required = false) Integer loanSubject,
                                             @RequestParam(value = "loanClassify", required = false) Integer loanClassify,
                                             @RequestParam(value = "orderId", required = false) String orderId,
                                             @RequestParam(value = "oaLoanId", required = false) String oaLoanId,
                                             @RequestParam(value = "userName", required = false) String userName,
                                             @RequestParam(value = "mobilePhone", required = false) String mobilePhone,
                                             @RequestParam(value = "acctName", required = false) String acctName,
                                             @RequestParam(value = "amountStar", required = false) Long amountStar,
                                             @RequestParam(value = "amountEnd", required = false) Long amountEnd,
                                             @RequestParam(value = "plateNumber", required = false) String plateNumber,
                                             @RequestParam(value = "queryType", required = false) Integer queryType,
                                             @RequestParam(value = "state", required = false) Integer state,
                                             @RequestParam(value = "flowId", required = false) String flowId,
                                             @RequestParam(value = "noPayedStar", required = false) Long noPayedStar,
                                             @RequestParam(value = "noPayedEnd", required = false) Long noPayedEnd,
                                             @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<Long> subOrgList = sysOrganizeService.getSubOrgList(accessToken);//获取当前部门以及所有子部门
        IPage<OaLoanOutDto> oaLoanOutDtoIPage = oaLoanThreeService.queryOaLoanTable(startTime, endTime, loanType, loanSubject, loanClassify,
                orderId, oaLoanId, userName, mobilePhone, acctName, amountStar, amountEnd,
                plateNumber, queryType, state, flowId, noPayedStar, noPayedEnd, accessToken,subOrgList,pageNum,pageSize);
        return  ResponseResult.success(oaLoanOutDtoIPage);
    }


    /**
     * 方法说明 核销报表 统计
     * 聂杰伟
     *   todo 前端暂时不需要
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param loanType 类别
     * @param loanSubject 科目
     * @param loanClassify 分类 1内部借支 2车管中心借支
     * @param orderId 订单id
     * @param oaLoanId 操作人组织id
     * @param userName 申请人
     * @param mobilePhone 申请人手机
     * @param acctName 帐户姓名
     * @param amountStar 开始金额
     * @param amountEnd 结束金额
     * @param plateNumber 车牌号码
     * @param queryType 借支人属性
     * @param state 状态
     * @param flowId 支付id
     * @param noPayedStar 未报销金额
     * @param noPayedEnd 未报销金额
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/queryOaLoanTableSum")
    public ResponseResult queryOaLoanTableSum (@RequestParam(value = "startTime", required = false) String startTime,
                                             @RequestParam(value = "endTime", required = false) String endTime,
                                             @RequestParam(value = "loanType", required = false) Integer loanType,
                                             @RequestParam(value = "loanSubject", required = false) Integer loanSubject,
                                             @RequestParam(value = "loanClassify", required = false) Integer loanClassify,
                                             @RequestParam(value = "orderId", required = false) String orderId,
                                             @RequestParam(value = "oaLoanId", required = false) String oaLoanId,
                                             @RequestParam(value = "userName", required = false) String userName,
                                             @RequestParam(value = "mobilePhone", required = false) String mobilePhone,
                                             @RequestParam(value = "acctName", required = false) String acctName,
                                             @RequestParam(value = "amountStar", required = false) Long amountStar,
                                             @RequestParam(value = "amountEnd", required = false) Long amountEnd,
                                             @RequestParam(value = "plateNumber", required = false) String plateNumber,
                                             @RequestParam(value = "queryType", required = true) Integer queryType,
                                             @RequestParam(value = "state", required = true) Integer state,
                                             @RequestParam(value = "flowId", required = false) String flowId,
                                             @RequestParam(value = "noPayedStar", required = false) Long noPayedStar,
                                             @RequestParam(value = "noPayedEnd", required = false) Long noPayedEnd,
                                             @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize){

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<Long> subOrgList = sysOrganizeService.getSubOrgList(accessToken);//获取当前部门以及所有子部门
        OaLoanOutDto dto = oaLoanThreeService.queryOaLoanTableSum(startTime, endTime, loanType,
                loanSubject, loanClassify, orderId, oaLoanId, userName, mobilePhone,
                acctName, amountStar, amountEnd, plateNumber, queryType, state, flowId,
                noPayedStar, noPayedEnd, accessToken, subOrgList, pageNum, pageSize);
        return ResponseResult.success(dto);

    }
    /**
     * 借支核销报表导出
     * @return
     */
    @GetMapping("/excelReport")
    public  ResponseResult excelReport(@RequestParam(value = "startTime", required = false) String startTime,
                                        @RequestParam(value = "endTime", required = false) String endTime,
                                        @RequestParam(value = "loanType", required = false) Integer loanType,
                                        @RequestParam(value = "loanSubject", required = false) Integer loanSubject,
                                        @RequestParam(value = "loanClassify", required = false) Integer loanClassify,
                                        @RequestParam(value = "orderId", required = false) String orderId,
                                        @RequestParam(value = "oaLoanId", required = false) String oaLoanId,
                                        @RequestParam(value = "userName", required = false) String userName,
                                        @RequestParam(value = "mobilePhone", required = false) String mobilePhone,
                                        @RequestParam(value = "acctName", required = false) String acctName,
                                        @RequestParam(value = "amountStar", required = false) Long amountStar,
                                        @RequestParam(value = "amountEnd", required = false) Long amountEnd,
                                        @RequestParam(value = "plateNumber", required = false) String plateNumber,
                                        @RequestParam(value = "queryType", required = false) Integer queryType,
                                        @RequestParam(value = "state", required = false) Integer state,
                                        @RequestParam(value = "flowId", required = false) String flowId,
                                        @RequestParam(value = "noPayedStar", required = false) Long noPayedStar,
                                        @RequestParam(value = "noPayedEnd", required = false) Long noPayedEnd){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<Long> subOrgList = sysOrganizeService.getSubOrgList(accessToken);//获取当前部门以及所有子部门

        ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("借支核销报表导出");
            record.setBussinessType(2);
            record.setState(1);
            record= importOrExportRecordsService.saveRecords(record, accessToken);
            oaLoanThreeService.excel_Report(startTime,endTime,loanType,loanSubject,loanClassify,orderId,oaLoanId,userName,mobilePhone,acctName,amountStar,amountEnd,plateNumber,queryType
            ,state,flowId,noPayedStar,noPayedEnd,accessToken,subOrgList,record);
            return  ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
    }
    /**
     * njw
     * 方法说明 借支核销导出
     * @param waitDeal
     * @param queryType
     * @param oaLoanId
     * @param state
     * @param loanSubject
     * @param userName
     * @param acctName
     * @param mobilePhone
     * @param orderId
     * @param plateNumber
     * @param startTime
     * @param endTime
     * @param flowId
     * @return
     */
    @GetMapping("/excelExport")
    public  ResponseResult excel_Export(@RequestParam(value = "waitDeal", required = true, defaultValue = "false") Boolean waitDeal,
                                        @RequestParam(value = "queryType", required = false) Integer queryType,
                                        @RequestParam(value = "oaLoanId", required = false) String oaLoanId,
                                        @RequestParam(value = "state", required = false) Integer state,
                                        @RequestParam(value = "loanSubject", required = false) Integer loanSubject,
                                        @RequestParam(value = "userName", required = false) String userName,
                                        @RequestParam(value = "acctName", required = false) String acctName,
                                        @RequestParam(value = "mobilePhone", required = false) String mobilePhone,
                                        @RequestParam(value = "orderId", required = false) String orderId,
                                        @RequestParam(value = "plateNumber", required = false) String plateNumber,
                                        @RequestParam(value = "startTime", required = false) String startTime,
                                        @RequestParam(value = "endTime", required = false) String endTime,
                                        @RequestParam(value = "flowId", required = false) String flowId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<SysMenuBtn> sysMenuBtns = sysMenuBtnService.selectAllByUserIdAndData(accessToken);//查询用户的数据操作权限
        List<Long> dataPermissionIds = Lists.newArrayList();
        for (SysMenuBtn sysMenuBtn : sysMenuBtns) {
            dataPermissionIds.add(sysMenuBtn.getId());
        }

        List<Long> subOrgList = sysOrganizeService.getSubOrgList(accessToken);//获取当前部门以及所有子部门
            com.youming.youche.commons.domain.ImportOrExportRecords record = new  com.youming.youche.commons.domain.ImportOrExportRecords();
            record.setName("借支核销列表导出");
            record.setBussinessType(2);
            record.setState(1);
            record= importOrExportRecordsService.saveRecords(record, accessToken);
            oaLoanThreeService.excel_Export(waitDeal,queryType,oaLoanId,state,loanSubject,
                    userName,orderId,plateNumber,startTime,endTime,flowId,dataPermissionIds,
                    subOrgList,acctName,mobilePhone,accessToken,record);
            return  ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
    }

    /**
     * 方法说明  员工借支新增
     * njw
     * 2022-3-1 14:00
     * @param oaLoanVo
     * @return
     */
    @PostMapping("/saveOrUpdateOaLoanCar")
    public  ResponseResult saveOrUpdateOaLoanCar(@RequestBody OaLoanVo oaLoanVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        oaLoanThreeService.saveOrUpdateOaLoanCar(oaLoanVo, accessToken);
        return ResponseResult.success("员工借支操作成功");
    }

    /**
     * njw
     * 2022-3-4 16:33
     * 方法说明  借支审核
     */
    @PostMapping("/examineOaLoanCar")
    public ResponseResult examineOaLoanCar (@RequestBody OaLoanVo oaLoanVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        int i = oaLoanThreeService.examineOaLoanCar(oaLoanVo, accessToken);
        return  ResponseResult.success(i);
    }


    /**
     * 2022-3-7 10:20
     * njw
     * 方法说明 : 借支核销
     * @param
     * @param
     * @param
     * @param
     * @param
     * @return
     */
    @PostMapping("/verificationOaLoanCar")
    public  ResponseResult verificationOaLoanCar ( @RequestBody OaLoanOutVo oaLoanOutVo){
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            oaLoanThreeService.verificationOaLoanCar(oaLoanOutVo.getId(), oaLoanOutVo.getCashAmountStr(),
                    oaLoanOutVo.getBillAmountStr(), oaLoanOutVo.getFileUrl(),oaLoanOutVo.getFileId(),
                    oaLoanOutVo.getVerifyRemark(),oaLoanOutVo.getFromWx(), accessToken);
            return ResponseResult.success("核销成功");
    }



    /**
     * 上传借支图片
     * @param oaFilesVo
     */
    @PostMapping("/saveOaLoanPic")
    public  ResponseResult saveOaLoanPic(@RequestBody OaFilesVo oaFilesVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        oaLoanThreeService.saveOaLoanPic(oaFilesVo,accessToken);
        return  ResponseResult.success();
    }


    /**
     * 查询收款人
     * @param userName 名称
     * @param mobilePhone 手机号
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getSelfAndBusinessBank")
    public ResponseResult getSelfAndBusinessBank(@RequestParam(value = "userName") String userName,
                                                 @RequestParam(value = "mobilePhone")String mobilePhone,
                                                 @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<AccountBankRelDto> selfAndBusinessBank = oaLoanThreeService.getSelfAndBusinessBank(userName, mobilePhone, pageNum, pageSize, accessToken);
        return ResponseResult.success(selfAndBusinessBank);
    }

    /**
     * 查询订单
     * @param isHis
     * @param userName
     * @param mobilePhone
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getOrder")
    public  ResponseResult getOrder(@RequestParam(value = "isHis") Boolean isHis,
                                    @RequestParam(value = "userName")String userName,
                                    @RequestParam(value = "mobilePhone")String mobilePhone,
                                    @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize){

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<SysMenuBtn> sysMenuBtns = sysMenuBtnService.selectAllByUserIdAndData(accessToken);
        List<Long> dataPermissionIds = Lists.newArrayList();
        for (SysMenuBtn sysMenuBtn : sysMenuBtns) {
            dataPermissionIds.add(sysMenuBtn.getId());
        }
        List<Long> subOrgList = sysOrganizeService.getSubOrgList(accessToken);//获取当前部门以及所有子部门
                Page<OrderSchedulerDto> orderSchedulerDtoIPage = oaLoanThreeService.queryOrderByCarDriver(isHis, userName,
                mobilePhone, pageNum, pageSize, accessToken, dataPermissionIds, subOrgList);
        return  ResponseResult.success(orderSchedulerDtoIPage);
    }



    /**
     * 借支取消
     * @param oaLoanVo id  借支id
     * @return
     */
    @PostMapping("/cancelOaLoan")
    public  ResponseResult cancelOaLoan(@RequestBody OaLoanVo oaLoanVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String s = oaLoanThreeService.cancelOaLoan(oaLoanVo, accessToken);
        return ResponseResult.success(s);
    }

    /**
     * 21014
     * @param  id  借支id
     * @return
     */
    @PostMapping("cancelOaLoans")
    public  ResponseResult cancelOaLoans(Long id){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String s = oaLoanThreeService.cancelOaLoans(id, accessToken);
        return ResponseResult.success(s);
    }

    /**
     * 订单详情页查询借支情况
     */
    @GetMapping({"/getOrderPage"})
    public ResponseResult getOrderPage(@RequestParam(value = "orderId", required = false) String orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        OaLoan oaLoanPage = oaLoanThreeService.selectByOrder(orderId,accessToken );

        return ResponseResult.success(oaLoanPage);
    }

    /**
     * 20000
     * 该函数的功能描述:驻场号-首页数字统计
     */
    @PostMapping("getStatistics")
    public ResponseResult getStatistics() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        return ResponseResult.success(
                oaLoanThreeService.getStatistics(accessToken)
        );
    }

    /**
     * 微信接口-借支核销 22006
     *
     * @param LId           借支Id
     * @param cashAmountStr 现金核销
     * @param billAmountStr 票据核销
     * @param strfileId     附件
     * @param verifyRemark  核销说明
     */
    @PostMapping("verificationOaLoan")
    public ResponseResult verificationOaLoan(Long LId, String cashAmountStr, String billAmountStr, String strfileId, String verifyRemark) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        oaLoanThreeService.verificationOaLoanCar(LId.toString(), cashAmountStr, billAmountStr, null, strfileId, verifyRemark, 1, accessToken);

        return ResponseResult.success();
    }

    /**
     * 微信接口-获取收款人银行账号列表
     * 接口编码 ：22112
     * acctName 收款人姓名  支持模糊查询
     */
    @GetMapping("getSelfAndBusinessBankWx")
    public ResponseResult getSelfAndBusinessBank(String acctName,
                                                 @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<AccountBankRelDto> selfAndBusinessBankWx = oaLoanThreeService.getSelfAndBusinessBankWx(acctName, "", pageNum, pageSize, accessToken);
        return ResponseResult.success(selfAndBusinessBankWx);
    }

    /**
     * 更新收款人信息
     * 接口编码：22114
     */
    @PostMapping("updatePayeeBankInfo")
    public ResponseResult updatePayeeBankInfo(String accName, String accNo, Long accUserId, String bankName,
                                              String bankBranch, String bankType, String collectAcctId, Long id) {

        oaLoanThreeService.updatePayeeBankInfo(accName, accNo, accUserId, bankName, bankBranch, bankType, collectAcctId, id);
        return ResponseResult.success();

    }

    /**
     * app接口-借支列表查询21011
     */
    @GetMapping("queryOaLoanList")
    public ResponseResult queryOaLoanList(Long orderId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<QueryOaloneDto> queryOaloneDto = oaLoanThreeService.queryOaLoanList(orderId,accessToken);
        return ResponseResult.success(queryOaloneDto);
    }

    /**
     * 21013  新增员工借支
     */
    @GetMapping("saveOrUpdate")
    public ResponseResult saveOrUpdate(Long LId,String amountString,String appReason,Integer loanSubject,String plateNumber,Long orderId,
                                       Long accUserId,String weight,String isNeedBill,String strfileId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String s = oaLoanThreeService.saveOrUpdateApp(LId,amountString,appReason,loanSubject,plateNumber,orderId,accUserId,weight,isNeedBill,strfileId,accessToken);
        return ResponseResult.success(s);
    }
}
