package com.sohu.mp.sharingplan.model;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Withdraw {
    private int id;
    private int userId;
    private BigDecimal total;
    private BigDecimal amount;
    private BigDecimal shouldTax;
    private BigDecimal tax;
    private BigDecimal fee;
    private int status;
    private Date createTime;
    private String passport;
    private String ip;
}

