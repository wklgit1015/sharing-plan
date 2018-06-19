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
@RequestMapping("/inner")
public class MulctController {

    @Resource
    private MpProfileService mpProfileService;

    @Resource
    private MulctService mulctService;

    @PostMapping("/mulct")
    public ResponseEntity mulct(@RequestParam("sign") String sign,
                                @RequestParam("reason") String reason,
                                @RequestParam("passport") String passport,
                                @RequestParam("operator") String operator,
                                @DateTimeFormat(pattern = "yyyy-MM-dd") Date periodDay) {
        if (periodDay == null) {
            throw new InvalidParameterException("periodDay param is null");
        }
        if (StringUtils.isBlank(reason)) {
            throw new InvalidParameterException("reason is empty");
        }
        if (!sign.equals(CodecUtil.hmacSha1(operator))) {
            throw MulctNoAuthException.INSTANCE;
        }
        LocalDate now = LocalDate.now();
        LocalDate period = DateUtil.convert2LocalDate(periodDay);
        if (period.getYear() != now.getYear() || period.getMonth() != now.getMonth()) {
            throw new InvalidParameterException("只能处罚当月计划收益");
        }
        MpProfile mpProfile = mpProfileService.getByPassport(passport);
        if (mpProfile == null) {
            throw UserNotFoundException.INSTANCE;
        }
        if (!mulctService.canLockMulct(passport)) {
            throw new InvalidParameterException("it is mulcting, please wait");
        }
        mulctService.dealMulct(mpProfile, operator, periodDay, reason);
        return SuccessResponse.INSTANCE;
    }

}
