package com.youming.youche.finance.dto;

import com.youming.youche.finance.domain.VehicleExpense;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(staticName = "of")
public class VehicleExpenseDto extends VehicleExpense implements Serializable {

	private static final long serialVersionUID = 6361439294675258581L;

	private List<String> urls;

	private String typeName;

}
