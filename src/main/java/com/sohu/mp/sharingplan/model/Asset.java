package com.sohu.mp.sharingplan.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Asset {
    private int id;
    private int userId;
    private int source;
    private BigDecimal totalAmount;
    private BigDecimal validAmount;
    private BigDecimal withdrawAmount;
    private BigDecimal withdrawingAmount;
    private BigDecimal deposit;
    private BigDecimal mulct;
    private Date createTime;
    private Date modifiedTime;

    private BigDecimal addTotalAmount;
    private BigDecimal addValidAmount;
}
