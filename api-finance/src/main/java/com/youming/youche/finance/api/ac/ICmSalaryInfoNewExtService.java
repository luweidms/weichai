package com.youming.youche.finance.api.ac;

import com.youming.youche.commons.base.IBaseService;

import com.youming.youche.finance.domain.ac.CmSalaryInfoNewExt;

/**
 * @author: luona
 * @date: 2022/5/12
 * @description: T0DO
 * @version: 1.0
 */
public interface ICmSalaryInfoNewExtService extends IBaseService<CmSalaryInfoNewExt> {

    CmSalaryInfoNewExt  getCmSalaryExtBysalaryId(Long salaryId);
}
