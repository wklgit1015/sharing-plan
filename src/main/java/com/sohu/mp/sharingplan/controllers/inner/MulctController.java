package com.sohu.mp.sharingplan.controllers.inner;

import com.sohu.mp.common.exception.InvalidParameterException;
import com.sohu.mp.common.exception.UserNotFoundException;
import com.sohu.mp.common.response.SuccessResponse;
import com.sohu.mp.common.util.CodecUtil;
import com.sohu.mp.common.util.DateUtil;
import com.sohu.mp.sharingplan.enums.exception.MulctNoAuthException;
import com.sohu.mp.sharingplan.model.MpProfile;
import com.sohu.mp.sharingplan.service.MpProfileService;
import com.sohu.mp.sharingplan.service.MulctService;
import org.apache.commons.lang3.StringUtils;
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
    private MpProfileService mpProfileService;

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
    @PostMapping("/base")
    public ResponseEntity baseMulct(@RequestParam("sign") String sign,
                                    @RequestParam("reason") String reason,
                                    @RequestParam("passport") String passport,
                                    @RequestParam("operator") String operator,
                                    @DateTimeFormat(pattern = "yyyy-MM-dd") Date periodDay) {
        if (periodDay == null) {
            throw new InvalidParameterException("periodDay param is null");
        }

        LocalDate now = LocalDate.now();
        LocalDate period = DateUtil.convert2LocalDate(periodDay);
        if (period.getYear() != now.getYear() || period.getMonth() != now.getMonth()) {
//            throw new InvalidParameterException("只能处罚当月计划收益");
        }

        MpProfile mpProfile = checkParam(sign, reason, passport, operator);
        if (!mulctService.canLockBaseMulct(passport)) {
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
    @PostMapping("/bonus")
    public ResponseEntity bonusMulct(@RequestParam("sign") String sign,
                                     @RequestParam("code") String code,
                                     @RequestParam("reason") String reason,
                                     @RequestParam("passport") String passport,
                                     @RequestParam("operator") String operator) {
        MpProfile mpProfile = checkParam(sign, reason, passport, operator);
        if (!mulctService.canLockBonusMulct(passport)) {
            throw new InvalidParameterException("bonus it is mulcting, please wait");
        }
        mulctService.dealBonusMulct(mpProfile, operator, code, reason);
        return SuccessResponse.INSTANCE;
    }

    private MpProfile checkParam(String sign, String reason, String passport, String operator) {
        if (StringUtils.isBlank(reason)) {
            throw new InvalidParameterException("reason is empty");
        }
        if (!sign.equals(CodecUtil.hmacSha1(operator))) {
            throw MulctNoAuthException.INSTANCE;
        }
        MpProfile mpProfile = mpProfileService.getByPassport(passport);
        if (mpProfile == null) {
            throw UserNotFoundException.INSTANCE;
        }
        return mpProfile;
    }

}
