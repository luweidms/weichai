package com.youming.youche.cloud.business.controller.ocr;


import com.youming.youche.cloud.api.ocr.OcrService;
import com.youming.youche.cloud.dto.ocr.VehicleLicenseDto;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author liangyan
 * @since 2022-03-26
 */
@RestController
@RequestMapping("ocr/recognition")
public class OcrControllerController{

    @DubboReference(version = "1.0.0")
    OcrService ocrService;

    /**
     * 证照识别
     * @param urlImage
     * @param flag
     * @param isEctype
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("/character")
    public ResponseResult characterRecognition(@RequestParam("urlImage") String urlImage, @RequestParam(value = "flag",defaultValue = "1") Integer flag,@RequestParam(value = "isEctype",defaultValue = "1") Integer isEctype){
        //根据flowId 查询图片地址
        switch (flag){
            case 1:
                //身份证识别
                return ResponseResult.success(ocrService.recognizeIdCardSolution(urlImage));
            case 2:
                //驾驶证识别
                return ResponseResult.success(ocrService.recognizeDriverLicenseSolution(urlImage));
            case 3:
                //行驶证识别
                VehicleLicenseDto vehicleLicenseDto = ocrService.recognizeVehicleLicenseSolution(urlImage);
                if(vehicleLicenseDto != null){
                    if(StringUtils.isEmpty(vehicleLicenseDto.getInspectionRecord())){
                        //正本
                        if(isEctype != 1){
                            throw new BusinessException("行驶证正本应该上传到正本框");
                        }
                    }else{
                        //副本
                        if(isEctype != 2){
                            throw new BusinessException("行驶证副本应该上传到副本框");
                        }
                    }
                }
                return ResponseResult.success(vehicleLicenseDto);
        }
        return ResponseResult.success();
    }



}
