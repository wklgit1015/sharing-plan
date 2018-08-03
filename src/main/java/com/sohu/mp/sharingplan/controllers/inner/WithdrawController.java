package com.sohu.mp.sharingplan.controllers.inner;

import com.sohu.mp.common.exception.InvalidParameterException;
import com.sohu.mp.common.response.SuccessResponse;
import com.sohu.mp.common.util.DateUtil;
import com.sohu.mp.sharingplan.annotation.MonitorServerError;
import com.sohu.mp.sharingplan.annotation.Passport;
import com.sohu.mp.sharingplan.model.MpProfile;
import com.sohu.mp.sharingplan.service.WithdrawService;
import com.sohu.mp.sharingplan.util.ParamCheckUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;


@RestController
@RequestMapping("/inner/withdraw")
public class WithdrawController {

    private static final Logger log = LoggerFactory.getLogger(WithdrawController.class);

    @Resource
    private WithdrawService withdrawService;

    /**
     * @api {POST} /inner/withdraw/rollback 提现相关接口
     * @apiName withdrawRollback
     * @apiGroup withdraw
     * @apiParam {String} sign 权限验证码, 找mp开通
     * @apiParam {String} reason 罚金原因
     * @apiParam {String} passport 自媒体passport
     * @apiParam {String} operator 操作人公司邮箱地址
     * @apiParam {Date} withdrawMonth 提现日期，每个月只能提现一次， 格式: yyyy-MM
     * @apiSuccess (200){json} responseBody success
     * @apiSuccessExample Success-Response
     * HTTP/1.1 200 OK
     * {
     * "success": true //除此结果之外, 均为失败
     * }
     */
    @MonitorServerError
    @PostMapping("/rollback")
    public ResponseEntity withdrawRollback(@Passport MpProfile mpProfile,
                                           @RequestParam("sign") String sign,
                                           @RequestParam("reason") String reason,
                                           @RequestParam("operator") String operator,
                                           @DateTimeFormat(pattern = "yyyy-MM") Date withdrawMonth) {
        ParamCheckUtil.checkOperatorAuth(sign, reason, operator);
        if (withdrawMonth == null) {
            throw new InvalidParameterException("withdrawMonth param is null");
        }
        String month = DateUtil.getMonthStr(withdrawMonth);
        if (!withdrawService.canLockWithdraw(mpProfile.getPassport())) {
            throw new InvalidParameterException("withdraw rollback is on progress, please wait");
        }
        withdrawService.withdrawRollback(mpProfile, month, reason);
        return SuccessResponse.INSTANCE;
    }

}
