package com.sohu.mp.sharingplan.enums.exception;

import com.sohu.mp.common.exception.BaseException;

public class MulctNoAuthException extends BaseException {

    private static final long serialVersionUID = 6537850552049506083L;

    public static final MulctNoAuthException INSTANCE = new MulctNoAuthException("sig error");

    private MulctNoAuthException(String detail) {
        super(SharingPlanExceptionEnum.MULCT_NO_AUTH, detail);
    }
}
