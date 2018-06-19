package com.sohu.mp.sharingplan.enums.exception;

import com.sohu.mp.common.exception.BaseException;

public class AmountCheckException extends BaseException {

    private static final long serialVersionUID = 3290850784549506083L;

    public AmountCheckException(String detail) {
        super(SharingPlanExceptionEnum.AMOUNT_CHECK_ERROR, detail);
    }
}
