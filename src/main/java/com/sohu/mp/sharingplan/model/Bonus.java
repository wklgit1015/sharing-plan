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
    private BigDecimal mulct;
    private int source;
    private int type;
    private String sourceName;
    private String typeName;
    private int status;
    private Date createTime;
    private Date modifiedTime;
}
