package com.sohu.mp.sharingplan.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Bonus {
    private int id;
    private int userId;
    private String passport;
    private String rightInterestCode;
    private BigDecimal amount;
    private int source;
    private int type;
    private String sourceName;
    private String typeName;
    private int status;
    private Date createTime;
    private Date modifiedTime;

    private Integer mpChannelId;

    public Bonus() {
    }

    public Bonus(int userId, String passport, String rightInterestCode, BigDecimal amount, int source,
                 int type, String sourceName, String typeName, int status, Integer mpChannelId) {
        this.userId = userId;
        this.passport = passport;
        this.rightInterestCode = rightInterestCode;
        this.amount = amount;
        this.source = source;
        this.type = type;
        this.sourceName = sourceName;
        this.typeName = typeName;
        this.status = status;
        this.mpChannelId = mpChannelId;
    }
}
