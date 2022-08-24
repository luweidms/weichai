package com.youming.youche.finance.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.IOilEntityService;
import com.youming.youche.finance.domain.OilEntity;
import com.youming.youche.finance.vo.OilEntityVo;
import com.youming.youche.order.api.order.other.IOperationOilService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
* <p>
* 油充值核销表 前端控制器
* </p>
* @author WuHao
* @since 2022-04-14
*/
    @RestController
@RequestMapping("oil/entity")
        public class OilEntityController extends BaseController<OilEntity, IOilEntityService> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OilEntityController.class);

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;
    @DubboReference(version = "1.0.0")
    IOilEntityService iOilEntityService;
    @DubboReference(version = "1.0.0")
    IOperationOilService iOperationOilService;
    @Override
    public IOilEntityService getService() {
        return null;
    }

    /**
     * 油卡导出
     * @return
     */
    @GetMapping("OilExpory")
    public ResponseResult OilExpory(OilEntityVo oilEntityVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("油卡充值导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iOilEntityService.OilCardList1(accessToken, record,oilEntityVo);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出油卡充值列表异常" + e);
            return ResponseResult.failure("导出油卡充值列表异常");
        }
    }
    /**
     * 清零邮费
     */
    @PostMapping("clearOil")
    public ResponseResult clearOil(Long userId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iOperationOilService.clearAccountOil(userId, accessToken);
        return ResponseResult.success("操作成功");
    }
}
