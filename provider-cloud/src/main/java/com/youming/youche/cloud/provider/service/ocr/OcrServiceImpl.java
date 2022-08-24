package com.youming.youche.cloud.provider.service.ocr;

import cn.hutool.core.bean.BeanUtil;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.ocr.v1.OcrClient;
import com.huaweicloud.sdk.ocr.v1.model.DriverLicenseRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.IdCardRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeDriverLicenseRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeDriverLicenseResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeIdCardRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeIdCardResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeVehicleLicenseRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeVehicleLicenseResponse;
import com.huaweicloud.sdk.ocr.v1.model.VehicleLicenseRequestBody;
import com.huaweicloud.sdk.ocr.v1.region.OcrRegion;
import com.youming.youche.cloud.api.ocr.OcrService;
import com.youming.youche.cloud.dto.ocr.DriverLicenseDto;
import com.youming.youche.cloud.dto.ocr.OcrCardDto;
import com.youming.youche.cloud.dto.ocr.VehicleLicenseDto;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;

@DubboService(version = "1.0.0")
public class OcrServiceImpl implements OcrService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OcrServiceImpl.class);
    @Value("${ocr.ak}")
    String ak;

    @Value("${ocr.sk}")
    String sk;

    @Value("${ocr.region}")
    String region;


    /***
     * @Description: 身份证识别
     * @Author: luwei
     * @Date: 2022/7/20 12:17
     * @Param base64Image:
     * @return: void
     * @Version: 1.0
     **/
    @Override
    public OcrCardDto recognizeIdCardSolution(String url) {
        OcrCardDto ocrDto = new OcrCardDto();
        ICredential auth = new BasicCredentials()
                .withAk(ak)
                .withSk(sk);
        OcrClient client = OcrClient.newBuilder()
                .withCredential(auth)
                .withRegion(OcrRegion.valueOf(region))
                .build();
        RecognizeIdCardRequest request = new RecognizeIdCardRequest();
        IdCardRequestBody body = new IdCardRequestBody();
        body.withUrl(url);
        request.withBody(body);
        try {
            RecognizeIdCardResponse response = client.recognizeIdCard(request);
            if (response != null && response.getResult() != null) {
                BeanUtil.copyProperties(response.getResult(), ocrDto);
                LOGGER.info(ocrDto.toString());
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
            return null;
        } catch (RequestTimeoutException e) {
            e.printStackTrace();
            return null;
        } catch (ServiceResponseException e) {
            e.printStackTrace();
            System.out.println(e.getHttpStatusCode());
            System.out.println(e.getErrorCode());
            System.out.println(e.getErrorMsg());
            return null;
        }
        if(checkObjAllFieldsIsNull(ocrDto)){
            return null;
        }
        return ocrDto;
    }

    /***
     * @Description: 行驶证识别
     * @Author: luwei
     * @Date: 2022/7/20 12:50
     * @Param base64Image:
     * @return: com.youming.youche.cloud.dto.OcrDto
     * @Version: 1.0
     **/
    @Override
    public VehicleLicenseDto recognizeVehicleLicenseSolution(String url) {
        VehicleLicenseDto vehicleLicenseDto = new VehicleLicenseDto();
        ICredential auth = new BasicCredentials()
                .withAk(ak)
                .withSk(sk);
        OcrClient client = OcrClient.newBuilder()
                .withCredential(auth)
                .withRegion(OcrRegion.valueOf(region))
                .build();
        RecognizeVehicleLicenseRequest request = new RecognizeVehicleLicenseRequest();
        VehicleLicenseRequestBody body = new VehicleLicenseRequestBody();
        body.withUrl(url);
        request.withBody(body);
        try {
            RecognizeVehicleLicenseResponse response = client.recognizeVehicleLicense(request);
            if (response != null && response.getResult() != null) {
                BeanUtil.copyProperties(response.getResult(), vehicleLicenseDto);
                LOGGER.info(vehicleLicenseDto.toString());
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
            return null;
        } catch (RequestTimeoutException e) {
            e.printStackTrace();
            return null;
        } catch (ServiceResponseException e) {
            e.printStackTrace();
            System.out.println(e.getHttpStatusCode());
            System.out.println(e.getErrorCode());
            System.out.println(e.getErrorMsg());
            return null;
        }
        if(checkObjAllFieldsIsNull(vehicleLicenseDto)){
            return null;
        }
        return vehicleLicenseDto;
    }


    /***
     * @Description: 驾驶证识别
     * @Author: luwei
     * @Date: 2022/7/20 12:50
     * @Param base64Image:
     * @return: com.youming.youche.cloud.dto.OcrDto
     * @Version: 1.0
     **/
    @Override
    public DriverLicenseDto recognizeDriverLicenseSolution(String url) {
        DriverLicenseDto driverLicenseDto = new DriverLicenseDto();
        ICredential auth = new BasicCredentials()
                .withAk(ak)
                .withSk(sk);
        OcrClient client = OcrClient.newBuilder()
                .withCredential(auth)
                .withRegion(OcrRegion.valueOf(region))
                .build();
        RecognizeDriverLicenseRequest request = new RecognizeDriverLicenseRequest();
        DriverLicenseRequestBody driverLicenseRequestBody = new DriverLicenseRequestBody();
        driverLicenseRequestBody.withUrl(url);
        request.withBody(driverLicenseRequestBody);
        try {
            RecognizeDriverLicenseResponse response = client.recognizeDriverLicense(request);
            if (response != null && response.getResult() != null) {
                BeanUtil.copyProperties(response.getResult(), driverLicenseDto);
                LOGGER.info(driverLicenseDto.toString());
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
            return null;
        } catch (RequestTimeoutException e) {
            e.printStackTrace();
            return null;
        } catch (ServiceResponseException e) {
            e.printStackTrace();
            System.out.println(e.getHttpStatusCode());
            System.out.println(e.getErrorCode());
            System.out.println(e.getErrorMsg());
            return null;
        }
        if(checkObjAllFieldsIsNull(driverLicenseDto)){
            return null;
        }
        return driverLicenseDto;
    }


    public static void main(String[] args) {
        OcrServiceImpl ocrService = new OcrServiceImpl();
        VehicleLicenseDto ocrDto = ocrService.recognizeVehicleLicenseSolution("http://1.116.37.17:1080/group1/M00/00/E6/rBEQD2LXz_qATCNJAAAT6O6H_js709_big.png");
        System.out.println(ocrDto.toString());
    }

    public static boolean checkObjAllFieldsIsNull(Object object) {
        if (null == object) {
            return true;
        }
        try {
            for (Field f : object.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.get(object) != null && StringUtils.isNotBlank(f.get(object).toString())) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
