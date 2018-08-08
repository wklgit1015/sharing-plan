package com.sohu.mp.sharingplan.model;

import lombok.Data;

import java.util.Date;

@Data
public class WithdrawProgress {
    private int id;
    private int withdrawId;
    private int userId;
    private int type;
    private String batchId;
    private String pstransid;
    private String detail;
    private Date createTime;
    private Date modifiedTime;
    private String operator;
    private String reason;

    private Withdraw withdraw;

    public WithdrawProgress(int withdrawId, int userId, int type, String detail, Date createTime, String operator, String reason) {
        this.withdrawId = withdrawId;
        this.userId = userId;
        this.type = type;
        this.detail = detail;
        this.createTime = createTime;
        this.operator = operator;
        this.reason = reason;
    }
}

