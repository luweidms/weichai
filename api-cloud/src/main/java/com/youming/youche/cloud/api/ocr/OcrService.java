package com.youming.youche.cloud.api.ocr;

import com.youming.youche.cloud.dto.ocr.DriverLicenseDto;
import com.youming.youche.cloud.dto.ocr.OcrCardDto;
import com.youming.youche.cloud.dto.ocr.VehicleLicenseDto;

/***
 * @Description: ocrApi
 * @Author: luwei
 * @Date: 2022/7/20 12:20
 * @Param null:
 * @return: null
 * @Version: 1.0
 **/
public interface OcrService {

    /***
     * @Description: 身份证识别
     * @Author: luwei
     * @Date: 2022/7/20 12:21
     * @Param base64Image: 图片64编码
     * @return: void
     * @Version: 1.0
     **/
    OcrCardDto recognizeIdCardSolution(String base64Image);

    /***
     * @Description: 行驶证识别
     * @Author: luwei
     * @Date: 2022/7/20 12:50
     * @Param base64Image:
     * @return: com.youming.youche.cloud.dto.OcrDto
     * @Version: 1.0
     **/
    VehicleLicenseDto recognizeVehicleLicenseSolution(String base64Image);

    /***
     * @Description: 驾驶证识别
     * @Author: luwei
     * @Date: 2022/7/20 12:50
     * @Param base64Image:
     * @return: com.youming.youche.cloud.dto.OcrDto
     * @Version: 1.0
     **/
    DriverLicenseDto recognizeDriverLicenseSolution(String base64Image);
}
