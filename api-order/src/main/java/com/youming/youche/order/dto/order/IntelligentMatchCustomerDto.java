package com.youming.youche.order.dto.order;

import com.youming.youche.order.vo.CompanyVo;
import lombok.Data;

import java.util.List;

@Data
public class IntelligentMatchCustomerDto {

    private static final long serialVersionUID = 1L;

    private List<CompanyVo> items;

    private Long numRows;

    private Long totalNum;

}
