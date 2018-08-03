package com.sohu.mp.sharingplan.controllers.inner;

import com.sohu.mp.common.exception.InvalidParameterException;
import com.sohu.mp.common.response.SuccessResponse;
import com.sohu.mp.common.util.DateUtil;
import com.sohu.mp.sharingplan.annotation.MonitorServerError;
import com.sohu.mp.sharingplan.annotation.Passport;
import com.sohu.mp.sharingplan.model.MpProfile;
import com.sohu.mp.sharingplan.service.MulctService;
import com.sohu.mp.sharingplan.util.ParamCheckUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("/inner/mulct")
public class MulctController {

    @Resource
    private MulctService mulctService;

    /**
     * @api {POST} /inner/mulct/base 处罚base接口
     * @apiName baseMulct
     * @apiGroup mulct
     * @apiParam {String} sign 权限验证码, 找mp开通
     * @apiParam {String} reason 罚金原因
     * @apiParam {String} passport 自媒体passport
     * @apiParam {String} operator 操作人公司邮箱地址
     * @apiParam {Date} periodDay 处罚的收益日期,只能处罚当月base。 格式: yyyy-MM-dd
     * @apiSuccess (200){json} responseBody success
     * @apiSuccessExample Success-Response
     * HTTP/1.1 200 OK
     * {
     * "success": true //除此结果之外, 均为失败
     * }
     */
    @MonitorServerError
    @PostMapping("/base")
    public ResponseEntity baseMulct(@Passport MpProfile mpProfile,
                                    @RequestParam("sign") String sign,
                                    @RequestParam("reason") String reason,
                                    @RequestParam("operator") String operator,
                                    @DateTimeFormat(pattern = "yyyy-MM-dd") Date periodDay) {
        ParamCheckUtil.checkOperatorAuth(sign, reason, operator);
        if (periodDay == null) {
            throw new InvalidParameterException("periodDay param is null");
        }
        LocalDate now = LocalDate.now();
        if (DateUtil.judgeTodayFirstDayOfMonth()) {
            now = DateUtil.convert2LocalDate(DateUtil.getPreFirstDayOfMonth(1));
        }
        LocalDate period = DateUtil.convert2LocalDate(periodDay);
        if (period.getYear() != now.getYear() || period.getMonth() != now.getMonth()) {
            throw new InvalidParameterException("只能处罚当月计划收益");
        }

        if (!mulctService.canLockBaseMulct(mpProfile.getPassport())) {
            throw new InvalidParameterException("base it is mulcting, please wait");
        }
        mulctService.dealBaseMulct(mpProfile, operator, periodDay, reason);
        return SuccessResponse.INSTANCE;
    }

    /**
     * @api {POST} /inner/mulct/bonus 处罚bonus接口
     * @apiName bonusMulct
     * @apiGroup mulct
     * @apiParam {String} sign 权限验证码, 找mp开通
     * @apiParam {String} reason 罚金原因
     * @apiParam {String} passport 自媒体passport
     * @apiParam {String} operator 操作人公司邮箱地址
     * @apiParam {String} code 处罚的某期分成code,只能处罚上个月bonus。 格式: 分成五月计划为-> flow201805
     * @apiSuccess (200){json} responseBody success
     * @apiSuccessExample Success-Response
     * HTTP/1.1 200 OK
     * {
     * "success": true //除此结果之外, 均为失败
     * }
     */
    @MonitorServerError
    @PostMapping("/bonus")
    public ResponseEntity bonusMulct(@Passport MpProfile mpProfile,
                                     @RequestParam("sign") String sign,
                                     @RequestParam("code") String code,
                                     @RequestParam("reason") String reason,
                                     @RequestParam("operator") String operator) {
        ParamCheckUtil.checkOperatorAuth(sign, reason, operator);
        if (!mulctService.canLockBonusMulct(mpProfile.getPassport())) {
            throw new InvalidParameterException("bonus it is mulcting, please wait");
        }
        mulctService.dealBonusMulct(mpProfile, operator, code, reason);
        return SuccessResponse.INSTANCE;
    }

}

