package com.sohu.mp.sharingplan.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Profit {
    private int id;
    private int userId;
    private String rightInterestCode;
    private int source;
    private int type;
    private int status;
    private BigDecimal amount;
    private BigDecimal mulct;
    private Date periodDay;
    private Date createTime;
    private Date modifiedTime;
}
