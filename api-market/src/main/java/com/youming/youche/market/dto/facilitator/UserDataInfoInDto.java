package com.youming.youche.market.dto.facilitator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDataInfoInDto implements Serializable {
    private static final long serialVersionUID = -253340185505743284L;

    private Long userId;
    //姓名
    private String linkman;
    //用户头像flowId
    private Long userPrice;
    //用户头像url
    private String userPriceUrl;
    //身份证正面flowId
    private Long idenPictureFront;
    //身份证正面url
    private String idenPictureFrontUrl;
    //身份证背面flowId
    private Long idenPictureBack;
    //身份证背面url
    private String idenPictureBackUrl;
    //身份证号
    private String identification;
}
