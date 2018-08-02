package com.sohu.mp.sharingplan.enums.exception;

import com.sohu.mp.common.exception.BaseException;

/**
 * @author kailiwang
 */
public class WithdrawStatusCheckException extends BaseException {

    private static final long serialVersionUID = -2833840126284044526L;

    public WithdrawStatusCheckException(String detail){
        super(SharingPlanExceptionEnum.WITHDRAW_STATUS_ERROR,detail);
    }

}
