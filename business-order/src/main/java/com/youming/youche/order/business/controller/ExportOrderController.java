package com.youming.youche.order.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.system.utils.excel.ExcelParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 订单录入页面 前端控制器
 * </p>
 *
 * @author ly
 * @since 2022-02-24
 */
@RestController
@RequestMapping("/export")
public class ExportOrderController extends BaseController<OrderInfo, IOrderInfoService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportOrderController.class);

    @Override
    public IOrderInfoService getService() {
        return null;
    }




    /**
     * @author 梁岩
     *  批量导入开单接口      DOTO
     * @date 16:48 2022/3/16
     * @Param []
     */
    @PostMapping("/order/saveExportOrders")
    public ResponseResult saveExportOrders(@RequestParam("file") MultipartFile file) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//            LoginInfo loginInfo= service.getLoginUser(accessToken);
//            Long tenantId = loginInfo.getTenantId();
            ExcelParse parse = new ExcelParse();
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            // 获取真实行数
            int sheetNo = 1;
            int rows = parse.getRowCount(sheetNo);
            //int rowss = parse.getRealRowCount(sheetNo);
            if (rows > 100) {
                throw new BusinessException("最多一次性导入100条数据");
            }


            return ResponseResult.success(null);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.failure("查询异常");
        }
    }

}
