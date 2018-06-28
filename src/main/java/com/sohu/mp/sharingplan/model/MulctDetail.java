package com.sohu.mp.sharingplan.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MulctDetail {
    private int id;
    private long userId;
    private String passport;
    private String rightInterestCode;
    private int source;
    private Date periodDay;
    private BigDecimal amount;
    private int status;
    private int type;
    private String operator;
    private String reason;
    private Date createTime;
    private Date modifiedTime;

    public MulctDetail() {
    }

    public MulctDetail(long userId, String passport, String rightInterestCode, int source,
                       Date periodDay, BigDecimal amount, int status, int type, String operator, String reason) {
        this.userId = userId;
        this.passport = passport;
        this.rightInterestCode = rightInterestCode;
        this.source = source;
        this.periodDay = periodDay;
        this.amount = amount;
        this.status = status;
        this.type = type;
        this.operator = operator;
        this.reason = reason;
    }
}
