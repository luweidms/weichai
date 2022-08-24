package com.youming.youche.order.vo;

import com.youming.youche.order.domain.order.OrderReceipt;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *  聂杰伟
 *  回单 图片上传入参
 */
@Data
public class OrderReceiptVo implements Serializable {


    private Long orderId;
    private String loadUrl;
    private String loadPicIdS;
    private Long receiptId;
    private List<OrderReceipt> orderRecipts;
    private Boolean load;// 合同
    private Boolean receipt;// 回单
    private String verifyString;// 备注
    private String receiptsNumber;// 回单号
    private  String type;
    private  String  checkOrder;// 订单号集合
    private  String verifyDesc;// 审核备注

    private String receiptsStr;
}
