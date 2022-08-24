package com.youming.youche.cloud.domin.sms;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author liangyan
* @since 2022-03-26
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class SmsController extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 短信类型
            */
    private Integer smsType;

            /**
            * 短信模板id
            */
    private Long templateId;

            /**
            * 租户id
            */
    private Long tenantId;

            /**
            * 用户id
            */
    private Long userId;


}
