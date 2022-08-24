package com.youming.youche.market.dto.facilitator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceUserInDto implements Serializable {
    /**
     * 登陆账户
     */
    private String loginAcct;
    /**
     * 用户名称
     */
    private String linkman;
    /**
     * 身份证号
     */
    private String identification;
    /**
     *
     */
    private Long idenPicture;
    private String idenPictureUrl;
    /**
     * 密码
     */
    private String password;
    /**
     *
     */
    private Long userPrice;
    /**
     *
     */
    private String userPriceUrl;
    private Long idenPictureFront;
    private String idenPictureFrontUrl;
    private Long idenPictureBack;
    private String idenPictureBackUrl;
    /**
     *运营审核状态，1:待审核,2:已审核,3:审核未通过
     */
    private Integer authFlg=0;
    private Long tenantId;
}
