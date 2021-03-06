package com.sohu.mp.sharingplan.controllers.inner;

import com.sohu.mp.common.exception.InvalidParameterException;
import com.sohu.mp.common.response.SuccessResponse;
import com.sohu.mp.common.util.DateUtil;
import com.sohu.mp.sharingplan.annotation.Passport;
import com.sohu.mp.sharingplan.model.MpProfile;
import com.sohu.mp.sharingplan.service.WithdrawService;
import com.sohu.mp.sharingplan.util.ParamCheckUtil;
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

    @Resource
    private WithdrawService withdrawService;

    /**
     * @api {POST} /inner/withdraw/rollback 提现回滚接口
     * @apiName withdrawRollback
     * @apiGroup withdraw
     * @apiParam {String} sign   权限验证码, 找mp开通
     * @apiParam {String} reason 罚金原因
     * @apiParam {String} passport 自媒体passport
     * @apiParam {String} operator 操作人公司邮箱地址
     * @apiParam {Date} withdrawDate 提现创建年月，每个月只能提现一次， 格式: yyyyMM
     * @apiSuccess (200){json} responseBody success
     * @apiSuccessExample Success-Response
     * HTTP/1.1 200 OK
     * {
     * "success": true //除此结果之外, 均为失败，只有mp待处理的提现(WithdrawStatusEnum.PENDING(0))才能调用此接口
     * }
     */
    @PostMapping("/rollback")
    public ResponseEntity withdrawRollback(@Passport MpProfile mpProfile,
                                           @RequestParam("sign") String sign,
                                           @RequestParam("reason") String reason,
                                           @RequestParam("operator") String operator,
                                           @DateTimeFormat(pattern = "yyyyMM") Date withdrawDate) {
        ParamCheckUtil.checkOperatorAuth(sign, reason, operator);
        if (withdrawDate == null) {
            throw new InvalidParameterException("withdrawDate param is null");
        }
        String date = DateUtil.getMonthStr(withdrawDate);

        withdrawService.withdrawRollback(mpProfile, date, reason,operator);
        return SuccessResponse.INSTANCE;
    }

}
